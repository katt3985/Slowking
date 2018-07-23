package project.slowking.service;

import project.slowking.annotation.CommandController;
import project.slowking.annotation.EventController;
import project.slowking.domain.CommandInvocation;
import project.slowking.domain.CommandRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.MessageTokenizer;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@EventController
@CommandController
public class CommandDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandDispatcher.class);



    private CommandRegistrationDirectory registeredObjects;

    private String commandCharacter = "%";

    private Pattern phrasePattern = Pattern.compile("(\"[^\n]+\"|\\S+)");


    @Autowired
    public CommandDispatcher(@Value("${discord.command-prefix}") String commandCharacter,
                             CommandRegistrationDirectory registeredObjects) {
        this.commandCharacter = commandCharacter;
        this.registeredObjects = registeredObjects;
    }

    @EventSubscriber
    public void onMessage(MessageEvent event)
    {
        //LOGGER.info("Message: \"{}\"",event.getMessage().getContent());
        IMessage message = event.getMessage();
        if (message.getContent().startsWith(commandCharacter)){
            MessageTokenizer tokenizer = message.tokenize();
            List<String> commandList = new ArrayList<>();
            while (tokenizer.hasNextRegex(phrasePattern)){
                commandList.add(tokenizer.nextRegex(phrasePattern).toString());
            }
            if (commandList.isEmpty()) return;


            CommandInvocation commandInvocation = new CommandInvocation();
            commandInvocation.setCommand(commandList.remove(0).substring(1));
            commandInvocation.setArgs(Collections.emptyList());
            if ( commandList.size() >= 1)
            {
                commandInvocation.setSubCommand(Optional.of(commandList.get(0)));
                if (registeredObjects.containsKey(commandInvocation.getFragment()))
                {
                    commandList.remove(0);
                }
                else
                {
                    commandInvocation.setSubCommand(Optional.empty());
                }
                commandInvocation.setArgs(commandList);
            }
            else
            {
                commandInvocation.setSubCommand(Optional.empty());
            }

            if (!registeredObjects.containsKey(commandInvocation.getFragment())) return;
            CommandRegistration reg = registeredObjects.get(commandInvocation.getFragment());

            List<Object> args = Arrays.stream(reg.getMethod().getParameters())
                    .map(parameter -> {
                        if (parameter.getType().equals(MessageEvent.class))
                            return event;
                        else if (parameter.getType().equals(CommandInvocation.class))
                            return commandInvocation;
                        else
                            return null;
                    }).collect(Collectors.toList());

            try {
                reg.getMethod().invoke(reg.getComponent(),args.toArray());
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOGGER.error("failed to invoke DiscordCommand: {} (Class: {} Method: {})"
                        , commandInvocation.getCommand(), reg.getComponent().getClass().getCanonicalName(), reg.getMethod().getName());
                LOGGER.error("Exception caught:", e);
            }


        };
    }







}
