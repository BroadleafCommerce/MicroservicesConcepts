package com.tutorial.domain;

import javax.money.MonetaryAmount;

import lombok.Data;

/**
 * Simple POJO projection that explicitly declares custom JSON serialization for money
 */
@Data
public class PricingProjection {

    private Integer maxPower;

    private MonetaryAmount pricePerMinute;

}
