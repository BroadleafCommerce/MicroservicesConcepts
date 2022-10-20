package com.tutorial;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.broadleafcommerce.catalog.provider.jpa.repository.product.JpaProductRepository;
import com.broadleafcommerce.common.jpa.autoconfigure.AutoConfigureTestDb;
import com.broadleafcommerce.data.tracking.core.type.TrackingLevel;
import com.broadleafcommerce.data.tracking.jpa.filtering.domain.CatalogJpaTracking;
import com.tutorial.domain.ElectricCar;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import io.azam.ulidj.ULID;

/**
 * Confirm the override fragments are registered with {@link JpaProductRepository} and that they are
 * effective.
 */
@SpringBootTest
@AutoConfigureTestDb
@AutoConfigureMockMvc
@TestPropertySource(properties = "broadleaf.default.data.route=catalog")
class RepositoryCustomizationOverrideIT {

    @Autowired
    protected JpaProductRepository<ElectricCar> repo;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TransactionTemplate template;

    @AfterEach
    private void tearDown() {
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                em.createQuery("DELETE FROM ElectricCar").executeUpdate();
            }
        });
    }

    @Test
    void testRepositoryOverride() {
        ElectricCar car = new ElectricCar();
        car.setContextId(ULID.random());
        car.setModel("test");
        CatalogJpaTracking tracking = new CatalogJpaTracking();
        tracking.setLevel(TrackingLevel.PRODUCTION.getLevel());
        car.setTracking(tracking);
        repo.save(car, null);

        List<ElectricCar> cars = repo.findAll(null);
        assertThat(cars).hasSize(1).extracting("model").contains("test Modified");
    }

}
