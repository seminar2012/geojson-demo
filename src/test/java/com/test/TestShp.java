/**************************************
 * Copyright(C),Navinfo
 * Package: com.navinfo.a1m
 * Author: seminar
 * Date: Created in 2021/2/23 14:16
 **************************************/
package com.test;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/*************************************
 *Class Name: TestShp
 *Description: <测试shp>
 *@author: seminar
 *@create: 2021/2/23
 *@since 1.0.0
 *************************************/
public class TestShp {

    /**
     * 测试读取shp
     */
    public void testReadShp() {
        String path1 = "G:/work/china_map/shp/BOUNT_poly.shp";

        //读取shp
        SimpleFeatureCollection colls1 = readShp(path1);
        //拿到所有features
        SimpleFeatureIterator iters = colls1.features();
        //遍历打印
        while (iters.hasNext()) {
            SimpleFeature sf = iters.next();
            System.out.println(sf.getID() + " , " + sf.getAttributes());
        }
    }

    /*
      *
      * @param path
      * @return
      */
    public static SimpleFeatureCollection readShp(String path) {
        return readShp(path, null);
    }

    public static SimpleFeatureCollection readShp(String path, Filter filter) {

        SimpleFeatureSource featureSource = readStoreByShp(path);

        if (featureSource == null) return null;

        try {
            return filter != null ? featureSource.getFeatures(filter) : featureSource.getFeatures();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static SimpleFeatureSource readStoreByShp(String path) {

        File file = new File(path);

        FileDataStore store;
        SimpleFeatureSource featureSource = null;
        try {
            store = FileDataStoreFinder.getDataStore(file);
            ((ShapefileDataStore) store).setCharset(Charset.forName("UTF-8"));
            featureSource = store.getFeatureSource();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return featureSource;
    }
}
