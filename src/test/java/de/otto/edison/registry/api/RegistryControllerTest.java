package de.otto.edison.registry.api;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import de.otto.edison.jobtrigger.util.TestViewResolverBuilder;
import de.otto.edison.registry.service.RegisteredService;
import de.otto.edison.registry.service.Registry;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Duration;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RegistryControllerTest {

    MockMvc mockMvc;

    @Mock
    Registry registry;


    @InjectMocks
    RegistryController controller;

    @BeforeEach
    public void setUp() {
        reset(registry);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers(TestViewResolverBuilder.createViewResolver()).build();
    }

    @Test
    public void shouldReturnDistinctEnvironmentsDocument() throws Exception {
        List<RegisteredService> serviceList = ImmutableList.of(service().build(), service().build(), service().build());
        when(registry.findServices()).thenReturn(serviceList);

        EnvironmentsDocument expectedResult = new EnvironmentsDocument(
                ImmutableList.of("env1"),
                ImmutableList.of("group1", "group2"),
                ""
        );

        MvcResult mvcResult = mockMvc.perform(get("/environments"))
                .andReturn();


        EnvironmentsDocument environmentsDocument = new Gson().fromJson(mvcResult.getResponse().getContentAsString(), EnvironmentsDocument.class);
        assertThat(expectedResult, is(environmentsDocument));
    }

    @Test
    public void shouldReturnDistinctGroupsAndMultipleServiceLinks() throws Exception {
        List<RegisteredService> serviceList = ImmutableList.of(
                service()
                        .withService("serviceName1")
                        .withHref("service1")
                        .build()
                ,
                service()
                        .withService("serviceName2")
                        .withHref("service2")
                        .build());
        when(registry.findServices()).thenReturn(serviceList);

        EnvironmentDocument expectedResult = new EnvironmentDocument(
                ImmutableList.of(service()
                                .withService("serviceName1")
                                .withHref("service1")
                                .build(),

                        service()
                                .withService("serviceName2")
                                .withHref("service2")
                                .build())
                ,
                "env1",
                ""
        );

        MvcResult mvcResult = mockMvc.perform(get("/environments/env1"))
                .andReturn();


        EnvironmentDocument environmentsDocument = new Gson().fromJson(mvcResult.getResponse().getContentAsString(), EnvironmentDocument.class);
        assertThat(expectedResult, is(environmentsDocument));
    }


    @Test
    public void shouldReturnService() throws Exception {
        List<RegisteredService> serviceList = ImmutableList.of(
                service().withService("serviceName1").withHref("service1URL").build(),
                service().withService("serviceName2").withHref("service2URL").build());
        when(registry.findServices()).thenReturn(serviceList);

        MvcResult mvcResult = mockMvc.perform(get("/environments/env1/serviceName1"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        ServiceDocument serviceDocument = new Gson().fromJson(mvcResult.getResponse().getContentAsString(), ServiceDocument.class);

        assertThat(serviceDocument.getExpire(), is(0L));
        assertThat(serviceDocument.getGroups(), Matchers.contains("group1", "group2"));
        assertThat(serviceDocument.getGroups(), hasSize(2));
        assertThat(serviceDocument.getLinks(), hasSize(3));
        assertThat(serviceDocument.getLinks(), containsInAnyOrder(
                Link.link("self", "/environments/env1/serviceName1", "Self"),
                Link.link("collection", "/environments/env1", "All services in env1"),
                Link.link(ServiceDocument.MICROSERVICE_LINK_RELATION_TYPE, "service1URL", "serviceDescription")
        ));


    }

    private RegisteredService.Builder service() {
        return RegisteredService.newBuilder()
                .withDescription("serviceDescription")
                .withService("serviceName")
                .withEnvironment("env1")
                .withExpireAfter(Duration.ZERO)
                .withGroups(ImmutableList.of("group1", "group2"))
                .withHref("serviceURL");
    }

}