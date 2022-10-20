package com.tutorial.service;

import com.broadleafcommerce.catalog.domain.product.Product;
import java.util.concurrent.atomic.AtomicInteger;

public class MyIntegrationService {

    AtomicInteger registrationCount = new AtomicInteger(0);

    public void register(Product product) {
        registrationCount.incrementAndGet();
    }

    public int getRegistrationCount() {
        return registrationCount.get();
    }

}
