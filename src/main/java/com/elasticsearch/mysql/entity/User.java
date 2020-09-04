package com.elasticsearch.mysql.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @Author hgm
 * @Time 2020/8/24 15:49
 */
@Data
@NoArgsConstructor
public class User {

    public String name;

    public Integer age;

    public User(String name, Integer age){
        this.name = name;
        this.age = age;
    }
}
