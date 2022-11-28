package com.tutorial.domain;

import static com.broadleafcommerce.common.jpa.JpaConstants.CONTEXT_ID_LENGTH;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;

import com.broadleafcommerce.common.jpa.JpaConstants;
import com.broadleafcommerce.common.jpa.converter.AbstractListConverter;
import com.broadleafcommerce.data.tracking.core.TenantTrackable;
import com.broadleafcommerce.data.tracking.jpa.filtering.TrackingListener;
import com.broadleafcommerce.data.tracking.jpa.filtering.domain.TenantJpaTracking;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * New domain (not extending from Broadleaf domain). Represents a charging location for electric
 * cars (see {@code ElectricCar} domain in earlier tutorial).
 */
@Entity
@Table(name = "CHARGING_STATION")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode
@ToString
@EntityListeners(TrackingListener.class) // Broadleaf DataTracking requirement
public class ChargingStation
        implements Serializable, TenantTrackable<TenantJpaTracking> { // Broadleaf DataTracking
                                                                      // requirement.
                                                                      // Multiple tracking types
                                                                      // available.

    @Id
    @GeneratedValue(generator = "blcid")
    @GenericGenerator(name = "blcid", strategy = "blcid")
    @Type(type = "com.broadleafcommerce.data.tracking.jpa.hibernate.ULidType")
    @Column(name = "ID", nullable = false, length = CONTEXT_ID_LENGTH)
    private String contextId;

    @Column(name = "ADDRESS_1")
    private String address1;

    @Column(name = "ADDRESS_2")
    private String address2;

    @Column(name = "CITY")
    private String city;

    @Column(name = "STATE")
    private String state;

    @Column(name = "ZIPCODE")
    private String zipcode;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER,
            mappedBy = "chargingStation")
    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    private List<Charger> chargers = new ArrayList<>();

    @Column(name = "PRICING", length = JpaConstants.MEDIUM_TEXT_LENGTH)
    @Convert(converter = PricingConverter.class)
    private List<Pricing> pricing = new ArrayList<>();

    // Broadleaf DataTracking requirement - matches Trackable interface generics

    @Embedded
    private TenantJpaTracking tracking;

    public static class PricingConverter extends AbstractListConverter<Pricing> {
        public PricingConverter(
                @Nullable @Qualifier("converterObjectMapper") ObjectMapper objectMapper) {
            super(Pricing.class, objectMapper);
        }
    }

}
