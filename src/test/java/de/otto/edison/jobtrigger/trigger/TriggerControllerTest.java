package de.otto.edison.jobtrigger.trigger;

import de.otto.edison.jobtrigger.definition.JobDefinition;
import de.otto.edison.jobtrigger.util.TestViewResolverBuilder;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TriggerControllerTest {

    @Mock
    private TriggerService triggerService;

    @InjectMocks
    private TriggerController controller;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers(TestViewResolverBuilder.createViewResolver()).build();
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

    @Test
    public void shouldLimitNumberOfTriggersIfMoreAreAvailable() throws Exception {
        List<TriggerResult> triggers = createEmptyTriggerResults(40);
        when(triggerService.getLastResults()).thenReturn(triggers);

        mockMvc
                .perform(get("/triggers")
                        .param("startFrom", "unknownId"))
                .andExpect(model()
                        .attribute("results", Matchers.hasSize(TriggerController.PAGE_SIZE)));
    }

    @Test
    public void shouldReturnExactNumberOfTriggersIfLessThanPageSize() throws Exception {
        int numberOfTriggers = 12;
        List<TriggerResult> triggers = createEmptyTriggerResults(numberOfTriggers);
        when(triggerService.getLastResults()).thenReturn(triggers);

        mockMvc
                .perform(get("/triggers"))
                .andExpect(model()
                        .attribute("results", Matchers.hasSize(numberOfTriggers)));
    }

    @Test
    public void shouldStartWithGivenIdIfStartFromIdIsGiven() throws Exception {
        int numberOfTriggers = 30;
        String startId = "5";

        List<TriggerResult> triggers = createEmptyTriggerResults(numberOfTriggers);
        when(triggerService.getLastResults()).thenReturn(triggers);

        mockMvc
                .perform(get("/triggers")
                        .param("startFrom", startId))
                .andExpect(model()
                        .attribute("results", Matchers.hasSize(TriggerController.PAGE_SIZE)));
    }

    @Test
    public void shouldHaveNoNextLinkIfNoMoreResultsAvailable() throws Exception {
        List<TriggerResult> triggers = createEmptyTriggerResults(10);
        when(triggerService.getLastResults()).thenReturn(triggers);

        mockMvc
                .perform(get("/triggers"))
                .andExpect(model()
                        .attributeDoesNotExist("next"));
    }

    @Test
    public void shouldHaveNextLinkIfMoreResultsAvailable() throws Exception {
        List<TriggerResult> triggers = createEmptyTriggerResults(26);
        when(triggerService.getLastResults()).thenReturn(triggers);

        mockMvc
                .perform(get("/triggers"))
                .andExpect(model()
                        .attribute("next", is("triggers?startFrom=25")));
    }

    @Test
    public void shouldHaveNoPreviousLinkIfResultsStartAtZero() throws Exception {
        List<TriggerResult> triggers = createEmptyTriggerResults(25);
        when(triggerService.getLastResults()).thenReturn(triggers);

        mockMvc
                .perform(get("/triggers"))
                .andExpect(model()
                        .attributeDoesNotExist("prev"));
    }

    @Test
    public void shouldHavePreviousLinkIfPreviousResultsAvailable() throws Exception {
        List<TriggerResult> triggers = createEmptyTriggerResults(30);
        when(triggerService.getLastResults()).thenReturn(triggers);

        mockMvc
                .perform(get("/triggers")
                        .param("startFrom", "1"))
                .andExpect(model()
                        .attribute("prev", is("triggers?startFrom=0")))
                .andExpect(model()
                        .attribute("prev", is("triggers?startFrom=0")));
    }

    private List<TriggerResult> createEmptyTriggerResults(int numberOfTriggers) {
        List<TriggerResult> result = new ArrayList<>();
        for (int i = 0; i < numberOfTriggers; i++) {
            result.add(new TriggerResult("" + i, TriggerStatus.fromHttpStatus(200), Optional.empty(), mock(JobDefinition.class)));
        }
        return result;
    }
}