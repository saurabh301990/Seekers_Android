package com.tv.seekers.constant;

/**
 * Created by shoeb on 6/11/15.
 */
public class WebServiceConstants {

    //    public static final String MAIN_URL = "http://198.12.150.220/Seeker/index.php/ws_seeker/";
    public static final String MAIN_URL = "http://192.168.1.15:8080/seekerapp/rest/";
//    public static final String MAIN_URL = "http://54.172.107.206:8080/seekerapp/rest/";

    public static final String LOGIN = "user/login";
    public static final String FORGOT_PASSWORD = "user/forgot_password";

    public static final String CHANGE_PASSWORD = "user/change_password";

    public static final String GET_USER_PROFILE = "user/profile_detail";

    public static final String GET_USER_SAVED_LOC = "user/location/getList";
    public static final String USER_SAVE_KEYWORD = "keyword/create";

    public static final String GET_USER_SAVED_KEYWORDS = "keyword/getList";

    public static final String DELETE_SAVE_KEYWORD = "keyword/delete";

    public static final String TGL_SAVE_KEYWORD = "keyword/update";

    public static final String LEGAL_CONTENT = "legal/legal_support/get";
    public static final String HELP = "legal/help/get";
    public static final String GET_ALL_POSTS = "post/get_posts";
    public static final String START_THREAD = "post/initiate";
    public static final String UPDATE_USER_FILTER = "updateUserFilter";
    public static final String GET_USER_FILTER = "getUserFilter";
    public static final String GET_ALL_USER = "user/get_all_users";
    public static final String FOLLOW_USER = "user/follow_user";
    public static final String UN_FOLLOW_USER = "user/unfollow_user";
    public static final String FOLLOWED_USER_DETAILS = "user/get_detailed_user";




    public static final String GET_FOLLOWED_USERS = "user/get_followed_users";


    public static final String USER_SAVE_LOCATION = "user/location/create";

    public static final String DELETE_SAVE_LOCATION = "user/location/delete_mulitiple";


    public static final String TERMS = "legal/terms/get";
    public static final String UPDATE_USER_PROF = "user/update_porfile_pic";

    public static final String GET_POST_DETAILS = "post/get";
    public static final String GET_ACTIVITY_REPORT = "post/get_activity_report";
    public static final String GET_ACTIVITY_REPORT_SINGLE = "post/get_single_activity_report";



    public static final String IMAGE_URL = "http://198.12.150.220/Seeker/uploads/profileImage/";


    public static String getMethodUrl(String methodName) {
        String url = "";
        url = MAIN_URL + methodName;
        return url;
    }
}
