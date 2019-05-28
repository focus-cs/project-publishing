package com.schneider.mstt.project.publishing.service;

import com.sciforma.psnext.api.PSException;
import com.sciforma.psnext.api.Project;
import com.sciforma.psnext.api.Session;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("${propertySource}")
public class SciformaService {

    private static final Logger LOG = Logger.getLogger(SciformaService.class);

    @Value("${mstt.psnext.url}")
    private String url;
    @Value("${mstt.psnext.login}")
    private String username;
    @Value("${mstt.psnext.password}")
    private String password;

    private Session session;

    public boolean createConnection() {

        try {

            LOG.info("Connection to " + url + " with username " + username);
            session = new Session(url);
            session.login(username, password.toCharArray());
            LOG.info("Connection successful");

            return true;

        } catch (PSException e) {
            LOG.error("Failed to connect to Sciforma : " + e.getMessage(), e);
        }

        return false;

    }

    public void closeConnection() {

        if (session != null) {
            try {

                if (session.isLoggedIn()) {
                    LOG.info("Logging out from Sciforma");
                    session.logout();
                    LOG.info("Logout successful");
                }

            } catch (PSException e) {
                LOG.error("Failed to logout", e);
            }
        }

    }

    public List<Project> getProjects() {

        if (session.isLoggedIn()) {

            try {

                LOG.info("Retrieving project list");
                return session.getProjectList(Project.VERSION_WORKING, Project.READWRITE_ACCESS);

            } catch (PSException e) {
                LOG.error("Failed to retrieve project list", e);
            }

        }

        return new ArrayList<>();

    }

}
