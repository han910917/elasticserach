package com.elasticsearch.mysql.service;

import java.io.IOException;

/**
 * @Description
 * @Author hgm
 * @Time 2020/8/28 9:19
 */
public interface CaiDanService {
    Object findByKey(String key) throws IOException;

    Object multiSearch(String key) throws IOException;

    Object searchTemplate(String key) throws IOException;

    Object searchTemplates(String key) throws IOException;

    Object explainRequest(String key) throws IOException;

    Object countRequest(String key) throws IOException;

    Object info() throws IOException;

    Object xPackInfoRequest() throws IOException;

    Object analyzeRequest() throws IOException;

    Object createIndexRequest(String index) throws IOException;
}
