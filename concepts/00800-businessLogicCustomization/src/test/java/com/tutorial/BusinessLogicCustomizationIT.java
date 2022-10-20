package com.tutorial;

import static org.assertj.core.api.Assertions.assertThat;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.broadleafcommerce.catalog.domain.product.Product;
import com.broadleafcommerce.catalog.service.product.ProductService;
import com.broadleafcommerce.common.jpa.autoconfigure.AutoConfigureTestDb;
import com.tutorial.service.ExtendedProductService;
import com.tutorial.service.MyIntegrationService;

import java.time.Instant;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Confirm the extension of {@link ProductService} is registered with Spring and is effective. This
 * example focuses on basic customization of service business logic using out-of-the-box domain and
 * repository.
 */
@SpringBootTest
@AutoConfigureTestDb
@TestPropertySource(properties = "broadleaf.default.data.route=catalog")
class BusinessLogicCustomizationIT {

    @Autowired
    private ExtendedProductService service;

    @Autowired
    private MyIntegrationService integrationService;

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
        Product product = new Product();
        product.setName("test");
        product.setSku("test");
        product.setActiveStartDate(Instant.now());
        product.setDefaultPrice(Money.of(12, "USD"));
        service.create(product, null);

        assertThat(integrationService.getRegistrationCount()).isEqualTo(1);
    }

}
