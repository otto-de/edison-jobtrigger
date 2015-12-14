package de.otto.edison.jobtrigger.trigger;

import de.otto.edison.jobtrigger.definition.JobDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
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

    protected static final int PAGE_SIZE = 25;

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
    public ModelAndView getTriggers(final @RequestParam(required = false) String startFrom,
                                    final HttpServletResponse response) {
        final List<TriggerResult> triggerResults = triggerService.getLastResults();
        final int startIndex = startIndexOf(startFrom, triggerResults);
        final int endIndex = endIndexOf(triggerResults, startIndex);

        return new ModelAndView("triggers") {{
            addObject("isStarted", triggerService.isStarted());
            if (startIndex > 0) {
                addObject("prev", "triggers?startFrom=" + triggerResults.get(max(0, startIndex - PAGE_SIZE)).getId());
            }
            if (endIndex < triggerResults.size()) {
                addObject("next", "triggers?startFrom=" + triggerResults.get(min(endIndex, triggerResults.size())).getId());
            }
            addObject("results", toView(triggerResults.subList(startIndex, endIndex)));
            addObject("failed", toView(lastFiveFailedOfLastFifty(triggerResults)));
        }};
    }

    private int endIndexOf(List<TriggerResult> triggerResults, int startIndex) {
        return min(triggerResults.size(), startIndex + PAGE_SIZE);
    }

    private List<TriggerResult> lastFiveFailedOfLastFifty(final List<TriggerResult> triggerResults) {
        return triggerResults.stream().limit(50).filter(TriggerResult::failed).limit(5).collect(toList());
    }

    private int startIndexOf(final String startFrom, final List<TriggerResult> results) {
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
                    final JobDefinition jobDefinition = triggerResult.getJobDefinition();
                    put("id", triggerResult.getId());
                    put("time", triggerResult.getTime());
                    put("job", jobDefinition.getEnv() + "/" + jobDefinition.getService() + "/" + jobDefinition.getJobType());
                    put("state", triggerResult.getTriggerStatus().getState().name());
                    put("message", triggerResult.getTriggerStatus().getMessage());
                    put("location", triggerResult.getLocation().orElse("#"));
                }})
                .collect(toList());
    }

}
