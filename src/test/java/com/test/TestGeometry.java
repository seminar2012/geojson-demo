/**************************************
 * Copyright(C),Navinfo
 * Package: com.navinfo.a1m
 * Author: seminar
 * Date: Created in 2021/2/23 14:17
 **************************************/
package com.test;

import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/*************************************
 *Class Name: TestGeometry
 *Description: <测试geom>
 *@author: seminar
 *@create: 2021/2/23
 *@since 1.0.0
 *************************************/
public class TestGeometry {

    GeometryFactory geometryFactory = new GeometryFactory();
    int decimals = 15;
    GeometryJSON geometryJSON = new GeometryJSON(decimals);

    @Test
    public void LineString2geojson() throws ParseException, IOException {
        // 由wkt字符串构造LineString对象
        WKTReader reader = new WKTReader(geometryFactory);
        LineString lineString = (LineString) reader.read("LINESTRING (254058.76074485347 475001.2186020431, 255351.04293761664 474966.9279243938)");
        // 设置保留6位小数，否则GeometryJSON默认保留4位小数
        GeometryJSON geometryJson = new GeometryJSON(6);
        StringWriter writer = new StringWriter();
        geometryJson.write(lineString, writer);
        System.out.println(writer.toString());
        writer.close();
    }

    @Test
    public void geojson2LineString() throws IOException {
        LineString lineString = (LineString) geometryJSON.read(new StringReader("{\n" +
                "                \"type\": \"LineString\",\n" +
                "                \"coordinates\": [\n" +
                "                    [\n" +
                "                        120.6584555,\n" +
                "                        30.45144\n" +
                "                    ],\n" +
                "                    [\n" +
                "                        120.1654515,\n" +
                "                        30.54848\n" +
                "                    ]\n" +
                "                ]\n" +
                "            }"));
        System.out.println("lineString: " + lineString.toString());
    }
}
