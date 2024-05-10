package com.jobrunr.jobRunr.order.scheduler;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
	Optional<Order> findOrderByOpportunityId(String opportunityId);

}
