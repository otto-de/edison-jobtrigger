package de.otto.edison.jobtrigger.discovery;

import com.google.common.collect.ImmutableList;
import de.otto.edison.jobtrigger.definition.JobDefinition;
import de.otto.edison.jobtrigger.util.TestViewResolverBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class DiscoveryControllerTest {

    @Mock
    private DiscoveryService discoveryService;

    @InjectMocks
    private DiscoveryController controller;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers(TestViewResolverBuilder.createViewResolver()).build();
    }

    @Test
    public void shouldCallRediscover() throws Exception {
        mockMvc.perform(post("/discover"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("discover"));
        verify(discoveryService).rediscover();
    }

    @Test
    public void shouldCallEnvironmentsOf() throws Exception {
        JobDefinition jobDefinition = jobDefinition("expectedEnv", "someUrl", "someService");
        List<JobDefinition> jobDefinitions = ImmutableList.of(jobDefinition);
        when(discoveryService.allJobDefinitions()).thenReturn(ImmutableList.of(
                jobDefinition("expectedEnv1", "firstUrl", "someService1"),
                jobDefinition("expectedEnv2", "secondsUrl", "someService2"),
                jobDefinition("expectedEnv2", "thirdUrl", "someService3")
        ));

        mockMvc.perform(get("/discover"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("discover"))
                .andExpect(model().attribute("environments", is(not(nullValue()))));
    }

    private JobDefinition jobDefinition(String environment, String jobDefinitionUrl, String service) {
        return new JobDefinition(
                jobDefinitionUrl,
                environment,
                service,
                "someTriggerUrl",
                "someJobType",
                "someDescription",
                Optional.of("someCronString"),
                Optional.empty(),
                3,
                Optional.empty());
    }
}
