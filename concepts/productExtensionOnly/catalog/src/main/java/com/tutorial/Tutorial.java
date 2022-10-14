package com.tutorial;

import org.springframework.context.annotation.Configuration;
import com.broadleafcommerce.catalog.provider.jpa.domain.product.JpaProduct;
import com.broadleafcommerce.common.jpa.data.entity.JpaEntityScan;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import lombok.Data;

import static com.broadleafcommerce.catalog.provider.RouteConstants.Persistence.CATALOG_ROUTE_PACKAGE;

public class Tutorial {

    @Configuration
    @JpaEntityScan(basePackages = "com.tutorial", routePackage = CATALOG_ROUTE_PACKAGE)
    public static class Config {}

    @Entity
    @Table(name = "MY_PRODUCT_EXTENSION")
    @Inheritance(strategy = InheritanceType.JOINED)
    @Data
    public static class JpaProductExtension extends JpaProduct {

        @Column(name = "COLOR")
        private String color;

    }

}
