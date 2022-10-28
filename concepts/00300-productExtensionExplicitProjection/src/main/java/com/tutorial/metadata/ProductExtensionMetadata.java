package com.tutorial.metadata;

import static com.broadleafcommerce.catalog.metadata.support.DefaultProductType.values;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.broadleafcommerce.catalog.metadata.autoconfigure.CatalogMetadataProperties;
import com.broadleafcommerce.catalog.metadata.support.DefaultProductType;
import com.broadleafcommerce.catalog.metadata.support.ProductGroups;
import com.broadleafcommerce.catalog.metadata.support.ProductIds;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Customize the admin UI for our domain extension. Utilize the metadata DSL.
 */
@Configuration
public class ProductExtensionMetadata {

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
                .addField(modelField())
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
    private DefaultField modelField() {
        return Fields.string()
                .name("model")
                .label("model");
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
