package com.tutorial.domain;

import com.broadleafcommerce.money.jackson.CurrencyAwareBigDecimalSerializer;
import com.broadleafcommerce.money.jackson.OptionalMonetaryAmountDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;

import lombok.Data;

/**
 * Simple POJO projection that explicitly declares custom JSON serialization for money
 */
@Data
public class PricingProjection {

    private Integer maxPower;

    @JsonDeserialize(using = OptionalMonetaryAmountDeserializer.class)
    @JsonSerialize(using = CurrencyAwareBigDecimalSerializer.class)
    private BigDecimal pricePerMinute;

}
