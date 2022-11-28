package com.tutorial.domain;

import static com.broadleafcommerce.common.jpa.JpaConstants.CONTEXT_ID_LENGTH;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;

import com.broadleafcommerce.common.extension.mapping.ModelMapperConfigurationHelper;
import com.broadleafcommerce.common.jpa.JpaConstants;
import com.broadleafcommerce.common.jpa.converter.AbstractListConverter;
import com.broadleafcommerce.data.tracking.core.TenantTrackable;
import com.broadleafcommerce.data.tracking.core.mapping.BusinessTypeAware;
import com.broadleafcommerce.data.tracking.core.mapping.ModelMapperMappable;
import com.broadleafcommerce.data.tracking.jpa.filtering.TrackingListener;
import com.broadleafcommerce.data.tracking.jpa.filtering.domain.TenantJpaTracking;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * New domain (not extending from Broadleaf domain). Represents a charging location for electric
 * cars (see {@code ElectricCar} domain in earlier tutorial). This implementation includes and
 * explicit projection and explicit mapping to/from hook points.
 */
@Entity
@Table(name = "CHARGING_STATION")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode
@ToString
@EntityListeners(TrackingListener.class) // Broadleaf DataTracking requirement
public class ChargingStation implements
        Serializable,
        TenantTrackable<TenantJpaTracking>, // DataTracking requirement
        ModelMapperMappable, // Provide the toMe and fromMe implementations for ModelMapper
        BusinessTypeAware { // Report the explicit projection type

    @Id
    @GeneratedValue(generator = "blcid")
    @GenericGenerator(name = "blcid", strategy = "blcid")
    @Type(type = "com.broadleafcommerce.data.tracking.jpa.hibernate.ULidType")
    @Column(name = "ID", nullable = false, length = CONTEXT_ID_LENGTH)
    private String contextId;

    @Column(name = "ADDRESS_1")
    private String address1;

    @Column(name = "ADDRESS_2")
    private String address2;

    @Column(name = "CITY")
    private String city;

    @Column(name = "STATE")
    private String state;

    @Column(name = "ZIPCODE")
    private String zipcode;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER,
            mappedBy = "chargingStation")
    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    private List<Charger> chargers = new ArrayList<>();

    @Column(name = "PRICING", length = JpaConstants.MEDIUM_TEXT_LENGTH)
    @Convert(converter = PricingConverter.class)
    private List<Pricing> pricing = new ArrayList<>();

    // Broadleaf DataTracking requirement - matches Trackable interface generics

    @Embedded
    private TenantJpaTracking tracking;

    @Override
    public Class<?> getBusinessDomainType() {
        return ChargingStationProjection.class;
    }

    @Override
    public ModelMapper fromMe() {
        ModelMapper mapper = ModelMapperConfigurationHelper.create();
        // Handle the synthetic map as a post converter
        Converter<ChargingStation, ChargingStationProjection> postConverter = context -> {
            ChargingStation source = context.getSource();
            ChargingStationProjection destination = context.getDestination();
            Optional.ofNullable(source.getContextId()).ifPresent(destination::setId);
            Map<String, List<Integer>> byConnector = new LinkedHashMap<>();
            for (Charger charger : source.getChargers()) {
                byConnector.computeIfAbsent(charger.getConnectorType(), key -> new ArrayList<>())
                        .add(charger.getWatts());
            }
            destination.setWattageByConnectorType(byConnector);
            return destination;
        };
        mapper.createTypeMap(ChargingStation.class, ChargingStationProjection.class)
                .setPostConverter(postConverter);
        return mapper;
    }

    @Override
    public ModelMapper toMe() {
        ModelMapper mapper = ModelMapperConfigurationHelper.create();
        // Handle the synthetic map as a post converter
        Converter<ChargingStationProjection, ChargingStation> postConverter = context -> {
            ChargingStationProjection source = context.getSource();
            ChargingStation destination = context.getDestination();
            Optional.ofNullable(source.getId()).ifPresent(destination::setContextId);
            List<Charger> chargerList = destination.getChargers();
            chargerList.clear();
            source.getWattageByConnectorType().forEach((key, value) -> value.forEach(wattage -> {
                Charger charger = new Charger();
                charger.setChargingStation(destination);
                charger.setWatts(wattage);
                charger.setConnectorType(key);
                chargerList.add(charger);
            }));
            return destination;
        };
        mapper.createTypeMap(ChargingStationProjection.class, ChargingStation.class)
                .setPostConverter(postConverter);
        return mapper;
    }

    public static class PricingConverter extends AbstractListConverter<Pricing> {
        public PricingConverter(
                @Nullable @Qualifier("converterObjectMapper") ObjectMapper objectMapper) {
            super(Pricing.class, objectMapper);
        }
    }

}
