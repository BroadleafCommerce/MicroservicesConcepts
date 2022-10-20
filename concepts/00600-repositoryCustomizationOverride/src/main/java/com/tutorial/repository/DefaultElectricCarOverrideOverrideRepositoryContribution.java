package com.tutorial.repository;

import org.springframework.transaction.annotation.Transactional;
import com.broadleafcommerce.data.tracking.core.Trackable;
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
public class DefaultElectricCarOverrideOverrideRepositoryContribution
        implements ElectricCarOverrideRepositoryContribution {

    private final JpaTrackableRepositoryDelegateSupplier<ElectricCar> supplier;

    @PersistenceContext
    private EntityManager em;

    /**
     * Delegate to Broadleaf implementation when appropriate. Need to re-declare the
     * {@link Transactional} annotation for the persistence - even if it was already declared on the
     * original method.
     */
    @Override
    @Transactional
    public Trackable save(Trackable entity, ContextInfo contextInfo) {
        ((ElectricCar) entity).setModel(((ElectricCar) entity).getModel() + " Modified");
        return supplier.getRepository().save((ElectricCar) entity, contextInfo);
    }

    /**
     * Or, create your own override implementation
     */
    @Override
    public List<Trackable> findAll(ContextInfo contextInfo) {
        CriteriaQuery<ElectricCar> query =
                em.getCriteriaBuilder().createQuery(ElectricCar.class);
        Root<ElectricCar> root = query.from(ElectricCar.class);
        query.select(root);
        return supplier.getHelper().fetchAll(new JpaNarrowingHelper.JpaCriterias<>(query),
                ElectricCar.class, null, contextInfo);
    }

}
