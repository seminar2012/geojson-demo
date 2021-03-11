package com.demo.util;

import java.text.DecimalFormat;

/**
 * 通过角度，起始点经纬度，计算多边形的各个定点坐标
 */
public class AngleUtil {

    /**
     * 求B点经纬度
     *
     * @param A 已知点的经纬度，
     * @param distance   AB两地的距离  单位km
     * @param angle  AB连线与正北方向的夹角（0~360）
     */
    final static double Rc = 6378137;
    final static double Rj = 6356725;
    double m_LoDeg, m_LoMin, m_LoSec;
    double m_LaDeg, m_LaMin, m_LaSec;
    double m_Longitude, m_Latitude;
    double m_RadLo, m_RadLa;
    double Ec;
    double Ed;
    private final static DecimalFormat DOUBLE_FORMAT = new DecimalFormat("#.000000");

    public AngleUtil(double longitude, double latitude) {
        m_LoDeg = (int) longitude;
        m_LoMin = (int) ((longitude - m_LoDeg) * 60);
        m_LoSec = (longitude - m_LoDeg - m_LoMin / 60.) * 3600;

        m_LaDeg = (int) latitude;
        m_LaMin = (int) ((latitude - m_LaDeg) * 60);
        m_LaSec = (latitude - m_LaDeg - m_LaMin / 60.) * 3600;

        m_Longitude = longitude;
        m_Latitude = latitude;
        m_RadLo = longitude * Math.PI / 180.;
        m_RadLa = latitude * Math.PI / 180.;
        Ec = Rj + (Rc - Rj) * (90. - m_Latitude) / 90.;
        Ed = Ec * Math.cos(m_RadLa);
    }

    /**
     * @Return:坐标 B点的经纬度
     */
    public static String getMyLatLng(AngleUtil A, double distance, double angle) {
        double dx = distance * 1000 * Math.sin(Math.toRadians(angle));
        double dy = distance * 1000 * Math.cos(Math.toRadians(angle));
        double bjd = (dx / A.Ed + A.m_RadLo) * 180. / Math.PI;
        double bwd = (dy / A.Ec + A.m_RadLa) * 180. / Math.PI;
        bjd = Double.parseDouble(DOUBLE_FORMAT.format(bjd));
        bwd = Double.parseDouble(DOUBLE_FORMAT.format(bwd));
        String lnglat = "[" + bjd + "," + bwd + "]";
        return lnglat;
    }

    /**
     * 获取两点坐标距离(米)
     *
     * @param lng1 起始经度
     * @param lat1 起始纬度
     * @param lng2 目地地经度
     * @param lat2 目的地纬度
     * @return
     */
    public static int getDistance(double lng1, double lat1, double lng2, double lat2) {
            /*double x, y, distance;
            x = (lon2 - lon1) * PI * R * Math.cos(((lat1 + lat2) / 2) * PI / 180) / 180;
            y = (lat2 - lat1) * PI * R / 180;
            distance = Math.hypot(x, y);
            return (int) (distance + 0.5);*/
        double dx = lng1 - lng2; // 经度差值
        double dy = lat1 - lat2; // 纬度差值
        double b = (lat1 + lat2) / 2.0; // 平均纬度
        double Lx = Math.toRadians(dx) * Rc * Math.cos(Math.toRadians(b)); // 东西距离
        double Ly = Rc * Math.toRadians(dy); // 南北距离
        return (int) Math.sqrt(Lx * Lx + Ly * Ly);
    }

    /**
     * 生成多边形经纬度 Coordinates
     *
     * @param longitude  经度
     * @param latitude   维度
     * @param linelength 距离（单位：km）
     * @param brim       几边形
     * @return
     */
    public static String getLongAndLatString(double longitude, double latitude, double linelength, int brim) {
        int angle = 0;
        StringBuffer sb = new StringBuffer();
        String myLatLng = "";
        if (brim > 0) {
            angle = 360 / brim;
            AngleUtil angleUtil = new AngleUtil(longitude, latitude);
            for (int i = 0; i < brim; i++) {
                myLatLng = AngleUtil.getMyLatLng(angleUtil, linelength, angle * (i + 1));
                sb.append(myLatLng + ",");
            }
        } else {
            sb.append("请输入正确的参数！！！");
        }
        String longAndLats = sb.toString();
        return longAndLats.substring(0, longAndLats.length() - 1);
    }

    public static void main(String[] args) {
        double longitude = 120.04022;
        double latitude = 30.37818;
        double linelength = 3;
        int angle = 6;
        String longAndLatString = AngleUtil.getLongAndLatString(longitude, latitude, linelength, angle);
        System.out.println(longAndLatString);
    }
}