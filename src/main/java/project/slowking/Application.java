package project.slowking;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class Application{

    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        context = new SpringApplicationBuilder(Application.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }



}
