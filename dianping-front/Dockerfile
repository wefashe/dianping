# 设置基础镜像
FROM nginx
# 定义作者
MAINTAINER wefashe <wefashe@qq.com>
# 将dist文件中的内容复制到 /usr/share/nginx/html/ 这个目录下面
COPY . /usr/share/nginx/html/
RUN rm /usr/share/nginx/html/nginx.conf
COPY nginx.conf /etc/nginx/conf.d/default.conf
RUN echo 'echo init ok!!'
