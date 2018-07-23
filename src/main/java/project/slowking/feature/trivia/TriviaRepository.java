package project.slowking.feature.trivia;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TriviaRepository {


    private RestTemplate client;



    @Autowired
    public TriviaRepository(RestTemplateBuilder builder){

        this.client = builder.build();
    }

    public List<TriviaDto> getTriviaQuestions(int count){
        Map<String,Object> args = new HashMap<>();
        args.put("amount",count);
        args.put("encode","base64");
        Response responce =
                client.getForObject("https://opentdb.com/api.php", Response.class,args);
        return responce.getResults()
                .stream()
                .map(TriviaDto::decode)
                .collect(Collectors.toList());

    }

    @Data
    private static class Response {
        private int response_code;
        private List<TriviaDto> results;
    }
}
