package com.tutorial;

import static org.assertj.core.api.Assertions.assertThat;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.broadleafcommerce.catalog.service.product.ProductService;
import com.broadleafcommerce.common.extension.projection.Projection;
import com.broadleafcommerce.microservices.AbstractStandardIT;
import com.broadleafcommerce.microservices.DefaultTestDataRoutes.TestCatalogRouted;
import com.tutorial.domain.ElectricCar;
import com.tutorial.service.ElectricCarService;

import java.time.Instant;
import java.util.List;

/**
 * Confirm the extension of {@link ProductService} is registered with Spring and is effective.
 */
@TestCatalogRouted
class BusinessLogicCustomizationAutoProjectionIT extends AbstractStandardIT {

    @Autowired
    private ElectricCarService service;

    @Override
    protected void transactionalTeardown() {
        getEntityManager().createQuery("DELETE FROM JpaProduct").executeUpdate();
    }

    @Test
    void testBusinessLogicCustomizationAutoProjection() {
        service.create(projection().as(), null);
        List<Projection<ElectricCar>> cars = service.readUsingModel("test", null);
        assertThat(cars).hasSize(1).extracting("model").contains("test");
    }

    private Projection<ElectricCar> projection() {
        Projection<ElectricCar> projection = Projection.get(ElectricCar.class);
        ElectricCar car = projection.expose();
        car.setName("test");
        car.setSku("test");
        car.setActiveStartDate(Instant.now());
        car.setModel("test");
        car.setDefaultPrice(Money.of(12, "USD"));
        return projection;
    }
}
