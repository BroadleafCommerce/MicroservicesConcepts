package com.tutorial;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.broadleafcommerce.catalog.provider.jpa.repository.product.JpaProductRepository;
import com.broadleafcommerce.microservices.AbstractStandardIT;
import com.broadleafcommerce.microservices.DefaultTestDataRoutes.TestCatalogRouted;
import com.tutorial.domain.ElectricCar;
import com.tutorial.repository.ElectricCarRepositoryConcreteContribution;
import com.tutorial.repository.ElectricCarRepositoryDynamicContribution;

import java.util.List;

import io.azam.ulidj.ULID;

/**
 * Confirm the contribution fragments are registered with {@link JpaProductRepository} and that they
 * are effective.
 */
@TestCatalogRouted
class RepositoryCustomizationContributionIT extends AbstractStandardIT {

    @Autowired
    protected JpaProductRepository<ElectricCar> repo;

    @Override
    protected void transactionalTeardown() {
        getEntityManager().createQuery("DELETE FROM ElectricCar").executeUpdate();
    }

    @Test
    void testRepositoryOverride() {
        ElectricCar car = new ElectricCar();
        car.setContextId(ULID.random());
        car.setModel("test");
        repo.save(car, null);

        List<ElectricCar> cars =
                ((ElectricCarRepositoryConcreteContribution) repo).findUsingModel("test", null);
        assertThat(cars).hasSize(1).extracting("model").contains("test");

        cars = ((ElectricCarRepositoryDynamicContribution) repo)
                .findAllByModelContainingIgnoreCase("te", null);
        assertThat(cars).hasSize(1).extracting("model").contains("test");
    }

}
