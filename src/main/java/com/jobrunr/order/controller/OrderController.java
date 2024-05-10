package com.jobrunr.order.controller;


import com.jobrunr.order.entity.Order;
import com.jobrunr.order.service.OrderCreationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderCreationService service;

    @PostMapping("/scheduleOrderJob")
    public ResponseEntity<Void> scheduleOrderJob(){
        service.scheduleOrderJob();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/scheduleOrderPurgingJob")
    public ResponseEntity<Void> scheduleOrderPurgingJob(){
        //service.purgeFailedOrders();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/saveOrder")
    public void saveOrder(@RequestBody Order order){
        service.saveOrder(order);
    }
}
