package de.otto.edison.jobtrigger.testsupport.applicationdriver;

import de.otto.edison.jobtrigger.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class AbstractSpringTest {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSpringTest.class);

    static {
        Server.main(new String[0]);
    }

    protected static ApplicationContext applicationContext() {
        return Server.applicationContext();
    }
}
