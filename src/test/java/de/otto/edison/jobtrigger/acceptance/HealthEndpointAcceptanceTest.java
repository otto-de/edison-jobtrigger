package de.otto.edison.jobtrigger.acceptance;

import org.testng.annotations.Test;

import java.io.IOException;

import static de.otto.edison.jobtrigger.testsupport.applicationdriver.HealthApi.the_internal_health_is_retrieved;
import static de.otto.edison.jobtrigger.testsupport.applicationdriver.HealthApi.the_status_code;
import static de.otto.edison.jobtrigger.testsupport.dsl.Then.assertThat;
import static de.otto.edison.jobtrigger.testsupport.dsl.Then.then;
import static de.otto.edison.jobtrigger.testsupport.dsl.When.when;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.OK;

@Test
public class HealthEndpointAcceptanceTest {

    @Test
    public void shouldGetApplicationHealth() throws IOException {
        when(
                the_internal_health_is_retrieved()
        );

        then(
                assertThat(
                        the_status_code(), is(OK)
                )
        );
    }
}
