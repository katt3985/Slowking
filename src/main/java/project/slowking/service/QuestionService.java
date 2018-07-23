package project.slowking.service;

import project.slowking.domain.Pair;
import org.springframework.stereotype.Component;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class QuestionService {

    private final Map<Integer,String> numbers = new HashMap<>();

    public QuestionService() {
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

    public IMessage postQuestion(IChannel channel, String question, List<String> options){


        List<Pair<String,String>> questionOptions = IntStream
                .range(1, options.size()+1)
                .mapToObj(i -> Pair.of(numbers.get(i),options.get(i-1)))
                .collect(Collectors.toList());


        EmbedBuilder builder = new EmbedBuilder().withTitle(question);

        questionOptions.forEach(pair -> {
            builder.appendField(pair.getA(),pair.getB(),false);
        });
        IMessage sent = RequestBuffer.request(()-> channel.sendMessage(builder.build())).get();
        for (int i = 1; i <= questionOptions.size() ; i++) {
            final int j = i;
            RequestBuffer.request(() -> sent.addReaction(ReactionEmoji.of(numbers.get(j)))).get();

        }
        return sent;
    }

    public IMessage postQuestionWithTitles(IChannel channel, String question, List<Pair<String,String>> options){

        List<Pair<String,String>> questionOptions = IntStream
                .range(1, options.size()+1)
                .mapToObj(i ->
                {
                    Pair<String,String> q = options.get(i-1);
                    return Pair.of(numbers.get(i) +" "+ q.getA(), q.getB());
                })
                .collect(Collectors.toList());


        EmbedBuilder builder = new EmbedBuilder().withTitle(question);

        questionOptions.forEach(pair -> {
            builder.appendField(pair.getA(),pair.getB(),false);
        });
        IMessage sent = RequestBuffer.request(()-> channel.sendMessage(builder.build())).get();
        for (int i = 1; i <= questionOptions.size() ; i++) {
            final int j = i;
            RequestBuffer.request(() -> sent.addReaction(ReactionEmoji.of(numbers.get(j)))).get();

        }
        return sent;
    }

    public List<Pair<Integer,List<IUser>>> TallyAnswers(IMessage question)
    {
        Map<ReactionEmoji,Integer> reverse = numbers.entrySet().stream()
            .map(entry -> Pair.of(ReactionEmoji.of(entry.getValue()), entry.getKey()))
            .collect(Collectors.toMap(Pair::getA, Pair::getB));
        return question.getReactions()
                .stream()
                .filter(iReaction -> numbers.values().contains(iReaction.getEmoji().getName()))
                .map(iReaction -> {
                    Integer a = reverse.get(iReaction.getEmoji());
                    return Pair.of(a, iReaction.getUsers()
                            .stream()
                            .filter(iUser -> !iUser.equals(question.getAuthor()))
                            .collect(Collectors.toList()));
                }).collect(Collectors.toList());
    }
}
