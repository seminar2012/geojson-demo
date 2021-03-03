package com.demo.util;

import lombok.extern.slf4j.Slf4j;

/****************************
 * Class Name： LagrangeUtil
 * Description： <拉格朗日插值算法>
 * @Author: seminar
 * @create: 2020/12/10
 * @since: 1.0.0
 ***************************/
@Slf4j
public class LagrangeUtil {

    private static double[] Lag(double x[], double y[], double x0[]) {
        int m = x.length;
        int n = x0.length;
        double y0[] = new double[n];
        for (int ia = 0; ia < n; ia++) {
            double j = 0;
            for (int ib = 0; ib < m; ib++) {
                double k = 1;
                for (int ic = 0; ic < m; ic++) {
                    if (ib != ic) {
                        k = k * (x0[ia] - x[ic]) / (x[ib] - x[ic]);
                    }
                }
                k = k * y[ib];
                j = j + k;
            }
            y0[ia] = j;
        }
        return y0;
    }

    public static double[] lagDouble(long x[], double y[], long x0[]) {
        int m = x.length;
        int n = x0.length;
        double y0[] = new double[n];
        for (int ia = 0; ia < n; ia++) {
            double j = 0;
            for (int ib = 0; ib < m; ib++) {
                double k = 1;
                for (int ic = 0; ic < m; ic++) {
                    if (ib != ic) {
                        k = k * (x0[ia] - x[ic]) / (x[ib] - x[ic]);
                    }
                }
                k = k * y[ib];
                j = j + k;
            }
            y0[ia] = j;
        }
        return y0;
    }

    public static void main(String[] args) {
        long[] gpsTimeArr = new long[]{1607395389600L, 1607395389640L};
        long[] gpsTimeExpectArr = new long[]{1607395389600L, 1607395389640L, 1607395389633L};
        double[] lonArr = new double[]{22.5433462d, 22.5433463d};
        double[] lonRes = LagrangeUtil.lagDouble(gpsTimeArr, lonArr, gpsTimeExpectArr);

        log.info("原始数据:");
        for (int i = 0; i < gpsTimeArr.length; i++) {
            log.info("x0: {},y0: {}", gpsTimeArr[i], lonArr[i]);
        }
        log.info("运用拉格朗日插值法求解得:");
        for (int i = 0; i < gpsTimeExpectArr.length; i++) {
            log.info("x0: {},y0: {}", gpsTimeExpectArr[i], lonRes[i]);
            log.info("x0: {},y0: {}", gpsTimeExpectArr[i], Double.parseDouble(String.format("%.7f", lonRes[i])));
        }
    }
}
