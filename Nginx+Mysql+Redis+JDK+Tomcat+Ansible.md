###安装机器
```
172.27.13.92
172.27.13.98
```

###分区
```
查看分区情况和硬盘情况
fdisk -l
df -h
```

###分区：由上一步  fdksk -l 中的未分区的情况得到  /dev/sdb没分区：
```
fdisk /dev/sdb  #对第二块硬盘进行操作  
Command (m for help): n  #新增加一个分区  
p  #选择“增加主分区“  
Partition number (1-4):1     #选择作为1号分区  
回车  
回车  
Command (m for help): t   #选择分区类型  
Selected partition 1  
Hex code (type L to list codes): 83   #选择第83号分区类型（linux类型）
Command (m for help): w  #保存到硬盘
```

###格式化与挂载
```
mkfs.ext4 /dev/sdb
mount /dev/sdb /export
df -h   #查看硬盘情况，这时已经有刚分区的那盘了
```

### 下载安装包
```
PCRE库
ftp://ftp.csx.cam.ac.uk/pub/software/programming/pcre/pcre-8.39.tar.gz
zlib库
http://zlib.net/zlib-1.2.11.tar.gz
SSL
https://www.openssl.org/source/openssl-1.1.0b.tar.gz
Nginx
http://nginx.org/download/nginx-1.10.2.tar.gz
Mysql 
https://dev.mysql.com/get/Downloads/MySQL-5.7/mysql-5.7.18-linux-glibc2.5-x86_64.tar.gz
redis3.2.9
http://download.redis.io/releases/redis-3.2.9.tar.gz
php7.0.6
http://cn2.php.net/get/php-7.0.6.tar.gz/from/this/mirror
tomcat8.5
http://ftp.cuhk.edu.hk/pub/packages/apache.org/tomcat/tomcat-8/v8.5.15/bin/apache-tomcat-8.5.15.tar.gz
其他软件如libxml2,libpng等
http://linux.softpedia.com/get/Programming/Libraries/?utm_source=spd&utm_campaign=postdl_redir
下载好了以后放到 /usr/local/src中
```

### 安装Nginx
```
### 安装PCRE库
cd /usr/local/src
tar -zxvf pcre-8.39.tar.gz
cd pcre-8.39
./configure
make && make install

### 安装zlib库
cd /usr/local/src
tar -zxvf zlib-1.2.11.tar.gz
cd zlib-1.2.11
./configure
make && make install

### 安装ssl
cd /usr/local/src
tar -zxvf openssl-1.1.0b.tar.gz
cd openssl-1.1.0b
./config
make && make install

### 安装Nginx
cd /usr/local/src
tar -zxvf nginx-1.10.2.tar.gz
cd nginx-1.10.2
groupadd -r nginx
useradd -r -g nginx nginx

./configure \
  --prefix=/usr/local/nginx \
  --sbin-path=/usr/local/nginx/sbin/nginx \
  --conf-path=/usr/local/nginx/nginx.conf \
  --pid-path=/usr/local/nginx/nginx.pid \
  --user=nginx \
  --group=nginx \
  --with-http_ssl_module \
  --with-http_flv_module \
 --with-http_mp4_module  \
 --with-http_stub_status_module \
 --with-http_gzip_static_module \
 --http-client-body-temp-path=/var/tmp/nginx/client/ \
 --http-proxy-temp-path=/var/tmp/nginx/proxy/ \
 --http-fastcgi-temp-path=/var/tmp/nginx/fcgi/ \
 --http-uwsgi-temp-path=/var/tmp/nginx/uwsgi \
 --http-scgi-temp-path=/var/tmp/nginx/scgi \
 --with-pcre=/usr/local/src/pcre-8.39 \
 --with-zlib=/usr/local/src/zlib-1.2.11 \
 --with-openssl=/usr/local/src/openssl-1.1.0b
 
make && make install
mkdir -p /var/tmp/nginx/client
netstat -ano|grep 80
/usr/local/nginx/sbin/nginx
```


### 安装Mysql
```
groupadd mysql
useradd -r -g mysql mysql
cd /usr/local/src
tar -vxzf mysql-5.7.18-linux-glibc2.5-x86_64.tar.gz
mv mysql-5.7.18-linux-glibc2.5-x86_64 /usr/local/mysql
cd /usr/local/
chown -R mysql mysql/
chgrp -R mysql mysql/
cd /usr/local/mysql
bin/mysqld --initialize --user=mysql --basedir=/usr/local/mysql --datadir=/usr/local/mysql/data
# 密码为随机的 uaUHOChhf0+h 或cat /root/.mysql_secret这样去看密码
bin/mysql_ssl_rsa_setup  --datadir=/usr/local/mysql/data
cp -a support-files/mysql.server  /etc/init.d/mysqld
# 修改配置文件  把里面的datadir 改为  datadir = /usr/local/mysql/data
sed -i "/^datadir=/c\datadir=/usr/local/mysql/data" /etc/my.cnf
sed -i "/^pid-file=/c\pid-file=/var/lib/mysql/mysqlid.pid" /etc/my.cnf
ln -s /var/lib/mysql/mysql.sock /tmp/mysql.sock
# 启动mysql
bin/mysqld_safe --user=mysql --skip-grant-tables&
# 进入mysql命令行
bin/mysql -uroot -p
# 在mysql命令中输入  
use mysql;
update user set authentication_string=password('123456') where user='root';
grant all privileges on *.* to root@'%' identified by '1qaz@WSX123';
flush privileges;
# 重启与开机启动
/etc/init.d/mysqld restart
chkconfig --level 35 mysqld on
```



