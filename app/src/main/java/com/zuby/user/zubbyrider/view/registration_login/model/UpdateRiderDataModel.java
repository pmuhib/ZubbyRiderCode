package com.zuby.user.zubbyrider.view.registration_login.model;

public class UpdateRiderDataModel {

    /**
     * type : success
     * message : updated
     * data : {"rider_id":"CAB0368_00000002","first_name":"anu","last_name":"dar","email_id":"a@gmail.com"}
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
         * rider_id : CAB0368_00000002
         * first_name : anu
         * last_name : dar
         * email_id : a@gmail.com
         */

        private String rider_id;
        private String first_name;
        private String last_name;
        private String email_id;

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
    }
}
