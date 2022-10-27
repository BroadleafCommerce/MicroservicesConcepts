package com.tutorial.repository;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.broadleafcommerce.catalog.provider.jpa.repository.product.JpaProductRepository;
import com.broadleafcommerce.data.tracking.core.Trackable;
import com.broadleafcommerce.data.tracking.core.context.ContextInfo;
import com.broadleafcommerce.data.tracking.core.policy.Policy;
import com.broadleafcommerce.data.tracking.jpa.filtering.JpaTrackableRepository;

import java.util.List;

/**
 * Declare method signatures to override from {@link JpaProductRepository}. In this case, the
 * overrides are focused on methods inherited from {@link JpaTrackableRepository}. Note, these
 * method declarations automatically inherit any {@link Policy} behavior declared on the original
 * (e.g. {@code JpaTrackableRepository}).
 */
public interface ElectricCarRepositoryOverride {

    /**
     * Serves as override for {@link JpaTrackableRepository#save(Trackable, ContextInfo)}
     */
    Trackable save(@NonNull Trackable entity, @Nullable ContextInfo contextInfo);

    /**
     * Serves as an override for {@link JpaTrackableRepository#findAll(ContextInfo)}
     */
    List<Trackable> findAll(@Nullable ContextInfo contextInfo);

}
