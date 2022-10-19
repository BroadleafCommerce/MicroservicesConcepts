package com.tutorial.repository;

import com.broadleafcommerce.data.tracking.core.context.ContextInfo;
import com.broadleafcommerce.data.tracking.jpa.filtering.narrow.JpaNarrowingHelper;
import com.broadleafcommerce.data.tracking.jpa.filtering.narrow.factory.JpaTrackableRepositoryDelegateSupplier;
import com.tutorial.domain.ElectricCar;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultElectricCarRepositoryContribution implements ElectricCarRepositoryContribution {

    private final JpaTrackableRepositoryDelegateSupplier<ElectricCar> supplier;

    @PersistenceContext
    private EntityManager em;

    /**
     * Delegate to Broadleaf implementation when appropriate
     */
    @Override
    public ElectricCar save(ElectricCar entity, ContextInfo contextInfo) {
        entity.setModel(entity.getModel() + " Modified");
        return supplier.getRepository().save(entity, contextInfo);
    }

    /**
     * Or, create your own implementation
     */
    @Override
    public List<ElectricCar> findAll(ContextInfo contextInfo) {
        CriteriaQuery<ElectricCar> query =
                em.getCriteriaBuilder().createQuery(ElectricCar.class);
        Root<ElectricCar> root = query.from(ElectricCar.class);
        query.select(root);
        return supplier.getHelper().fetchAll(new JpaNarrowingHelper.JpaCriterias<>(query),
                ElectricCar.class, null, contextInfo);
    }

}
