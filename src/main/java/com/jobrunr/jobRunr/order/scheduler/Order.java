package com.jobrunr.jobRunr.order.scheduler;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
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
}
