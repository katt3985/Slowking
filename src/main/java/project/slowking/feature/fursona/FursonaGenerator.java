package project.slowking.feature.fursona;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import project.slowking.annotation.CommandController;
import project.slowking.annotation.DiscordCommand;
import project.slowking.domain.CommandInvocation;
import org.springframework.core.io.ClassPathResource;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.util.EmbedBuilder;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Random;

@CommandController
public class FursonaGenerator {

    private WordBank wordBank;

    private Random rnd=new Random();

    @PostConstruct
    private void loadWordBank()
    {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        try {
            ClassPathResource resource = new ClassPathResource("FursonaWords.yml");
            wordBank = mapper.readValue(resource.getInputStream(),WordBank.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @DiscordCommand(command = "fursona", help = "generate a random fursona"
            ,parameters = {"[name]"})
    public void onEvent(MessageEvent event, CommandInvocation cmd)
    {

        String name = cmd.getArgs().isEmpty()? "Someone": String.join(" ", cmd.getArgs());
        EmbedBuilder builder = new EmbedBuilder();
        builder.withTitle(name +"'s Fursona");
        builder.appendField("Animal", getRandom(wordBank.getAnimals()),false);
        builder.appendField("Fur color",getRandom(wordBank.getColors()), false);
        StringBuilder sb = new StringBuilder();
        sb.append(getRandom(wordBank.getTraits()));
        sb.append(", ");
        sb.append(getRandom(wordBank.getTraits()));
        sb.append(". Likes ");
        sb.append(getRandom(wordBank.getItems()));
        sb.append(". Hates ");
        sb.append(getRandom(wordBank.getItems()));

        builder.appendField("Personality", sb.toString(),false);
        builder.appendField("Habitat", getRandom(wordBank.getHabitats()), false);

        event.getChannel().sendMessage(builder.build());

    }

    private String getRandom(List<String> list)
    {
        int num = Math.abs(rnd.nextInt());
        num %= list.size();
        return list.get(num);
    }

}
