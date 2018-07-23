package project.slowking.service;

import project.slowking.annotation.CommandController;
import project.slowking.annotation.DiscordCommand;
import project.slowking.domain.CommandFragment;
import project.slowking.domain.CommandRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Component
public class CommandRegistrationDirectory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandRegistrationDirectory.class);

    private static final Predicate<Annotation> isDiscordCommand =
            annotation -> annotation.annotationType().equals(DiscordCommand.class);
    private static final Function<String,Optional<String>> emptyCheck =
            s -> "".equals(s)? Optional.empty(): Optional.of(s);

    private Map<CommandFragment,CommandRegistration> registeredObjects = new ConcurrentHashMap<>();


    public void register(Object o) {
        Optional<CommandController> cmdObj = Optional.ofNullable(o.getClass().getAnnotation(CommandController.class));
        if (!cmdObj.isPresent()) {
            LOGGER.info("did not register un-Annotated class {}", o.getClass().toString());
            return;
        }
        Optional<String> superCommand = ((Supplier<Optional<String>>) () -> {
            if (!"".equals(cmdObj.get().command())) {
                return Optional.of(cmdObj.get().command());
            } else if (!"".equals(cmdObj.get().value())) {
                return Optional.of(cmdObj.get().command());
            } else {
                return Optional.empty();
            }
        }).get();


        Arrays.stream(o.getClass().getMethods()).filter(method ->
                Arrays.stream(method.getDeclaredAnnotations())
                        .anyMatch(isDiscordCommand)
        ).forEach(method -> {
            CommandRegistration registration = new CommandRegistration();
            DiscordCommand a = method.getAnnotation(DiscordCommand.class);
            Optional<String> command = superCommand.isPresent()? superCommand:
                    emptyCheck.apply(a.command());
            Optional<String> subCommand = emptyCheck.apply(a.subCommand());


            if (!(command.isPresent()))
            {
                LOGGER.warn("class {} has an improper @DiscordCommand Annotation for {}",
                        o.getClass().toString(), method.toString());
                return;
            }

            CommandFragment key = new CommandFragment();
            key.setCommand(command.get());
            key.setSubCommand(subCommand);

            if (registeredObjects.containsKey(key))
            {
                LOGGER.warn("Attepted to register duplicate commands when registering {} method {}",
                        o.getClass().toString(), method.toString());
                return;
            }
            registration.setCommand(key.getCommand());
            registration.setSubCommand(key.getSubCommand());
            registration.setComponent(o);
            registration.setMethod(method);
            registration.setHelp(a.help());
            registration.setParameters(Arrays.asList(a.parameters()));
            registeredObjects.put(key, registration);
            LOGGER.info("Successfully registed command: {}", key.getFullCommandString());
        });
    }

    public boolean containsKey(CommandFragment key) {
        return registeredObjects.containsKey(key);

    }
    public CommandRegistration get(CommandFragment key){
        return registeredObjects.get(key);
    }
    public Set<Map.Entry<CommandFragment, CommandRegistration>> entrySet()
    {
        return registeredObjects.entrySet();
    }
}
