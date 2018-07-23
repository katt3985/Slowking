package project.slowking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

@Configuration
public class DiscordConfig {

    @Bean
    public IDiscordClient createClient(@Value("${discord.token}") String token) {
        return new ClientBuilder().withToken(token).login();
    }
}
