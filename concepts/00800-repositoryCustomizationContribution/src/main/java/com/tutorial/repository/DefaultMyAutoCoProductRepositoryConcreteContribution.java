package com.tutorial.repository;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.broadleafcommerce.data.tracking.core.context.ContextInfo;
import com.broadleafcommerce.data.tracking.jpa.filtering.narrow.JpaNarrowingHelper;
import com.broadleafcommerce.data.tracking.jpa.filtering.narrow.factory.JpaTrackableRepositoryDelegateHelper;
import com.tutorial.domain.MyAutoCoProduct;

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
public class DefaultMyAutoCoProductRepositoryConcreteContribution
        implements MyAutoCoProductRepositoryConcreteContribution {

    private final JpaTrackableRepositoryDelegateHelper<MyAutoCoProduct> helper;

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<MyAutoCoProduct> findUsingModel(@NonNull String model,
            @Nullable ContextInfo contextInfo) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<MyAutoCoProduct> query = builder.createQuery(MyAutoCoProduct.class);
        Root<MyAutoCoProduct> root = query.from(MyAutoCoProduct.class);
        query.select(root);
        Map<String, Object> params = new HashMap<>();
        query.where(builder.equal(root.get("model"), builder.parameter(String.class, "model")));
        params.put("model", model);
        return helper.getHelper().fetchAll(
                new JpaNarrowingHelper.JpaCriterias<>(query, null, params),
                MyAutoCoProduct.class, null, contextInfo);
    }

}
