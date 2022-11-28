package com.tutorial.domain;

import com.broadleafcommerce.catalog.domain.product.option.ProductOption;
import com.broadleafcommerce.catalog.provider.jpa.domain.product.option.JpaProductOption;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Repository domain extension counterpart to {@link MyAutoCoProductOption}. This is necessary,
 * since {@link ProductOption} has the repository domain counterpart of {@link JpaProductOption}.
 */
@Data
@EqualsAndHashCode(callSuper = true) // The Data annotation includes @EqualsAndHashCode and
@ToString(callSuper = true) // @ToString, so we should override them here to make sure we're
                            // calling super for our extension
public class JpaMyAutoCoProductOption extends JpaProductOption {

    /**
     * Is this option fulfilled at the dealership
     */
    private Boolean isDealerOption = false;

}
