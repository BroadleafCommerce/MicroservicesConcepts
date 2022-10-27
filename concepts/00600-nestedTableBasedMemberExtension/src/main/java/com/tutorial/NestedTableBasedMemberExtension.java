package com.tutorial;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.broadleafcommerce.common.extension.mapping.ProjectionReferredTypeOverride;
import com.broadleafcommerce.common.jpa.autoconfigure.CommonJpaAutoConfiguration;
import com.broadleafcommerce.common.jpa.data.entity.JpaEntityScan;
import com.tutorial.domain.ElectricCar;
import com.tutorial.domain.ExtendedUpgrade;
import com.tutorial.domain.Upgrade;
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
@AutoConfigureBefore(CommonJpaAutoConfiguration.class)
@AutoConfigureAfter(ProductExtensionComplexFieldTableBased.class)
@JpaEntityScan(basePackages = "com.tutorial.domain")
@Import(ProductNestedExtensionMetadata.class)
public class NestedTableBasedMemberExtension {

    /**
     * Set override for nested member in the {@link ElectricCar} object graph (i.e.
     * {@link ElectricCar#getUpgrades()}).
     * <p>
     * </p>
     * Note, overriding a nested type contributed by {@code ElectricCar} would normally not be
     * necessary, since {@code ElectricCar} is already at the override level. However,
     * {@code JpaProduct} does not have a nested type matching this use case, so we're improvising
     * by extending our own earlier extension contribution.
     */
    @Bean
    public ProjectionReferredTypeOverride upgradeOverride() {
        return new ProjectionReferredTypeOverride(
                Upgrade.class,
                ExtendedUpgrade.class);
    }
}
