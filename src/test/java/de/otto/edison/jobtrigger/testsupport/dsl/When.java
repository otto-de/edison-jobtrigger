package de.otto.edison.jobtrigger.testsupport.dsl;

public class When {
    public static final When WHEN = new When();

    public static void when(When... actions) {}
    public static When and(When actions, When... more) { return WHEN; }

    public static void when() {
    }
}
