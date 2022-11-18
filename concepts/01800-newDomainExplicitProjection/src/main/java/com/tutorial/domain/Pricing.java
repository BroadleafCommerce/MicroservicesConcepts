package com.tutorial.domain;

import java.math.BigDecimal;

import lombok.Data;

/**
 * New domain (not extending from Broadleaf domain). Represents pricing for using a charger. This
 * entity is embedded in JSON as part of a converted collection on {@link ChargingStation}. As such,
 * it may be represented as a simple POJO.
 */
@Data
public class Pricing {

    private Integer maxPower;
    private BigDecimal pricePerMinute;

}
