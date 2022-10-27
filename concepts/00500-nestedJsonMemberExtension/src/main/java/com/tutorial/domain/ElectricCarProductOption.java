package com.tutorial.domain;

import com.broadleafcommerce.catalog.domain.product.option.ProductOption;
import com.broadleafcommerce.catalog.provider.jpa.domain.product.JpaProduct;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Extension to product option on the product. These exist in an embedded collection in
 * {@link JpaProduct#getOptions()}.
 */
@Data
@EqualsAndHashCode(callSuper = true) // The Data annotation includes @EqualsAndHashCode and
@ToString(callSuper = true) // @ToString, so we should override them here to make sure we're
                            // calling super for our extension
public class ElectricCarProductOption extends ProductOption {

    /**
     * Is this option fulfilled at the dealership
     */
    private Boolean isDealerOption = false;

}
