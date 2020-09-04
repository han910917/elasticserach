package com.elasticsearch.mysql.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Description
 * @Author hgm
 * @Time 2020/8/27 19:06
 */
@Entity
@Data
@Table(name = "caidan")
public class CaiDan {

    @Id
    private Integer ID;

    @Column(name = "CaiMing")
    private String caiMing;

    @Column(name = "Address")
    private String address;

    @Column(name = "CaiLiao")
    private String caiLiao;

    @Column(name = "TuPian_Address")
    private String tuPianAddress;

    @Column(name = "LiuLan")
    private Integer liuLan;

    @Column(name = "SouCang")
    private Integer souCang;
}
