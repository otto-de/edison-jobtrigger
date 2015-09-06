package de.otto.edison.jobtrigger.configuration;

import com.ning.http.client.AsyncHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author Guido Steinacker
 * @since 05.09.15
 */
@Configuration
@EnableAsync
@EnableScheduling
public class JobTriggerConfiguration {

    public static final int POOL_SIZE = 10;

    @Bean
    public AsyncHttpClient asyncHttpClient() {
        return new AsyncHttpClient();
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(POOL_SIZE);
        return taskScheduler;
    }


}
