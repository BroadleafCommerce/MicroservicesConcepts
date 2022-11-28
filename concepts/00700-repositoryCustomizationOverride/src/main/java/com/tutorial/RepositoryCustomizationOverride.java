package com.tutorial;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.broadleafcommerce.catalog.provider.jpa.repository.product.JpaProductRepository;
import com.broadleafcommerce.data.tracking.core.RepositoryContribution;
import com.broadleafcommerce.data.tracking.jpa.filtering.narrow.factory.DefaultJpaTrackableRepositoryDelegateHelper;
import com.broadleafcommerce.data.tracking.jpa.filtering.narrow.factory.JpaTrackableRepositoryDelegateHelper;
import com.tutorial.domain.MyAutoCoProduct;
import com.tutorial.repository.DefaultMyAutoCoProductRepositoryOverride;
import com.tutorial.repository.MyAutoCoProductRepositoryOverride;

import java.util.Collections;

/**
 * Setup components for the extension case.
 */
@Configuration
public class RepositoryCustomizationOverride {

    @Bean
    public JpaTrackableRepositoryDelegateHelper<MyAutoCoProduct> delegateSupplier() {
        return new DefaultJpaTrackableRepositoryDelegateHelper<>(MyAutoCoProduct.class,
                JpaProductRepository.class);
    }

    @Bean
    public MyAutoCoProductRepositoryOverride fragment(
            JpaTrackableRepositoryDelegateHelper<MyAutoCoProduct> delegateSupplier) {
        return new DefaultMyAutoCoProductRepositoryOverride(delegateSupplier);
    }

    @Bean
    public RepositoryContribution contribution(MyAutoCoProductRepositoryOverride fragment) {
        return new RepositoryContribution()
                .withBaseRepositoryInterface(JpaProductRepository.class)
                .withConcreteFragments(
                        Collections.singletonMap(MyAutoCoProductRepositoryOverride.class,
                                fragment));
    }

}
