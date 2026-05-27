package com.sgarden.util;

public final class AlertConstants {

    private AlertConstants() {}

    public static final int DEFAULT_THRESHOLD = 10;

    public static final String SEVERITY_CRITICAL = "critical";
    public static final String SEVERITY_WARNING = "warning";
    public static final String SEVERITY_INFO = "info";

    public static final double CRITICAL_RATIO = 0.25;
    public static final double WARNING_RATIO = 0.5;
}
