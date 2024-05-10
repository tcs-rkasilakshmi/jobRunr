package com.jobrunr.order.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

@Table(name = "orders")
@Entity
@Data
public class Order implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "opportunity_id")
    private String opportunityId;

    @Column(name = "proposal_id")
    private String proposalId;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "status")
    private String status;

    @Column(name = "created_time")
    private ZonedDateTime createdTime;
}
