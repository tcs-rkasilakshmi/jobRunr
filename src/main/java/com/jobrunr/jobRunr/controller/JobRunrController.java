package com.jobrunr.jobRunr.controller;


import com.jobrunr.jobRunr.order.scheduler.Order;
import com.jobrunr.jobRunr.order.scheduler.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@Slf4j
public class JobRunrController {

    @Autowired
    OrderRepository orderRepository;

    @PostMapping("/addOrder")
    public void addOrder(@RequestBody Order order){
        log.info("Save order :: " + order);
        orderRepository.save(order);
    }
}
