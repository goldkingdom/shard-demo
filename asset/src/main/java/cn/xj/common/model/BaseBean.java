package cn.xj.common.model;

import java.io.Serializable;

public class BaseBean implements Serializable, Cloneable {

    public Object clone() {
        BaseBean bean = null;
        try {
            bean = (BaseBean) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return bean;
    }

}