### 安装Redis
```
cd /usr/local/src
tar -vxzf redis-3.2.9.tar.gz
cd redis-3.2.9
make
cd src
make install
mkdir -p /usr/local/redis/bin
mkdir -p /usr/local/redis/etc
mv /usr/local/src/redis-3.2.9/redis.conf /usr/local/redis/etc
mv redis-benchmark redis-check-aof redis-cli redis-server /usr/local/redis/bin
#vim /usr/local/redis/etc/redis.conf  把daemonize属性改为yes
#requirepass foobared 前面的注释去掉并把foobared改为如jd.com密码
#启动
/usr/local/bin/redis-server /usr/local/redis/etc/redis.conf
```

### 安装jdk
```
cd /usr/local/src
tar -vxzf jdk-8u131-linux-x64.tar.gz
mv jdk1.8.0_131 /usr/local/java
#配置环境变量
# vim /etc/profile
export JAVA_HOME=/usr/local/java
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
export PATH=$JAVA_HOME/bin:$PATH
#source /etc/profile
```

### 安装Tomcat
```
cd /usr/local/src
tar -vxzf apache-tomcat-8.5.15.tar.gz
mv apache-tomcat-8.5.15 /usr/local/tomcat8.5
/usr/local/tomcat8.5/bin/startup.sh
```

### 安装PHP7.0.6
```
cd /usr/local/src
tar -vxzf php-7.0.6.tar.gz
cd php-7.0.6

#以下两行是简单的安装
yum install libxml2 libxml2-devel
./configure -prefix=/usr/local/php -enable-fpm

#这是安装时带上其他模块，libxml,png-dir,freetype等都是提前源码安装好，并且不需要yum任何东西，就可以安装成功。json,pdo,mysqli等这些php里就自带了，在安装php时带上就可以。
./configure \
--prefix=/usr/local/php \
--with-libxml-dir=/usr/local/libxml2 \
--with-png-dir=/usr/local/libpng \
--with-freetype-dir=/usr/local/freetype \
--enable-soap \
--enable-mbstring=all \
--enable-sockets \
--enable-fpm \
--enable-zip \
--enable-json \
--with-mysqli=mysqlnd \
--with-pdo-mysql=mysqlnd \
--with-mysql-sock=mysqlnd \
--enable-pdo

make && make install
cp php.ini-production /usr/local/php/etc/php.ini
cp /usr/local/php/etc/php-fpm.conf.default /usr/local/php/etc/php-fpm.conf
cp /usr/local/php/etc/php-fpm.d/www.conf.default /usr/local/php/etc/php-fpm.d/www.conf
# 启动 /usr/local/php/sbin/php-fpm
# 查看安装了哪些模块
/usr/local/php/bin/php -m
```

### 安装Ansible

1、先更新yum源
```
cd /etc/yum.repos.d
mv CentOS-6.6-Base.repo CentOS-6.6-Base.repo.bak
#用163的源
wget http://mirrors.163.com/.help/CentOS6-Base-163.repo
#或中科大源
wget http://centos.ustc.edu.cn/CentOS-Base.repo
#或sohu源
wget http://mirrors.sohu.com/help/CentOS-Base-sohu.repo
yum makecache
yum clean all
yum update
```

2、安装ansible
```
yum install ansible
#可能会安不上，提示  No package ansible available.
#原理：Ansible是属于Extra Packages for Enterprise Linux (EPEL)库的一部分，因此要先安装EPEL
yum install epel-release
yum repolist
#再执行
yum install ansible
```

3、配置ansible要集群的服务器
```
vim /etc/ansible/hosts
在最后加入一组机器
[webservers]
172.27.13.92
172.27.13.98
#在92和98中 vim ~/.ssh/authorized_keys
#把96(安装ansible的机器)的~/.ssh/id_rsa.pub的内容复制到此authorized_keys文件的最后。
#先在96上执行 ssh root@172.27.13.92  和 ssh root@172.27.13.98看能不能连进去。
#测试看是否通
ansible all -m ping
```

4、写ansible安装要用到的相关脚本
```
以php来说，进入/etc/ansible目录，自己写的程序都放在这目录下。首先这目录下新建一个php.yml文件:
```
```
---
- name: php
  hosts: webservers
  remote_user: root
  roles:
    - php
```
```
新建roles目录，里面放php目录，php目录里有子目录files、handlers、tasks、templates、vars:
files 里放置一个安装的shell文件(install_php.sh)，和安装时要用的源码包如php7.0.6.tar.gz
注意install_php.sh文件需要是utf-8编码格式的。
vars 目录里放属性常量定义的，如各种目录的路径。
tasks 放安装前的准备工作，安装工作，启动工作等三部分的脚本，如main.yml里这样写：
---
- include: ready.yml
- include: install.yml
- include: startup.yml
```
5、执行安装
```
cd /etc/ansible
ansible-playbook php.yml
#或者这样执行
ansible-playbook ags-deploy.yml --extra-vars="{'host':'webservers'}"
ansible-playbook ags-deploy.yml --extra-vars="{'host':'webservers','mysql_ip':'172.27.13.92','redis_ip':'172.27.13.92'}"
###数据计算&数据集成 安装传递参数实例
ansible-playbook xdata-deploy.yml --extra-vars="{'host':'webservers','mysql_ip':'172.27.13.92','redis_ip':'172.27.13.92','maps':[{'name':'bds-rest-dbus','port':16001},{'name':'dts-web','port':16002},{'name':'map-web','port':16003},{'name':'xdata-ras-api','port':16004}]}"
```
