package de.otto.edison.jobtrigger.trigger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * The DiscoveryController is responsible to handle the discovery page of the JobTrigger.
 *
 * Discovery is used to lookup job definitions from a server. These job definitions contain
 * information about the jobs of the server. JobTrigger is using the definition to find out,
 * how often jobs should be triggered.
 *
 * The URL of the server used for discovery is either the URL of a edison-microservice (or
 * some other implementation that is providing job definitions in the same way as edison microservices do),
 * or the URL of a supported discovery service like Marathon. Discovery services provide information (especially URLs)
 * about other services. These information is then used to get the list of possible microservices, and then to
 * ask all of these services for job definitions.
 *
 * @author Guido Steinacker
 * @since 05.09.15
 */
@Controller
public class TriggerController {

    @Autowired
    private TriggerService triggerService;

    @RequestMapping(value = "/triggers/start", method = RequestMethod.POST)
    public String startTriggering() {
        triggerService.startTriggering();
        return "redirect:/triggers";
    }

    @RequestMapping(value = "/triggers/stop", method = RequestMethod.POST)
    public String stopTriggering() {
        triggerService.stopTriggering();
        return "redirect:/triggers";
    }

    @RequestMapping(value = "/triggers", method = RequestMethod.GET)
    public ModelAndView getTriggers() {
        return new ModelAndView("triggers") {{
            addObject("isStarted", triggerService.isStarted());
            addObject("results", getResults());
        }};
    }

    private List<Map<String, String>> getResults() {
        return triggerService.getLastResults()
                .stream()
                .map(def-> new LinkedHashMap<String, String>() {{
                    put("jobType", def.getJobDefinition().getJobType());
                    put("status", String.valueOf(def.getStatusCode()));
                    put("location", def.getLocation());}})
                .collect(toList());
    }

}
