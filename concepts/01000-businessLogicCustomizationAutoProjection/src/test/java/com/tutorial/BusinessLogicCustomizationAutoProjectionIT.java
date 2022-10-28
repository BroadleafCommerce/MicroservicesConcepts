package com.tutorial;

import static org.assertj.core.api.Assertions.assertThat;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.broadleafcommerce.catalog.domain.product.Product;
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
@TestCatalogRouted // Notifies the system that catalog data routing will be employed during the
                   // scope of this test. This is a requirement of Broadleaf data tracking.
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
        Product asProduct = (Product) projection;
        asProduct.setName("test");
        asProduct.setSku("test");
        asProduct.setActiveStartDate(Instant.now());
        asProduct.setDefaultPrice(Money.of(12, "USD"));
        ElectricCar car = projection.expose();
        car.setModel("test");
        return projection;
    }
}
