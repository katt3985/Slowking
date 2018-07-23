package project.slowking.feature.catgirl.api;

import project.slowking.annotation.CommandController;
import project.slowking.annotation.DiscordCommand;
import project.slowking.annotation.EventController;
import project.slowking.domain.CommandInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.List;

@EventController
@CommandController(command = "catgirl")
public class CatGirlController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CatGirlController.class);

    private CatGirlRepository repo;

    @Autowired
    public CatGirlController(CatGirlRepository repo) {
        this.repo = repo;
    }

    public void postRandomCatgirl(IChannel channel){
        CatGirl cg;
        do {
             cg = repo.getRandomImage();
        } while (cg.isNsfw());

        postCatGirl(cg, channel,"Random Catgirl(s)",false);
    }

    private void postCatGirl(CatGirl catGirl, IChannel channel, String title, boolean post){
        if (catGirl.isNsfw() && !channel.isNSFW())
        {
            LOGGER.warn("rejected NSFW Catgirl");
            if (post) channel.sendMessage("Sorry but thats no allowed here");
            return;
        }

        EmbedBuilder image = new EmbedBuilder();
        EmbedBuilder builder = new EmbedBuilder();
        image.withImage("https://nekos.moe/image/" + catGirl.getId());

        String artist = catGirl.getArtist() == null? "" : "Artist: " + catGirl.getArtist() +" " ;

        builder.withFooterText(artist);
        builder.withTimestamp(catGirl.getCreatedAt().toInstant());

        builder.appendField("\uD83D\uDC4D", Long.toString(catGirl.getLikes()),true);
        builder.appendField("\uD83D\uDC96", Long.toString(catGirl.getFavorites()), true);

        builder.withTitle(title);
        builder.withUrl("https://nekos.moe/post/"+ catGirl.getId());

        RequestBuffer.request(() -> channel.sendMessage(image.build())).get();
        RequestBuffer.request(() -> channel.sendMessage(builder.build())).get();
    }


    @EventSubscriber
    public void onMention(MentionEvent event){
        LOGGER.info("was mentioned: {}", event.getMessage(),toString());
        if(event.getMessage().toString().toLowerCase().contains("catgirl")) {
            postRandomCatgirl(event.getChannel());
        }

    }

    @DiscordCommand(help = "fetches a random image from neko.moe, you can also just ask nicely")
    public void command(MessageEvent event, CommandInvocation command){
        postRandomCatgirl(event.getChannel());
    }

    @DiscordCommand(subCommand = "id", help = "fetch a catgirl by her id", parameters = {"id"})
    public void postById(MessageEvent event, CommandInvocation command)
    {
        if(command.getArgs().isEmpty())
        {
            event.getMessage().reply("I need an Id to do that.");
            return;
        }
        command.getArgs().forEach(id ->{
            CatGirl cg = repo.getImage(id);
            postCatGirl(cg,event.getChannel(),id,true);
        });
    }

    @DiscordCommand(subCommand = "search", help = "fetch a catgirls by tags", parameters = {"[count]", "[skip]", "[...tags]"})
    public void searchAndPost(MessageEvent event, CommandInvocation command)
    {

        if(command.getArgs().isEmpty())
        {
            event.getMessage().reply("I need at least one tag");
            return;
        }

        List<String> args = command.getArgs();
        int count = 0;
        int skip = 0;

        if(!args.isEmpty()) count = args.get(0).matches("\\d+")? Integer.valueOf(args.remove(0)): 0;
        if(!args.isEmpty()) skip = args.get(0).matches("\\d+")? Integer.valueOf(args.remove(0)): 0;

        if(args.isEmpty())
        {
            event.getMessage().reply("I need at least one tag");
            return;
        }
        repo.findByTags(args,event.getChannel().isNSFW(),count,skip)
                .forEach(catGirl -> postCatGirl(catGirl,event.getChannel(),catGirl.getId(),false));
    }
}
