package com.test;

import com.demo.geojson.FeatureJSON;
import org.geotools.data.DataUtilities;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geojson.geom.GeometryJSON;
import org.junit.Test;
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

/*************************************
 *Class Name: TestFeatureCollection
 *Description: <测试fc>
 *@author: seminar
 *@create: 2021/2/23
 *@since 1.0.0
 *************************************/
public class TestFeatureCollection {

    @Test
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

    @Test
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
                        "mag:Integer"
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
}
