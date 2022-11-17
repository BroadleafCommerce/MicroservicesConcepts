package com.tutorial.domain;

import com.broadleafcommerce.data.tracking.core.mapping.ExplicitProjectionFieldConfiguration;
import com.broadleafcommerce.money.jackson.CurrencyAwareBigDecimalSerializer;
import com.broadleafcommerce.money.jackson.OptionalMonetaryAmountDeserializer;

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

    @ExplicitProjectionFieldConfiguration(
            usingDeserializer = OptionalMonetaryAmountDeserializer.class,
            usingSerializer = CurrencyAwareBigDecimalSerializer.class) // Exposes MonetaryAmount on
                                                                       // the API
    private BigDecimal pricePerMinute;

}
