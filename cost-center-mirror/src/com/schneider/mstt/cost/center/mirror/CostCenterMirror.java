package com.schneider.mstt.cost.center.mirror;

import com.schneider.mstt.cost.center.mirror.processor.CostCenterMirrorProcessor;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackages = "com.schneider.mstt")
@Configuration
public class CostCenterMirror {

    @Autowired
    private CostCenterMirrorProcessor costCenterMirrorProcessor;
    
    public static void main(String[] args) {
        
        PropertyConfigurator.configure("conf/log4j.properties");
        
        ApplicationContext context = new AnnotationConfigApplicationContext(CostCenterMirror.class);
        CostCenterMirror api = context.getBean(CostCenterMirror.class);
        
        api.start();
    }
    
    private void start() {
        costCenterMirrorProcessor.process();
    }   
    
}
