package de.otto.edison.jobtrigger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;

import static java.time.Duration.ofDays;
import static java.util.Arrays.asList;

/**
 * @author Guido Steinacker
 * @since 05.09.15
 */
@Controller
public class DiscoveryController {

    @RequestMapping(value = "/discover", method = RequestMethod.POST)
    public String startDiscovery(@RequestParam String discoveryUrl, HttpServletResponse response) {
        if (discoveryUrl.isEmpty()) {
            Cookie cookie = new Cookie("jobtrigger-discovery-url", "");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        } else {
            Cookie cookie = new Cookie("jobtrigger-discovery-url", discoveryUrl);
            cookie.setMaxAge(-1 * (int) ofDays(365).getSeconds());
            response.addCookie(cookie);
        }
        return "redirect:discover";
    }

    @RequestMapping(value = "/discover", method = RequestMethod.GET)
    public ModelAndView discover(HttpServletRequest request) {
        final String discoveryUrl = getDiscoveryUrlFrom(request);
        if (!discoveryUrl.isEmpty()) {
            return new ModelAndView("discover") {{
                addObject("discoveryUrl", discoveryUrl);
                addObject("hasDefinitions", true);
                addObject("definitions", asList(
                        new LinkedHashMap<String,String>() {{
                            put("server", "http://example.com/internal/jobs/FullImport");
                            put("jobType", "Full Import");
                            put("definition", "Once every hour");
                        }},
                        new LinkedHashMap<String,String>() {{
                            put("server", "http://example.com/internal/jobs/DeltaImport");
                            put("jobType", "Delta Import");
                            put("definition", "Once every minute");
                }}));
            }};
        } else {
            return new ModelAndView("discover");
        }
    }

    private String getDiscoveryUrlFrom(HttpServletRequest request) {
        String discoverFrom = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jobtrigger-discovery-url")) {
                    discoverFrom = cookie.getValue();
                }
            }
        }
        return discoverFrom;
    }
}
