package com.dc.server.scheduler2.quartz.jobs;

import com.dc.server.common.exceptions.DCBusinessException;
import com.dc.server.scheduler2.quartz.tasks.RefillSuggestionTask;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RefillSuggestionJob implements Job {

    @Autowired
    private RefillSuggestionTask task;

    @Override
    public void execute (JobExecutionContext context) throws JobExecutionException {
        try {
            task.startJob();
        } catch (DCBusinessException e) {
            throw new JobExecutionException(e);
        }
    }
}

