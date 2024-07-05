package com.dc.server.scheduler2.quartz.tasks;

import com.dc.server.common.exceptions.DCBusinessException;
import com.dc.server.scheduler2.services.RefillSuggestionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class RefillSuggestionTask {
    private final RefillSuggestionService refillSuggestionService;

    @Autowired
    public RefillSuggestionTask(RefillSuggestionService refillSuggestionService) {
        this.refillSuggestionService = refillSuggestionService;

    }

    public void startJob() throws DCBusinessException {
        refillSuggestionService.process();
    }
}