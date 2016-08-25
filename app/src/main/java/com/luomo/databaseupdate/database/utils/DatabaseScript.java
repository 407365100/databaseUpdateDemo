package com.luomo.databaseupdate.database.utils;

/**
 * @author :renpan
 * @version :v1.0
 * @class :com.luomo.databaseupdate.database.utils
 * @date :2016-08-22 16:14
 * @description:数据库版本、名称、表创建语句等常量
 */
public class DatabaseScript {
    /**
     * 数据库
     */
    public final static String DATABASE_NAME = "testDataBaseUpdate.db";
    /**
     * DATABASE_HIGH_VERSION 数据库的版本如1，可手动修改。修改后即可触发onUpgrade方法
     */
    public final static int DATABASE_LOW_VERSION = 1;
    public final static int DATABASE_HIGH_VERSION = 2;

    /**
     * 表名
     */
    public final static String TABLE_USER_INFO = "userInfo";

    /**
     * 用户表创建语句(低版本的)
     */
    public static final String CREATE_TABLE_LOW_USER_INFO = "CREATE TABLE IF NOT EXISTS " + TABLE_USER_INFO + " (" +
            " id TEXT PRIMARY KEY NOT NULL," +
            " name TEXT)";

    /**
     * 用户表创建语句(高版本的)
     */
    public static final String CREATE_TABLE_HIGH_USER_INFO = "CREATE TABLE IF NOT EXISTS " + TABLE_USER_INFO + " (" +
            " userId TEXT PRIMARY KEY NOT NULL," +
            " phone TEXT ," +
            " userName TEXT)";
}
