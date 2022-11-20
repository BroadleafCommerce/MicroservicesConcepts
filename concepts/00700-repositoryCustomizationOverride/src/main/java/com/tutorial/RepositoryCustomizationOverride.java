package com.tutorial;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.broadleafcommerce.catalog.provider.jpa.repository.product.JpaProductRepository;
import com.broadleafcommerce.data.tracking.core.RepositoryContribution;
import com.broadleafcommerce.data.tracking.jpa.filtering.narrow.factory.DefaultJpaTrackableRepositoryDelegateHelper;
import com.broadleafcommerce.data.tracking.jpa.filtering.narrow.factory.JpaTrackableRepositoryDelegateHelper;
import com.tutorial.domain.ElectricCar;
import com.tutorial.repository.DefaultElectricCarRepositoryOverride;
import com.tutorial.repository.ElectricCarRepositoryOverride;

import java.util.Collections;

/**
 * Setup components for the extension case.
 */
@Configuration
public class RepositoryCustomizationOverride {

    @Bean
    public JpaTrackableRepositoryDelegateHelper<ElectricCar> delegateSupplier() {
        return new DefaultJpaTrackableRepositoryDelegateHelper<>(ElectricCar.class,
                JpaProductRepository.class);
    }

    @Bean
    public ElectricCarRepositoryOverride fragment(
            JpaTrackableRepositoryDelegateHelper<ElectricCar> delegateSupplier) {
        return new DefaultElectricCarRepositoryOverride(delegateSupplier);
    }

    @Bean
    public RepositoryContribution contribution(ElectricCarRepositoryOverride fragment) {
        return new RepositoryContribution()
                .withBaseRepositoryInterface(JpaProductRepository.class)
                .withConcreteFragments(
                        Collections.singletonMap(ElectricCarRepositoryOverride.class,
                                fragment));
    }

}
