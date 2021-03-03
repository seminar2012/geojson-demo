package com.test.doulag;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.util.CSVUtil;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.demo.util.Doulag.convert;

/*************************************
 *Class Name: Test
 *Description: <测试读取>
 *@author: seminar
 *@create: 2021/3/2
 *@since 1.0.0
 *************************************/
@Slf4j
public class TestDoulag {

    @org.junit.Test
    public void test() {

        String path = "D:\\learn1\\geojson-demo\\data\\hangzhou-track_.json";
        List<String> str = CSVUtil.importCsv(new File(path));
        log.info("size: {}", str.size());
        log.info(str.toString());
        String[] arr = str.toArray(new String[str.size()]);
        JSONArray jsonArray = JSONArray.parseArray(org.apache.commons.lang3.StringUtils.join(arr, ""));
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONArray array = (JSONArray) jsonArray.get(i);
            List<Coordinate> coordinateList = new ArrayList<>();
            for (int j = 0; j < array.size(); j++) {
                JSONObject object = (JSONObject) array.get(j);
                if (j == 0) {
                    log.info("{},{}", object.get("coord"), object.get("elevation"));
                }
                object.remove("elevation");
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
            double dinstanceTolerance = 0.0001;    //0.0005——0.05公里范围内的点抽稀掉    0.0000005 5厘米范围内的点抽稀掉
            LineString geometry = gf.createLineString(coordinates1);
            // 使用原来jar包中带的方法
            Geometry g1 = DouglasPeuckerSimplifier.simplify(geometry, dinstanceTolerance);
            log.info("oriSize: {},cxSize: {}", coordinates1.length, g1.getCoordinates().length);
//            JSONArray arrayNew = new JSONArray();
            array.clear();
            for (Coordinate coordinate : g1.getCoordinates()) {
                JSONObject object = new JSONObject();
                JSONArray jsonArray1 = new JSONArray();
                jsonArray1.add(coordinate.x);
                jsonArray1.add(coordinate.y);
                object.put("coord", jsonArray1);
                array.add(object);
            }
        }
        List<String> res = new ArrayList<>();
        res.add(JSON.toJSONString(jsonArray));
        CSVUtil.exportCsv(path, res);
    }

}
