package de.otto.edison.jobtrigger.trigger;

import com.google.common.collect.ImmutableList;
import de.otto.edison.jobtrigger.Server;
import de.otto.edison.jobtrigger.discovery.DiscoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringApplicationConfiguration({Server.class})
@WebIntegrationTest
public class TriggerControllerIntegrationTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private TriggerService triggerService;

    @Autowired
    WebApplicationContext wac;

    @Autowired
    private DiscoveryService discoveryService;

    @Configuration
    @PropertySource(value = "/version.properties", ignoreResourceNotFound = false)
    static class TestConfig {
        @Bean
        @Primary
        public DiscoveryService discoveryService() {
            return mock(DiscoveryService.class);
        }
    }

    private MockMvc mockMvc;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test(enabled = false) // TODO: hv/sr currently disabled since this test won't run with gradle :(
    public void shouldReturnListOfTriggers() throws Exception {
        when(discoveryService.allJobDefinitions()).thenReturn(ImmutableList.of());

        mockMvc.perform(post("/triggers/start"));
        mockMvc.perform(get("/triggers"));

        verify(discoveryService).allJobDefinitions();
    }
}