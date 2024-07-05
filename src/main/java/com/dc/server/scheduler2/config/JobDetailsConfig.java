package com.dc.server.scheduler2.config;

import com.dc.server.scheduler2.quartz.jobs.*;
import lombok.extern.log4j.Log4j2;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import static com.dc.server.scheduler2.api.constants.SchedulerConstants.*;
import static org.quartz.CronScheduleBuilder.cronSchedule;

@Log4j2
@Configuration
public class JobDetailsConfig {


    @Bean
    public Scheduler refillSuggestion(SchedulerFactoryBean springBeanJobFactory)
            throws SchedulerException {
        final Scheduler scheduler = springBeanJobFactory.getScheduler();
        final JobDetail job =
                JobBuilder.newJob()
                        .ofType(RefillSuggestionJob.class)
                        .withIdentity("RefillSuggestionJob")
                        .storeDurably()
                        .build();
        final CronTrigger trigger =
                TriggerBuilder.newTrigger()
                        .forJob(job)
                        .withIdentity("RefillSuggestionJobTrigger")
                        .withSchedule(cronSchedule(CRON_REFILL_SUGGESTION))
                        .build();
        scheduler.scheduleJob(job, trigger);

        return scheduler;
    }

    @Bean
    public Scheduler machineWiseSaleJobScheduler(SchedulerFactoryBean springBeanJobFactory)
            throws SchedulerException {
        final Scheduler scheduler = springBeanJobFactory.getScheduler();
        final JobDetail job =
                JobBuilder.newJob()
                        .ofType(MachineWiseSaleJob.class)
                        .withIdentity("MachineWiseSaleJob")
                        .storeDurably()
                        .build();
        final CronTrigger trigger =
                TriggerBuilder.newTrigger()
                        .forJob(job)
                        .withIdentity("MachineWiseSaleJobTrigger")
                        .withSchedule(cronSchedule(
                                CRON_MACHINE_WISE_SALE))
                        .build();
        scheduler.scheduleJob(job, trigger);

        return scheduler;
    }


    @Bean
    public Scheduler HourlyMachineWiseSaleJobScheduler(SchedulerFactoryBean springBeanJobFactory)
            throws SchedulerException {
        final Scheduler scheduler = springBeanJobFactory.getScheduler();
        final JobDetail job =
                JobBuilder.newJob()
                        .ofType(HourlyMachineWiseSaleJob.class)
                        .withIdentity("HourlyMachineWiseSaleJob")
                        .storeDurably()
                        .build();
        final CronTrigger trigger =
                TriggerBuilder.newTrigger()
                        .forJob(job)
                        .withIdentity("HourlyMachineWiseSaleJobTrigger")
                        .withSchedule(cronSchedule(
                                CRON_HOURLY_MACHINE_WISE_SALE))
                        .build();
        scheduler.scheduleJob(job, trigger);

        return scheduler;
    }
}
