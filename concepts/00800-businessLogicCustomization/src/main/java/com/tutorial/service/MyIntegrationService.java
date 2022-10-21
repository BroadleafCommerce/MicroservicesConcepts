package com.tutorial.service;

import org.springframework.stereotype.Component;
import com.broadleafcommerce.catalog.domain.product.Product;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MyIntegrationService {

    AtomicInteger registrationCount = new AtomicInteger(0);

    public void register(Product product) {
        registrationCount.incrementAndGet();
    }

    public int getRegistrationCount() {
        return registrationCount.get();
    }

}
