package com.tutorial.domain;

import static com.broadleafcommerce.common.jpa.JpaConstants.CONTEXT_ID_LENGTH;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.broadleafcommerce.data.tracking.core.SandboxTrackable;
import com.broadleafcommerce.data.tracking.jpa.filtering.TrackingListener;
import com.broadleafcommerce.data.tracking.jpa.filtering.domain.SandboxJpaTracking;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
        implements Serializable, SandboxTrackable<SandboxJpaTracking> { // Broadleaf DataTracking
                                                                        // requirement.
                                                                        // Multiple tracking types
                                                                        // available.

    @Id
    @GeneratedValue(generator = "blcid")
    @GenericGenerator(name = "blcid", strategy = "blcid")
    @Type(type = "com.broadleafcommerce.data.tracking.jpa.hibernate.ULidType")
    @Column(name = "ID", nullable = false, length = CONTEXT_ID_LENGTH)
    private String contextId;

    // Generic sample location information

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

    // Broadleaf DataTracking requirement - matches Trackable interface generics

    @Embedded
    private SandboxJpaTracking tracking;

}
