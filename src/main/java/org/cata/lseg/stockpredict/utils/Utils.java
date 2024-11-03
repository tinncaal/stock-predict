package org.cata.lseg.stockpredict.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Utils {
    private Utils() {}

    public static double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();

        /* variant 1
        return Precision.round(value, 2);
        */

        /* variant 2
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(value));
         */
    }

    public static double meanOfSubArray(double[] data, int start, int end) {
        var sum = 0.0;
        for (var i = start; i < end; i++) {
            sum += data[i];
        }

        return sum / (end - start);
    }
}
