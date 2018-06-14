package cn.xj.common.service;

import cn.xj.common.model.BaseInfo;

import java.util.List;

public interface BaseService {

    List query(BaseInfo info) throws Exception;

    List queryOnes(BaseInfo info) throws Exception;

    Object queryOne(BaseInfo info) throws Exception;

    boolean save(BaseInfo info) throws Exception;

    boolean update(BaseInfo info) throws Exception;

    boolean remove(BaseInfo info) throws Exception;

}
