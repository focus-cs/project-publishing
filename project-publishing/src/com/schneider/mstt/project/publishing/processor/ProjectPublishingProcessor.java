package com.schneider.mstt.project.publishing.processor;

import com.schneider.mstt.project.publishing.service.SciformaService;
import com.sciforma.psnext.api.PSException;
import com.sciforma.psnext.api.Project;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StopWatch;

@Configuration
@PropertySource("${propertySource}")
public class ProjectPublishingProcessor {

    private static final Logger LOG = Logger.getLogger(ProjectPublishingProcessor.class);

    @Autowired
    private SciformaService sciformaService;

    public void process() {

        StopWatch stopWatch = new StopWatch("project-publishing");

        stopWatch.start("Logging to Sciforma");
        if (sciformaService.createConnection()) {

            stopWatch.stop();

            stopWatch.start("Retrieving project list");
            List<Project> projects = sciformaService.getProjects();
            stopWatch.stop();

            if (projects != null && !projects.isEmpty()) {

                stopWatch.start("Publishing " + projects.size() + " project(s)");
                LOG.info("Publishing " + projects.size() + " project(s)");
                try {
                    
                    Project.publish(projects);
                    
                } catch (PSException e) {
                    LOG.error("An error occured during publication of projects : " + e.getMessage(), e);
                }
                stopWatch.stop();

            } else {
                LOG.info("No project to publish");
            }

        }

        if (stopWatch.isRunning()) {
            stopWatch.stop();
        }

        stopWatch.start("Logout from Sciforma");
        sciformaService.closeConnection();
        stopWatch.stop();

        LOG.info(stopWatch.prettyPrint());

    }

}
