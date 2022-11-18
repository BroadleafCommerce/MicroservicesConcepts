package com.tutorial;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.broadleafcommerce.catalog.provider.jpa.repository.product.JpaProductRepository;
import com.broadleafcommerce.data.tracking.core.RepositoryContribution;
import com.broadleafcommerce.data.tracking.jpa.filtering.narrow.factory.DefaultJpaTrackableRepositoryDelegateHelper;
import com.broadleafcommerce.data.tracking.jpa.filtering.narrow.factory.JpaTrackableRepositoryDelegateHelper;
import com.tutorial.domain.ElectricCar;
import com.tutorial.repository.DefaultElectricCarRepositoryConcreteContribution;
import com.tutorial.repository.ElectricCarRepositoryConcreteContribution;
import com.tutorial.repository.ElectricCarRepositoryDynamicContribution;

import java.util.Collections;

/**
 * Setup components for the extension case.
 */
@Configuration
public class RepositoryCustomizationContribution {

    @Bean
    public JpaTrackableRepositoryDelegateHelper<ElectricCar> delegateSupplier() {
        return new DefaultJpaTrackableRepositoryDelegateHelper<>(ElectricCar.class,
                JpaProductRepository.class);
    }

    @Bean
    public ElectricCarRepositoryConcreteContribution fragment(
            JpaTrackableRepositoryDelegateHelper<ElectricCar> delegateSupplier) {
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
                        .singletonList(ElectricCarRepositoryDynamicContribution.class));
    }

}
