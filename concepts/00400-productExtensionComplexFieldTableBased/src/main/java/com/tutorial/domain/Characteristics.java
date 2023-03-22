package com.tutorial.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.util.Assert;
import com.broadleafcommerce.data.tracking.core.mapping.ProjectionPostConvert;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.broadleafcommerce.common.jpa.JpaConstants.CONTEXT_ID_LENGTH;

@Entity
@Table(name = "CHARACTERISTICS")
@Inheritance(strategy = InheritanceType.JOINED)
@ToString
@EqualsAndHashCode(exclude = {"id", "car"})
public class Characteristics {

    @Id
    @GeneratedValue(generator = "blcid")
    @GenericGenerator(name = "blcid", strategy = "blcid")
    @Type(type = "com.broadleafcommerce.data.tracking.jpa.hibernate.ULidType")
    @Column(name = "ID", nullable = false, length = CONTEXT_ID_LENGTH)
    private String id;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "ELECTRIC_CAR_ID")
    @ToString.Exclude // Avoid infinite recursion in toString()
    @JsonIgnore // Avoid infinite recursion in data tracking
    private MyAutoCoProduct car;

    @Column(name = "MODEL")
    @Getter
    @Setter
    private String model;

    @Column(name = "LENGTH")
    @Getter
    @Setter
    private BigDecimal length;

    @Column(name = "WIDTH")
    @Getter
    @Setter
    private BigDecimal width;

    @Column(name = "HEIGHT")
    @Getter
    @Setter
    private BigDecimal height;

    @Column(name = "WEIGHT")
    @Getter
    @Setter
    private BigDecimal weight;

    @ProjectionPostConvert // called after mapping of Projection<MyAutoCoProduct> to MyAutoCoProduct
    // is complete
    public void postConvert(Object source, Object parent) {
        Assert.isTrue(parent instanceof MyAutoCoProduct, "Expected an instance of MyAutoCoProduct");
        this.car = (MyAutoCoProduct) parent;
        this.model = ((MyAutoCoProduct) parent).getModel();
    }

}
