package com.tutorial.repository;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import com.broadleafcommerce.data.tracking.core.Trackable;
import com.broadleafcommerce.data.tracking.core.context.ContextInfo;
import com.broadleafcommerce.data.tracking.jpa.filtering.JpaTrackableRepository;
import com.tutorial.domain.ElectricCar;
import java.util.List;

public interface ElectricCarRepositoryContribution {

    /**
     * Serves as override for {@link JpaTrackableRepository#save(Trackable, ContextInfo)}
     */
    ElectricCar save(@NonNull ElectricCar entity, @Nullable ContextInfo contextInfo);

    /**
     * Serves as an override for {@link JpaTrackableRepository#findAll(ContextInfo)}
     */
    List<ElectricCar> findAll(@Nullable ContextInfo contextInfo);

}
