package com.tutorial.repository;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.broadleafcommerce.data.tracking.core.context.ContextInfo;
import com.broadleafcommerce.data.tracking.jpa.filtering.narrow.JpaNarrowingHelper;
import com.broadleafcommerce.data.tracking.jpa.filtering.narrow.factory.JpaTrackableRepositoryDelegateSupplier;
import com.tutorial.domain.ElectricCar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultElectricCarRepositoryConcreteContribution
        implements ElectricCarRepositoryConcreteContribution {

    private final JpaTrackableRepositoryDelegateSupplier<ElectricCar> supplier;

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ElectricCar> findUsingModel(@NonNull String model,
            @Nullable ContextInfo contextInfo) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<ElectricCar> query = builder.createQuery(ElectricCar.class);
        Root<ElectricCar> root = query.from(ElectricCar.class);
        query.select(root);
        Map<String, Object> params = new HashMap<>();
        query.where(builder.equal(root.get("model"), builder.parameter(String.class, "model")));
        params.put("model", model);
        return supplier.getHelper().fetchAll(
                new JpaNarrowingHelper.JpaCriterias<>(query, null, params),
                ElectricCar.class, null, contextInfo);
    }

}
