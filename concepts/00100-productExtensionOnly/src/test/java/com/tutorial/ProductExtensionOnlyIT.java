package com.tutorial;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import com.broadleafcommerce.catalog.provider.jpa.repository.product.JpaProductRepository;
import com.broadleafcommerce.common.jpa.autoconfigure.AutoConfigureTestDb;
import com.broadleafcommerce.data.tracking.core.type.TrackingLevel;
import com.broadleafcommerce.data.tracking.jpa.filtering.domain.CatalogJpaTracking;
import com.tutorial.domain.ElectricCar;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import io.azam.ulidj.ULID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDb
class ProductExtensionOnlyIT {

    @Autowired
    private JpaProductRepository<ElectricCar> repo;

    @PersistenceContext(unitName = "catalog")
    private EntityManager em;

    @Autowired
    @Qualifier("catalogTransactionTemplate")
    private TransactionTemplate template;

    private String id;

    @BeforeEach
    private void init() {
        ElectricCar car = new ElectricCar();
        car.setContextId(ULID.random());
        car.setModel("test");
        CatalogJpaTracking tracking = new CatalogJpaTracking();
        tracking.setLevel(TrackingLevel.PRODUCTION.getLevel());
        car.setTracking(tracking);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                em.persist(car);
                id = car.getContextId();
            }
        });
    }

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
    void testDomainExtension() {
        ElectricCar car = repo.findByContextId(id, null).orElseThrow();
        assertThat(car.getModel()).isEqualTo("test");
    }

}
