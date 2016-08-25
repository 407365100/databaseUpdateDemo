# databaseUpdateDemo
数据库升级简单案例
##概述
当app升级时，不可避免的数据库可能会有所改变。比如新增一张表、改变某张表中的字段名、添加一个新的字段等一系列表结构的改变。按照现在来说，解决的方法只有两个：
1. 卸载当前版本，安装最新版的；
2. 更新数据库；
当然第一种方案，简单但同时用户体验会 超差。所以我们选另外一个
##实现方案
更新数据库又可分为两种：
例如app版本有v1、v2、v3，当前版本为v3
方案1:
v1到v2,v2到v3这样一级一级往上升，比如我现在是v1就先要升级到v2，再升级到v3
**优点**是每次更新数据库的时候只需要在onUpgrade方法的末尾加一段从上个版本升级到新版本的代码，易于理解和维护，**缺点**是当版本变多之后，多次迭代升级可能需要花费不少时间，增加用户等待；
方案2：
v1到v2或v2到v3或v1到v3，不论版本之间相差多少，数据库只用升级一次。但当版本比较多时，语句特别复杂
**优点**则是可以保证每个版本的用户都可以在消耗最少的时间升级到最新的数据库而无需做无用的数据多次转存，**缺点**是强迫开发者记忆所有版本数据库的完整结构，且每次升级时onUpgrade方法都必须全部重写
###逻辑导图
![这里写图片描述](http://img.blog.csdn.net/20160825104808887)
##相关类
**SQLiteOpenHelper.java**
```java
/**
 * A helper class to manage database creation and version management.
 * 一个数据库创建及版本管理的类
 * <p>You create a subclass implementing {@link #onCreate}, {@link #onUpgrade} and
 * 你需要创建当前类的一个子类，并且实现onCreate、onUpgrade、onOpen(可选的，如果要实现可重写父类该方法)
 * optionally {@link #onOpen}, and this class takes care of opening the database
 * if it exists, creating it if it does not, and upgrading it as necessary.
 * 这个类负责连接数据库、创建数据、更新数据库的操作
 * Transactions are used to make sure the database is always in a sensible state.
 * 事务保证数据库每次操作后都在一个合理状态
 * <p>This class makes it easy for {@link android.content.ContentProvider}
 * implementations to defer opening and upgrading the database until first use,
 * to avoid blocking application startup with long-running database upgrades.
 * 这个类让ContentProvider更容易实现延迟连接、更新数据库，避免更新数据库时间过久而导致阻塞
 * 
 * <p>For an example, see the NotePadProvider class in the NotePad sample application,
 * in the <em>samples/</em> directory of the SDK.</p>
 *
 * <p class="note"><strong>Note:</strong> this class assumes
 * monotonically increasing version numbers for upgrades.</p>
 * 对于更新（升级）数据库而言当前版本号是单调递增的
 */
public abstract class SQLiteOpenHelper 
```
系统对SQLiteOpenHelper的释义
**SQLiteOpenHelperWrap.java**
getWritableDatabase()的时候才会创建数据库
```java
private final static int version = DatabaseScript.DATABASE_HIGH_VERSION;

public SQLiteOpenHelperWrap(Context context) {
    super(context, databaseName, null, version);
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
```
databaseName创建的数据库名，version当前的版本；假定初始数据库版本为1，如果传入的版本为2则会走onUpgrade方法，更新数据库表结构。具体代码详见案例
**UpgradeUtils.java**
数据库升级工具类 
```java
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
		              break;
		      }
		  }
		}
}
```
##效果图
###高低版本如何切换
1. SQLiteOpenHelperWrap.java29、30行做相应改变
```java
//        private final static int version = DatabaseScript.DATABASE_LOW_VERSION;
    private final static int version = DatabaseScript.DATABASE_HIGH_VERSION;
```
2. MainActivity.java43、44做相应改变
```java
//        mUserList = SQLiteOpenHelperWrap.getLowUserList(mContext);
        mUserList = SQLiteOpenHelperWrap.getHighUserList(mContext);
```

###版本1
####**数据库表结构:**
![这里写图片描述](http://img.blog.csdn.net/20160825095419315)
####**数据库表记录:**
![这里写图片描述](http://img.blog.csdn.net/20160825095604614)
####**效果图：**
![这里写图片描述](http://img.blog.csdn.net/20160825100016384)
###版本2
####**数据库表结构:**
![这里写图片描述](http://img.blog.csdn.net/20160825102240486)
####**数据库表记录:**
![这里写图片描述](http://img.blog.csdn.net/20160825102318549)
####**效果图：**
![这里写图片描述](http://img.blog.csdn.net/20160825101939173)
##项目地址
[https://github.com/407365100/databaseUpdateDemo](https://github.com/407365100/databaseUpdateDemo)
