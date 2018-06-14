package cn.xj.common.sql.service;

import cn.xj.common.model.BaseInfo;
import cn.xj.common.service.BaseService;

import java.util.List;
import java.util.Map;

public interface SqlService extends BaseService {

    Map queryById(BaseInfo info) throws Exception;

    boolean saveBean(BaseInfo info) throws Exception;

}
