package com.jobrunr.order.service;

import com.jobrunr.order.entity.Order;
import com.jobrunr.order.feign.OrderClient;
import com.jobrunr.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.RecurringJobBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.ZonedDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderCreationService {

    public final OrderRepository orderRepository;
    public final OrderClient orderClient;

    public void scheduleOrderJob(){
        BackgroundJob.createRecurrently(RecurringJobBuilder.aRecurringJob().withName("Order Scheduler")
                .withId("order_scheduler").withCron("*/50 * * * *").withDetails(this::processOrder));
    }

    public void processOrder() {
        var orders = orderRepository.findAll();
        if(!orders.isEmpty()){
            orders.forEach(order -> {
                log.info("Calling CP endpoint to mark opp_id as successful");
                ResponseEntity<Void> response =  orderClient.getStatusCode(order.getStatus());
                log.info("response is --> "+ response);
                //orderRepository.delete(order);

            });
        }
    }

   /* @Job(name = "Order Purging Scheduler")
    @Recurring(id = "order_purging_scheduler", cron = "0 0 * * *")
    public void purgeFailedOrders(){
        log.info("Starting Purging Scheduler");
        var orders = orderRepository.findByCreatedTimeGreaterThanEqual(ZonedDateTime.now().minusDays(10));
        if(!orders.isEmpty()){
            orders.forEach(order -> {
                log.info("Deleting order :: "+ order);
                orderRepository.delete(order);
            });
        }
    }*/

    public void saveOrder(Order order){
        order.setCreatedTime(ZonedDateTime.now());
        log.info("Save order :: " + order);
        orderRepository.save(order);
    }
}