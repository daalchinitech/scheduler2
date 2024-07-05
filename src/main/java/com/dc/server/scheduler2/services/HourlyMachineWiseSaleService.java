package com.dc.server.scheduler2.services;

import com.dc.server.common.utility.DateTimeHelper;
import com.dc.server.dao.entities.HourlyMachineWiseSale;
import com.dc.server.dao.entities.OrderLineItem;
import com.dc.server.dao.repository.HourlyMachineWiseSaleRepository;
import com.dc.server.dao.repository.jpa.OrderLineItemRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.dc.server.dao.repository.specification.OrderLineItemSpecification.statusIsCompletedOrPartiallyCompleted;
import static org.springframework.data.jpa.domain.Specification.where;

@Service
@Log4j2
public class HourlyMachineWiseSaleService {

    private final HourlyMachineWiseSaleRepository hourlyMachineWiseSaleRepository;
    private final OrderLineItemRepository orderLineItemRepository;

    public HourlyMachineWiseSaleService(HourlyMachineWiseSaleRepository hourlyMachineWiseSaleRepository,
                                        OrderLineItemRepository orderLineItemRepository) {
        this.hourlyMachineWiseSaleRepository = hourlyMachineWiseSaleRepository;
        this.orderLineItemRepository = orderLineItemRepository;
    }

    public void processMachineWiseSaleHourly() {
        var orderLineItems = orderLineItemRepository.findAll(where(statusIsCompletedOrPartiallyCompleted()));
         Map<Long, Double> saleMap = orderLineItems.stream().collect(Collectors.groupingBy(OrderLineItem::getVendingMachineId,
                Collectors.summingDouble(item -> item.getSuccessQuantity() * item.getOfferPrice())));
        var currentHour = DateTimeHelper.now().getHour();
        var currentHourField = "hour" + currentHour;
        var date = DateTimeHelper.today();

        log.debug(" currentHour : {},  date : {}, saleMap : {}", currentHour, date, saleMap);
        var hourlyMachineWiseSales = hourlyMachineWiseSaleRepository.findByCreatedAt(date);

        Map<Long, Integer> successQuantityByMachineId = orderLineItems.stream()
                .collect(Collectors.groupingBy(
                        OrderLineItem::getVendingMachineId,
                        Collectors.summingInt(OrderLineItem::getSuccessQuantity)
                ));
        log.debug("Processing successQuantityByMachineId : {}", successQuantityByMachineId);

        successQuantityByMachineId.forEach((machineId, success) -> {
            if (hourlyMachineWiseSales.stream().noneMatch(i -> i.getMachineId().equals(machineId))) {
                HourlyMachineWiseSale hourlySaleNew = HourlyMachineWiseSale.builder()
                        .machineId(machineId)
                        .build();
                var hourlyMachineWiseSalesNew = hourlyMachineWiseSaleRepository.save(hourlySaleNew);
                log.debug("Processing hourlyMachineWiseSales : {}", hourlyMachineWiseSalesNew);
                hourlyMachineWiseSales.add(hourlyMachineWiseSalesNew);
            }
            for (var hourlySale : hourlyMachineWiseSales) {
                AtomicReference<Double> count = new AtomicReference<>(0.0);
                if (hourlySale.getMachineId().equals(machineId)) {
                    for (int i = 0; i < 24; i++) {
                        var hourField = "hour" + i;
                        try {
                            Field field = hourlySale.getClass().getDeclaredField(hourField);
                            field.setAccessible(true);
                            if (hourField.equals(currentHourField)) {
                                field.set(hourlySale, saleMap.get(machineId));
                            }
                            var value = field.get(hourlySale);
                            if (value != null) {
                                log.debug("Processing hourlyMachineWiseSale {}: {}", hourField, value);
                                count.updateAndGet(currentCount -> currentCount + (Double) value);
                            }
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            log.error("Error in processing machine wise sale : {}", e.getMessage());
                        }
                    }
                    hourlySale.setTotalSale(count.get());
                }
            }
        });
        hourlyMachineWiseSaleRepository.saveAll(hourlyMachineWiseSales);

    }
}
