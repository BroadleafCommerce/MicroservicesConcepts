package com.tutorial;

import org.springframework.context.annotation.Configuration;

import com.broadleafcommerce.common.extension.data.DataRoutes;
import com.broadleafcommerce.data.tracking.jpa.filtering.auto.EnableJpaTrackableFlow;
import com.tutorial.domain.ChargingStation;

/**
 * Setup components for the customization case.
 */
@Configuration
@EnableJpaTrackableFlow(
        entityClass = ChargingStation.class,
        routeKey = DataRoutes.CATALOG) // Auto establishes all plumbing for this domain
public class NewDomainOnly {}
