FROM openjdk:8
MAINTAINER wefashe "wefashe@qq.com"
# 挂载容器/tmp目录至宿主机，SpringBoot使用内置Tomcat，
# 默认工作目录/tmp；VOLUME不能指定挂载目录，默认挂载到宿主机/var/lib/docker目录。
VOLUME /tmp
# 变量声明，对应pom.xml的JAR_FILE标签的变量
#ARG JAR_FILE
ADD target/*.jar /app.jar
#EXPOSE 9999
ENTRYPOINT ["java","-jar","/app.jar"]