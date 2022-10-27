package com.tutorial.metadata;

import static com.broadleafcommerce.catalog.metadata.support.DefaultProductType.values;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import com.broadleafcommerce.catalog.metadata.autoconfigure.CatalogMetadataProperties;
import com.broadleafcommerce.catalog.metadata.support.DefaultProductType;
import com.broadleafcommerce.catalog.metadata.support.ProductForm;
import com.broadleafcommerce.catalog.metadata.support.ProductGroups;
import com.broadleafcommerce.catalog.metadata.support.ProductIds;
import com.broadleafcommerce.catalog.metadata.support.ProductProps;
import com.broadleafcommerce.metadata.dsl.core.extension.fields.ResidentGridField;
import com.broadleafcommerce.metadata.dsl.core.extension.views.details.EntityView;
import com.broadleafcommerce.metadata.dsl.core.utils.Fields;
import com.broadleafcommerce.metadata.dsl.registry.ComponentSource;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
public class ProductNestedExtensionMetadata {

    @Bean
    public ComponentSource tutorialProductNestedAdminMetadata(
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

    private void customizeView(EntityView<? extends EntityView<?>> view) {
        if (Objects.requireNonNull(view.getId()).contains("update")) {
            ResidentGridField<?> optionGrid =
                    (ResidentGridField<?>) view.getForm(ProductForm.OPTIONS)
                            .getGroup(ProductGroups.OPTIONS)
                            .getField(ProductProps.OPTIONS);
            optionGrid.getCreateAction().addField("isDealerOption",
                    Fields.string()
                            .label("Is Dealer Option")
                            .required()
                            .order(3000));
            optionGrid.getUpdateAction().addField("isDealerOption",
                    Fields.string()
                            .label("Is Dealer Option")
                            .required()
                            .order(3000));
        }

        ResidentGridField<?> featureGrid = (ResidentGridField<?>) view.getGeneralForm()
                .getGroup(ProductGroups.BASIC_INFORMATION).getField("features");
        featureGrid.getCreateAction().addField("corporateId",
                Fields.string()
                        .label("Corporate ID")
                        .required()
                        .order(3000));
        featureGrid.getUpdateAction().addField("corporateId",
                Fields.string()
                        .label("Corporate ID")
                        .required()
                        .order(3000));
    }

}
