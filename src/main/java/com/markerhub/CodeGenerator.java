package com.markerhub;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CodeGenerator {
   public static String scanner(String tip){
       Scanner scanner = new Scanner(System.in);
       StringBuilder help = new StringBuilder();
       help.append("请输入"+tip+"：");
       System.out.println(help.toString());
       if(scanner.hasNext()){
           String ipt = scanner.next();
           if(ipt!=null&&!"".equals(ipt)){
               return ipt;
           }
       }
       throw new MybatisPlusException("请输入正确的"+tip+"!");
   }
    public static void main(String[] args){
       //代码生成器
        AutoGenerator mpg = new AutoGenerator();

        //全局配置
        GlobalConfig gc =new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath+"/src/main/java");
        gc.setAuthor("jobob");
        gc.setOpen(false);
//        gc.setSwagger2(true);实体属性Swagger2注解
        gc.setServiceName("%sService");
        mpg.setGlobalConfig(gc);

        //数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://localhost:3306/vueadmin?useUnicode=ture&useSSL=false&characterEncodeing=utf-8&serverTimezone=Asia/Shanghai");
//        dsc.setSchemaName("public");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("123456");
        mpg.setDataSource(dsc);

        //包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("com.markerhub");
//        pc.setModuleName(scanner("模块名"));//模块名
        mpg.setPackageInfo(pc);
        //自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {

            }
        };

        //模板引擎freemarker
        String templatePath = "templates/mapper.xml.ftl";
        //模板引擎velocity
        //String templatePath = "templates/mapper.xml.vm";

        //自定义输出配置
        List<FileOutConfig> foclist =new ArrayList<>();
        //自定义配置会被优先输出
        foclist.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                //自定义输出文件名，如果你Entity设置了前后缀，此处注意xml的名称会跟着发生变化

                return projectPath+"/src/main/resources/mapper/"+pc.getModuleName()
                        +"/"+tableInfo.getEntityName()+"Mapper"+ StringPool.DOT_XML;
            }
        });

        cfg.setFileOutConfigList(foclist);
        mpg.setCfg(cfg);
        TemplateConfig templateConfig = new TemplateConfig();


        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setNaming(NamingStrategy.underline_to_camel);
        strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);
        strategyConfig.setSuperEntityClass("BaseEntity");
        strategyConfig.setEntityLombokModel(true);
        strategyConfig.setRestControllerStyle(true);
        //公共父类
        strategyConfig.setSuperControllerClass("BaseController");
        //写于父类中的公共给字段
        strategyConfig.setSuperEntityColumns("id","created","update","status");
        strategyConfig.setInclude(scanner("表名，多个英文逗号分割").split(","));
        strategyConfig.setControllerMappingHyphenStyle(true);
//        strategyConfig.setTablePrefix("sys_");//动态调整
        mpg.setStrategy(strategyConfig);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }

}
