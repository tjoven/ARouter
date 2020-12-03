package com.example.compiler;

import com.example.annotation.Constant;
import com.example.annotation.Path;
import com.example.annotation.RouteMeta;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static java.lang.System.out;

/**
 * ARouter原理剖析及手动实现
 * https://www.jianshu.com/p/857aea5b54a8
 *
 */
@AutoService(Processor.class)//触发注解处理器处理注解，并生成相应代码和class文件
@SupportedAnnotationTypes("com.example.annotation.Path")// 注解处理器中支持的注解类型
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AnnotationProcessor extends AbstractProcessor {

    /**
     * key:组名 value:类名
     */
    private Map<String, String> rootMap = new TreeMap<>();
    /**
     * 分组 key:组名 value:对应组的路由信息
     */
    private Map<String, List<RouteMeta>> groupMap = new HashMap<>();



    /**
     * 节点工具类 (类、函数、属性都是节点)
     */
    private Elements elementUtils;
    /**
     * type(类信息)工具类
     */
    private Types typeUtils;
    /**
     * 文件生成器 类/资源
     */
    private Filer filerUtils;

    private String moduleName;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@ init");
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        filerUtils = processingEnvironment.getFiler();

        //参数是模块名 为了防止多模块/组件化开发的时候 生成相同的 xx$$ROOT$$文件
        Map<String, String> options = processingEnvironment.getOptions();
        if (!Utils.isEmpty(options)) {
            moduleName = options.get(Constant.ARGUMENTS_NAME);
        }
        if (Utils.isEmpty(moduleName)) {
            throw new RuntimeException("Not set processor moudleName option !");
        }

    }

    /**
     *
     * @param annotations 使用了支持处理注解的节点集合
     * @param roundEnvironment 表示当前或是之前的运行环境,可以通过该对象查找找到的注解。
     * @return true 表示后续处理器不会再处理(已经处理)
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@ process");
        out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@ annotations"+annotations.size());
        if (annotations != null && annotations.size() > 0) {
            //被Route注解的节点集合
            Set<? extends Element> rootElements = roundEnvironment.getElementsAnnotatedWith(Path.class);
            if (rootElements != null && rootElements.size() > 0) {
                processorRoute(rootElements);
            }
            return true;
        }
        return false;
    }

    private void processorRoute(Set<? extends Element> rootElements) {
        //获得Activity这个类的节点信息
        TypeElement activity = elementUtils.getTypeElement(Constant.ACTIVITY);
        out.println("activity: "+activity.toString());
        for (Element element:rootElements){
            RouteMeta routeMeta;
            //类信息
            TypeMirror typeMirror = element.asType();
            Path route = element.getAnnotation(Path.class);
            if (typeUtils.isSubtype(typeMirror, activity.asType())) {//封装 path
                routeMeta = new RouteMeta(RouteMeta.Type.ACTIVITY, route, element);
            }else {
                throw new RuntimeException("Just support Activity Route: " + element);
            }
            categories(routeMeta);//path 加入到map集合
        }

        TypeElement iRouteGroup = elementUtils.getTypeElement(Constant.IROUTE_GROUP);
        TypeElement iRouteRoot = elementUtils.getTypeElement(Constant.IROUTE_ROOT);
        out.println("iRouteGroup: "+iRouteGroup.toString());
        out.println("iRouteRoot: "+iRouteRoot.toString());

        //生成Group记录分组表。  生成java文件
        generatedGroup(iRouteGroup);

        //生成Root类 作用：记录<分组，对应的Group类> 。 生成java文件
        generatedRoot(iRouteRoot, iRouteGroup);
    }

    /**
     * 生成Root类  作用：记录<分组，对应的Group类>
     * @param iRouteRoot
     * @param iRouteGroup
     */
    private void generatedRoot(TypeElement iRouteRoot, TypeElement iRouteGroup) {
        //创建参数类型 Map<String,Class<? extends IRouteGroup>> routes>
        //Wildcard 通配符
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(iRouteGroup))
                ));
        //参数 Map<String,Class<? extends IRouteGroup>> routes> routes
        ParameterSpec parameter = ParameterSpec.builder(parameterizedTypeName, "routes").build();
        //函数 public void loadInfo(Map<String,Class<? extends IRouteGroup>> routes> routes)
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constant.METHOD_LOAD_INTO)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(parameter);
        //函数体
        for (Map.Entry<String, String> entry : rootMap.entrySet()) {
            methodBuilder.addStatement("routes.put($S, $T.class)", entry.getKey(), ClassName.get(Constant.PACKAGE_OF_GENERATE_FILE, entry.getValue()));
        }
        //生成$Root$类
        String className = Constant.NAME_OF_ROOT + moduleName;
        TypeSpec typeSpec = TypeSpec.classBuilder(className)
                .addSuperinterface(ClassName.get(iRouteRoot))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodBuilder.build())
                .build();
        try {
            JavaFile.builder(Constant.PACKAGE_OF_GENERATE_FILE, typeSpec).build().writeTo(filerUtils);
            out.println("Generated RouteRoot：" + Constant.PACKAGE_OF_GENERATE_FILE + "." + className);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generatedGroup(TypeElement iRouteGroup) {
        //创建参数类型 Map<String, RouteMeta>
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouteMeta.class));
        ParameterSpec altas = ParameterSpec.builder(parameterizedTypeName, "atlas").build();

        for (Map.Entry<String, List<RouteMeta>> entry : groupMap.entrySet()) {
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constant.METHOD_LOAD_INTO)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(altas);  //定义函数 类变量

            String groupName = entry.getKey();
            List<RouteMeta> groupData = entry.getValue();
            for (RouteMeta routeMeta : groupData) {
                //函数体的添加
                methodBuilder.addStatement("atlas.put($S,$T.build($T.$L,$T.class,$S,$S))",
                        routeMeta.getPath(),
                        ClassName.get(RouteMeta.class),
                        ClassName.get(RouteMeta.Type.class),
                        routeMeta.getType(),
                        ClassName.get(((TypeElement) routeMeta.getElement())),
                        routeMeta.getPath(),
                        routeMeta.getGroup());
            }
            String groupClassName = Constant.NAME_OF_GROUP + groupName;
            TypeSpec typeSpec = TypeSpec.classBuilder(groupClassName)
                    .addSuperinterface(ClassName.get(iRouteGroup))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(methodBuilder.build())
                    .build();
            JavaFile javaFile = JavaFile.builder(Constant.PACKAGE_OF_GENERATE_FILE, typeSpec).build();
            try {
                javaFile.writeTo(filerUtils);
            } catch (IOException e) {
                out.println(e.toString());
                e.printStackTrace();
            }
            rootMap.put(groupName, groupClassName);

        }
    }
    /**
     * 检查是否配置 group 如果没有配置 则从path截取出组名
     * @param routeMeta
     */
    private void categories(RouteMeta routeMeta) {
        if (routeVerify(routeMeta)) {
            out.println("Group : " + routeMeta.getGroup() + " path=" + routeMeta.getPath());
            //分组与组中的路由信息
            List<RouteMeta> routeMetas = groupMap.get(routeMeta.getGroup());
            if (Utils.isEmpty(routeMetas)) {
                routeMetas = new ArrayList<>();
                routeMetas.add(routeMeta);
                groupMap.put(routeMeta.getGroup(), routeMetas);
            } else {
                routeMetas.add(routeMeta);
            }
        } else {
            out.println("Group info error:" + routeMeta.getPath());
        }
    }

    /**
     * 验证path路由地址的合法性
     * @param routeMeta
     * @return
     */
    private boolean routeVerify(RouteMeta routeMeta) {
        String path = routeMeta.getPath();
        String group = routeMeta.getGroup();
        // 必须以 / 开头来指定路由地址
        if (!path.startsWith("/")) {
            return false;
        }
        //如果group没有设置 我们从path中获得group
        if (Utils.isEmpty(group)) {
            String defaultGroup = path.substring(1, path.indexOf("/", 1));
            //截取出的group还是空
            if (Utils.isEmpty(defaultGroup)) {
                return false;
            }
            routeMeta.setGroup(defaultGroup);
        }
        return true;
    }

}
