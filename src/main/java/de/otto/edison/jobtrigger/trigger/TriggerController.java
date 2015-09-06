package de.otto.edison.jobtrigger.trigger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.max;
import static java.lang.Math.min;
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

    private static final int PAGE_SIZE = 25;

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
    public ModelAndView getTriggers(final @RequestParam(required = false) String startFrom) {
        final List<TriggerResult> triggerResults = triggerService.getLastResults();
        final int startIndex = indexOf(startFrom, triggerResults);
        final int endIndex = min(triggerResults.size(), startIndex + PAGE_SIZE);
        final List<TriggerResult> page = triggerResults.subList(startIndex, endIndex);
        final List<TriggerResult> failedTriggers = triggerResults.stream().filter(r->r.getStatusCode() > 299).limit(5).collect(toList());
        final List<Map<String, ?>> results = toView(page);
        return new ModelAndView("triggers") {{
            addObject("isStarted", triggerService.isStarted());
            if (startIndex > 0) {
                addObject("prev", "triggers?startFrom=" + triggerResults.get(max(0, startIndex - PAGE_SIZE)).getId());
            }
            if (endIndex < triggerResults.size()) {
                addObject("next", "triggers?startFrom=" + triggerResults.get(min(endIndex, triggerResults.size())).getId());
            }
            addObject("results", results);
            addObject("failed", toView(failedTriggers));
        }};
    }

    private int indexOf(final String startFrom, final List<TriggerResult> results) {
        if (startFrom != null) {
            for (int i = 0, n = results.size(); i < n; ++i) {
                if (results.get(i).getId().equals(startFrom)) {
                    return i;
                }
            }
        }
        return 0;
    }

    private List<Map<String, ?>> toView(final List<TriggerResult> results) {
        return results.stream()
                .map(triggerResult -> new LinkedHashMap<String, Object>() {{
                    put("id", triggerResult.getId());
                    put("time", triggerResult.getTime());
                    put("jobType", triggerResult.getJobDefinition().getJobType());
                    put("success", triggerResult.getStatusCode()<300);
                    put("status", String.valueOf(triggerResult.getStatusCode()));
                    put("location", triggerResult.getLocation());
                }})
                .collect(toList());
    }

}
