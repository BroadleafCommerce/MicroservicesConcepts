package com.tutorial.domain;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;

import com.broadleafcommerce.catalog.domain.product.Product;
import com.broadleafcommerce.catalog.provider.jpa.domain.product.JpaProduct;
import com.broadleafcommerce.common.jpa.JpaConstants;
import com.broadleafcommerce.common.jpa.converter.AbstractListConverter;
import com.broadleafcommerce.common.jpa.converter.AbstractMapConverter;
import com.broadleafcommerce.data.tracking.core.mapping.MappingUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tutorial.projection.ElectricCarProjection;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Domain extension with complex fields using embedded JSON. Also explicitly declares handling for
 * {@code #fromMe}, {@code #toMe}, and {@code #getBusinessDomainType}.
 */
@Entity
@Table(name = "ELECTRIC_CAR")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public class ElectricCar extends JpaProduct {

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
        TypeMap<ElectricCar, ElectricCarProjection> typeMap =
                mapper.createTypeMap(ElectricCar.class, ElectricCarProjection.class);
        typeMap.includeBase(JpaProduct.class, Product.class); // include parent mappings
        Converter<ElectricCar, ElectricCarProjection> postConverter = context -> {
            ElectricCar source = context.getSource();
            Set<Material> allMaterials = new HashSet<>();
            for (Feature feature : source.getFeatures()) {
                allMaterials.addAll(feature.getMaterials());
            }
            ElectricCarProjection destination = context.getDestination();
            destination.setAllMaterials(allMaterials); // map the synthetic aggregation field
            return destination;
        };
        MappingUtils.includeBaseConverters(
                mapper,
                typeMap,
                JpaProduct.class,
                Product.class,
                null,
                postConverter,
                false); // Handles our postConverter override, as well as making sure any parent
                        // converters are also called. Converters are not automatically inherited
                        // during TypeMap#includeBase(..).
        return mapper;
    }

    @Override
    public ModelMapper toMe() {
        ModelMapper mapper = super.toMe();
        TypeMap<ElectricCarProjection, ElectricCar> typeMap =
                mapper.createTypeMap(ElectricCarProjection.class, ElectricCar.class);
        typeMap.includeBase(Product.class, JpaProduct.class); // include parent mappings
        MappingUtils.includeBaseConverters(
                mapper,
                typeMap,
                Product.class,
                JpaProduct.class); // Handles adding parent pre and post converters, if applicable
        return mapper;
    }

    @Override
    public Class<?> getBusinessDomainType() {
        return ElectricCarProjection.class;
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
