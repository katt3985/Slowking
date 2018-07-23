package project.slowking.feature.trivia;

import project.slowking.domain.Pair;
import project.slowking.service.QuestionService;
import lombok.Builder;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Builder
public class TriviaGame {

    private QuestionService questionService;
    private IChannel channel;
    private IGuild guild;
    private List<IUser> players;
    private Map<String,Integer> score;
    private List<TriviaDto> questions;



    public void run()
    {
        for (TriviaDto question:questions) {

            List<String> options = new ArrayList<>(question.getIncorrect_answers());
            options.add(question.getCorrect_answer());

            Collections.shuffle(options);
            Integer answer=  options.indexOf(question.getCorrect_answer());

            IMessage sent = questionService.postQuestion(channel,question.getQuestion(),options);
            RequestBuffer.request(() -> channel.sendMessage("60 seconds, GO!"));

            try {
                Thread.sleep(3600000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            RequestBuffer.request(()->channel.sendMessage("Times up!"));

            List<IUser> peeps = questionService.TallyAnswers(sent)
                    .stream()
                    .filter(pair -> pair.getA().equals(answer))
                    .findAny()
                    .map(Pair::getB)
                    .orElse(Collections.emptyList());

            Future<IMessage> seq = RequestBuffer.request(() -> channel.sendMessage("the correct answer is: " + question.getCorrect_answer()));

            List<String> peepnames = peeps
                    .stream()
                    .filter(iUser -> players.contains(iUser))
                    .map(iUser -> iUser.getDisplayName(guild))
                    .peek(s -> score.put(s, score.get(s) + 1))
                    .collect(Collectors.toList());

            RequestBuffer.request(() ->channel.sendMessage("Congrats " +
                    String.join(", ",peepnames) + "! \n next question in 30 seconds." ));

            try {
                Thread.sleep(1800000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



        }



    }
}
