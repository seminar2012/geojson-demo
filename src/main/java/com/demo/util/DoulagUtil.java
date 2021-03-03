package com.demo.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/*************************************
 *Class Name:Doulag
 *Description:<道格拉斯普克抽稀>
 *@author:Seminar
 *@create:2021/1/14
 *@since 1.0.0
 *************************************/
@Slf4j
public class DoulagUtil {

    /**
     * 计算两点距离
     *
     * @param point1
     * @param point2
     * @return
     */
    private static double calculationDistance(double[] point1, double[] point2) {
        double lat1 = point1[0];
        double lat2 = point2[0];
        double lng1 = point1[1];
        double lng2 = point2[1];
        double radLat1 = lat1 * Math.PI / 180.0;
        double radLat2 = lat2 * Math.PI / 180.0;
        double a = radLat1 - radLat2;
        double b = (lng1 * Math.PI / 180.0) - (lng2 * Math.PI / 180.0);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        return s * 6370996.81;

    }

    /**
     * 计算点pX到点pA和pB所确定的直线的距离
     *
     * @param start
     * @param end
     * @param center
     * @return
     */
    private static double distToSegment(double[] start, double[] end, double[] center) {
        double a = Math.abs(calculationDistance(start, end));
        double b = Math.abs(calculationDistance(start, center));
        double c = Math.abs(calculationDistance(end, center));
        double p = (a + b + c) / 2.0;
        double s = Math.sqrt(Math.abs(p * (p - a) * (p - b) * (p - c)));
        return s * 2.0 / a;
    }

    /**
     * 递归方式压缩轨迹
     *
     * @param coordinate
     * @param result
     * @param start
     * @param end
     * @param dMax
     * @return
     */
    private static List<double[]> compressLine(List<double[]> coordinate, List<double[]> result, int start, int end, int dMax) {
        if (start < end) {
            double maxDist = 0;
            int currentIndex = 0;
            double[] startPoint = coordinate.get(start);
            double[] endPoint = coordinate.get(end);
            for (int i = start + 1; i < end; i++) {
                double currentDist = distToSegment(startPoint, endPoint, coordinate.get(i));
                if (currentDist > maxDist) {
                    maxDist = currentDist;
                    currentIndex = i;
                }
            }
            if (maxDist >= dMax) {
                //将当前点加入到过滤数组中
                result.add(coordinate.get(currentIndex));
                //将原来的线段以当前点为中心拆成两段，分别进行递归处理
                compressLine(coordinate, result, start, currentIndex, dMax);
                compressLine(coordinate, result, currentIndex + 1, end, dMax);
            } else {
                result.remove(endPoint);
            }
        }
        return result;
    }

    /**
     * @param coordinate 原始轨迹Array<{latitude,longitude}>
     * @param dMax       允许最大距离误差
     * @return douglasResult 抽稀后的轨迹
     */
    public static List<double[]> douglasPeucker(List<double[]> coordinate, int dMax) {
        //抽稀点数量需要大于2
        if (coordinate == null || coordinate.size() <= 2) {
            return null;
        }

        List<double[]> coordinate2 = new ArrayList<>();
        for (int i = 0; i < coordinate.size(); i++) {
            double[] point = Arrays.copyOf(coordinate.get(i), 3);
            point[2] = i;
            coordinate2.add(point);
        }
        List<double[]> result = new ArrayList<>();
        result = compressLine(coordinate2, result, 0, coordinate2.size() - 1, dMax);
        result.add(coordinate2.get(0));
        result.add(coordinate2.get(coordinate.size() - 1));
        Collections.sort(result, new Comparator<double[]>() {
            @Override
            public int compare(double[] u1, double[] u2) {
                if (u1[2] > u2[2]) {
                    return 1;
                } else if (u1[2] < u2[2]) {
                    return -1;
                }
                return 0; //相等为0
            }
        });

        return result;
    }

    public static void main(String[] args) {
/*
        GeometryFactory gf = new GeometryFactory();
        Coordinate c1 = new Coordinate(1, 1.4);
        Coordinate c2 = new Coordinate(1, 1.5);
        Coordinate c3 = new Coordinate(1, 1.6);
        Coordinate c4 = new Coordinate(10, 100);
        Coordinate c5 = new Coordinate(100, 200);
        int dinstanceTolerance = 1;
        LineString geometry = gf.createLineString(new Coordinate[]{c1, c2, c3, c4, c5});
        // 使用原来jar包中带的方法
        Geometry g1 = DouglasPeuckerSimplifier.simplify(geometry, 1);  // 1米范围内的点抽稀掉
        log.info("g1: " + g1.getGeometryType().toString());
        log.info("g1Coordinates size: " + g1.getCoordinates().length);
        log.info("g1Coordinates: " + JSON.toJSONString(g1.getCoordinates()));


        // 使用自己写的方法
        List<double[]> coordinates = new ArrayList<>();
        coordinates.add(new double[]{1d, 1.4d});
        coordinates.add(new double[]{1d, 1.5d});
        coordinates.add(new double[]{1d, 1.6d});
        coordinates.add(new double[]{10d, 100d});
        coordinates.add(new double[]{100d, 200d});
        List<double[]> res = douglasPeucker(coordinates, 1);
        log.info("douglasPeucker size: " + res.size());
        log.info("g1Coordinates: " + JSON.toJSONString(res));
*/

        List<String> str = CSVUtil.importCsv(new File("D:\\learn1\\geojson-demo\\src\\main\\java\\com\\demo\\util\\hangzhou-track.json"));
        log.info("size: {}", str.size());
        JSONArray jsonArray = JSONArray.parseArray(str.get(0));
//        log.info("{}", JSON.toJSONString(jsonArray));

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONArray array = (JSONArray) jsonArray.get(i);
            List<Coordinate> coordinateList = new ArrayList<>();
            for (int j = 0; j < array.size(); j++) {
                JSONObject object = (JSONObject) array.get(j);
                if (j == 0) {
                    log.info("{},{}", object.get("coord"), object.get("elevation"));
                }
                JSONArray array1 = (JSONArray) object.get("coord");
                BigDecimal lat = (BigDecimal) array1.get(0);
                BigDecimal lng = (BigDecimal) array1.get(1);
                Double lat1 = Double.parseDouble(String.valueOf(lat));
                Double lng1 = Double.parseDouble(String.valueOf(lng));
                Coordinate coordinate = new Coordinate(lat1, lng1);
                coordinateList.add(coordinate);
            }
            log.info("");
            Coordinate[] coordinates1 = convert(coordinateList);
            GeometryFactory gf = new GeometryFactory();
            double dinstanceTolerance = 0.005;
            LineString geometry = gf.createLineString(coordinates1);
            // 使用原来jar包中带的方法
            Geometry g1 = DouglasPeuckerSimplifier.simplify(geometry, dinstanceTolerance);  // 1米范围内的点抽稀掉
            log.info("g1: {}", g1.getGeometryType().toString());
            log.info("g1Coordinates size: {}", g1.getCoordinates().length);
            log.info("g1Coordinates: {}", JSON.toJSONString(g1.getCoordinates()));
            break;
        }
    }

    public static Coordinate[] convert(List<Coordinate> lists) {
        Coordinate[] a = lists.toArray(new Coordinate[lists.size()]);
        log.info(Arrays.toString(a));
        return a;
    }
}