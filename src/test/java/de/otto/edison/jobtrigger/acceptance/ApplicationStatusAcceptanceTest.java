package de.otto.edison.jobtrigger.acceptance;

import org.testng.annotations.Test;

import java.io.IOException;

import static de.otto.edison.jobtrigger.testsupport.applicationdriver.HealthApi.*;
import static de.otto.edison.jobtrigger.testsupport.dsl.Given.given;
import static de.otto.edison.jobtrigger.testsupport.dsl.Then.and;
import static de.otto.edison.jobtrigger.testsupport.dsl.Then.assertThat;
import static de.otto.edison.jobtrigger.testsupport.dsl.Then.then;
import static de.otto.edison.jobtrigger.testsupport.dsl.When.when;
import static de.otto.edison.jobtrigger.testsupport.applicationdriver.HealthApi.*;
import static de.otto.edison.jobtrigger.testsupport.dsl.Given.given;
import static de.otto.edison.jobtrigger.testsupport.dsl.Then.then;
import static de.otto.edison.jobtrigger.testsupport.dsl.When.when;
import static org.hamcrest.Matchers.*;

@Test
public class ApplicationStatusAcceptanceTest {

    @Test
    public void shouldGetApplicationStatus() throws IOException {
        given(
                application_status_aggregator_is_up_to_date()
        );

        when(
                the_internal_status_is_retrieved_as("application/json")
        );

        then(
                assertThat(the_status_code().value(), is(200)),
                and(
                        assertThat(the_returned_json().at("/application/status").asText(), is(notNullValue()))),
                and(
                        assertThat(the_returned_json().at("/application/name").asText(), is("jobtrigger"))
                )
        );
    }

    @Test
    public void shouldGetApplicationStatusDetailsAsJson() throws IOException {
        when(
                the_internal_status_is_retrieved_as("application/json")
        );

        then(
                assertThat(the_status_code().value(), is(200)),
                and(
                        assertThat(the_returned_json().at("/application/statusDetails"), is(notNullValue()))
                )
        );
    }

    @Test
    public void shouldGetApplicationStatusAsHtml() throws IOException {
        when(
                the_internal_status_is_retrieved_as("text/html")
        );

        then(
                assertThat(the_status_code().value(), is(200)),
                and(
                        assertThat(the_returned_content(), startsWith("<html"))
                )
        );
    }
}
