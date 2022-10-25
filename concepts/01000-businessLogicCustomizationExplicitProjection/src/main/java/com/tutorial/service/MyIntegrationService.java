package com.tutorial.service;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.broadleafcommerce.catalog.domain.product.Product;
import com.tutorial.projection.ElectricCarProjection;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MyIntegrationService {

    AtomicInteger registrationCount = new AtomicInteger(0);

    public void register(ElectricCarProjection product) {
        Assert.isTrue(ElectricCarProjection.class == product.getClass(),
                "Expected an instance of ElectricCarProjection");
        registrationCount.incrementAndGet();
    }

    public int getRegistrationCount() {
        return registrationCount.get();
    }

}
