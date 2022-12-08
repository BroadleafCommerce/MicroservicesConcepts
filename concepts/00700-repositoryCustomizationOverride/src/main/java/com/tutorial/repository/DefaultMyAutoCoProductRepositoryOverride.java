package com.tutorial.repository;

import org.springframework.transaction.annotation.Transactional;

import com.broadleafcommerce.data.tracking.core.Trackable;
import com.broadleafcommerce.data.tracking.core.context.ContextInfo;
import com.broadleafcommerce.data.tracking.jpa.filtering.narrow.JpaNarrowingHelper;
import com.broadleafcommerce.data.tracking.jpa.filtering.narrow.factory.JpaTrackableRepositoryDelegateHelper;
import com.tutorial.domain.MyAutoCoProduct;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultMyAutoCoProductRepositoryOverride
        implements MyAutoCoProductRepositoryOverride {

    private final JpaTrackableRepositoryDelegateHelper<MyAutoCoProduct> helper;

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
        ((MyAutoCoProduct) entity).setModel(((MyAutoCoProduct) entity).getModel() + " Modified");
        return helper.getRepository().save((MyAutoCoProduct) entity, contextInfo);
    }

    /**
     * Or, create your own override implementation
     */
    @Override
    public List<Trackable> findAll(ContextInfo contextInfo) {
        CriteriaQuery<MyAutoCoProduct> query =
                em.getCriteriaBuilder().createQuery(MyAutoCoProduct.class);
        Root<MyAutoCoProduct> root = query.from(MyAutoCoProduct.class);
        query.select(root);
        return helper.getHelper().fetchAll(new JpaNarrowingHelper.JpaCriterias<>(query),
                MyAutoCoProduct.class, null, contextInfo);
    }

}
