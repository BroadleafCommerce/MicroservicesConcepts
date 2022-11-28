package com.tutorial;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.broadleafcommerce.catalog.provider.jpa.repository.product.JpaProductRepository;
import com.broadleafcommerce.data.tracking.core.RepositoryContribution;
import com.broadleafcommerce.data.tracking.jpa.filtering.narrow.factory.DefaultJpaTrackableRepositoryDelegateHelper;
import com.broadleafcommerce.data.tracking.jpa.filtering.narrow.factory.JpaTrackableRepositoryDelegateHelper;
import com.tutorial.domain.MyAutoCoProduct;
import com.tutorial.repository.DefaultMyAutoCoProductRepositoryConcreteContribution;
import com.tutorial.repository.MyAutoCoProductRepositoryConcreteContribution;
import com.tutorial.repository.MyAutoCoProductRepositoryDynamicContribution;

import java.util.Collections;

/**
 * Setup components for the extension case.
 */
@Configuration
public class RepositoryCustomizationContribution {

    @Bean
    public JpaTrackableRepositoryDelegateHelper<MyAutoCoProduct> delegateSupplier() {
        return new DefaultJpaTrackableRepositoryDelegateHelper<>(MyAutoCoProduct.class,
                JpaProductRepository.class);
    }

    @Bean
    public MyAutoCoProductRepositoryConcreteContribution fragment(
            JpaTrackableRepositoryDelegateHelper<MyAutoCoProduct> delegateSupplier) {
        return new DefaultMyAutoCoProductRepositoryConcreteContribution(delegateSupplier);
    }

    @Bean
    public RepositoryContribution contribution(
            MyAutoCoProductRepositoryConcreteContribution fragment) {
        return new RepositoryContribution()
                .withBaseRepositoryInterface(JpaProductRepository.class)
                .withConcreteFragments(
                        Collections.singletonMap(
                                MyAutoCoProductRepositoryConcreteContribution.class,
                                fragment))
                .withQueryFragments(Collections
                        .singletonList(MyAutoCoProductRepositoryDynamicContribution.class));
    }

}
