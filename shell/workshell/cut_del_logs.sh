#!/bin/bash
#初始化
LOGS_PATH=/export/wwwlogs
#按分钟切割
#YESTERDAY=$(date -d "yesterday" +%Y%m%d%H%M)
#按每天切割
YESTERDAY=$(date -d "yesterday" +%Y%m%d%)
#按天切割日志
mv ${LOGS_PATH}/api.access.log ${LOGS_PATH}/api.access_${YESTERDAY}.log
#向nginx主进程发送USR1信号，重新打开日志文件，否则会继续往mv后的文件写数据的。原因在于：linux系统中，内核是根据文件描述符来找文件的。如果不这样操作导致日志切割失败。
kill -USR1 `ps axu | grep "nginx: master process" | grep -v grep | awk '{print $2}'`
#删除7天前的日志
cd ${LOGS_PATH}
find . -mtime +7 -name "api.access_*" | xargs rm -f
exit 0



#每天零点切割
#0 0 * * * /bin/sh /usr/local/nginx/conf/vhost/cut_del_logs.sh
#每分钟切割一次
#* */1 * * * /bin/sh /usr/local/nginx/conf/vhost/cut_del_logs.sh