package com.tutorial;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.broadleafcommerce.catalog.provider.jpa.repository.product.JpaProductRepository;
import com.broadleafcommerce.data.tracking.core.RepositoryContribution;
import com.broadleafcommerce.data.tracking.jpa.filtering.narrow.factory.DefaultJpaTrackableRepositoryDelegateSupplier;
import com.broadleafcommerce.data.tracking.jpa.filtering.narrow.factory.JpaTrackableRepositoryDelegateSupplier;
import com.tutorial.domain.ElectricCar;
import com.tutorial.repository.DefaultElectricCarRepositoryConcreteContribution;
import com.tutorial.repository.ElectricCarRepositoryConcreteContribution;
import com.tutorial.repository.ElectricCarAdditionRepositoryDynamicContribution;

import java.util.Collections;

/**
 * Setup components for the extension case.
 */
@Configuration
public class RepositoryCustomizationContribution {

    @Bean
    public JpaTrackableRepositoryDelegateSupplier<ElectricCar> delegateSupplier() {
        return new DefaultJpaTrackableRepositoryDelegateSupplier<>(ElectricCar.class,
                JpaProductRepository.class);
    }

    @Bean
    public ElectricCarRepositoryConcreteContribution fragment(
            JpaTrackableRepositoryDelegateSupplier<ElectricCar> delegateSupplier) {
        return new DefaultElectricCarRepositoryConcreteContribution(delegateSupplier);
    }

    @Bean
    public RepositoryContribution contribution(
            ElectricCarRepositoryConcreteContribution fragment) {
        return new RepositoryContribution()
                .withBaseRepositoryInterface(JpaProductRepository.class)
                .withConcreteFragments(
                        Collections.singletonMap(
                                ElectricCarRepositoryConcreteContribution.class,
                                fragment))
                .withQueryFragments(Collections
                        .singletonList(ElectricCarAdditionRepositoryDynamicContribution.class));
    }

}
