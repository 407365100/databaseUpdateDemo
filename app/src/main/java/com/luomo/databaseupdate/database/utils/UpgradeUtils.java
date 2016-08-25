package com.luomo.databaseupdate.database.utils;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author :renpan
 * @version :v1.0
 * @class :com.luomo.databaseupdate.database.utils
 * @date :2016-08-22 16:19
 * @description:数据库升级工具列
 */
public class UpgradeUtils {

    /**
     * 第一种：1到2,2到3;这样一级一级网上升
     * 第二种：1到2,2到3，1到3;将每种情况都考虑到
     * 数据库更新
     */
    public static void upgradeVersion(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        try {
            if(oldVersion == 1) {
                switch (newVersion) {
                    case 2://相对于旧数据表,字段id、name改变为userId,userName、并新增了以个字段phone
                        sqLiteDatabase.beginTransaction();
                        // 1, 将表TABLE_USER_INFO重命名为TABLE_USER_INFO+"_temp"
                        String tempTableName = DatabaseScript.TABLE_USER_INFO + "_temp";
                        String sql = "ALTER TABLE " + DatabaseScript.TABLE_USER_INFO + " RENAME TO " + tempTableName;
                        sqLiteDatabase.execSQL(sql);
                        // 2, 创建用户表(字段id、name改变、并新增了以个字段phone)
                        sqLiteDatabase.execSQL(DatabaseScript.CREATE_TABLE_HIGH_USER_INFO);
                        // 3, 将旧表数据导入到新表中
                        sql = "INSERT INTO " + DatabaseScript.TABLE_USER_INFO + " (" + "userId,userName" + ") " + " SELECT " + "id,name" + " FROM " + tempTableName;
                        sqLiteDatabase.execSQL(sql);
                        // 4, 删除旧的表
                        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + tempTableName);
                        sqLiteDatabase.setTransactionSuccessful();
                        sqLiteDatabase.setVersion(2);
                        loadHighVersionData(sqLiteDatabase);
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * 更新成功之后，模拟插入数据
     * @param sqLiteDatabase
     */
    private static void loadHighVersionData(SQLiteDatabase sqLiteDatabase) {
        //模拟高版本初始化已有的数据
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put("userId", "5");
        contentValues1.put("userName", "张三");
        contentValues1.put("phone", "18900000000");
        sqLiteDatabase.insert(DatabaseScript.TABLE_USER_INFO, null, contentValues1);
        ContentValues contentValues2 = new ContentValues();
        contentValues2.put("userId", "6");
        contentValues2.put("userName", "李四");
        contentValues2.put("phone", "18900000000");
        sqLiteDatabase.insert(DatabaseScript.TABLE_USER_INFO, null, contentValues2);
        ContentValues contentValues3 = new ContentValues();
        contentValues3.put("userId", "7");
        contentValues3.put("userName", "王五");
        contentValues3.put("phone", "18900000000");
        sqLiteDatabase.insert(DatabaseScript.TABLE_USER_INFO, null, contentValues3);
        ContentValues contentValues4 = new ContentValues();
        contentValues4.put("userId", "8");
        contentValues4.put("userName", "陈琦");
        contentValues4.put("phone", "18900000000");
        sqLiteDatabase.insert(DatabaseScript.TABLE_USER_INFO, null, contentValues4);
    }
}