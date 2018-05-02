package com.zuby.user.zubbyrider.view.navigationdrawer.model;

import java.util.List;

public class NearbyDriverModel {

    /**
     * type : success
     * message : found
     * data : [{"driver_id":"CAB5199_00000005","current_car_category_id_selected":123,"current_car_id":123,"driver_lat":"28.5848809","driver_long":"77.3138721","distance":0.0049455175195304,"ETA":""},{"driver_id":"CAB3325_00000001","current_car_category_id_selected":123,"current_car_id":123,"driver_lat":"28.5848736","driver_long":"77.3138863","distance":0.006494261163337,"ETA":""}]
     */

    private String type;
    private String message;
    private List<DataBean> data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * driver_id : CAB5199_00000005
         * current_car_category_id_selected : 123
         * current_car_id : 123
         * driver_lat : 28.5848809
         * driver_long : 77.3138721
         * distance : 0.0049455175195304
         * ETA :
         */

        private String driver_id;
        private int current_car_category_id_selected;
        private int current_car_id;
        private String driver_lat;
        private String driver_long;
        private double distance;
        private String ETA;

        public String getDriver_id() {
            return driver_id;
        }

        public void setDriver_id(String driver_id) {
            this.driver_id = driver_id;
        }

        public int getCurrent_car_category_id_selected() {
            return current_car_category_id_selected;
        }

        public void setCurrent_car_category_id_selected(int current_car_category_id_selected) {
            this.current_car_category_id_selected = current_car_category_id_selected;
        }

        public int getCurrent_car_id() {
            return current_car_id;
        }

        public void setCurrent_car_id(int current_car_id) {
            this.current_car_id = current_car_id;
        }

        public String getDriver_lat() {
            return driver_lat;
        }

        public void setDriver_lat(String driver_lat) {
            this.driver_lat = driver_lat;
        }

        public String getDriver_long() {
            return driver_long;
        }

        public void setDriver_long(String driver_long) {
            this.driver_long = driver_long;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public String getETA() {
            return ETA;
        }

        public void setETA(String ETA) {
            this.ETA = ETA;
        }
    }
}
