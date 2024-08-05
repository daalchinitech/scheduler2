package com.dc.server.scheduler2.api.constants;

import com.dc.server.common.properties.PropertiesContext;

import java.util.Properties;

public class SchedulerConstants {
    private static final Properties schedulerProperties = PropertiesContext.getSchedulerProperties();

    public static String CRON_REFILL_SUGGESTION = schedulerProperties
            .getProperty("scheduler.cron.refill-suggestion");

    public static String CRON_HOURLY_MACHINE_WISE_SALE = schedulerProperties
            .getProperty("scheduler.cron.hourly.machine-wise-sale");

    public static String CRON_MACHINE_WISE_SALE = schedulerProperties
            .getProperty("scheduler.cron.machine-wise-sale");

    public static String BRAND_VM_COUNT= schedulerProperties
            .getProperty("scheduler.cron.partner.auto-brandCategory");

}
