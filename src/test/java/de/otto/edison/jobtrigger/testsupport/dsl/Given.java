package de.otto.edison.jobtrigger.testsupport.dsl;

public class Given {
    public static final Given GIVEN = new Given();

    public static void given(final Given... givenStuff) {}

    public static Given and(final Given givenStuff, final Given... moreGivenStuff) { return GIVEN; }

    public static void given() {
    }
}
