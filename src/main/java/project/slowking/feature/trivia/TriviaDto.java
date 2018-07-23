package project.slowking.feature.trivia;


import lombok.Data;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class TriviaDto {
    private String category;
    private String type;
    private String difficulty;
    private String question;
    private String correct_answer;
    private List<String> incorrect_answers;

    public TriviaDto decode()
    {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        category = new String(decoder.decode(category));
        type = new String(decoder.decode(type));
        difficulty = new String(decoder.decode(difficulty));
        question = new String(decoder.decode(question));
        correct_answer = new String(decoder.decode(correct_answer));

        incorrect_answers = incorrect_answers.stream()
                .map(s -> new String(decoder.decode(s)))
                .collect(Collectors.toList());

        return this;
    }

}
