package com.tutorial.service;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.tutorial.domain.MyAutoCoProductProjection;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MyIntegrationService {

    AtomicInteger registrationCount = new AtomicInteger(0);

    public void register(MyAutoCoProductProjection product) {
        Assert.isTrue(MyAutoCoProductProjection.class == product.getClass(),
                "Expected an instance of MyAutoCoProductProjection");
        registrationCount.incrementAndGet();
    }

    public int getRegistrationCount() {
        return registrationCount.get();
    }

}
