package com.elasticsearch.mysql.entity;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.io.Serializable;

/**
 * @Description
 * @Author hgm
 * @Time 2020/8/17 16:47
 */
@Data
@Document(indexName = "elasticsearch", type = "product", shards = 1, replicas = 0)
public class EsProducts implements Serializable {
    private static final long serialVersionUID = -1;

    @Id
    private Long id;

    @Field(analyzer = "ik_max_word", type = FieldType.Text)
    private String name;

    @Field(analyzer = "ik_max_word", type = FieldType.Text)
    private String title;
}
