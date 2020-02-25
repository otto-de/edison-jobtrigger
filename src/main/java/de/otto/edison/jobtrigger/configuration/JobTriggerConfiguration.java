package de.otto.edison.jobtrigger.configuration;

import de.otto.edison.jobtrigger.security.AuthHeaderProvider;
import de.otto.edison.jobtrigger.security.BasicAuthCredentials;
import de.otto.edison.jobtrigger.security.BasicAuthHeaderProvider;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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


    @Bean
    @ConditionalOnMissingBean(AuthHeaderProvider.class)
    public AuthHeaderProvider basicAuthHeaderProvider(BasicAuthCredentials basicAuthCredentials) {
        return new BasicAuthHeaderProvider(basicAuthCredentials);
    }

}
