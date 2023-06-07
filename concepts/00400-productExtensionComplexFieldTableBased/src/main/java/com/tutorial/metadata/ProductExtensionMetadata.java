package com.tutorial.metadata;

import static com.broadleafcommerce.catalog.metadata.support.DefaultProductType.values;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import com.broadleafcommerce.catalog.metadata.autoconfigure.CatalogMetadataProperties;
import com.broadleafcommerce.catalog.metadata.support.DefaultProductType;
import com.broadleafcommerce.catalog.metadata.support.ProductGroups;
import com.broadleafcommerce.catalog.metadata.support.ProductIds;
import com.broadleafcommerce.metadata.dsl.core.Group;
import com.broadleafcommerce.metadata.dsl.core.extension.fields.DefaultField;
import com.broadleafcommerce.metadata.dsl.core.extension.fields.DefaultFieldArrayGridField;
import com.broadleafcommerce.metadata.dsl.core.extension.views.details.EntityView;
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
                .addField(upgradesField())
                .addField(weightField())
                .addField(widthField())
                .addField(lengthField())
                .addField(heightField());
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
    private DefaultField modelField() {
        return Fields.string()
                .name("model")
                .label("Model");
    }

    @NotNull
    private DefaultField weightField() {
        return Fields.decimal()
                .name("characteristics.weight")
                .label("Weight");
    }

    @NotNull
    private DefaultField widthField() {
        return Fields.decimal()
                .name("characteristics.width")
                .label("Width");
    }

    @NotNull
    private DefaultField lengthField() {
        return Fields.decimal()
                .name("characteristics.length")
                .label("Length");
    }

    @NotNull
    private DefaultField heightField() {
        return Fields.decimal()
                .name("characteristics.height")
                .label("Height");
    }
}
