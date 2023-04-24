package com.tutorial.domain;

import com.broadleafcommerce.catalog.provider.jpa.domain.product.JpaProduct;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Simple column domain extension
 */
@Entity
@Table(name = "ELECTRIC_CAR")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode(callSuper = true) // The Data annotation includes @EqualsAndHashCode and
@ToString(callSuper = true) // @ToString, so we should override them here to make sure we're
                            // calling super for our extension
public class MyAutoCoProduct extends JpaProduct {

    @Column(name = "MODEL")
    private String model;

    @Column(name = "IS_ALL_WHEEL_DRIVE")
    private boolean allWheelDrive;

}
