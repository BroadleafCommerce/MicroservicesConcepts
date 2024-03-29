package com.tutorial.domain;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;

import com.broadleafcommerce.catalog.provider.jpa.domain.product.JpaProduct;
import com.broadleafcommerce.common.jpa.JpaConstants;
import com.broadleafcommerce.common.jpa.converter.AbstractListConverter;
import com.broadleafcommerce.common.jpa.converter.AbstractMapConverter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Domain extension with complex fields using embedded JSON
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
     * Map horse power value to car run mode (e.g. efficiency, comfort, performance)
     */
    @Column(name = "HORSE_POWER", length = JpaConstants.MEDIUM_TEXT_LENGTH)
    @Convert(converter = HorsepowerMapConverter.class)
    private Map<String, HorsePower> horsePowerByMode;

    /**
     * Series of range and charge times based on the ambient temperature
     */
    @Column(name = "EFFICIENCY", length = JpaConstants.MEDIUM_TEXT_LENGTH)
    @Convert(converter = EfficiencyMapConverter.class)
    private Map<String, Efficiency> efficiencyByTempFahrenheit;

    /**
     * Car features (e.g. seats, headlamps, wheels, etc...). Includes materials where relevant (e.g.
     * vegan seat materials).
     */
    @Column(name = "FEATURES", length = JpaConstants.MEDIUM_TEXT_LENGTH)
    @Convert(converter = FeatureListConverter.class)
    private List<Feature> features;

    /**
     * Zero arg constructor required
     */
    @Data
    @EqualsAndHashCode
    public static class Efficiency implements Serializable {

        private BigDecimal rangeMiles;
        private Long chargeTimeMinutes;

    }

    /**
     * Zero arg constructor required
     */
    @Data
    @EqualsAndHashCode
    public static class HorsePower implements Serializable {

        private Integer value;

    }

    /**
     * Zero arg constructor required
     */
    @Data
    @EqualsAndHashCode
    public static class Feature implements Serializable {

        private String name;
        private String description;
        private List<Material> materials = new ArrayList<>();

    }

    /**
     * Zero arg constructor required
     */
    @Data
    @EqualsAndHashCode
    public static class Material implements Serializable {

        private String name;
        private String description;
        private String supplier;

    }

    /**
     * Convert to/from JSON
     */
    public static class FeatureListConverter extends AbstractListConverter<Feature> {
        public FeatureListConverter(
                @Nullable @Qualifier("converterObjectMapper") ObjectMapper objectMapper) {
            super(Feature.class, objectMapper);
        }
    }

    /**
     * Convert to/from JSON
     */
    public static class EfficiencyMapConverter
            extends AbstractMapConverter<String, Efficiency> {
        public EfficiencyMapConverter(
                @Nullable @Qualifier("converterObjectMapper") ObjectMapper objectMapper) {
            super(String.class, Efficiency.class, objectMapper);
        }
    }

    /**
     * Convert to/from JSON
     */
    public static class HorsepowerMapConverter extends AbstractMapConverter<String, HorsePower> {
        public HorsepowerMapConverter(
                @Nullable @Qualifier("converterObjectMapper") ObjectMapper objectMapper) {
            super(String.class, HorsePower.class, objectMapper);
        }
    }

}
