package com.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.apache.commons.io.FileUtils;
import org.geotools.data.FeatureReader;
import org.geotools.data.mif.MIFFile;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*************************************
 *Class Name: ReadMif2Json
 *Description: <读取Mif文件转为GeoJson格式>
 *@author: seminar
 *@create: 2021/3/2
 *@since 1.0.0
 *************************************/
public class ReadMif2Json {

    public static void main(String[] args) throws ParseException, IOException {
        String mifPathDir = "E:\\mat\\mif\\";
        List<Feature> roadLinkMifFeatures = readFeature(mifPathDir + File.separator + "ROAD_LINK.mif");
        //1、将mif内容加载到内存
        JSONArray jsonFeatures = new JSONArray();
        JSONObject resultJson = new JSONObject();

        for (int i = 0; i < roadLinkMifFeatures.size(); i++) {
            Feature roadLinkMifFeature = roadLinkMifFeatures.get(i);


            JSONObject jsonFeature = new JSONObject();
            // 组装geometry
            JSONObject geometry = getRoadLinkGeometry(roadLinkMifFeature);
            jsonFeature.put("geometry", geometry);

            //组装type
            jsonFeature.put("type", "Feature");
            //组装properties
            JSONObject roadLinkProperties = getRoadLinkProperties(roadLinkMifFeature);
            jsonFeature.put("properties", roadLinkProperties);
            //组装id 
            // todo 待定id的值
            jsonFeature.put("id", i * 2);
            jsonFeatures.add(jsonFeature);

            if (i == 0) {
                testField(roadLinkMifFeature);
                System.out.println("geojson: " + JSON.toJSONString(jsonFeature));
            }
        }
        resultJson.put("type", "FeatureCollection");
        resultJson.put("features", jsonFeatures);

        //将结果json写入制定目录
        writeJson(mifPathDir, resultJson, "roadlink");
    }

    /**
     * 读取Feature
     *
     * @param mifPath mif文件路径
     * @return List<Feature>
     */
    public static List<Feature> readFeature(String mifPath) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("version", "300");
        params.put("charset", "WindowsSimpChinese");
        params.put("delimiter", ",");
        params.put("coordsys", "Earth Projection 1, 0");
        transferEncoding(mifPath);
        transferEncoding(mifPath.replaceFirst("\\.(mif|MIF)", ".mid"));
        List<Feature> features = new ArrayList<Feature>();
        try {
            MIFFile mifFile = new MIFFile(mifPath, params);
            FeatureReader<SimpleFeatureType, SimpleFeature> reader = mifFile.getFeatureReader();

            while (reader.hasNext()) {
                Feature f = reader.next();
                features.add(f);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<Feature>();
        }
        return features;
    }

