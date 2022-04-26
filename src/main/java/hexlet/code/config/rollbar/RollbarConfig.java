package hexlet.code.config.rollbar;

import com.rollbar.notifier.Rollbar;
import com.rollbar.notifier.config.Config;
import com.rollbar.spring.webmvc.RollbarSpringConfigBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan({
    "hexlet.code",
        "com.rollbar.spring"
})
public class RollbarConfig {

    @Value("${ROLLBAR_TOKEN:0fd5315b677040d78d8b60ec5dc8f056}")
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
            .environment("development")
            .enabled(activeProfile == "prod")
            .build();
    }


}
