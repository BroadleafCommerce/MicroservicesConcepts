package com.tutorial;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.broadleafcommerce.catalog.service.product.ProductService;
import com.broadleafcommerce.common.extension.projection.Projection;
import com.broadleafcommerce.common.jpa.autoconfigure.AutoConfigureTestDb;
import com.tutorial.domain.ElectricCar;
import com.tutorial.service.ElectricCarService;

import java.time.Instant;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Confirm the extension of {@link ProductService} is registered with Spring and is effective. This
 * example focuses on basic customization of service business logic using out-of-the-box domain and
 * repository.
 */
@SpringBootTest
@AutoConfigureTestDb
@TestPropertySource(properties = "broadleaf.default.data.route=catalog")
class BusinessLogicCustomizationAutoProjectionIT {

    @Autowired
    private ElectricCarService service;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TransactionTemplate template;

    @AfterEach
    private void tearDown() {
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                em.createQuery("DELETE FROM JpaProduct").executeUpdate();
            }
        });
    }

    @Test
    void testRepositoryOverride() {
        Projection<ElectricCar> projection = Projection.get(ElectricCar.class);
        ElectricCar car = projection.expose();
        car.setName("test");
        car.setSku("test");
        car.setActiveStartDate(Instant.now());
        car.setModel("test");
        car.setDefaultPrice(Money.of(12, "USD"));
        service.create(projection.as(), null);
        List<Projection<ElectricCar>> cars = service.readUsingModel("test", null);
        assertThat(cars).hasSize(1).extracting("model").contains("test");
    }

}
