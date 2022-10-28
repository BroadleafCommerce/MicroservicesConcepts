package com.tutorial;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.broadleafcommerce.catalog.domain.product.option.ProductOption;
import com.broadleafcommerce.catalog.provider.jpa.domain.product.JpaProduct;
import com.broadleafcommerce.catalog.provider.jpa.domain.product.option.JpaProductOption;
import com.broadleafcommerce.common.extension.mapping.ProjectionReferredTypeOverride;
import com.broadleafcommerce.common.jpa.autoconfigure.CommonJpaAutoConfiguration;
import com.tutorial.domain.ElectricCar;
import com.tutorial.domain.ElectricCarProductOption;
import com.tutorial.domain.ExtendedFeature;
import com.tutorial.domain.JpaElectricCarProductOption;
import com.tutorial.metadata.ProductNestedExtensionMetadata;

/**
 * Setup components for the extension case. Note, admin metadata and resource tier information are
 * both contributed here. This works without further intervention for the tutorial use case because
 * both the metadata and catalog services are contained in the same Min FlexPackage runtime used by
 * the tutorial. In other FlexPackage combinations, where these two services are not co-located, the
 * jar containing these components would need to be referenced as a dependency in the multiple
 * relevant locations.
 */
@Configuration
@AutoConfigureBefore(CommonJpaAutoConfiguration.class) // Run this config before JPA configuration.
                                                       // Makes sure our entity scans are included
@AutoConfigureAfter(ProductExtensionExplicitProjection.class) // Configure after our earlier
                                                              // dependent concept
@Import(ProductNestedExtensionMetadata.class) // Include our admin customizations
public class NestedJsonMemberExtension {

    /**
     * Set override for nested member in the {@link ElectricCar} object graph (i.e.
     * {@link JpaProduct#getOptions()}). This nested type utilizes both a projection and repository
     * domain counterpart.
     */
    @Bean
    public ProjectionReferredTypeOverride productOptionOverride() {
        return new ProjectionReferredTypeOverride(
                ProductOption.class,
                ElectricCarProductOption.class).withRepositoryMapTo(
                        JpaProductOption.class,
                        JpaElectricCarProductOption.class);
    }

    /**
     * Set override for nested member in the {@link ElectricCar} object graph (i.e.
     * {@link ElectricCar#getFeatures()}). This nested type utilizes the same type at both the
     * ElectricCar projection and repository domain level.
     * <p>
     * </p>
     * Note, overriding a nested type contributed by {@code ElectricCar} would normally not be
     * necessary, since {@code ElectricCar} is already at the override level. However,
     * {@code JpaProduct} does not have a nested type matching this use case, so we're improvising
     * by extending our own earlier extension contribution.
     * <p>
     * </p>
     * Finally, it is not a requirement that top-level extension exist (e.g. ElectricCar extends
     * JpaProduct) to use {@link ProjectionReferredTypeOverride}. You could just as easily provide a
     * nested structure extension that will be consumed directly by JpaProduct without an extension
     * of JpaProduct.
     */
    @Bean
    public ProjectionReferredTypeOverride featureOverride() {
        return new ProjectionReferredTypeOverride(
                ElectricCar.Feature.class,
                ExtendedFeature.class);
    }
}
