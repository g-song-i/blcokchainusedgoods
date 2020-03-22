package com.example.myapplication;

public class URLS {
  
    private static final String URL_ROOT = "http://ec2-54-180-117-192.ap-northeast-2.compute.amazonaws.com:5000/api";
    public static final String URL_JOIN = URL_ROOT + "/join";
    public static  final String URL_TALK_SEND = URL_ROOT+"/talk_send";
    public static  final String URL_TALK_RECEIVE = URL_ROOT+"/talk_receive";
    public static final String URL_TALK_LIST = URL_ROOT+"/talk_list";
    public static final String URL_REVIEW_WRITE = URL_ROOT+"/review_write";
    public static final String URL_REVIEW_READ = URL_ROOT+"/review_read";
    public static final String URL_CHECK_ID_DUPLICATION = URL_ROOT + "/check_id";
    public static final String URL_CHECK_PHONENUMBER_DUPLICATION = URL_ROOT + "/check_phone_number";
    public static final String URL_AUTH_PHONENUMBER = URL_ROOT + "/auth";
    public static final String URL_FIND_ID = URL_ROOT + "/find_id";
    public static final String URL_CHANGE_PASSWORD_FIRST = URL_ROOT + "/change_password_first";
    public static final String URL_CHANGE_PASSWORD_SECOND = URL_ROOT + "/change_password_second";
    public static final String URL_LOGIN = URL_ROOT + "/login";
    public static final String URL_SENDING_AUTH_MASSEGE = URL_ROOT + "/send";
    public static final String URL_INSERT_OBJECT = URL_ROOT + "/insert_object";
}
