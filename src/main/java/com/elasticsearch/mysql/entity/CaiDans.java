package com.elasticsearch.mysql.entity;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Description
 * @Author hgm
 * @Time 2020/8/27 19:06
 */
@Data
@Document(indexName = "caidan", type = "product", shards = 1, replicas = 0)
public class CaiDans {

    @Id
    private Integer id;

    @Field(analyzer = "ik_max_word", type = FieldType.Text)
    private String cai_ming;

    @Field(type = FieldType.Keyword)
    private String address;

    @Field(analyzer = "ik_max_word", type = FieldType.Text)
    private String cai_liao;

    @Field(type = FieldType.Keyword)
    private String tu_pian_address;

    @Field(type = FieldType.Keyword)
    private Integer liu_lan;

    @Field(type = FieldType.Keyword)
    private Integer sou_cang;
}
