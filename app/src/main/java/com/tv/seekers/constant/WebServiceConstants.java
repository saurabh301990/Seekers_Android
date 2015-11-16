package com.tv.seekers.constant;

/**
 * Created by shoeb on 6/11/15.
 */
public class WebServiceConstants {

    public static final String MAIN_URL = "http://198.12.150.220/Seeker/index.php/ws_seeker/";

    public static final String LOGIN = "userLogin";
    public static final String FORGOT_PASSWORD = "userForgotPassword";
    public static final String CHANGE_PASSWORD = "updateUserPassword";
    public static final String GET_USER_PROFILE = "getUserProfile";
    public static final String GET_USER_SAVED_LOC = "getUserSavedLocation";
    public static final String USER_SAVE_KEYWORD = "userSaveKeyword";
    public static final String GET_USER_SAVED_KEYWORDS = "getUserSavedKeywords";
    public static final String DELETE_SAVE_KEYWORD = "deleteSaveKeyword";
    public static final String TGL_SAVE_KEYWORD = "toggleSaveKeyword";


    public static final String IMAGE_URL = "http://198.12.150.220/Seeker/uploads/profileImage/";


    public static String getMethodUrl(String methodName) {
        String url = "";
        url = MAIN_URL + methodName;
        return url;
    }
}
