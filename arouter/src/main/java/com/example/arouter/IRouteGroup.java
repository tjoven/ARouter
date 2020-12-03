package com.example.arouter;

import com.example.annotation.RouteMeta;

import java.util.Map;

/**
 * Author: 夏胜明
 * Date: 2018/3/29 0029
 * Email: xiasem@163.com
 * Description:
 */

public interface IRouteGroup {
    void loadInto(Map<String, RouteMeta> atlas);
}
