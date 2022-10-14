package com.tutorial;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.broadleafcommerce.catalog.metadata.autoconfigure.CatalogMetadataProperties;
import com.broadleafcommerce.catalog.metadata.support.DefaultProductType;
import com.broadleafcommerce.catalog.metadata.support.ProductGroups;
import com.broadleafcommerce.catalog.metadata.support.ProductIds;
import com.broadleafcommerce.catalog.provider.jpa.domain.product.JpaProduct;
import com.broadleafcommerce.common.jpa.data.entity.JpaEntityScan;
import com.broadleafcommerce.data.tracking.core.mapping.AutoConfigureProjectionMapping;
import com.broadleafcommerce.metadata.dsl.core.extension.views.details.EntityView;
import com.broadleafcommerce.metadata.dsl.core.utils.Fields;
import com.broadleafcommerce.metadata.dsl.registry.ComponentSource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import static com.broadleafcommerce.catalog.metadata.support.DefaultProductType.values;
import static com.broadleafcommerce.catalog.provider.RouteConstants.Persistence.CATALOG_ROUTE_PACKAGE;

/**
 * Setup components for the extension case. Note, admin metadata service and resource tier service
 * information are both contributed here. This works without further intervention for the tutorial
 * use case because both the metadata and catalog services are contained in the same Min FlexPackage
 * runtime used by the tutorial. In other FlexPackage combinations, where these two services are not
 * co-located, the jar containing these components would need to be referenced as a dependency in
 * the multiple relevant locations.
 */
@Configuration
public class ProductExtensionOnly {

    /**
     * Spring configuration to wire the extension
     */
    @Configuration
    @JpaEntityScan(basePackages = "com.tutorial", routePackage = CATALOG_ROUTE_PACKAGE)
    public static class Config {}

    /**
     * Spring configuration to establish admin metadata for the domain extension.
     */
    @Configuration
    @ConditionalOnBean(CatalogMetadataProperties.class)
    @RequiredArgsConstructor
    public static class AdminConfig {

        private final CatalogMetadataProperties properties;

        @Bean
        public ComponentSource myProductMetadataComponents() {
            return registry -> {
                List<DefaultProductType> types = Arrays.stream(values())
                        .filter(t -> properties.getActiveProductTypes().contains(t.name()))
                        .collect(Collectors.toList());
                for (DefaultProductType type : types) {
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
                                            .defaultValue("#ffffff")));
                }
            };
        }

    }

    /**
     * Simple column domain extension
     */
    @Entity
    @Table(name = "TUTORIAL_PRODUCT")
    @Inheritance(strategy = InheritanceType.JOINED)
    @Data
    @AutoConfigureProjectionMapping
    public static class TutorialProduct extends JpaProduct {

        @Column(name = "COLOR")
        private String color;

    }

}
