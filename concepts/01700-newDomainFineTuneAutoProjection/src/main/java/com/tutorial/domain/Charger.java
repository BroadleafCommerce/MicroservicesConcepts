package com.tutorial.domain;

import static com.broadleafcommerce.common.jpa.JpaConstants.CONTEXT_ID_LENGTH;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.util.Assert;

import com.broadleafcommerce.data.tracking.core.mapping.ProjectionPostConvert;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * New domain (not extending from Broadleaf domain). Represents a specific charger at a charging
 * station. This entity is wholly owned (i.e. OneToMany) by the {@link ChargingStation}, and may
 * therefore skip the tracking semantics, as it will benefit from the tracking enacted upon the
 * parent.
 */
@Entity
@Table(name = "CHARGER")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@ToString
@EqualsAndHashCode(exclude = {"id", "chargingStation"})
public class Charger implements Serializable {

    @Id
    @GeneratedValue(generator = "blcid")
    @GenericGenerator(name = "blcid", strategy = "blcid")
    @Type(type = "com.broadleafcommerce.data.tracking.jpa.hibernate.ULidType")
    @Column(name = "ID", nullable = false, length = CONTEXT_ID_LENGTH)
    private String id;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "CHARGING_STATION_ID")
    @ToString.Exclude // Avoid infinite recursion in toString()
    @JsonIgnore // Avoid infinite recursion in data tracking
    private ChargingStation chargingStation;

    @Column(name = "WATTS")
    private Integer watts;

    @Column(name = "CONNECTOR_TYPE")
    private String connectorType;

    @ProjectionPostConvert // called after mapping of Projection<ChargingStation> to ChargingStation
                           // is complete
    public void postConvert(Object source, Object parent) {
        Assert.isTrue(parent instanceof ChargingStation, "Expected an instance of ChargingStation");
        this.chargingStation = (ChargingStation) parent;
    }

}
