package it.pagopa.selfcare.external_interceptor.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableScheduling
@PropertySource("classpath:config/core-config.properties")
public class ScheduledConfig implements SchedulingConfigurer {

    @Value("${scheduler.threads.max-number}")
    private int maxScheduleThreadNumber;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(maxScheduleThreadNumber);
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        taskScheduler.initialize();
        taskRegistrar.setTaskScheduler(taskScheduler);
    }
    private boolean sendOldEvent;
    public boolean getSendOldEvent() {
        return sendOldEvent;
    }

    public void setScheduler(boolean sendOldEvent) {
        this.sendOldEvent = sendOldEvent;
    }

}
