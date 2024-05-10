package com.jobrunr.jobRunr.order.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.scheduling.RecurringJobBuilder;
import org.jobrunr.scheduling.cron.Cron;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppStartupRunner implements ApplicationRunner {

    @Autowired
    JobScheduler jobScheduler;

    @Autowired
    OrderRepository orderRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        jobScheduler.createRecurrently(RecurringJobBuilder.aRecurringJob().withCron(Cron.every5minutes())
                .withDetails(this::processPendingOrder)
                .withName("Order Scheduler")
                .withId("order_scheduler_id"));
    }

    /*@PreDestroy
    public void destroy() {
        log.info("Shutting down jobScheduler");
        jobScheduler.deleteRecurringJob("order_scheduler_id");
        jobScheduler.shutdown();
    }*/

    public void processPendingOrder() {
        log.info("JobRunr Scheduler running");
        var orders = orderRepository.findAll();
        if(!orders.isEmpty()){
            orders.forEach(order -> {
                log.info("Calling CP endpoint to mark opp_id as successful");
                orderRepository.delete(order);
            });
        }
    }
}
