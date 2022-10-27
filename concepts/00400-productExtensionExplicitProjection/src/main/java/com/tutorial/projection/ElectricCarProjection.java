package com.tutorial.projection;

import com.broadleafcommerce.catalog.domain.product.Product;
import com.broadleafcommerce.common.extension.RequestView;
import com.broadleafcommerce.common.extension.ResponseView;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.tutorial.domain.ElectricCar;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom projections can provide alternate structure via custom mapping
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonView({RequestView.class, ResponseView.class})
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true) // The Data annotation includes @EqualsAndHashCode and
@ToString(callSuper = true) // @ToString, so we should override them here to make sure we're
                            // calling super for our extension
public class ElectricCarProjection extends Product {

    private String model;
    private Map<String, ElectricCar.HorsePower> horsePowerByMode;
    private Map<String, ElectricCar.Efficiency> efficiencyByTempFahrenheit;
    private List<ElectricCar.Feature> features;

    @JsonView(ResponseView.class) // Only included in the response
    private Set<ElectricCar.Material> allMaterials; // A new aggregation field achieved through
                                                    // custom mapping

}
