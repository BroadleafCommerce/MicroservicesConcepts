package com.tutorial.repository;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.broadleafcommerce.catalog.provider.jpa.repository.product.JpaProductRepository;
import com.broadleafcommerce.data.tracking.core.context.ContextInfo;
import com.broadleafcommerce.data.tracking.core.policy.Policy;
import com.broadleafcommerce.data.tracking.jpa.filtering.JpaTrackableRepository;
import com.tutorial.domain.ElectricCar;

import java.util.List;

/**
 * Contribute new method signatures to {@link JpaProductRepository}. In this case, using dynamic
 * query methods without an implementation counterpart
 * (https://www.baeldung.com/spring-data-derived-queries)
 */
public interface ElectricCarAdditionRepositoryDynamicContribution {

    List<ElectricCar> findAllByModelContainingIgnoreCase(@NonNull String partialModel,
            @Nullable ContextInfo contextInfo);

}
