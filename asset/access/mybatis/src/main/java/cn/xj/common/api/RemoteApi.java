package cn.xj.common.api;

import cn.xj.common.model.BaseInfo;
import cn.xj.common.sql.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class RemoteApi {

    @Autowired
    private SqlService sqlService;

    @RequestMapping("/query")
    public List query(@RequestBody BaseInfo info) throws Exception {
        return sqlService.query(info);
    }

    @RequestMapping("/queryOnes")
    public List queryOnes(@RequestBody BaseInfo info) throws Exception {
        return sqlService.queryOnes(info);
    }

    @RequestMapping("/queryOne")
    public Object queryOne(@RequestBody BaseInfo info) throws Exception {
        return sqlService.queryOne(info);
    }

    @RequestMapping("/save")
    public boolean save(@RequestBody BaseInfo info) throws Exception {
        return sqlService.save(info);
    }

    @RequestMapping("/update")
    public boolean update(@RequestBody BaseInfo info) throws Exception {
        return sqlService.update(info);
    }

    @RequestMapping("/remove")
    public boolean remove(@RequestBody BaseInfo info) throws Exception {
        return sqlService.remove(info);
    }

    @RequestMapping("/queryById")
    public Map queryById(@RequestBody BaseInfo info) throws Exception {
        return sqlService.queryById(info);
    }

    @RequestMapping("/saveBean")
    public boolean saveBean(@RequestBody BaseInfo info) throws Exception {
        return sqlService.saveBean(info);
    }

}
