package cn.xj.common.api;

import cn.xj.common.model.BaseInfo;
import feign.Headers;
import feign.RequestLine;
import org.springframework.cloud.bus.ConditionalOnBusEnabled;
import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@ConditionalOnConsulEnabled
@ConditionalOnBusEnabled
public interface FeignApi {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @RequestLine("POST /server/query")
    List query(@RequestBody BaseInfo info) throws Exception;

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @RequestLine("POST /server/queryOnes")
    List queryOnes(@RequestBody BaseInfo info) throws Exception;

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @RequestLine("POST /server/queryOne")
    Object queryOne(@RequestBody BaseInfo info) throws Exception;

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @RequestLine("POST /server/save")
    boolean save(@RequestBody BaseInfo info) throws Exception;

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @RequestLine("POST /server/update")
    boolean update(@RequestBody BaseInfo info) throws Exception;

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @RequestLine("POST /server/remove")
    boolean remove(@RequestBody BaseInfo info) throws Exception;

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @RequestLine("POST /server/queryById")
    Map queryById(@RequestBody BaseInfo info) throws Exception;

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @RequestLine("POST /server/saveBean")
    boolean saveBean(@RequestBody BaseInfo info) throws Exception;

}
