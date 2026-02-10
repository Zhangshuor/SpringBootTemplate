package com.example.demo.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * MyBatis-Plus 代码生成器示例
 *
 * 使用前请根据实际数据库配置修改常量，然后执行 main 方法生成代码。
 */
public class MybatisPlusCodeGenerator {

    private static final String URL = "jdbc:mysql://localhost:3306/demo_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
        String outputDir = projectPath + "/src/main/java";

        FastAutoGenerator.create(URL, USERNAME, PASSWORD)
                .globalConfig(builder -> builder
                        .author("generator")
                        .disableOpenDir()
                        .outputDir(outputDir)
                )
                .packageConfig(builder -> builder
                        .parent("com.example.demo")
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl")
                        .controller("controller")
                        .pathInfo(Collections.singletonMap(OutputFile.xml, projectPath + "/src/main/resources/mapper"))
                )
                .strategyConfig(builder -> builder
                        .addInclude("t_user")
                        .entityBuilder()
                        .enableLombok()
                        .enableTableFieldAnnotation()
                        .controllerBuilder()
                        .enableRestStyle()
                        .mapperBuilder()
                        .enableBaseResultMap()
                        .enableBaseColumnList()
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}

