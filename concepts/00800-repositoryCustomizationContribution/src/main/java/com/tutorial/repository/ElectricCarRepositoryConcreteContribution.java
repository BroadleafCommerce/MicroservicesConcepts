package com.tutorial.repository;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.broadleafcommerce.catalog.provider.jpa.repository.product.JpaProductRepository;
import com.broadleafcommerce.data.tracking.core.context.ContextInfo;
import com.tutorial.domain.ElectricCar;

import java.util.List;

/**
 * Contribute new method signatures to {@link JpaProductRepository}. In this case, declaring a
 * signature that will be backed by a concrete implementation. See
 * {@link DefaultElectricCarRepositoryConcreteContribution}.
 */
public interface ElectricCarRepositoryConcreteContribution {

    List<ElectricCar> findUsingModel(@NonNull String model, @Nullable ContextInfo contextInfo);

}
