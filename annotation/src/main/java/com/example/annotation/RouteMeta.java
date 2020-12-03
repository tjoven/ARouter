package com.example.annotation;

import javax.lang.model.element.Element;

public class RouteMeta {

    private Type type;

    public void setType(Type type) {
        this.type = type;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public void setDestination(Class<?> destination) {
        this.destination = destination;
    }

    /**
     * 注解使用的类对象
     */
    private Class<?> destination;

    /**
     * 节点（Activity）
     */
    private Element element;

    public RouteMeta(Type type, Element element, Class<?> destination, String path, String
            group) {
        this.type = type;
        this.destination = destination;
        this.element = element;
        this.path = path;
        this.group = group;
    }

    public RouteMeta() {
    }

    public Type getType() {
        return type;
    }

    public Element getElement() {
        return element;
    }

    public String getPath() {
        return path;
    }

    public String getGroup() {
        return group;
    }

    /**
     * 路由地址
     */
    private String path;

    /**
     * 路由组
     */
    private String group;

    public RouteMeta(Type type, Path route, Element element) {
        this.type = type;
        this.element = element;
        this.path = route.path();
    }


    public static RouteMeta build(Type type, Class<?> destination, String path, String
            group) {
        return new RouteMeta(type, null, destination, path, group);
    }


    public void setPath(String path) {
        this.path = path;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public enum Type {
        ACTIVITY
    }

}
