package project.slowking.feature.polls;

import project.slowking.annotation.CommandController;
import project.slowking.annotation.DiscordCommand;
import project.slowking.annotation.EventController;
import project.slowking.domain.CommandInvocation;
import project.slowking.domain.Pair;
import project.slowking.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionEvent;
import sx.blah.discord.handle.obj.IEmbed;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@EventController
@CommandController
public class PollController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PollController.class);

    private IDiscordClient client;

    private Pattern phrasePattern = Pattern.compile("(\"[^\n]+\"|\\w+)");

    private QuestionService qService;


    private final Map<Integer,String> numbers = new HashMap<>();

    @Autowired
    public PollController(IDiscordClient client, QuestionService qService) {
        this.client = client;
        this.qService = qService;
        numbers.put(1,"1⃣");
        numbers.put(2,"2⃣");
        numbers.put(3,"3⃣");
        numbers.put(4,"4⃣");
        numbers.put(5,"5⃣");
        numbers.put(6,"6⃣");
        numbers.put(7,"7⃣");
        numbers.put(8,"8⃣");
        numbers.put(9,"9⃣");
        numbers.put(10,"\uD83D\uDD1F");


    }

    @DiscordCommand(command = "poll", help = "create a simple poll with a title",
            parameters = {"title","option1", "option2","[... option10]"})
    public void createPoll(MessageEvent event, CommandInvocation command){

        if (command.getArgs().size() < 3)
        {
            event.getMessage().reply("a poll must have a title and at least two options.");
            return;
        }
        else if(command.getArgs().size() > 11)
        {
            event.getMessage().reply("a poll cannot have more than 10 options");
            return;
        }

        String title = "Poll: " + command.getArgs().remove(0);

        List<Pair<String,String>> pollOptions = command.getArgs()
                .stream()
                .map(s -> Pair.of(s, "nobody"))
                .collect(Collectors.toList());

        qService.postQuestionWithTitles(event.getChannel(),title,pollOptions);

    }

    @EventSubscriber
    public void updatePoll(ReactionEvent event){
        IMessage message = event.getMessage();
        if(message.getAuthor().equals(client.getOurUser()) &&
                !event.getMessage().getEmbeds().isEmpty() &&
                event.getMessage().getEmbeds().get(0).getTitle().startsWith("Poll:"))
        {
            LOGGER.info("starting edit");
            IEmbed previousEmbed = message.getEmbeds().get(0);
            Optional<IEmbed.IEmbedField> toAlter = previousEmbed.getEmbedFields()
                    .stream()
                    .filter(field -> field.getName().startsWith(event.getReaction().getEmoji().getName()))
                    .findAny()
                    ;
            if (!toAlter.isPresent()) return;

            EmbedBuilder builder = new EmbedBuilder();
            builder.withTitle(previousEmbed.getTitle());

            String name = toAlter.get().getName();
            List<String> users = event.getReaction()
                    .getUsers()
                    .stream()
                    .filter(user -> !user.equals(client.getOurUser()))
                    .map(user -> user.getDisplayName(event.getGuild()))
                    .collect(Collectors.toList());


            for (IEmbed.IEmbedField field:previousEmbed.getEmbedFields()) {
                if(field.equals(toAlter.get()))
                {
                    String value = users.isEmpty()? "nobody": String.join(", ",users);
                    builder.appendField(name, value, false);
                }
                else
                {
                    builder.appendField(field);
                }
            }

            RequestBuffer.request(() -> message.edit(builder.build()));


        }
    }
}
