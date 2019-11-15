package com.freak.circularbead.constants;


/**
 * 常量
 *
 * @author freak
 * @date 2019/9/11.
 */

public class Constants {
    /**
     * 切换服务器地址只需要修改对应的serverTYpe即可
     * DEBUG          sit测试环境（调试接口环境）
     * UAT            uat测试环境（模拟生产测试环境）
     * RELEASE        生产环境（线上服务器）
     * OTHER          同事主机IP服务器（需要同事协助调试时使用）
     */
//    public final static String SERVER_TYPE = "UAT";
    public final static String SERVER_TYPE = "DEBUG";
//    public final static String SERVER_TYPE = "RELEASE";
//    public final static String SERVER_TYPE = "OTHER";

    public static final String UO_LOGIN = "用户未登录";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String STORE = "STORE";
    public static final String USER_NAME = "user_name";
    public static final String YS_ACCESS_TOKEN = "ys_access_token";
    public static final String HUAN_XIN_ID = "huan_xin_id";
    public static final String HUAN_XIN_PWD = "huan_xin_pwd";
    public static final int LOGIN_OUT = 10000;
    public static final String SEQUENCE = "SEQUENCE";
    public static final String USER_CODE = "user_code";
    public static final String VERSION_CODE = "version_code";
    public static final String VERSION_NAME = "version_name";



    /**
     * 获取服务器地址
     *
     * @param serverType
     * @return
     */
    public static String getBaseUrl(String serverType) {
        String serverUrl;
        switch (serverType) {
            //测试服务器
            case "DEBUG":
                serverUrl = "https://test1.huang-dou.com/";
                break;
            //UAT测试服务器
            case "UAT":
                serverUrl = "http://192.168.1.90:8081/";
                break;
            //生产服务器
            case "RELEASE":
                serverUrl = "https://jplife.huang-dou.com/";
                break;
            //同事主机IP服务器
            case "OTHER":
                serverUrl = "http://192.168.1.28:8080/";
                break;
            default:
                serverUrl = "http://192.168.1.89:8081/";
                break;
        }
        return serverUrl;
    }
}
