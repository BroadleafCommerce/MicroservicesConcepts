package com.tutorial.domain;

import com.broadleafcommerce.common.extension.RequestView;
import com.broadleafcommerce.common.extension.ResponseView;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonView({RequestView.class, ResponseView.class})
@Slf4j
@Data
public class ChargingStationProjection {

    private String id; // Naming 'id' is a DataTracking requirement

    @JsonView(ResponseView.class)
    private String address1;

    @JsonView(ResponseView.class)
    private String address2;

    @JsonView(ResponseView.class)
    private String city;

    @JsonView(ResponseView.class)
    private String state;

    @JsonView(ResponseView.class)
    private String zipcode;

    private List<PricingProjection> pricing = new ArrayList<>();

    private Map<String, List<Integer>> wattageByConnectorType = new LinkedHashMap<>();

}
