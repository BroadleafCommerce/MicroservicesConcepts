package com.tutorial.domain;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.modelmapper.ModelMapper;

import com.broadleafcommerce.catalog.domain.product.Product;
import com.broadleafcommerce.catalog.provider.jpa.domain.product.JpaProduct;
import com.broadleafcommerce.data.tracking.core.mapping.MappingUtils;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Domain extension with complex fields using a table based OneToMany association. This is modeled
 * using an explicit projection, but could easily be modeled with auto projection instead.
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

    /**
     * Upgrades available for this car
     */
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER,
            mappedBy = "car")
    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    private List<Upgrade> upgrades = new ArrayList<>();

    @Override
    public ModelMapper fromMe() {
        ModelMapper mapper = super.fromMe();
        // Handle type map setup for the extended types
        MappingUtils.setupExtensions(mapper, MyAutoCoProduct.class, MyAutoCoProductProjection.class,
                JpaProduct.class, Product.class);
        return mapper;
    }

    @Override
    public ModelMapper toMe() {
        ModelMapper mapper = super.toMe();
        // Handle type map setup for the extended types
        MappingUtils.setupExtensions(mapper, MyAutoCoProductProjection.class, MyAutoCoProduct.class,
                Product.class, JpaProduct.class);
        return mapper;
    }

    @Override
    public Class<?> getBusinessDomainType() {
        return MyAutoCoProductProjection.class;
    }

}
