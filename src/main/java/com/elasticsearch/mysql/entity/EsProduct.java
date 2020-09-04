package com.elasticsearch.mysql.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @Description
 * @Author hgm
 * @Time 2020/8/17 16:47
 */
@Entity
@Data
@Table(name = "esproduct")
public class EsProduct implements Serializable {
    private static final long serialVersionUID = -1;

    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "title")
    private String title;
}
