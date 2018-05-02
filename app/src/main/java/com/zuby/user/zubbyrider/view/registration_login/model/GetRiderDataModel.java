package com.zuby.user.zubbyrider.view.registration_login.model;

public class GetRiderDataModel {

    /**
     * type : success
     * message : data found
     * data : {"rider_id":"CAB0640_00000011","first_name":"anu","last_name":"podar","email_id":"","phone_no":918003640640,"preferred_payment_method":"","area_name":"Sector 15","home":"","work":"","referring_driver_id":"","refer_gifted":0,"referral_date":"","city":"noida","last_updated":"2018-04-05 11:07:07","rider_created_on":"2018-04-05 11:07:07","num_completed_rides":0,"selected_language":"English","credit_balance":0,"profile_image_path":"","download_latitude":"","download_longitude":""}
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
         * email_id :
         * phone_no : 918003640640
         * preferred_payment_method :
         * area_name : Sector 15
         * home :
         * work :
         * referring_driver_id :
         * refer_gifted : 0
         * referral_date :
         * city : noida
         * last_updated : 2018-04-05 11:07:07
         * rider_created_on : 2018-04-05 11:07:07
         * num_completed_rides : 0
         * selected_language : English
         * credit_balance : 0
         * profile_image_path :
         * download_latitude :
         * download_longitude :
         */

        private String rider_id;
        private String first_name;
        private String last_name;
        private String email_id;
        private long phone_no;
        private String preferred_payment_method;
        private String area_name;
        private String home;
        private String work;
        private String referring_driver_id;
        private int refer_gifted;
        private String referral_date;
        private String city;
        private String last_updated;
        private String rider_created_on;
        private int num_completed_rides;
        private String selected_language;
        private int credit_balance;
        private String profile_image_path;
        private String download_latitude;
        private String download_longitude;

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

        public String getEmail_id() {
            return email_id;
        }

        public void setEmail_id(String email_id) {
            this.email_id = email_id;
        }

        public long getPhone_no() {
            return phone_no;
        }

        public void setPhone_no(long phone_no) {
            this.phone_no = phone_no;
        }

        public String getPreferred_payment_method() {
            return preferred_payment_method;
        }

        public void setPreferred_payment_method(String preferred_payment_method) {
            this.preferred_payment_method = preferred_payment_method;
        }

        public String getArea_name() {
            return area_name;
        }

        public void setArea_name(String area_name) {
            this.area_name = area_name;
        }

        public String getHome() {
            return home;
        }

        public void setHome(String home) {
            this.home = home;
        }

        public String getWork() {
            return work;
        }

        public void setWork(String work) {
            this.work = work;
        }

        public String getReferring_driver_id() {
            return referring_driver_id;
        }

        public void setReferring_driver_id(String referring_driver_id) {
            this.referring_driver_id = referring_driver_id;
        }

        public int getRefer_gifted() {
            return refer_gifted;
        }

        public void setRefer_gifted(int refer_gifted) {
            this.refer_gifted = refer_gifted;
        }

        public String getReferral_date() {
            return referral_date;
        }

        public void setReferral_date(String referral_date) {
            this.referral_date = referral_date;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getLast_updated() {
            return last_updated;
        }

        public void setLast_updated(String last_updated) {
            this.last_updated = last_updated;
        }

        public String getRider_created_on() {
            return rider_created_on;
        }

        public void setRider_created_on(String rider_created_on) {
            this.rider_created_on = rider_created_on;
        }

        public int getNum_completed_rides() {
            return num_completed_rides;
        }

        public void setNum_completed_rides(int num_completed_rides) {
            this.num_completed_rides = num_completed_rides;
        }

        public String getSelected_language() {
            return selected_language;
        }

        public void setSelected_language(String selected_language) {
            this.selected_language = selected_language;
        }

        public int getCredit_balance() {
            return credit_balance;
        }

        public void setCredit_balance(int credit_balance) {
            this.credit_balance = credit_balance;
        }

        public String getProfile_image_path() {
            return profile_image_path;
        }

        public void setProfile_image_path(String profile_image_path) {
            this.profile_image_path = profile_image_path;
        }

        public String getDownload_latitude() {
            return download_latitude;
        }

        public void setDownload_latitude(String download_latitude) {
            this.download_latitude = download_latitude;
        }

        public String getDownload_longitude() {
            return download_longitude;
        }

        public void setDownload_longitude(String download_longitude) {
            this.download_longitude = download_longitude;
        }
    }
}
