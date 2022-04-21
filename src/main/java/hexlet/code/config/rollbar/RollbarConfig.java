package hexlet.code.config.rollbar;

import com.rollbar.notifier.Rollbar;
import com.rollbar.notifier.config.Config;
import com.rollbar.spring.webmvc.RollbarSpringConfigBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@Configuration
@EnableWebMvc
@ComponentScan({

// UPDATE TO YOUR PROJECT PACKAGE
    "hexlet.code",
        "com.rollbar.spring"


})
public class RollbarConfig {

    // Добавляем токен через переменные окружения
    @Value("${ROLLBAR_TOKEN}")
    private String rollbarToken;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    /**
     * Register a Rollbar bean to configure App with Rollbar.
     */
    @Bean
    public Rollbar rollbar() {

    return new Rollbar(getRollbarConfigs(rollbarToken));
    }

    private Config getRollbarConfigs(String accessToken) {

    return RollbarSpringConfigBuilder.withAccessToken(accessToken)
            .environment("dev")
            .enabled(activeProfile == "prod")
            .build();
    }

}
