package com.dc.server.scheduler2.quartz.tasks;

import com.dc.server.common.exceptions.DCBusinessException;
import com.dc.server.scheduler2.services.BrandCategoryService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class BrandCategoryTask {
    private final BrandCategoryService brandCategoryService;

    @Autowired
    public BrandCategoryTask(BrandCategoryService brandCategoryService) {
        this.brandCategoryService = brandCategoryService;
    }

    public void startJob () throws DCBusinessException {
        brandCategoryService.BrandMachineSaleCountProcess();
    }
}
