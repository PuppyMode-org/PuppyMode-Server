package umc.puppymode.config;

import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Bean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class AsyncConfig {

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(1);
    }
}
