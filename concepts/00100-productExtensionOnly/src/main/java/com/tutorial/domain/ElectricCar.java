package com.tutorial.domain;

import com.broadleafcommerce.catalog.provider.jpa.domain.product.JpaProduct;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import lombok.Data;

/**
 * Simple column domain extension
 */
@Entity
@Table(name = "ELECTRIC_CAR")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public class ElectricCar extends JpaProduct {

    @Column(name = "MODEL")
    private String model;

}
