package com.tutorial;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.broadleafcommerce.catalog.provider.jpa.repository.product.JpaProductRepository;
import com.broadleafcommerce.data.tracking.core.RepositoryContribution;
import com.broadleafcommerce.data.tracking.jpa.filtering.narrow.factory.DefaultJpaTrackableRepositoryDelegateSupplier;
import com.broadleafcommerce.data.tracking.jpa.filtering.narrow.factory.JpaTrackableRepositoryDelegateSupplier;
import com.tutorial.domain.ElectricCar;
import com.tutorial.repository.DefaultElectricCarRepositoryContribution;
import com.tutorial.repository.ElectricCarRepositoryContribution;
import java.util.Collections;

/**
 * Setup components for the extension case.
 */
@Configuration
public class RepositoryCustomizationOverride {

    @Bean
    public JpaTrackableRepositoryDelegateSupplier<ElectricCar> delegateSupplier() {
        return new DefaultJpaTrackableRepositoryDelegateSupplier<>(ElectricCar.class,
                JpaProductRepository.class);
    }

    @Bean
    public ElectricCarRepositoryContribution fragment(
            JpaTrackableRepositoryDelegateSupplier<ElectricCar> delegateSupplier) {
        return new DefaultElectricCarRepositoryContribution(delegateSupplier);
    }

    @Bean
    public RepositoryContribution contribution(ElectricCarRepositoryContribution fragment) {
        return new RepositoryContribution()
                .withBaseRepositoryInterface(JpaProductRepository.class)
                .withConcreteFragments(
                        Collections.singletonMap(ElectricCarRepositoryContribution.class,
                                fragment));
    }

}