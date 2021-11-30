## mb-project-generator

使用正则表达式替换文件内的内容，

比如：把你的一个项目转换成一个新的项目，修改部分包名、pom中的项目名等

<br/>

**缺点**： 没有模板引擎一样灵活

**优点**：不限于项目代码，只要是文本文件都可以

<br/>

如果需要做特殊处理，可以自定义处理器

​    文件处理类：[TemplateResolver.java](src/main/java/cc/wanforme/mbgenerator/template/resolver/TemplateResolver.java)

​    文件到处类：[ExportResolver.java](src/main/java/cc/wanforme/mbgenerator/template/resolver/ExportResolver.java)

项目是基于 `SpringBoot` 的非 `web` 项目，所以程序入口在 [MBGenerator.java](src/main/java/cc/wanforme/mbgenerator/MBGenerator.java)



#### 配置文件

**基础配置**:  [application-template.yml](src/main/resources/config/application-template.yml)

**文本替换规则**： [rules.yml](src/main/resources/rules.yml)

**文件导出规则**： [pathRules.yml](src/main/resources/pathRules.yml)

