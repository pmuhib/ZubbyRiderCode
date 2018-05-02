package com.zuby.user.zubbyrider.view.registration_login.model;

public class LoginThroughPassword {

    /**
     * type : success
     * message :
     * data : {"user_id":"CAB0640_00000001","session_id":"B4nDvW26QLKZSx93UHZiERwt0nBk5ndfLjgqbwRn","session_login_type":"rider"}
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
         * user_id : CAB0640_00000001
         * session_id : B4nDvW26QLKZSx93UHZiERwt0nBk5ndfLjgqbwRn
         * session_login_type : rider
         */

        private String user_id;
        private String session_id;
        private String session_login_type;

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
