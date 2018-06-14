package cn.xj.common.util;

import cn.xj.common.constant.Type;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.cglib.beans.BeanMap;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ConvertUtil {

    public static String obj2Str(Object obj) {
        String s = Objects.equal(obj, null) ? null : String.valueOf(obj);
        return s;
    }

    public static Integer obj2Int(Object obj) {
        String objStr = obj2Str(obj);
        if (Strings.isNullOrEmpty(objStr)) {
            return null;
        } else if (objStr.contains(".")) {
            return Integer.valueOf(objStr.substring(0, objStr.indexOf(".")));
        } else {
            return Integer.valueOf(String.valueOf(obj));
        }
    }

    public static Long obj2Long(Object obj) {
        String objStr = obj2Str(obj);
        if (Strings.isNullOrEmpty(objStr)) {
            return null;
        } else if (objStr.contains(".")) {
            return Long.valueOf(objStr.substring(0, objStr.indexOf(".")));
        } else {
            return Long.valueOf(String.valueOf(obj));
        }
    }

    public static Double obj2Double(Object obj) {
        Double d = Objects.equal(obj, null) ? null : Double.parseDouble(String.valueOf(obj));
        return d;
    }

    public static Float obj2Float(Object obj) {
        Float f = Objects.equal(obj, null) ? null : Float.parseFloat(String.valueOf(obj));
        return f;
    }

    public static BigDecimal obj2Decimal(Object obj) {
        BigDecimal b = Objects.equal(obj, null) ? null : new BigDecimal(String.valueOf(obj));
        return b;
    }

    public static Boolean obj2Boolean(Object obj) {
        if (obj == null) {
            return false;
        }
        String s = ConvertUtil.obj2Str(obj);
        return !("0".equals(s) || "false".equals(s)) && ("1".equals(s) || "true".equals(s));
    }

    public static Object convertByType(Object obj, String type) {
        switch (Type.valueOf(type)) {
            case String:
                return obj2Str(obj);
            case Integer:
                return obj2Int(obj);
            case Long:
                return obj2Long(obj);
            case Double:
                return obj2Double(obj);
            case Float:
                return obj2Float(obj);
            case BigDecimal:
                return obj2Decimal(obj);
            case Boolean:
                return obj2Boolean(obj);
            default:
                return null;
        }
    }

    /**
     * 将对象转换为map
     *
     * @param bean
     * @return
     */
    public static <T> Map bean2map(T bean) {
        Map map = Maps.newHashMap();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(key + "", beanMap.get(key));
            }
        }
        return map;
    }

    /**
     * 将map转换为bean对象
     *
     * @param map
     * @param bean
     * @return
     */
    public static <T> T map2bean(Map map, T bean) {
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }

    /**
     * 将map转换为bean对象
     *
     * @param map
     * @param clazz
     * @return
     */
    public static <T> T map2bean(Map map, Class<T> clazz) throws IllegalAccessException, InstantiationException {
        T bean = map2bean(map, clazz.newInstance());
        return bean;
    }

    /**
     * 将List<Map>转换为List<T>
     *
     * @param maps
     * @param clazz
     * @param <T>
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> maps2beans(List<Map> maps, Class<T> clazz) throws InstantiationException, IllegalAccessException {
        List<T> list = Lists.newArrayList();
        if (maps != null && maps.size() > 0) {
            for (Map map : maps) {
                T bean = clazz.newInstance();
                map2bean(map, bean);
                list.add(bean);
            }
        }
        return list;
    }

}
