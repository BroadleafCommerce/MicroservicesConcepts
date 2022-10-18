package com.tutorial;

import org.jetbrains.annotations.NotNull;
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
import com.broadleafcommerce.common.jpa.converter.AbstractMapConverter;
import com.broadleafcommerce.common.jpa.data.entity.JpaEntityScan;
import com.broadleafcommerce.metadata.dsl.core.Group;
import com.broadleafcommerce.metadata.dsl.core.extension.actions.CreateModalFormAction;
import com.broadleafcommerce.metadata.dsl.core.extension.actions.FormAction;
import com.broadleafcommerce.metadata.dsl.core.extension.actions.ModalFormAction;
import com.broadleafcommerce.metadata.dsl.core.extension.actions.ResidentMapCreateAction;
import com.broadleafcommerce.metadata.dsl.core.extension.fields.DefaultField;
import com.broadleafcommerce.metadata.dsl.core.extension.fields.DefaultResidentGridField;
import com.broadleafcommerce.metadata.dsl.core.extension.fields.DefaultResidentMapField;
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
     * Domain extension with complex fields using embedded JSON
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
        @Convert(converter = HorsepowerMapConverter.class)
        private Map<String, HorsePower> horsePowerByMode;

        /**
         * Series of range and charge times based on the ambient temperature
         */
        @Column(name = "EFFICIENCY", length = JpaConstants.MEDIUM_TEXT_LENGTH)
        @Convert(converter = EfficiencyMapConverter.class)
        private Map<String, Efficiency> efficiencyByTempFahrenheit;

        /**
         * Car features (e.g. seats, headlamps, wheels, etc...). Includes materials where relevant
         * (e.g. vegan seat materials).
         */
        @Column(name = "FEATURES", length = JpaConstants.MEDIUM_TEXT_LENGTH)
        @Convert(converter = FeatureListConverter.class)
        private List<Feature> features;

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
                            .forEach(this::customizeView);
                }
            };
        }

        @NotNull
        private Group<?> customizeView(EntityView<? extends EntityView<?>> view) {
            return view.getGeneralForm()
                    .getGroup(ProductGroups.BASIC_INFORMATION)
                    .addField(colorField())
                    .addField(horsePowerField())
                    .addField(efficiencyField())
                    .addField(featuresField());
        }

        @NotNull
        private DefaultResidentGridField featuresField() {
            return Fields.residentGrid()
                    .name("features")
                    .label("features")
                    .addColumn("name", Columns.string().order(1000))
                    .addColumn("description", Columns.string().order(2000))
                    .createAction(createForm -> {
                        CreateModalFormAction<?> response = createForm.label("create");
                        setupFeatureFields(response);
                        return response;
                    })
                    .updateAction(updateForm -> {
                        ModalFormAction<?> response = updateForm.label("update");
                        setupFeatureFields(response);
                        return response;
                    })
                    .deleteLabel(
                            "delete");
        }

        private void setupFeatureFields(FormAction<?> action) {
            action.addField("name",
                    Fields.string().required().order(1000))
                    .addField("description",
                            Fields.string().order(2000))
                    .addField("materials", materialsField())
                    .order(3000);
        }

        @NotNull
        private DefaultResidentMapField efficiencyField() {
            return Fields.residentMap()
                    .name("efficiencyByTempFahrenheit")
                    .label("efficiency")
                    .notCollapsedByDefault()
                    .emptyMessage("Efficiency By Temperature")
                    .addColumn("rangeMiles", Columns.decimal()
                            .order(1000).label("range"))
                    .addColumn("chargeTimeMinutes", Columns.longInteger()
                            .order(2000).label("chargeTime"))
                    .createAction(create -> {
                        ResidentMapCreateAction<?> response = create
                                .label("create")
                                .addEntryKeyField(Fields.select()
                                        .label("temperature")
                                        .options(TemperatureOptionEnum.toOptions())
                                        .required());
                        setupEfficiencyFields(response);
                        return response;
                    })
                    .deleteAction(delete -> delete
                            .label("delete")
                            .order(1000))
                    .updateAction(update -> {
                        ModalFormAction<?> response = update.label("update");
                        setupEfficiencyFields(response);
                        return response;
                    });
        }

        private void setupEfficiencyFields(FormAction<?> action) {
            action.addField("rangeMiles", Fields.decimal()
                    .required()
                    .order(1000).label("range"))
                    .addField("chargeTimeMinutes", Fields.longInteger()
                            .required()
                            .order(2000).label("chargeTime"));
        }

        @NotNull
        private DefaultResidentMapField horsePowerField() {
            return Fields.residentMap()
                    .name("horsePowerByMode")
                    .label("horsePower")
                    .notCollapsedByDefault()
                    .emptyMessage("Horse power by run mode")
                    .addColumn("value", Columns.integer()
                            .order(1000).label("value"))
                    .createAction(create -> {
                        ResidentMapCreateAction<?> response = create.label("create")
                                .addEntryKeyField(Fields.select()
                                        .label("key")
                                        .options(RunModeOptionEnum.toOptions())
                                        .required());
                        setupHorsePowerFields(response);
                        return response;
                    })
                    .deleteAction(delete -> delete
                            .label("delete")
                            .order(1000))
                    .updateAction(update -> {
                        ModalFormAction<?> response = update.label("update");
                        setupHorsePowerFields(response);
                        return response;
                    });
        }

        private void setupHorsePowerFields(FormAction<?> action) {
            action.addField("value", Fields.integer().required().order(1000));
        }

        @NotNull
        private DefaultField colorField() {
            return Fields.colorPicker()
                    .name("color")
                    .label("color")
                    .defaultValue("#ffffff");
        }

        @NotNull
        private DefaultResidentGridField materialsField() {
            return Fields.residentGrid()
                    .label("materials")
                    .addColumn("name", Columns.string().order(1000))
                    .addColumn("description", Columns.string().order(2000))
                    .addColumn("supplier", Columns.string().order(3000))
                    .createAction(
                            nestedCreate -> nestedCreate
                                    .label("create")
                                    .addField("name", Fields.string().required().order(1000))
                                    .addField("description", Fields.string().required().order(2000))
                                    .addField("supplier", Fields.string().required().order(3000)))
                    .updateAction(
                            nestedUpdate -> nestedUpdate
                                    .label("update")
                                    .addField("name", Fields.string().required().order(1000))
                                    .addField("description", Fields.string().required().order(2000))
                                    .addField("supplier", Fields.string().required().order(3000)))
                    .deleteLabel("delete")
                    .order(3000);
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

    public enum TemperatureOptionEnum implements SelectOption.SelectOptionEnum {

        LOW("Low"), STANDARD("Standard"), HIGH("High");

        private String label;

        TemperatureOptionEnum(String label) {
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