    /**
     * 获取文件的编码字符集
     *
     * @param filePath
     * @return
     */
    public static String getEncoding(String filePath) throws IOException {
        String code = "";
        try {
            File file = new File(filePath);
            byte[] head = FileUtils.readFileToByteArray(file);
            if (head[0] == -1 && head[1] == -2) {
                code = "UTF-16";
                System.err.println("文件编码错误: " + file.getName() + " : " + code);
            } else if (head[0] == -2 && head[1] == -1) {
                code = "Unicode";
                System.err.println("文件编码错误: " + file.getName() + " : " + code);
            } else if (head[0] == -17 && head[1] == -69 && head[2] == -65) {
                code = "UTF-8";
            } else {
                int i = 0;
                int headSize = head.length;
                code = "UTF-8 NoBom";
                while (i < headSize - 2) {
                    if ((head[i] & 0x00FF) < 0x80) {
                        // (10000000)值小于0x80的为ASCII字符
                        i++;
                        continue;
                    } else if ((head[i] & 0x00FF) < 0xC0) {
                        // (11000000)值在0x80和0xC0之间的,不是开头第一个
                        code = "Not UTF-8";
                        System.err.println("文件编码错误: " + file.getName() + " : " + code + "1000");
                        break;
                    } else if ((head[i] & 0x00FF) < 0xE0) {
                        // (11100000)此范围内为2字节UTF-8字符
                        if ((head[i + 1] & (0xC0)) != 0x8) {
                            code = "Not UTF-8";
                            System.err.println("文件编码错误: " + file.getName() + " : " + code + "1100");
                            break;
                        } else {
                            i += 2;
                        }
                    } else if ((head[i] & 0x00FF) < 0xF0) {
                        // (11110000)此范围内为3字节UTF-8字符
                        if ((head[i + 1] & (0xC0)) != 0x80 || (head[i + 2] & (0xC0)) != 0x80) {
                            code = "Not UTF-8";
                            System.err.println("文件编码错误: " + file.getName() + " : " + code + "11100000" + (head[i + 1] & (0xC0)));
                            break;
                        } else {
                            i += 3;
                        }
                    } else {
                        code = "Not UTF-8";
                        System.err.println("文件编码错误: " + file.getName() + " : " + code + "1111");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * 将gbk字符集文件转换为UTF-8编码文件
     *
     * @param filePath
     */
    public static void transferEncoding(String filePath) throws IOException {
        String encoding = getEncoding(filePath);
        if (encoding.startsWith("UTF-8")) {
            return;
        }
        List<String> list = new ArrayList<String>();
        File infile = new File(filePath);
        try {
            InputStream inputStream = new FileInputStream(infile);
            InputStreamReader isReader = new InputStreamReader(inputStream, "GBK");
            BufferedReader br = new BufferedReader(isReader);
            String str;
            // 按行读取字符串
            while ((str = br.readLine()) != null) {
                list.add(str);
            }
            br.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!list.isEmpty()) {
            File outFile = new File(filePath);
            try {
                OutputStream outStream = new FileOutputStream(outFile);
                OutputStreamWriter outWriter = new OutputStreamWriter(outStream, "UTF-8");
                BufferedWriter bw = new BufferedWriter(outWriter);
                for (String line : list) {
                    bw.write(line);
                    bw.write("\r\n");
                }

                bw.flush();
                bw.close();
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 给定路径与Json文件，存储到硬盘
     *
     * @param path     给定路径
     * @param json     json文件内容
     * @param fileName 文件名
     */
    public static void writeJson(String path, Object json, String fileName) {
        BufferedWriter writer = null;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, fileName);
        //如果文件不存在，则新建一个
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //写入
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + "文件写入成功！");

    }

    /**
     * 根据单个roadLink的要素和Z要素生成对应的Geometry对象
     *
     * @return
     */
    private static JSONObject getRoadLinkGeometry(Feature roadLinkMifFeature) throws ParseException {
        //从linkmif中解析出xyz坐标
        JSONObject geometry = new JSONObject();
        geometry.put("type", "LineString");
        String xY = roadLinkMifFeature.getProperty("the_geom").getValue().toString();
        WKTReader reader = new WKTReader();
        Geometry xYGeo = reader.read(xY);
        //从geo对象中解析出坐标
        Coordinate[] coordinates = xYGeo.getCoordinates();
        //合并xyz坐标，形成新的Coordinate数组
        List<List<Double>> coordinatesList = new ArrayList<List<Double>>();
        for (int i = 0; i < coordinates.length; i++) {
            List<Double> coordinateList = new ArrayList<Double>();
            coordinateList.add(coordinates[i].x);
            coordinateList.add(coordinates[i].y);
            coordinateList.add(coordinates[i].z);
            coordinatesList.add(coordinateList);
        }
        geometry.put("coordinates", coordinatesList);
        return geometry;
    }

    public static void testField(Feature feature) {
        Field[] fields = feature.getClass().getDeclaredFields();
        for (Field field : fields) {
            //设置是否允许访问，不是修改原来的访问权限修饰词
            field.setAccessible(true);
            //获取字段名，和字段的值
            try {
                System.out.println("key: " + field.getName() + " value: " + field.get(feature));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据单个roadLink的要素和Z要素生成对应的Geometry对象
     * 传入roadLink mif对象和Z mif对象
     *
     * @return
     */
    private static JSONObject getRoadLinkGeometry(Feature roadLinkMifFeature, List<Feature> roadLinkZMifFeatures) throws ParseException {
        //从linkmif中解析出xy坐标
        //从zmif中解析出z坐标
        JSONObject geometry = new JSONObject();
        geometry.put("type", "LineString");
        String xY = roadLinkMifFeature.getProperty("the_geom").getValue().toString();
        WKTReader reader = new WKTReader();
        Geometry xYGeo = reader.read(xY);
        //从geo对象中解析出坐标
        Coordinate[] coordinates = xYGeo.getCoordinates();
        //合并xyz坐标，形成新的Coordinate数组
        List<List<Double>> coordinatesList = new ArrayList<List<Double>>();
        for (int i = 0; i < coordinates.length; i++) {
            List<Double> coordinateList = new ArrayList<Double>();
            Double z = Double.parseDouble(roadLinkZMifFeatures.remove(0).getProperty("Z").getValue().toString());
            coordinates[i].setOrdinate(2, z / 100.0);
            coordinateList.add(coordinates[i].x);
            coordinateList.add(coordinates[i].y);
            coordinateList.add(coordinates[i].z);
            coordinatesList.add(coordinateList);
        }
        geometry.put("coordinates", coordinatesList);
        return geometry;

    }

    /**
     * 拼装一个properties对象，并且返回
     *
     * @param mifFeature
     * @return
     */
    private static JSONObject getRoadLinkProperties(Feature mifFeature) {
        JSONObject properties = new JSONObject();
        //挨个字段注入
        properties.put("BRIDGE_TYPE", Integer.parseInt(mifFeature.getProperty("BridgeFlag").getValue().toString()));
        properties.put("DIRECT", Integer.parseInt(mifFeature.getProperty("Direction").getValue().toString()));
        properties.put("PROVINCE_CODE_RIGHT", Integer.parseInt(mifFeature.getProperty("ProvAdminR").getValue().toString()));
        properties.put("LINK_PID", Integer.parseInt(mifFeature.getProperty("LinkID").getValue().toString()));
        properties.put("ACCESS_CHARACTERISTIC", mifFeature.getProperty("Accessible_By").getValue());
        //todo 待定
        properties.put("GUID", "to be determinated");
        properties.put("KIND", Integer.parseInt(mifFeature.getProperty("Kind").getValue().toString()));
        properties.put("VRU", Integer.parseInt(mifFeature.getProperty("VRU").getValue().toString()));
        properties.put("E_NODE_PID", Integer.parseInt(mifFeature.getProperty("EnodeID").getValue().toString()));
        properties.put("IS_VARIABLE_SPEED", Integer.parseInt(mifFeature.getProperty("VarSpeedLimit").getValue().toString()));
        //todo 待定
        properties.put("MEMO", "");
        //todo 待定
        properties.put("LEFT_NUM", 1);
        properties.put("MESH", mifFeature.getProperty("Mesh").getValue());
        properties.put("PROVINCE_CODE_LEFT", Integer.parseInt(mifFeature.getProperty("ProvAdminL").getValue().toString()));
        //todo
        properties.put("IS_REFLINE", 1);
        //todo
        properties.put("TRAFFIC_SIGNAL", 0);
        String LaneNumS2E = mifFeature.getProperty("LaneNumS2E").getValue().toString();
        if (LaneNumS2E.equals("N")) {
            LaneNumS2E = "0";
        }
        properties.put("S_NODE_PID", Integer.parseInt(mifFeature.getProperty("SnodeID").getValue().toString()));
        properties.put("LANE_NUM", Integer.parseInt(LaneNumS2E));
        properties.put("MULTI_DIGITIZED", Integer.parseInt(mifFeature.getProperty("IsMultiDZ").getValue().toString()));
        //todo
        properties.put("LENGTH", 0);
        properties.put("TRANTYPE", Integer.parseInt(mifFeature.getProperty("TranFlag").getValue().toString()));
        properties.put("OVERHEAD_OBSTRUCTION", Integer.parseInt(mifFeature.getProperty("Obstruction").getValue().toString()));
        return properties;
    }
}