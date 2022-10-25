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
import com.broadleafcommerce.metadata.dsl.core.extension.fields.DefaultFieldArrayGridField;
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
                .addField(featuresField())
                .addField(upgradesField());
    }

    @NotNull
    private DefaultFieldArrayGridField upgradesField() {
        return Fields.fieldArrayGrid()
                .name("upgrades")
                .label("Upgrades")
                .addField("name", Fields.string().label("Name").order(1000))
                .addField("description", Fields.string().label("Description").order(1000))
                .addField("manufacturerId", Fields.string().label("Manufacturer ID").order(3000));
    }

    @NotNull
    private DefaultResidentGridField featuresField() {
        return Fields.residentGrid()
                .name("features")
                .label("Features")
                .addColumn("name", Columns.string().label("Name").order(1000))
                .addColumn("description", Columns.string().label("Description").order(2000))
                .createAction(createForm -> {
                    CreateModalFormAction<?> response = createForm.label("Create");
                    setupFeatureFields(response);
                    return response;
                })
                .updateAction(updateForm -> {
                    ModalFormAction<?> response = updateForm.label("Update");
                    setupFeatureFields(response);
                    return response;
                })
                .deleteLabel(
                        "Delete");
    }

    private void setupFeatureFields(FormAction<?> action) {
        action.addField(Fields.string().name("name").label("Name").required().order(1000))
                .addField(Fields.string().name("description").label("Description").order(2000))
                .addField(materialsField())
                .order(3000);
    }

    @NotNull
    private DefaultResidentMapField efficiencyField() {
        return Fields.residentMap()
                .name("efficiencyByTempFahrenheit")
                .label("Efficiency")
                .notCollapsedByDefault()
                .emptyMessage("Efficiency By Temperature")
                .addColumn(Columns.decimal()
                        .order(1000).name("rangeMiles").label("Range"))
                .addColumn(Columns.longInteger()
                        .order(2000).name("chargeTimeMinutes").label("Charge Time"))
                .createAction(create -> {
                    ResidentMapCreateAction<?> response = create
                            .label("Create")
                            .addEntryKeyField(Fields.select()
                                    .label("Temperature")
                                    .options(TemperatureOptionEnum.toOptions())
                                    .required());
                    setupEfficiencyFields(response);
                    return response;
                })
                .deleteAction(delete -> delete
                        .label("Delete")
                        .order(1000))
                .updateAction(update -> {
                    ModalFormAction<?> response = update.label("Update");
                    setupEfficiencyFields(response);
                    return response;
                });
    }

    private void setupEfficiencyFields(FormAction<?> action) {
        action.addField(Fields.decimal()
                .required()
                .name("rangeMiles")
                .order(1000)
                .label("Range"))
                .addField("chargeTimeMinutes", Fields.longInteger()
                        .required()
                        .order(2000).label("Charge Time"));
    }

    @NotNull
    private DefaultResidentMapField horsePowerField() {
        return Fields.residentMap()
                .name("horsePowerByMode")
                .label("HorsePower")
                .notCollapsedByDefault()
                .emptyMessage("Horse power by run mode")
                .addColumn("value", Columns.integer()
                        .order(1000).label("Value"))
                .createAction(create -> {
                    ResidentMapCreateAction<?> response = create.label("Create")
                            .addEntryKeyField(Fields.select()
                                    .label("Key")
                                    .options(RunModeOptionEnum.toOptions())
                                    .required());
                    setupHorsePowerFields(response);
                    return response;
                })
                .deleteAction(delete -> delete
                        .label("Delete")
                        .order(1000))
                .updateAction(update -> {
                    ModalFormAction<?> response = update.label("Update");
                    setupHorsePowerFields(response);
                    return response;
                });
    }

    private void setupHorsePowerFields(FormAction<?> action) {
        action.addField(Fields.integer().name("value").label("Value").required().order(1000));
    }

    @NotNull
    private DefaultField modelField() {
        return Fields.string()
                .name("model")
                .label("Model");
    }

    @NotNull
    private DefaultResidentGridField materialsField() {
        return Fields.residentGrid()
                .name("materials")
                .label("Materials")
                .addColumn(Columns.string().name("name").label("Name").order(1000))
                .addColumn(Columns.string().name("description").label("Description").order(2000))
                .addColumn(Columns.string().name("supplier").label("supplier").order(3000))
                .createAction(
                        nestedCreate -> nestedCreate
                                .label("Create")
                                .addField(Fields.string().name("name").label("Name").required()
                                        .order(1000))
                                .addField(Fields.string().name("description").label("Description")
                                        .required().order(2000))
                                .addField(Fields.string().name("supplier").label("Supplier")
                                        .required().order(3000)))
                .updateAction(
                        nestedUpdate -> nestedUpdate
                                .label("Update")
                                .addField(Fields.string().name("name").label("Name").required()
                                        .order(1000))
                                .addField(Fields.string().name("description").label("Description")
                                        .required().order(2000))
                                .addField(Fields.string().name("supplier").label("Supplier")
                                        .required().order(3000)))
                .deleteLabel("Delete")
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
