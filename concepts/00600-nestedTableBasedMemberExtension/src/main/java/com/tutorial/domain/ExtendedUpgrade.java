package com.tutorial.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Extension to upgrades on the electric car. These exist in a table-based @OneToMany collection in
 * {@link MyAutoCoProduct#getUpgrades()}.
 */
@Entity
@Table(name = "EXT_ELECTRIC_CAR_UPGRADES")
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(callSuper = true) // The Data annotation includes @EqualsAndHashCode and
@ToString(callSuper = true) // @ToString, so we should override them here to make sure we're
                            // calling super for our extension
public class ExtendedUpgrade extends Upgrade {

    @Column(name = "CORPORATE_ID")
    @Getter
    @Setter
    private String corporateId;

}
