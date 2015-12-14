package de.otto.edison.jobtrigger.trigger;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Test
public class TriggerControllerTest {

    @Mock
    private TriggerService triggerService;

    @InjectMocks
    private TriggerController controller;

    private MockMvc mockMvc;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void shouldTriggerAndRedirectOnStartTriggerEndpoint() throws Exception {
        mockMvc.perform(post("/triggers/start"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/triggers"));

        verify(triggerService).startTriggering();
    }

    @Test
    public void shouldStopTriggeringAndRedirectOnStopTriggerEndpoint() throws Exception {
        mockMvc.perform(post("/triggers/stop"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/triggers"));

        verify(triggerService).stopTriggering();
    }
}