package com.souche.android.framework;

/**
 * Created by shenyubao on 14-5-8.
 */
public class Const {
    public static boolean net_error_try = false;

    public static boolean auto_inject = true;

    public static int DATABASE_VERSION = 1;

    //adapter 的分页相关
    public static String netadapter_page_no = "page";
    public static String netadapter_step = "step";
    public static Integer netadapter_step_default = 20;
    public static String netadapter_timeline = "timeline";
    public static String netadapter_json_timeline = "id";
    public static String netadapter_no_more = "已经没有了";

    public static String[] ioc_instal_pkg = null;

    //图片缓存相关
    public static String image_cache_dir = "dhcache";
    public static int image_cache_num = 12;
    public static Boolean image_cache_is_in_sd = false;
    public static long image_cache_time = 100000l;

    //网络访问返回数据的格式定义
    public static String response_msg = "message";
    public static String response_data = "data";
    public static String response_code = "code";
    public static int net_pool_size = 10;

    //错误代码
    public static String CODE_SUCCESS = "100000";
}
