package com.luomo.databaseupdate.database.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.luomo.databaseupdate.database.domain.UserDomain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :renpan
 * @version :v1.0
 * @class :SQLiteOpenHelper
 * @date :2016-08-22 8:45
 * @description:数据库操作类
 */
public class SQLiteOpenHelperWrap extends SQLiteOpenHelper {
    private static String TAG = "renpan";
    private static SQLiteOpenHelperWrap dbContainer;
    private final static String databaseName = DatabaseScript.DATABASE_NAME;
//        private final static int version = DatabaseScript.DATABASE_LOW_VERSION;
    private final static int version = DatabaseScript.DATABASE_HIGH_VERSION;

    public SQLiteOpenHelperWrap(Context context) {
        super(context, databaseName, null, version);
    }

    public SQLiteOpenHelperWrap(Context context, String name, CursorFactory factory, int version) {
        super(context, databaseName, factory, version);
    }

    public SQLiteOpenHelperWrap(Context context, String name, CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, databaseName, factory, version, errorHandler);
    }

    /**
     * 获取DBContainer实例
     *
     * @param context
     * @return
     */
    public static synchronized SQLiteDatabase getInstance(Context context) {
        if (dbContainer == null)
            dbContainer = new SQLiteOpenHelperWrap(context);
        //dbContainer.getWritableDatabase()的时候才会创建数据库
        return dbContainer.getWritableDatabase();
    }

    /**
     * 创建数据库
     *
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //低版本创建表，填充数据
        sqLiteDatabase.execSQL(DatabaseScript.CREATE_TABLE_LOW_USER_INFO);
        loadLowVersionData(sqLiteDatabase);
        //高版本创建表，填充数据
        /*sqLiteDatabase.execSQL(DatabaseScript.CREATE_TABLE_HIGH_USER_INFO);
        loadHighVersionData(sqLiteDatabase);*/
        Log.i(TAG, "创建数据库 数据库版本(这里并没有真正创建，只有在调用getWritableDatabase时才会创建)");
    }

    /**
     * 更新数据库
     *
     * @param sqLiteDatabase
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        UpgradeUtils.upgradeVersion(sqLiteDatabase, oldVersion, newVersion);
        Log.i(TAG, "升级数据库 旧数据库版本:" + oldVersion + ";" + "新数据库版本：" + newVersion);
    }

    /*==================================其他业务层操作====================================*/

    /**
     * 获取低版本的user列表
     *
     * @return
     */
    public static List<UserDomain> getLowUserList(Context context) {
        Cursor cursor = getInstance(context).rawQuery("select * from " + DatabaseScript.TABLE_USER_INFO, null);
        Log.i(TAG, "获取用户列表时数据库版本:" + getInstance(context).getVersion());
        List<UserDomain> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            UserDomain domain = new UserDomain();
            //新版本表中修改的字段
            domain.id = cursor.getString(cursor.getColumnIndex("id"));
            domain.name = cursor.getString(cursor.getColumnIndex("name"));
            list.add(domain);
        }
        cursor.close();
        return list;
    }

    /**
     * 获取高版本的user列表
     *
     * @return
     */
    public static List<UserDomain> getHighUserList(Context context) {
        Cursor cursor = getInstance(context).rawQuery("select * from " + DatabaseScript.TABLE_USER_INFO, null);
        Log.i(TAG, "获取用户列表时数据库版本:" + getInstance(context).getVersion());
        List<UserDomain> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            UserDomain domain = new UserDomain();
            //新版本表中修改的字段
            domain.id = cursor.getString(cursor.getColumnIndex("userId"));
            domain.name = cursor.getString(cursor.getColumnIndex("userName"));
            //新版本表中增加的
            domain.phone = cursor.getString(cursor.getColumnIndex("phone"));
            list.add(domain);
        }
        cursor.close();
        return list;
    }

    /**
     * 假设第一个版本数据库，已有部分信息（以一张表userInfo为例）。
     *
     * @param sqLiteDatabase
     */
    private void loadLowVersionData(SQLiteDatabase sqLiteDatabase) {
        //模拟低版本初始化已有的数据
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put("id", "1");
        contentValues1.put("name", "张三");
        sqLiteDatabase.insert(DatabaseScript.TABLE_USER_INFO, null, contentValues1);
        ContentValues contentValues2 = new ContentValues();
        contentValues2.put("id", "2");
        contentValues2.put("name", "李四");
        sqLiteDatabase.insert(DatabaseScript.TABLE_USER_INFO, null, contentValues2);
        ContentValues contentValues3 = new ContentValues();
        contentValues3.put("id", "3");
        contentValues3.put("name", "王五");
        sqLiteDatabase.insert(DatabaseScript.TABLE_USER_INFO, null, contentValues3);
        ContentValues contentValues4 = new ContentValues();
        contentValues4.put("id", "4");
        contentValues4.put("name", "陈琦");
        sqLiteDatabase.insert(DatabaseScript.TABLE_USER_INFO, null, contentValues4);
    }

    /**
     * 当前数据库版本号
     *
     * @param context
     * @return
     */
    public static int getVersion(Context context) {
        return getInstance(context).getVersion();
    }
}
