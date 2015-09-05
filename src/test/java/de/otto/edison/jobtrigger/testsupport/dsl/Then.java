package de.otto.edison.jobtrigger.testsupport.dsl;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

public class Then {

    public static final Then THEN = new Then();

    public static void then(final Then... thens) {}

    public static Then and(final Then then, final Then... more) { return Then.THEN; }

    public static <T> Then assertThat(T actual, Matcher<? super T> matcher) {
        MatcherAssert.assertThat(actual, matcher);
        return THEN;
    }

    public static void then() {
    }
}
