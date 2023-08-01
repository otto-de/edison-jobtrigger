package de.otto.edison.jobtrigger.discovery;

import de.otto.edison.jobtrigger.definition.JobDefinition;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
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
 * @author Guido Steinacker
 * @since 05.09.15
 */
@Controller
public class DiscoveryController {

    private final DiscoveryService discoveryService;

    @Autowired
    public DiscoveryController(final DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    @RequestMapping(value = "/discover", method = RequestMethod.POST)
    public String startDiscovery() {
        discoveryService.rediscover();
        return "redirect:discover";
    }

    @RequestMapping(value = "/discover", method = RequestMethod.GET)
    public ModelAndView discover(final HttpServletRequest request) {
        final List<JobDefinition> jobDefinitions = discoveryService.allJobDefinitions();
        final List<String> environments = jobDefinitions.stream().map(JobDefinition::getEnv).distinct().sorted().collect(toList());

        if (!environments.isEmpty()) {
            return new ModelAndView("discover") {{
                addObject("environments", environmentsOf(jobDefinitions, environments));
            }};
        } else {
            return new ModelAndView("discover");
        }
    }

    private List<Map<String, Object>> environmentsOf(final List<JobDefinition> jobDefinitions,
                                                     final List<String> environments) {
        final List<Map<String, Object>> result = new ArrayList<>();
        for (final String environment : environments) {
            result.add(environmentOf(environment, jobDefinitions
                    .stream()
                    .filter(def -> def.getEnv().equals(environment))
                    .collect(toList())));
        }
        return result;
    }

    private Map<String, Object> environmentOf(final String environment, final List<JobDefinition> environmentJobs) {
        return new LinkedHashMap<String, Object>() {{
            put("name", environment);
            put("services", servicesOf(environmentJobs));
        }};
    }

    private List<Map<String, Object>> servicesOf(final List<JobDefinition> environmentJobs) {
        List<String> services = environmentJobs.stream().map(JobDefinition::getService).distinct().collect(toList());
        return services
                .stream()
                .sorted()
                .map(s -> new LinkedHashMap<String, Object>() {{
                    put("name", s);
                    put("definitions", getDefinitions(environmentJobs.stream().filter(j -> j.getService().equals(s)).collect(toList())));
                }})
                .collect(toList());
    }

    private List<Map<String, String>> getDefinitions(final List<JobDefinition> definitions) {
        return definitions
                .stream()
                .map(def-> new LinkedHashMap<String, String>() {{
                    put("jobType", def.getJobType());
                    put("frequency", def.getFixedDelay().isPresent() ? "Every " + def.getFixedDelay().get().toMinutes() + " minutes" : def.getCron().isPresent() ? def.getCron().get() : "n/a");
                    put("description", def.getDescription());}})
                .collect(toList());
    }

}
