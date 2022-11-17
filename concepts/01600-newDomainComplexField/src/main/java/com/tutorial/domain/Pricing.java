package com.tutorial.domain;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Pricing {

    private Integer maxPower;
    private BigDecimal pricePerMinute;

}
