package com.dc.server.scheduler2.quartz.tasks;

import com.dc.server.scheduler2.services.HourlyMachineWiseSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HourlyMachineWiseSaleTask {

    private final HourlyMachineWiseSaleService service;

    @Autowired
    public HourlyMachineWiseSaleTask (HourlyMachineWiseSaleService service){
        this.service = service;
    }

    public void startJob(){
        service.processMachineWiseSaleHourly();
    }
}
