package com.tutorial;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.broadleafcommerce.catalog.domain.product.Product;
import com.broadleafcommerce.common.extension.TypeFactory;
import com.broadleafcommerce.common.extension.projection.Projection;
import com.broadleafcommerce.microservices.AbstractStandardIT;
import com.broadleafcommerce.microservices.DefaultTestDataRoutes.TestCatalogRouted;
import com.tutorial.domain.MyAutoCoProduct;

import java.time.Instant;
import java.util.Collections;

/**
 * Confirm the extended type's properties are accessible via the {@link Projection}.
 */
@TestCatalogRouted // Notifies the system that catalog data routing will be employed during the
                   // scope of this test. This is a requirement of Broadleaf data tracking.
class ProductExtensionProjectionIT extends AbstractStandardIT {

    @Autowired
    TypeFactory typeFactory;

    @Test
    void testModel() {
        Projection<MyAutoCoProduct> projection = projection();
        MyAutoCoProduct product = projection.expose();

        assertThat(product.getModel()).isEqualTo("test");
    }

    @Test
    void testIsAllWheelDrive() {
        Projection<MyAutoCoProduct> projection = projection();
        MyAutoCoProduct product = projection.expose();

        // Throws:
        // java.lang.IllegalStateException: Unable to find a matching invocation target at MyAutoCoProduct_AutoProjectionTIpY46#isAllWheelDrive
        //	at com.broadleafcommerce.common.extension.reflection.InvocationUtils$ApiInterceptor.lambda$getBestMatchMethod$1(InvocationUtils.java:292)
        //	at java.base/java.util.Optional.orElseThrow(Optional.java:403)
        //	at com.broadleafcommerce.common.extension.reflection.InvocationUtils$ApiInterceptor.getBestMatchMethod(InvocationUtils.java:292)
        //	at com.broadleafcommerce.common.extension.reflection.InvocationUtils$ApiInterceptor.delegate(InvocationUtils.java:249)
        //	at com.tutorial.domain.MyAutoCoProduct$ByteBuddy$T3kKp89a.isAllWheelDrive(Unknown Source)
        assertThat(product.isAllWheelDrive()).isTrue();
    }

    private Projection<MyAutoCoProduct> projection() {
        Projection<MyAutoCoProduct> projection = Projection.get(MyAutoCoProduct.class);
        Product asProduct = (Product) projection;
        asProduct.setTags(Collections.singletonList("test"));
        asProduct.setName("test");
        asProduct.setSku("test");
        asProduct.setActiveStartDate(Instant.now());
        asProduct.setDefaultPrice(Money.of(12, "USD"));
        MyAutoCoProduct car = projection.expose();
        car.setModel("test");
        car.setAllWheelDrive(true);
        return projection;
    }

}
