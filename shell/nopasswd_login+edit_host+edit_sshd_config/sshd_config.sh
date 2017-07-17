#!/bin/bash

#在某行后面追加一行
sed -i "/^ClientAliveInterval/d" /etc/ssh/sshd_config
sed -i '/^#ClientAliveInterval/a\ClientAliveInterval 60' /etc/ssh/sshd_config
sed -i "/^ClientAliveCountMax/d" /etc/ssh/sshd_config
sed -i '/^#ClientAliveCountMax/a\ClientAliveCountMax 6' /etc/ssh/sshd_config
#替换某行的内容
#sed -i "/^datadir=/c\datadir=/usr/local/mysql/data" /etc/my.cnf