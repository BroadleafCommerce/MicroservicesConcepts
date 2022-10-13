package com.broadleafdemo.domain;

import com.broadleafcommerce.catalog.provider.jpa.domain.product.JpaProduct;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "MY_PRODUCT_EXTENSION")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public class JpaProductExtension extends JpaProduct {

    @Column(name = "COLOR")
    private String color;

}
