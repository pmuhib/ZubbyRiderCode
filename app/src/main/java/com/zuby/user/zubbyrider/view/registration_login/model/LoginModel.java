package com.zuby.user.zubbyrider.view.registration_login.model;

import com.google.gson.annotations.SerializedName;

public class LoginModel {

    /**
     * type : success
     * message :
     * data : {"0":"success","user_id":"CAB0640_00000004","session_id":"OPI3emw2OV5NqUqwgSwwUBFc5ORr463y6ug01JDT","session_login_type":"rider"}
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
         * 0 : success
         * user_id : CAB0640_00000004
         * session_id : OPI3emw2OV5NqUqwgSwwUBFc5ORr463y6ug01JDT
         * session_login_type : rider
         */

        @SerializedName("0")
        private String _$0;
        private String user_id;
        private String session_id;
        private String session_login_type;

        public String get_$0() {
            return _$0;
        }

        public void set_$0(String _$0) {
            this._$0 = _$0;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getSession_id() {
            return session_id;
        }

        public void setSession_id(String session_id) {
            this.session_id = session_id;
        }

        public String getSession_login_type() {
            return session_login_type;
        }

        public void setSession_login_type(String session_login_type) {
            this.session_login_type = session_login_type;
        }
    }
}
