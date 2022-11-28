package com.tutorial;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.broadleafcommerce.catalog.provider.jpa.repository.product.JpaProductRepository;
import com.broadleafcommerce.microservices.AbstractStandardIT;
import com.broadleafcommerce.microservices.DefaultTestDataRoutes.TestCatalogRouted;
import com.tutorial.domain.MyAutoCoProduct;
import com.tutorial.repository.MyAutoCoProductRepositoryConcreteContribution;
import com.tutorial.repository.MyAutoCoProductRepositoryDynamicContribution;

import java.util.List;

import io.azam.ulidj.ULID;

/**
 * Confirm the contribution fragments are registered with {@link JpaProductRepository} and that they
 * are effective.
 */
@TestCatalogRouted // Notifies the system that catalog data routing will be employed during the
                   // scope of this test. This is a requirement of Broadleaf data tracking.
class RepositoryCustomizationContributionIT extends AbstractStandardIT {

    @Autowired
    protected JpaProductRepository<MyAutoCoProduct> repo;

    @Override
    protected void transactionalTeardown() {
        getEntityManager().createQuery("DELETE FROM MyAutoCoProduct").executeUpdate();
    }

    @Test
    void testRepositoryOverride() {
        MyAutoCoProduct car = new MyAutoCoProduct();
        car.setContextId(ULID.random());
        car.setModel("test");
        repo.save(car, null);

        List<MyAutoCoProduct> cars =
                ((MyAutoCoProductRepositoryConcreteContribution) repo).findUsingModel("test", null);
        assertThat(cars).hasSize(1).extracting("model").contains("test");

        cars = ((MyAutoCoProductRepositoryDynamicContribution) repo)
                .findAllByModelContainingIgnoreCase("te", null);
        assertThat(cars).hasSize(1).extracting("model").contains("test");
    }

}
