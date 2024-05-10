package com.jobrunr.order.feign;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        value = "orderCreationService",
        url = "https://httpbin.org/")
public interface OrderClient {

    @GetMapping(value = "/status/{statusCode}",
            headers = {"Content-Type=application/json"})
    ResponseEntity<Void> getStatusCode(@PathVariable("statusCode") String statusCode) throws FeignException;
}
