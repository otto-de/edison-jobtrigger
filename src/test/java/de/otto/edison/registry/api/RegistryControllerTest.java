package de.otto.edison.registry.api;

import com.google.common.base.Equivalence;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import de.otto.edison.jobtrigger.util.TestViewResolverBuilder;
import de.otto.edison.registry.service.RegisteredService;
import de.otto.edison.registry.service.Registry;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class RegistryControllerTest {

    MockMvc mockMvc;

    @Mock
    Registry registry;


    @InjectMocks
    RegistryController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers(TestViewResolverBuilder.createViewResolver()).build();
        ;
    }

    @Test
    public void shouldReturnEnvironments() throws Exception {
        List<RegisteredService> serviceList = ImmutableList.of(service());
        when(registry.findServices()).thenReturn(serviceList);

        EnvironmentsDocument expectedResult = new EnvironmentsDocument(
                ImmutableList.of("env1"),
                ImmutableList.of("group1", "group2"),
                ""
        );

        MvcResult mvcResult = mockMvc.perform(get("/environments"))
                .andReturn();


        EnvironmentsDocument environmentsDocument = new Gson().fromJson(mvcResult.getResponse().getContentAsString(), EnvironmentsDocument.class);
        assertThat(environmentsDocumentEquivalence.wrap(expectedResult), is(environmentsDocumentEquivalence.wrap(environmentsDocument)));
    }

    private Equivalence<EnvironmentsDocument> environmentsDocumentEquivalence = new Equivalence<EnvironmentsDocument>() {
        @Override
        protected boolean doEquivalent(EnvironmentsDocument a, EnvironmentsDocument b) {
            return Objects.equals(a.getGroups(), b.getGroups()) &&
                    Objects.equals(a.getLinks(), b.getLinks());

        }

        @Override
        protected int doHash(EnvironmentsDocument environmentsDocument) {
            return Objects.hash(environmentsDocument.getGroups(), environmentsDocument.getLinks());
        }
    };

    private RegisteredService service () {
        return new RegisteredService(
                "serviceName",
                "serviceURL",
                "serviceDescription",
                Duration.ZERO,
                "env1",
                ImmutableList.of("group1", "group2")
        );
    }
}