package com.dc.server.scheduler2.services;

import com.dc.server.common.data.dto.BrandSalesResponse;
import com.dc.server.dao.entities.Brand;
import com.dc.server.dao.entities.Warehouse;
import com.dc.server.dao.mapper.BrandSalesMapperlmpl;
import com.dc.server.dao.repository.BrandMachineSaleRepository;
import com.dc.server.dao.repository.BrandRepository;
import com.dc.server.dao.repository.WarehouseRepository;
import com.dc.server.data.transportable.BrandSales;
import com.dc.server.data.transportable.BrandVmCount;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Log4j2
@Service
public class BrandCategoryService {

    private final BrandRepository brandRepository;
    private final BrandMachineSaleRepository brandMachineSaleRepository;
    private final WarehouseRepository warehouseRepository;
    private final BrandSalesMapperlmpl brandSalesMapper;

    @Autowired
    public BrandCategoryService(BrandRepository brandRepository, BrandMachineSaleRepository brandMachineSaleRepository, WarehouseRepository warehouseRepository, BrandSalesMapperlmpl brandSalesMapper) {
        this.brandRepository = brandRepository;
        this.brandMachineSaleRepository = brandMachineSaleRepository;
        this.warehouseRepository = warehouseRepository;
        this.brandSalesMapper = brandSalesMapper;
    }

    public void BrandMachineSaleCountProcess() {
        var barndsSale = brandRepository.findByBrandsSale();
        var brandVmCountList = brandRepository.findByVmCount();
        var brandList = brandRepository.findAll();
        var warehouseId = warehouseRepository.findAll().stream().map(Warehouse::getId).toList();


        log.info("barndsSale {},brandVmCountList {}", barndsSale, brandVmCountList);
        var responselist = new ArrayList<BrandSalesResponse>();
        for (Brand brand : brandList) {
            for (Long warehouseid : warehouseId) {
               var brandSales = barndsSale
                        .stream()
                        .filter(i -> i.getbrandId().equals(brand.getId()) && i.getwarehouseId() != null && i.getwarehouseId().equals(warehouseid) && i.getsales() != null)
                        .map(BrandSales::getsales).findFirst().orElse(0L);

               var  brandVmCount = brandVmCountList
                        .stream()
                        .filter(i -> i.getbrandId().equals(brand.getId()) && i.getwarehouseId() != null && i.getwarehouseId().equals(warehouseid) && i.getmachinecount() != null)
                        .map(BrandVmCount::getmachinecount).findFirst().orElse(0L);


              if(!brandSales.equals(0L) || !brandVmCount.equals(0L))
              {
                  log.debug("brandSales {},brandVmCountList {}", brandSales, brandVmCount);
                  var response = BrandSalesResponse.builder()
                          .brandId(brand.getId())
                          .eodSaleAmount(brandSales)
                          .eodVmCount(brandVmCount)
                          .warehouseId(warehouseid)
                          .build();
                  responselist.add(response);
              }
            }
        }
        var brandResponselist = brandSalesMapper.toDtolist(responselist);
        brandMachineSaleRepository.saveAll(brandResponselist);
    }
}
