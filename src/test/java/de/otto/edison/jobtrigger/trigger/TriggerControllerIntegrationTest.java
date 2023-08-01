package de.otto.edison.jobtrigger.trigger;

import com.google.common.collect.ImmutableList;
import de.otto.edison.jobtrigger.Server;
import de.otto.edison.jobtrigger.discovery.DiscoveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(classes = {Server.class}, webEnvironment = DEFINED_PORT, properties = {"server.port=0"})
public class TriggerControllerIntegrationTest {

    @Autowired
    WebApplicationContext wac;

    @Autowired
    private DiscoveryService discoveryService;

    @TestConfiguration
    @PropertySource("/version.properties")
    static class TestConfig {
        @Bean(name = "DiscoveryService")
        @Primary
        public DiscoveryService discoveryServiceForTests() {
            return mock(DiscoveryService.class);
        }
    }

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void shouldReturnListOfTriggers() throws Exception {
        when(discoveryService.allJobDefinitions()).thenReturn(ImmutableList.of());

        mockMvc.perform(post("/triggers/start"));
        mockMvc.perform(get("/triggers"));

        verify(discoveryService).allJobDefinitions();
    }
}