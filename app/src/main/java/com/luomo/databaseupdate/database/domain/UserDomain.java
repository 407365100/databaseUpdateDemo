package com.luomo.databaseupdate.database.domain;

import java.io.Serializable;

/**
 * @author :renpan
 * @version :v1.0
 * @class :com.luomo.databaseupdate
 * @date :2016-08-22 9:41
 * @description:
 */
public class UserDomain implements Serializable {
    /*原生的字段*/
    public String id;
    public String name;
    /*版本2添加的字段*/
    public String phone;
    public String country;
}
