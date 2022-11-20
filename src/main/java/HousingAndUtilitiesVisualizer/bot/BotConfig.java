package HousingAndUtilitiesVisualizer.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:bot.properties")
public class BotConfig {
    @Autowired
    Environment environment;

    public String getToken() {
        return environment.getProperty("token");
    }

    public String getUsername() {
        return environment.getProperty("username");
    }
}
