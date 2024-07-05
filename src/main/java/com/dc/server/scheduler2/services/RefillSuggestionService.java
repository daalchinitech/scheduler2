package com.dc.server.scheduler2.services;

import com.dc.server.common.exceptions.DCBusinessException;
import com.dc.server.dao.mapper.RefillSuggestionMapper;
import com.dc.server.dao.repository.RefillSuggestionUrgencyScoreRepository;
import com.dc.server.dao.repository.jpa.RefillSuggestionRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class RefillSuggestionService {

    private final RefillSuggestionRepository refillSuggestionRepository;
    private final RefillSuggestionUrgencyScoreRepository refillSuggestionUrgencyScoreRepository;
    private final RefillSuggestionMapper refillSuggestionMapper;

    @Autowired
    public RefillSuggestionService(RefillSuggestionRepository refillSuggestionRepository, RefillSuggestionUrgencyScoreRepository refillSuggestionUrgencyScoreRepository, RefillSuggestionMapper refillSuggestionMapper) {
        this.refillSuggestionRepository = refillSuggestionRepository;
        this.refillSuggestionUrgencyScoreRepository = refillSuggestionUrgencyScoreRepository;
        this.refillSuggestionMapper = refillSuggestionMapper;
    }

    public void process() throws DCBusinessException {
        log.info("Processing refill Suggestion");
        var refillSuggestions =
                refillSuggestionRepository.findAll();
        for (var refillSuggestion : refillSuggestions) {
            var machineId = refillSuggestion.getMachineId();
            var optionalRefillSuggestionUrgencyScore = refillSuggestionUrgencyScoreRepository.findByMachineId(machineId);
            if (optionalRefillSuggestionUrgencyScore.isPresent()) {
                var refillSuggestionUrgencyScore = optionalRefillSuggestionUrgencyScore.get();
                refillSuggestionUrgencyScoreRepository.save(refillSuggestionUrgencyScore.updateRefillSuggestion(refillSuggestion));
            }
            else {
                refillSuggestionUrgencyScoreRepository.save(refillSuggestionMapper.toDoc(refillSuggestion));
            }
        }
    }
}

