package com.dc.server.scheduler2.quartz.jobs;

import com.dc.server.scheduler2.quartz.tasks.MachineWiseSaleTask;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MachineWiseSaleJob implements Job {

    @Autowired
    MachineWiseSaleTask machineWiseSaleTask;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        machineWiseSaleTask.startJob();
    }
}
