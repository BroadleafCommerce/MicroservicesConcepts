package com.tutorial;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import com.broadleafcommerce.catalog.metadata.autoconfigure.CatalogMetadataProperties;
import com.broadleafcommerce.catalog.metadata.support.DefaultProductType;
import com.broadleafcommerce.catalog.metadata.support.ProductGroups;
import com.broadleafcommerce.catalog.metadata.support.ProductIds;
import com.broadleafcommerce.catalog.provider.jpa.domain.product.JpaProduct;
import com.broadleafcommerce.common.jpa.JpaConstants;
import com.broadleafcommerce.common.jpa.autoconfigure.CommonJpaAutoConfiguration;
import com.broadleafcommerce.common.jpa.converter.AbstractListConverter;
import com.broadleafcommerce.common.jpa.converter.ObjectMapConverter;
import com.broadleafcommerce.common.jpa.data.entity.JpaEntityScan;
import com.broadleafcommerce.metadata.dsl.core.extension.fields.SelectOption;
import com.broadleafcommerce.metadata.dsl.core.extension.views.details.EntityView;
import com.broadleafcommerce.metadata.dsl.core.utils.Columns;
import com.broadleafcommerce.metadata.dsl.core.utils.Fields;
import com.broadleafcommerce.metadata.dsl.registry.ComponentSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.broadleafcommerce.catalog.metadata.support.DefaultProductType.values;
import static com.broadleafcommerce.catalog.provider.RouteConstants.Persistence.CATALOG_ROUTE_PACKAGE;

/**
 * Setup components for the extension case. Note, admin metadata service and resource tier service
 * information are both contributed here. This works without further intervention for the tutorial
 * use case because both the metadata and catalog services are contained in the same Min FlexPackage
 * runtime used by the tutorial. In other FlexPackage combinations, where these two services are not
 * co-located, the jar containing these components would need to be referenced as a dependency in
 * the multiple relevant locations.
 * <p>
 * </p>
 * If you leave this top-level configuration named {@code ExtensionCase} for the tutorial exercises,
 * you will be able to benefit from auto-generation of spring.factories for Spring auto
 * configuration.
 */
@Configuration
@AutoConfigureBefore(CommonJpaAutoConfiguration.class)
public class ExtensionCase {

    /**
     * Complex domain extension
     */
    @Entity
    @Table(name = "ELECTRIC_CAR")
    @Inheritance(strategy = InheritanceType.JOINED)
    @Data
    public static class ElectricCar extends JpaProduct {

        @Column(name = "COLOR")
        private String color;

        /**
         * Map horse power value to car run mode (e.g. efficiency, comfort, performance)
         */
        @Column(name = "HORSE_POWER", length = JpaConstants.MEDIUM_TEXT_LENGTH)
        @Convert(converter = ObjectMapConverter.class)
        private Map<String, Integer> horsePowerByMode;

        /**
         * Series of range and charge times based on the ambient temperature
         */
        @Column(name = "THROUGHPUT", length = JpaConstants.MEDIUM_TEXT_LENGTH)
        @Convert(converter = ObjectMapConverter.class)
        private Map<Integer, ElecticityThroughput> throughputByTempFahrenheit;

        /**
         * Car features (e.g. seats, headlamps, wheels, etc...). Includes materials where relevant
         * (e.g. vegan seat materials).
         */
        @Column(name = "FEATURES", length = JpaConstants.MEDIUM_TEXT_LENGTH)
        @Convert(converter = ObjectListConverter.class)
        private List<Feature> features;

    }

    @Data
    @EqualsAndHashCode
    public static class ElecticityThroughput implements Serializable {

        private final BigDecimal rangeMiles;
        private final Long chargeTime;

    }

    @Data
    @EqualsAndHashCode
    public static class Feature implements Serializable {

        private final String name;
        private final String description;
        private final BigDecimal msrp;
        private final List<Material> materials = new ArrayList<>();

    }

    @Data
    @EqualsAndHashCode
    public static class Material implements Serializable {

        private final String name;
        private final String description;
        private final String supplier;

    }

    public static class ObjectListConverter extends AbstractListConverter<Object> {
        public ObjectListConverter(
                @Nullable @Qualifier("converterObjectMapper") ObjectMapper objectMapper) {
            super(Object.class, objectMapper);
        }
    }

    /**
     * Spring configuration to wire the extension and establish admin metadata for the domain
     * extension.
     */
    @Configuration
    @JpaEntityScan(basePackages = "com.tutorial", routePackage = CATALOG_ROUTE_PACKAGE)
    public static class Config {

        @Bean
        public ComponentSource tutorialProductAdminMetadata(
                @Nullable CatalogMetadataProperties properties) {
            return registry -> {
                // Discover the active product types
                List<DefaultProductType> types = Arrays.stream(values())
                        .filter(t -> ((Optional.ofNullable(properties))
                                .map(item -> item.getActiveProductTypes().contains(t.name()))
                                .orElse(false)))
                        .collect(Collectors.toList());
                for (DefaultProductType type : types) {
                    // For each product type, add the new field to the create and update forms
                    Arrays.asList(
                            (EntityView<?>) registry
                                    .get(String.format(ProductIds.CREATE, type.name())),
                            (EntityView<?>) registry
                                    .get(String.format(ProductIds.UPDATE, type.name())))
                            .forEach(view -> view.getGeneralForm()
                                    .getGroup(ProductGroups.BASIC_INFORMATION)
                                    .addField(Fields.colorPicker()
                                            .name("color")
                                            .label("color")
                                            .defaultValue("#ffffff"))
                                    .addField(Fields.residentMap()
                                            .name("horsePower")
                                            .label("horsePower")
                                            .notCollapsedByDefault()
                                            .emptyMessage("Horse power by run mode")
                                            .addColumn("value", Columns.integer()
                                                    .label("value")
                                                    .order(1000))
                                            .createAction(create -> create
                                                    .label("create")
                                                    .addEntryKeyField(Fields.select()
                                                            .label("key")
                                                            .options(RunModeOptionEnum.toOptions())
                                                            .required())
                                                    .addField("value", Fields.integer()
                                                            .label("value")
                                                            .required()
                                                            .order(1000)))
                                            .deleteAction(delete -> delete
                                                    .label("delete")
                                                    .order(1000))
                                            .updateAction(update -> update
                                                    .label("update")
                                                    .addField("value", Fields.integer()
                                                            .label("value")
                                                            .required()
                                                            .order(1000))
                                                    .order(2000))));
                }
                // TODO add support for the complex extensions
            };
        }
    }

    public enum RunModeOptionEnum implements SelectOption.SelectOptionEnum {

        ECONOMY("Economy"), COMFORT("Comfort"), DYNAMIC("Dynamic");

        private String label;

        RunModeOptionEnum(String label) {
            this.label = label;
        }

        @Override
        @NonNull
        public String label() {
            return label;
        }

        public static List<SelectOption> toOptions() {
            return SelectOption.fromEnums(values());
        }
    }
}
