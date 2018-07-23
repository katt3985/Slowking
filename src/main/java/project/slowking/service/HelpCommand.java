package project.slowking.service;

import project.slowking.annotation.CommandController;
import project.slowking.annotation.DiscordCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

@CommandController
public class HelpCommand {


    private IDiscordClient client;
    private CommandRegistrationDirectory registeredObjects;
    private String commandCharacter;

    @Autowired
    public HelpCommand(IDiscordClient client,
                       CommandRegistrationDirectory registeredObjects,
                       @Value("${discord.command-prefix}") String commandCharacter) {
        this.client = client;
        this.registeredObjects = registeredObjects;
        this.commandCharacter = commandCharacter;
    }

    @DiscordCommand(command = "help", help = "display this message")
    public void getHelp(MessageEvent event)
    {
        EmbedBuilder helpBuilder = new EmbedBuilder().withTitle("Regal Commands");

        helpBuilder.withThumbnail(client.getOurUser().getAvatarURL());

        helpBuilder.appendField(commandCharacter+"command subCommand MANDATORY *[OPTIONAL]*",
                "include spaces in parameters by surrounding them with \"\"",false);

        registeredObjects.entrySet().forEach(entry -> {
            StringBuilder title = new StringBuilder()
                    .append(commandCharacter)
                    .append(entry.getKey().getFullCommandString());
            entry.getValue().getParameters().forEach(param ->
                    title.append(" ").append(param.toUpperCase())
            );
            helpBuilder.appendField(title.toString(),
                    entry.getValue().getHelp(),
                    false);
        });

        RequestBuffer.request(() -> event.getChannel().sendMessage(helpBuilder.build()));
    }
}
