package de.otto.edison.jobtrigger.configuration;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

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
        return new DefaultAsyncHttpClient();
    }

//    @Bean
//    @Autowired
//    public ThreadPoolTaskScheduler threadPoolTaskScheduler(JobTriggerProperties jobTriggerProperties) {
//        final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
//        taskScheduler.setPoolSize(jobTriggerProperties.getScheduler().getPoolsize());
//        return taskScheduler;
//    }


}
