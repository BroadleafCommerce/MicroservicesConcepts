package com.tutorial.domain;

import org.springframework.util.Assert;
import com.broadleafcommerce.data.tracking.core.mapping.ProjectionPostConvert;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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

    /**
     * Notice that we are not using {@code @GeneratedValue} but using the product's id as the
     * primary key. This is necessary for managing the subordinate entity in a {@code @OneToOne}
     * relationship in order for sandboxing transitions to function appropriately.
     */
    @Id
    @Column(name = "ELECTRIC_CAR_ID", nullable = false, length = CONTEXT_ID_LENGTH)
    private String id;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "ELECTRIC_CAR_ID")
    @MapsId // maps the product's id as the characteristic's primary key as well
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
