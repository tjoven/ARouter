package com.example.arouter;

import com.example.annotation.RouteMeta;

import java.util.HashMap;
import java.util.Map;

public class Warehouse {
    // root 映射表 保存分组信息
    static Map<String, Class<? extends IRouteGroup>> groupsIndex = new HashMap<>();

    // group 映射表 保存组中的所有数据
    static Map<String, RouteMeta> routes = new HashMap<>();
}
