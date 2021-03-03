package com.test;

import com.alibaba.fastjson.JSON;
import com.demo.geojson.FeatureJSON;
import lombok.extern.slf4j.Slf4j;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.referencing.CRS;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/*************************************
 *Class Name: ReadGeoJson
 *Description: <读取geojson>
 *@author: seminar
 *@create: 2021/2/23
 *@since 1.0.0
 *************************************/
@Slf4j
public class ReadGeoJson {

    public static void main(String[] a) throws Exception {
        // 坐标顺序是EAST_NORTH，即经度在前
        String json = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{\"area\":3865207830, \"text\": null},\"id\":\"polygon.1\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[116.19827270507814,39.78321267821705],[116.04446411132814,39.232253141714914],[116.89590454101562,39.3831409542565],[116.86981201171876,39.918162846609455],[116.19827270507814,39.78321267821705]]]}}],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}";
        log.info("{}", json);
        // 指定GeometryJSON构造器，15位小数
        FeatureJSON fjson_15 = new FeatureJSON(new GeometryJSON(15));
        // 读取为FeatureCollection
        FeatureCollection featureCollection = fjson_15.readFeatureCollection(json);
        CoordinateReferenceSystem crs = fjson_15.readCRS(json);
        // 获取SimpleFeatureType
        SimpleFeatureType simpleFeatureType = (SimpleFeatureType) featureCollection.getSchema();

        // 第1个问题。坐标顺序与实际坐标顺序不符合
        log.info("bounds: {},b-crs: {}",featureCollection.getBounds(),featureCollection.getBounds().getCoordinateReferenceSystem());
        log.info("{}", JSON.toJSONString(crs));
        log.info("-----------------------{},{}", crs.getCoordinateSystem(), crs.getClass());
        log.info("schema: {}, referenceSystem: {}, name: {}", CRS.getAxisOrder(simpleFeatureType.getCoordinateReferenceSystem()), crs.getCoordinateSystem(), crs.getName());  //输出：NORTH_EAST

        //第2个问题。查看空间列名称
        log.info(simpleFeatureType.getGeometryDescriptor().getLocalName());  //输出：geometry

        //第3个问题。坐标精度丢失
        //第4个问题。默认无坐标系和空值输出
        OutputStream ostream = new ByteArrayOutputStream();

        // fjson_15已经保留15位
        // 输出坐标系文本
        fjson_15.setEncodeFeatureCollectionCRS(true);
        fjson_15.setEncodeFeatureCRS(true);
        fjson_15.setEncodeNullValues(true);
        fjson_15.setEncodeFeatureBounds(true);
        fjson_15.setEncodeFeatureBounds(true);
        System.out.println(json);
        fjson_15.writeFeatureCollection(featureCollection,System.out);  // 控制台输出和原始geojson一致

//        GeoJSON.write(featureCollection, ostream);
//        log.info("1111111111111111111");
//        // 输出：{"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[[116.1983,39.7832],[116.0445,39.2323],[116.8959,39.3831],[116.8698,39.9182],[116.1983,39.7832]]]},"properties":{"area":3865207830},"id":"polygon.1"}]}
//        log.info("\n++++++++++++++++++++++ \nostrem： {}", ostream);

        // 第5个问题。坐标变换问题，由坐标顺序引发
        SimpleFeatureIterator iterator = (SimpleFeatureIterator) featureCollection.features();
        SimpleFeature simpleFeature = iterator.next();
        Geometry geom = (Geometry) simpleFeature.getDefaultGeometry();
        iterator.close();
        log.info("{}", geom.getArea());  // 输出：0.4043554020447081
        MathTransform transform_1 = CRS.findMathTransform(CRS.decode("EPSG:4326"), CRS.decode("EPSG:3857"), true);
        // 下面一行代码会报异常：Exception in thread "main" org.geotools.referencing.operation.projection.ProjectionException: Latitude 116°11.8'N is too close to a pole.
        /*Geometry geom_3857 = JTS.transform(geom, transform_1);
       log.info(geom_3857.getArea());*/
        log.info("\n\n");
    }


    @Test
    public void generate() throws IOException {
        // 坐标顺序是EAST_NORTH，即经度在前
        String json = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{\"area\":3865207830, \"text\": null},\"id\":\"polygon.1\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[116.19827270507814,39.78321267821705],[116.04446411132814,39.232253141714914],[116.89590454101562,39.3831409542565],[116.86981201171876,39.918162846609455],[116.19827270507814,39.78321267821705]]]}}],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}";
        log.info("{}", json);
        // 指定GeometryJSON构造器，15位小数
        FeatureJSON fjson_15 = new FeatureJSON(new GeometryJSON(15));
        // 读取为FeatureCollection
        FeatureCollection featureCollection = fjson_15.readFeatureCollection(json);
        CoordinateReferenceSystem crs = fjson_15.readCRS(json);

        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate cors = new Coordinate(111d,222d,3);
        Point point = geometryFactory.createPoint(cors);
        SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
        b.setName("test");
        b.setCRS(crs);
        b.add("the_geom", Point.class);
        SimpleFeatureType type = b.buildFeatureType();
        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(type);
        Object[] values = new Object[]{point};
        builder.addAll(values);
        SimpleFeature feature = builder.buildFeature(null);
        DefaultFeatureCollection newCollection = new DefaultFeatureCollection();
        newCollection.add(feature);

    }
}