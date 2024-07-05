package com.dc.server.scheduler2.quartz.tasks;

import com.dc.server.scheduler2.services.MachineWiseSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MachineWiseSaleTask {

    private final MachineWiseSaleService service;

    @Autowired
    public MachineWiseSaleTask (MachineWiseSaleService service){
        this.service = service;
    }

    public void startJob(){
        service.processMachineWiseSale();
    }
}
