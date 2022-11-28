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
import com.tutorial.domain.MyAutoCoProduct;
import com.tutorial.service.MyAutoCoProductService;

import java.time.Instant;
import java.util.List;

/**
 * Confirm the extension of {@link ProductService} is registered with Spring and is effective.
 */
@TestCatalogRouted // Notifies the system that catalog data routing will be employed during the
                   // scope of this test. This is a requirement of Broadleaf data tracking.
class BusinessLogicCustomizationAutoProjectionIT extends AbstractStandardIT {

    @Autowired
    private MyAutoCoProductService service;

    @Override
    protected void transactionalTeardown() {
        getEntityManager().createQuery("DELETE FROM JpaProduct").executeUpdate();
    }

    @Test
    void testBusinessLogicCustomizationAutoProjection() {
        service.create(projection().as(), null);
        List<Projection<MyAutoCoProduct>> cars = service.readUsingModel("test", null);
        assertThat(cars).hasSize(1).extracting("model").contains("test");
    }

    private Projection<MyAutoCoProduct> projection() {
        Projection<MyAutoCoProduct> projection = Projection.get(MyAutoCoProduct.class);
        Product asProduct = (Product) projection;
        asProduct.setName("test");
        asProduct.setSku("test");
        asProduct.setActiveStartDate(Instant.now());
        asProduct.setDefaultPrice(Money.of(12, "USD"));
        MyAutoCoProduct car = projection.expose();
        car.setModel("test");
        return projection;
    }
}
