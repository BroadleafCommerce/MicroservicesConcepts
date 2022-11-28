package com.tutorial.domain;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;

import com.broadleafcommerce.catalog.domain.product.Product;
import com.broadleafcommerce.catalog.provider.jpa.domain.JpaAttribute;
import com.broadleafcommerce.catalog.provider.jpa.domain.product.JpaProduct;
import com.broadleafcommerce.common.jpa.JpaConstants;
import com.broadleafcommerce.common.jpa.converter.AbstractListConverter;
import com.broadleafcommerce.common.jpa.converter.AbstractMapConverter;
import com.broadleafcommerce.data.tracking.core.mapping.MappingUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Domain extension with complex fields using embedded JSON. Also explicitly declares handling for
 * {@code #fromMe}, {@code #toMe}, and {@code #getBusinessDomainType}.
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

    @Override
    public ModelMapper fromMe() {
        ModelMapper mapper = super.fromMe();
        // Handle the synthetic property and attribute based property mapping as a post converter
        Converter<MyAutoCoProduct, MyAutoCoProductProjection> postConverter = context -> {
            MyAutoCoProduct source = context.getSource();
            Set<Material> allMaterials = new HashSet<>();
            Optional.ofNullable(source.getFeatures()).ifPresent(featuresList -> featuresList
                    .forEach(feature -> allMaterials.addAll(feature.getMaterials())));
            MyAutoCoProductProjection destination = context.getDestination();
            destination.setAllMaterials(allMaterials); // map the synthetic aggregation field

            String corporateId = Optional.ofNullable(source.getAttributes().get("corporateId"))
                    .map(attr -> String.valueOf(attr.getValue())).orElse(null);
            if (StringUtils.isNotEmpty(corporateId)) {
                destination.setCorporateId(corporateId);
            }
            return destination;
        };
        // Handle type map setup for the extended types
        MappingUtils.setupExtensions(mapper, MyAutoCoProduct.class, MyAutoCoProductProjection.class,
                JpaProduct.class, Product.class, null, postConverter, false);
        return mapper;
    }

    @Override
    public ModelMapper toMe() {
        ModelMapper mapper = super.toMe();
        // Handle the attribute based property mapping as a post converter
        Converter<MyAutoCoProductProjection, MyAutoCoProduct> postConverter = context -> {
            MyAutoCoProductProjection source = context.getSource();
            MyAutoCoProduct destination = context.getDestination();
            if (StringUtils.isNotEmpty(source.getCorporateId())) {
                JpaAttribute attribute = new JpaAttribute();
                attribute.setNameLabel("corporateId");
                attribute.setValue(source.getCorporateId());
                destination.getAttributes().put("corporateId", attribute);
            }
            return destination;
        };
        // Handle type map setup for the extended types
        MappingUtils.setupExtensions(mapper, MyAutoCoProductProjection.class, MyAutoCoProduct.class,
                Product.class, JpaProduct.class, null, postConverter, false);
        return mapper;
    }

    @Override
    public Class<?> getBusinessDomainType() {
        return MyAutoCoProductProjection.class;
    }

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
