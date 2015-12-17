package de.otto.edison.jobtrigger.discovery;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import de.otto.edison.jobtrigger.definition.JobDefinition;
import de.otto.edison.registry.service.RegisteredService;
import de.otto.edison.registry.service.Registry;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static de.otto.edison.registry.api.Link.link;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DiscoveryServiceTest {

    public static final String ENV_NAME = "someEnv";

    @InjectMocks
    DiscoveryService discoveryService;

    @Mock
    private AsyncHttpClient httpClient;

    @Mock
    private Registry serviceRegistry;

    @Before
    public void setUp() throws Exception {
        reset(httpClient, serviceRegistry);
    }

    @Test
    public void shouldConvertToJobDefinitions() throws IOException {
        RegisteredService service = someService();
        JobDefinitionRepresentation jobDefinitionRepresentation = new JobDefinitionRepresentation();
        jobDefinitionRepresentation.setCron("* * * * * *");
        jobDefinitionRepresentation.setFixedDelay(12l);
        jobDefinitionRepresentation.setLinks(ImmutableList.of(link("http://github.com/otto-de/edison/link-relations/job/trigger", "href",  "title")));
        jobDefinitionRepresentation.setName("MyJob");
        jobDefinitionRepresentation.setRetries(12);
        jobDefinitionRepresentation.setRetryDelay(12l);
        jobDefinitionRepresentation.setType("someType");


        Response response = mock(Response.class);

        when(response.getResponseBody()).thenReturn(
                new Gson().toJson(jobDefinitionRepresentation)
        );
        JobDefinition jd = discoveryService.jobDefinitionFrom("someDefinitionUrl", service, response);

        assertThat(discoveryService, is(not(nullValue())));
        assertThat(jd.getCron(), is(Optional.of("* * * * * *")));
        assertThat(jd.getDefinitionUrl(), is("someDefinitionUrl"));
        assertThat(jd.getDescription(), is("MyJob"));
        assertThat(jd.getEnv(), is(ENV_NAME));
        assertThat(jd.getFixedDelay(), is(Optional.of(Duration.ofSeconds(12l))));
        assertThat(jd.getJobType(), is("someType"));
        assertThat(jd.getRetries(), is(12));
        assertThat(jd.getRetryDelay(), is(Optional.of(Duration.ofSeconds(12l))));
        assertThat(jd.getService(), is("myService"));
        assertThat(jd.getTriggerUrl(), is("href"));
    }

    private RegisteredService someService() {
        return new RegisteredService(
                "myService",
                "someHref",
                "someDescription",
                Duration.ZERO,
                "someEnv",
                ImmutableList.of());
    }

    @Ignore("Ignored until Validation is implemented")
    @Test
    public void shouldValidateResponseBody() throws IOException, ExecutionException, InterruptedException {
        RegisteredService service = someService();

        Response response = mock(Response.class);
        when(response.getResponseBody()).thenReturn("");
        stubHttpResponse(response);

        JobDefinition jobDefintion = discoveryService.jobDefinitionFrom("someDefinitionUrl", service, response);

        assertThat(jobDefintion, is(not(nullValue())));
    }

    @Test
    public void shouldFetchJobDefinitionsURLsForEveryService() throws Exception {
        when(serviceRegistry.findServices())
                .thenReturn(ImmutableList.of(someService(), someService()));
        stubHttpResponse(mock(Response.class));

        discoveryService.rediscover();

        verify(httpClient, times(2)).prepareGet("someHref/internal/jobdefinitions");
    }

    @Test
    public void shouldFetchJobDefinitionsForAllJobs() throws Exception {
        LinksRepresentation linksRepresentation = new LinksRepresentation();
        linksRepresentation.setLinks(ImmutableList.of(
                link(DiscoveryService.JOB_DEFINITION_LINK_RELATION_TYPE, "someHref/internal/jobdefinitions" ,"someTitle"),
                link(DiscoveryService.JOB_DEFINITION_LINK_RELATION_TYPE, "someOtherHref/internal/jobdefinitions" ,"someOtherTitle")));

        Response response = mock(Response.class);
        when(response.getResponseBody()).thenReturn(
                new Gson().toJson(linksRepresentation)
        );
        when(response.getStatusCode()).thenReturn(200);
        stubHttpResponse(response);

        List<JobDefinition> jobDefinitions = discoveryService.jobDefinitionsFrom(someService(), response);

        verify(httpClient).prepareGet("someHref/internal/jobdefinitions");
        verify(httpClient).prepareGet("someOtherHref/internal/jobdefinitions");
        assertThat(jobDefinitions, hasSize(2));
    }

    private void stubHttpResponse(Response response) throws IOException, InterruptedException, java.util.concurrent.ExecutionException {
        AsyncHttpClient.BoundRequestBuilder requestBuilderStub = mock(AsyncHttpClient.BoundRequestBuilder.class);
        ListenableFuture listenableFutureStub = mock(ListenableFuture.class);
        when(httpClient.prepareGet(any())).thenReturn(requestBuilderStub);
        when(requestBuilderStub.setHeader(anyString(), anyString())).thenReturn(requestBuilderStub);
        when(requestBuilderStub.execute()).thenReturn(listenableFutureStub);
        when(listenableFutureStub.get()).thenReturn(response);
    }
}