package com.tutorial.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Extension to feature on the electric car. These exist in an embedded collection in
 * {@link ElectricCar#getFeatures()}. There is no repository domain counterpart, as both
 * {@link ElectricCar} and its projection share this type in the embedded collection of
 * {@link ElectricCar#getFeatures()}.
 */
@Data
@EqualsAndHashCode(callSuper = true) // The Data annotation includes @EqualsAndHashCode and
@ToString(callSuper = true) // @ToString, so we should override them here to make sure we're
                            // calling super for our extension
public class ExtendedFeature extends ElectricCar.Feature {

    private String corporateId;

}
