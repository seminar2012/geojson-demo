package com.demo.model;

import lombok.Data;

/*************************************
 *Class Name: Device
 *Description: <设备实体类>
 *@author: seminar
 *@create: 2021/2/23
 *@since 1.0.0
 *************************************/
@Data
public class Device {

    /**
     * 纬度
     */
    double lat;

    /**
     * 经度
     */
    double lng;

    /**
     * 主键id
     */
    String id;

    /**
     * 设备id
     */
    long deviceId;

    /**
     * 权重，叠加次数
     */
    int mag;

    public Device(double lat, double lng, String id, long deviceId, int mag) {
        this.lat = lat;
        this.lng = lng;
        this.id = id;
        this.deviceId = deviceId;
        this.mag = mag;
    }
}
