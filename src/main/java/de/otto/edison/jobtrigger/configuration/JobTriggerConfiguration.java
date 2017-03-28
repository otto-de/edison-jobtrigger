package de.otto.edison.jobtrigger.configuration;

import com.ning.http.client.AsyncHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(JobTriggerProperties.class)
public class JobTriggerConfiguration {

    @Bean
    public AsyncHttpClient asyncHttpClient() {
        return new AsyncHttpClient();
    }

    @Bean
    @Autowired
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(JobTriggerProperties jobTriggerProperties) {
        final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(jobTriggerProperties.getScheduler().getPoolsize());
        return taskScheduler;
    }


}
