package com.raddle.log.viewer.utils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ProperiesExt extends Properties {
    private static final long serialVersionUID = 1L;
    private Map<String, Object> map = new LinkedHashMap<String, Object>();

    public List<String> getPropertyNameList() {
        return new LinkedList<String>(map.keySet());
    }

    @Override
    public synchronized Object put(Object key, Object value) {
        if (key != null) {
            map.put(key.toString(), value);
        }
        return super.put(key, value);
    }

    @Override
    public synchronized Object remove(Object key) {
        map.remove(key);
        return super.remove(key);
    }

}
