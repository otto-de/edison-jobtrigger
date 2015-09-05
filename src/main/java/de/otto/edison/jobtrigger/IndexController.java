package de.otto.edison.jobtrigger;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Guido Steinacker
 * @since 05.09.15
 */
@RestController
public class IndexController {

    @RequestMapping("/hello")
    public String sayHello() {
        return "Hello World";
    }
}
