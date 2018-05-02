package com.zuby.user.zubbyrider.view.registration_login.model;

public class AddRiderDataModel {

    /**
     * type : success
     * message : record inserted
     * data : {"rider_id":"CAB0640_00000011","first_name":"anu","last_name":"podar","email_id":null,"phone_no":"918003640640","area_name":"Sector 15","last_updated":"2018-04-05 11:18:13","city":"noida"}
     */

    private String type;
    private String message;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * rider_id : CAB0640_00000011
         * first_name : anu
         * last_name : podar
         * email_id : null
         * phone_no : 918003640640
         * area_name : Sector 15
         * last_updated : 2018-04-05 11:18:13
         * city : noida
         */

        private String rider_id;
        private String first_name;
        private String last_name;
        private Object email_id;
        private String phone_no;
        private String area_name;
        private String last_updated;
        private String city;

        public String getRider_id() {
            return rider_id;
        }

        public void setRider_id(String rider_id) {
            this.rider_id = rider_id;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }

        public Object getEmail_id() {
            return email_id;
        }

        public void setEmail_id(Object email_id) {
            this.email_id = email_id;
        }

        public String getPhone_no() {
            return phone_no;
        }

        public void setPhone_no(String phone_no) {
            this.phone_no = phone_no;
        }

        public String getArea_name() {
            return area_name;
        }

        public void setArea_name(String area_name) {
            this.area_name = area_name;
        }

        public String getLast_updated() {
            return last_updated;
        }

        public void setLast_updated(String last_updated) {
            this.last_updated = last_updated;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }
}
