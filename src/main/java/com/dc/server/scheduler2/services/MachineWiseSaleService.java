package com.dc.server.scheduler2.services;

import com.dc.server.common.utility.DateTimeHelper;
import com.dc.server.dao.entities.MachineWiseSale;
import com.dc.server.dao.entities.OrderLineItem;
import com.dc.server.dao.repository.MachineWiseSaleRepository;
import com.dc.server.dao.repository.jpa.OrderLineItemRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.dc.server.dao.repository.specification.OrderLineItemSpecification.updatedInLast24Hours;
import static org.springframework.data.jpa.domain.Specification.where;

@Service
@Log4j2
public class MachineWiseSaleService {

    private final MachineWiseSaleRepository machineWiseSaleRepository;
    private final OrderLineItemRepository orderLineItemRepository;

    public MachineWiseSaleService(MachineWiseSaleRepository machineWiseSaleRepository,
                                  OrderLineItemRepository orderLineItemRepository) {
        this.machineWiseSaleRepository = machineWiseSaleRepository;
        this.orderLineItemRepository = orderLineItemRepository;
    }

    public void processMachineWiseSale() {

        var orderLineItems = orderLineItemRepository.findAll(where(updatedInLast24Hours()));
        log.debug("Processing MachineWiseSales : {}", orderLineItems);
        Map<Long, Double> saleMap = orderLineItems.stream().collect(Collectors.groupingBy(OrderLineItem::getVendingMachineId,
                Collectors.summingDouble(item -> item.getSuccessQuantity() * item.getOfferPrice())));

        orderLineItems
                .stream()
                .collect(Collectors.groupingBy(OrderLineItem::getVendingMachineId,
                        Collectors.summingInt(OrderLineItem::getSuccessQuantity)))
                .forEach((machineId, success) -> machineWiseSaleRepository.save(MachineWiseSale
                        .builder()
                        .machineId(machineId)
                        .successQuantity(success)
                        .sale(saleMap.get(machineId))
                        .date(DateTimeHelper.now().toLocalDate())
                        .build()));
    }
}
