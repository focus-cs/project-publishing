package com.schneider.mstt.project.publishing;

import com.schneider.mstt.project.publishing.processor.ProjectPublishingProcessor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackages = "com.schneider.mstt")
@Configuration
public class ProjectPublishing {

    private static final Logger LOG = Logger.getLogger(ProjectPublishing.class);
    
    @Autowired
    private ProjectPublishingProcessor projectPublishingProcessor;
    
    public static void main(String[] args) {
                
        ApplicationContext context = new AnnotationConfigApplicationContext(ProjectPublishing.class);
        ProjectPublishing api = context.getBean(ProjectPublishing.class);
        
        api.start();
    }
    
    private void start() {
        projectPublishingProcessor.process();
    }   
    
}
