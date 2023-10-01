package com.flightcomputer.mecc.utils;

import java.math.BigDecimal;

public class Normalizer {
    public static Float normalize(Number number, Number max) {
        float n = new BigDecimal(number.toString()).floatValue();
        float ma = new BigDecimal(max.toString()).floatValue();
        if (ma == 0) return 0.0F;
        return n / ma;
    }

    public static Float normalizeNegative(Number number, Number max, Number min) {
        Float n = new BigDecimal(number.toString()).floatValue();
        Float mi = new BigDecimal(min.toString()).floatValue();
        Float ma = new BigDecimal(max.toString()).floatValue();

        return (n - mi) / (ma - mi) * 2 - 1;

    }
}
