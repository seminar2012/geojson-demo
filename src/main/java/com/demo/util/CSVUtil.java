package com.demo.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/****************************
 * Class Name： CSVUtil
 * Description： <CSV操作(读取和写入>
 * @Author: seminar
 * @create: 2020/10/19
 * @since: 1.0.0
 ***************************/
@Slf4j
public class CSVUtil {

    /**
     * 读取
     *
     * @param file     csv文件(路径+文件名)，csv文件不存在会自动创建
     * @param dataList 数据
     * @return
     */
    public static boolean exportCsv(File file, List<String> dataList) {
        boolean isSucess = false;

        FileOutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(file, true);
            osw = new OutputStreamWriter(out);
            bw = new BufferedWriter(osw);
            if (dataList != null && !dataList.isEmpty()) {
                for (String data : dataList) {
                    bw.append(data).append("\r");
                }
            }
            isSucess = true;
        } catch (Exception e) {
            isSucess = false;
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                    bw = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                    osw = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                    out = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return isSucess;
    }

    /**
     * 导入
     *
     * @param file csv文件(路径+文件)
     * @return
     */
    public static List<String> importCsv(File file) {
        List<String> dataList = new ArrayList<String>();
        if (!file.exists()) {
            return dataList;
        }

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = br.readLine()) != null) {
                dataList.add(line);
            }
        } catch (Exception e) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                    br = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return dataList;
    }

    /**
     * CSV读取测试
     *
     * @throws Exception
     */
    public static void importCsv() {
        List<String> dataList = CSVUtil.importCsv(new File("ljq.csv"));
        if (dataList != null && !dataList.isEmpty()) {
            for (int i = 0; i < dataList.size(); i++) {
                if (i != 0) {//不读取第一行
                    String s = dataList.get(i);
                    System.out.println("s  " + s);
                    String[] as = s.split(",");
                    System.out.println(as[0]);
                    System.out.println(as[1]);
                    System.out.println(as[2]);
                }
            }
        }
    }

    /**
     * CSV写入测试
     *
     * @throws Exception
     */
    public static void exportCsv() {
        List<String> dataList = new ArrayList<String>();
        dataList.add("number,name,sex");
        dataList.add("1,张三,男");
        dataList.add("2,李四,男");
        dataList.add("3,小红,女");
        boolean isSuccess = CSVUtil.exportCsv(new File("ljq.csv"), dataList);
        log.info("write csv {}", isSuccess);
    }

    /**
     * CSV写入测试
     *
     * @throws Exception
     */
    public static void exportCsv(String filePath, List<String> dataList) {
        // 构建文件名
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateNowStr = sdf.format(d);
        boolean isSuccess = false;
        if (StringUtils.isEmpty(filePath)) {
            isSuccess = CSVUtil.exportCsv(new File("logs" + File.separator + "orderSplit_" + dateNowStr + ".csv"), dataList);
        } else {
            isSuccess = CSVUtil.exportCsv(new File(filePath), dataList);
        }
//        log.info("write csv {}", isSuccess);
    }

    /**
     * 测试
     *
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
//        exportCsv();
//        importCsv();
//        log.info("\n");
//                   1605606019148
//        long start = 1605601019148L;
//        log.info("start: {}", start); // 1605606019148
//        Thread.sleep(10 * 1000);
//        timeCostCal(start);

        List<String> str = CSVUtil.importCsv(new File("D:\\learn\\echarts\\test\\data\\hangzhou-tracks.json"));
        System.out.println(str.size());
        JSONArray object = JSONArray.parseArray(str.get(0));
        System.out.println("object: " + JSON.toJSONString(object));
    }

    public static void timeCostCal(long start) {
        long end = System.currentTimeMillis();
        List<String> csvList = new ArrayList<>();
        csvList.add("timeStart: " + stampToDate(start));
        csvList.add("timeEnd: " + stampToDate(end));
        csvList.add("timeCost: " + getFormatTime(end - start));
        CSVUtil.exportCsv("", csvList);
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(long timeStamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(timeStamp);
        Date date = new Date(lt);
        String res = simpleDateFormat.format(date);
        return res;
    }

    public static String getFormatTime(long time) {
        long hours = time / (1000 * 60 * 60);
        long minutes = (time - hours * (1000 * 60 * 60)) / (1000 * 60);
        long second = (time - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / 1000;
        String diffTime = "";
        if (hours > 0) {
            diffTime += hours + " h ";
        }
        if (minutes > 0) {
            diffTime = diffTime + minutes + " m ";
        }
        if (second > 0) {
            diffTime = diffTime + second + " s ";
        }
        return diffTime;
    }
}