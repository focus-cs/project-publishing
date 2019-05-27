package com.schneider.mstt.cost.center.mirror.service;

import com.sciforma.psnext.api.DataViewRow;
import com.sciforma.psnext.api.Global;
import com.sciforma.psnext.api.LockException;
import com.sciforma.psnext.api.PSException;
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
    private static final String DATAVIEW_NAME = "Cost center mirror";

    @Value("${mstt.psnext.url}")
    private String url;
    @Value("${mstt.psnext.login}")
    private String username;
    @Value("${mstt.psnext.password}")
    private String password;

    private Session session;
    private Global global;

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

    public List<DataViewRow> getDataView() {

        if (session.isLoggedIn()) {

            if (global == null) {
                global = new Global();
            }

            try {

                LOG.info("Retrieving data view : " + DATAVIEW_NAME);
                return session.getDataViewRowList(DATAVIEW_NAME, global);

            } catch (PSException e) {
                LOG.error("Failed to retrieve data view : " + DATAVIEW_NAME, e);
            }

        }

        return new ArrayList<>();

    }

    public DataViewRow createDataViewRow() throws PSException {
        return new DataViewRow(DATAVIEW_NAME, global);
    }

    public boolean openGlobal() {

        if (global == null) {
            global = new Global();
        }

        try {
            LOG.info("Locking Global");
            global.lock();
            LOG.info("Global locked");
            return true;

        } catch (LockException e) {
            LOG.error("Global already locked by : " + e.getLockingUser());
        } catch (PSException e) {
            LOG.error("Failed to lock global", e);
        }

        return false;

    }

    public boolean saveAndCloseGlobal() {

        boolean result = false;

        if (global != null) {

            try {
                global.save(false);
                LOG.info("Global saved");
                result = true;

            } catch (PSException e) {
                LOG.error("Failed to save global", e);
            } finally {

                try {
                    LOG.info("Unlocking Global");
                    global.unlock();
                    LOG.info("Global unlocked");
                    result = false;
                } catch (PSException e) {
                    LOG.error("Failed to unlock global", e);
                }

            }
        }

        return result;

    }

}
