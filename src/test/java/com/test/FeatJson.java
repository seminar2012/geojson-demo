package com.test;

import com.demo.geojson.FeatureJSON;
import lombok.extern.slf4j.Slf4j;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureCollection;
import org.geotools.geojson.GeoJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.*;

@Slf4j
public class FeatJson {


    public static String readFileContent(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }

    public static void main(String[] args) throws IOException {
        //读取本地文件
        String fileName = "D:\\project\\geojson-demo\\src\\test\\java\\com\\navinfo\\a1m\\geojson2.json";
        String dict = readFileContent(fileName);
        //按行读取文件
        //构造FeatureJSON对象，GeometryJSON保留15位小数
        FeatureJSON featureJSON = new FeatureJSON(new GeometryJSON(15));
        FeatureCollection featureCollection = featureJSON.readFeatureCollection(dict);
        SimpleFeatureType simpleFeatureType = (SimpleFeatureType) featureCollection.getSchema();
        log.info("{}", simpleFeatureType.getGeometryDescriptor().getLocalName());
        OutputStream ostream = new ByteArrayOutputStream();
        GeoJSON.write(featureCollection, ostream);
        log.info("{}", ostream);

        featureJSON.setEncodeFeatureCollectionCRS(true);
        OutputStream ostream2 = new ByteArrayOutputStream();
        featureJSON.writeFeatureCollection(featureCollection,ostream2);
        log.info("----------ostreams: {}", ostream2);

        SimpleFeatureIterator iterator = (SimpleFeatureIterator) featureCollection.features();
        SimpleFeature simpleFeature = iterator.next();
        Geometry geom = (Geometry) simpleFeature.getDefaultGeometry();
        iterator.close();
        log.info("{}", geom.getLength());
        log.info("{}", geom.getCoordinate());
        log.info("{}", geom.getBoundary());
        log.info("{}", geom.getGeometryType());
    }
}