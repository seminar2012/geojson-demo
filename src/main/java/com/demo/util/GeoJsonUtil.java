package com.demo.util;

import com.demo.geojson.FeatureJSON;
import com.demo.model.Device;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.geotools.data.DataUtilities;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geojson.geom.GeometryJSON;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*************************************
 *Class Name: GeoJsonUtil
 *Description: <GeoJsonUtil工具类>
 *@author: seminar
 *@create: 2021/2/23
 *@since 1.0.0
 *************************************/
@Slf4j
public class GeoJsonUtil {

    public void lineString2FeatureCollection() throws IOException, SchemaException, ParseException {
        String[] WKTS = {"LINESTRING (255351.04293761664 474966.9279243938, 255529.29662365236 474272.4599921228)",
                "LINESTRING (255529.29662365236 474272.4599921228, 256166.05830998957 473979.44920198264)"};

        final SimpleFeatureType TYPE = DataUtilities.createType("Link",
                "geometry:LineString," + // <- the geometry attribute: Point type
                        "gid:String," +   // <- a String attribute
                        "direction:Integer," +   // a number attribute
                        "orientation:Integer"
        );
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
        GeometryFactory geometryFactory = new GeometryFactory();
        WKTReader reader = new WKTReader(geometryFactory);
        FeatureJSON fjson = new FeatureJSON();
        List<SimpleFeature> features = new ArrayList<>();
        SimpleFeatureCollection collection = new ListFeatureCollection(TYPE, features);
        for (String wkt : WKTS) {
            LineString lineString = (LineString) reader.read(wkt);
            featureBuilder.add(lineString);
            featureBuilder.add("123456");
            featureBuilder.add(2);
            featureBuilder.add(1);
            SimpleFeature feature = featureBuilder.buildFeature(null);
            features.add(feature);
        }

        StringWriter writer = new StringWriter();
        fjson.writeFeatureCollection(collection, writer);
        System.out.println(writer.toString());
    }

    public void point2FeatureCollection() throws IOException, SchemaException, ParseException {
        String[] WKTS = {"POINT (255351.04293761664 474966.9279243938 1.0)",
                "POINT (255529.29662365236 474272.4599921228 2.0)",
                "POINT (256166.05830998957 473979.44920198264 3.111)"};

        // 坐标顺序是EAST_NORTH，即经度在前
        String json = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{\"area\":3865207830, \"text\": null},\"id\":\"polygon.1\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[116.19827270507814,39.78321267821705],[116.04446411132814,39.232253141714914],[116.89590454101562,39.3831409542565],[116.86981201171876,39.918162846609455],[116.19827270507814,39.78321267821705]]]}}],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}";
        FeatureJSON fjson_15 = new FeatureJSON(new GeometryJSON(15));
        CoordinateReferenceSystem crs = fjson_15.readCRS(json);

        final SimpleFeatureType TYPE = DataUtilities.createType("Point",
                "geometry:Point," + // <- the geometry attribute: Point type
                        "id:String," +   // <- a String attribute
                        "mag:Integer" // <- 权重，叠加次数
        );

        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
        GeometryFactory geometryFactory = new GeometryFactory();
        WKTReader reader = new WKTReader(geometryFactory);
        FeatureJSON fjson = new FeatureJSON();
        List<SimpleFeature> features = new ArrayList<>();
        SimpleFeatureCollection collection = new ListFeatureCollection(TYPE, features);
        for (String wkt : WKTS) {
            Point point = (Point) reader.read(wkt);
            featureBuilder.add(point);
            featureBuilder.add("1");
            featureBuilder.add(2);
            SimpleFeature feature = featureBuilder.buildFeature(null);
            features.add(feature);
        }

        StringWriter writer = new StringWriter();
        fjson.setEncodeFeatureCollectionCRS(true);
        fjson.writeFeatureCollection(collection, writer);
        System.out.println(writer.toString());
    }

    public void point2FeatureCollection(List<Device> deviceList) throws IOException, SchemaException, ParseException {
        // 坐标顺序是EAST_NORTH，即经度在前
        String json = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{\"area\":3865207830, \"text\": null},\"id\":\"Point.1\",\"geometry\":{\"type\":\"Point\",\"coordinate\":[116.04446411132814,39.232253141714914]}}],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}";
        FeatureJSON fjson_15 = new FeatureJSON(new GeometryJSON(15));

        final SimpleFeatureType TYPE = DataUtilities.createType("Point",
                "geometry:Point," + // <- the geometry attribute: Point type
                        "id:String," +   // <- a String attribute
                        "mag:Integer," + // <- 权重，叠加次数
                        "deviceId:String"
        );

        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
        GeometryFactory geometryFactory = new GeometryFactory();
        WKTReader reader = new WKTReader(geometryFactory);
        FeatureJSON fjson = new FeatureJSON();
        List<SimpleFeature> features = new ArrayList<>();
        SimpleFeatureCollection collection = new ListFeatureCollection(TYPE, features);
        deviceList.stream().forEach(x -> {
            try {
                Point point = (Point) reader.read("Point (" + x.getLng() + " " + x.getLat() + ")");
                featureBuilder.add(point);
                featureBuilder.add(x.getId());
                featureBuilder.add(x.getMag());
                featureBuilder.add(x.getDeviceId());
                SimpleFeature feature = featureBuilder.buildFeature(null);
                features.add(feature);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        StringWriter writer = new StringWriter();
        fjson.setEncodeFeatureCollectionCRS(true);
        fjson.writeFeatureCollection(collection, writer);

        JSONObject jsonObject = JSONObject.fromObject(writer.toString());
        System.out.println(writer.toString());
        log.info("-----jsonObject: {}", jsonObject);
    }

    public static void main(String[] args) throws ParseException, IOException, SchemaException {
        String[] wkts = {"POINT (255351.04293761664 474966.9279243938 1.0)",
                "POINT (255529.29662365236 474272.4599921228 2.0)",
                "POINT (256166.05830998957 473979.44920198264 3.111)"};

        GeometryFactory geometryFactory = new GeometryFactory();
        WKTReader reader = new WKTReader(geometryFactory);
        List<Device> devices = new ArrayList<>();
        int i = 1;
        Point point = null;
        for (String wkt : wkts) {
            point = (Point) reader.read(wkt);
            Device device = new Device(point.getX(), point.getY(), String.valueOf("deviceId00" + i), i, 1);
            i++;
            devices.add(device);
        }
        Device device = new Device(point.getX(), point.getY(), String.valueOf("deviceId00" + i), i, 1);
        devices.add(device);

        // 根据经纬度分组，然后相同的计数
        Map<String, List<Device>> groupByList = devices.stream().collect(Collectors.groupingBy(a -> StringUtil.format("{0}#{1}", a.getLat(), a.getLng())));
        List<Device> list = groupByList.entrySet().stream().map(x -> {
            Device device1 = x.getValue().get(0);
            device1.setMag(x.getValue().size());
            return device1;
        }).collect(Collectors.toList());

        GeoJsonUtil geoJsonUtil = new GeoJsonUtil();
//        geoJsonUtil.point2FeatureCollection();
        geoJsonUtil.point2FeatureCollection(list);


        List<String> list1 = new ArrayList<>();
        list1.add("1");
        list1.add("2");
        list1.add("3");
        String deArr[] = list1.toArray(new String[list1.size()]);

        String str = "('" + StringUtils.join(deArr, "','") + "')";
        log.info("str: {}", str);
    }
}
