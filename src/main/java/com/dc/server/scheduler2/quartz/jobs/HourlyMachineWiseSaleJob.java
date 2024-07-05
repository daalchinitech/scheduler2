package com.dc.server.scheduler2.quartz.jobs;

import com.dc.server.scheduler2.quartz.tasks.HourlyMachineWiseSaleTask;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HourlyMachineWiseSaleJob implements Job {

    @Autowired
    HourlyMachineWiseSaleTask hourlyMachineWiseSaleTask;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        hourlyMachineWiseSaleTask.startJob();
    }
}
