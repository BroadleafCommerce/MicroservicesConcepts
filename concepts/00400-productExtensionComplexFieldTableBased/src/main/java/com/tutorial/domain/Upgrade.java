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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * An upgrade specific to this car. Exposing as first-class domain (instead of embedded json) can
 * exposes additional query options that would be more difficult if all the collection information
 * is buried inside json. However, there is a performance cost to the additional join.
 * <p>
 * </p>
 * The {@link ProjectionPostConvert} annotation is used to denote a method to establish the
 * bi-directional reference back to the parent for the {@code ManyToOne} association. This is a
 * measure to support ORM requirements for the parent entity {@code OneToMany} collection.
 */
@Entity
@Table(name = "ELECTRIC_CAR_UPGRADES")
@Inheritance(strategy = InheritanceType.JOINED)
@ToString
@EqualsAndHashCode(exclude = {"id", "car"})
public class Upgrade implements Serializable {

    @Id
    @GeneratedValue(generator = "blcid")
    @GenericGenerator(name = "blcid", strategy = "blcid")
    @Type(type = "com.broadleafcommerce.data.tracking.jpa.hibernate.ULidType")
    @Column(name = "ID", nullable = false, length = CONTEXT_ID_LENGTH)
    private String id;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "ELECTRIC_CAR_ID")
    @ToString.Exclude // Avoid infinite recursion in toString()
    @JsonIgnore // Avoid infinite recursion in data tracking
    private ElectricCar car;

    @Column(name = "NAME")
    @Getter
    @Setter
    private String name;

    @Column(name = "DESCRIPTION")
    @Getter
    @Setter
    private String description;

    @Column(name = "MANUFACTURER_ID")
    @Getter
    @Setter
    private String manufacturerId;

    @ProjectionPostConvert // called after mapping of Projection<ElectricCar> to ElectricCar is
                           // complete
    public void postConvert(Object source, Object parent) {
        Assert.isTrue(parent instanceof ElectricCar, "Expected an instance of ElectricCar");
        this.car = (ElectricCar) parent;
    }

}
