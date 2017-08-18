#!/bin/bash
# /bin/bash ssh_xiupass.sh "172.22.178.209,172.22.178.210,172.22.178.211,172.22.178.212,172.22.178.213,172.22.178.214,172.22.178.215,172.22.178.216,172.22.178.217,172.22.178.218,172.22.178.219,172.22.178.220,172.22.178.221" 1qaz@WSX 1qaz@WSX456

# 要免密的服务器ip
SERVERS=$1
# 密码
PASSWORD=$2
OLD_PASSWORD=$3


hosts=""
#i=1
xiupass() {
        IFS=","
        arr=($SERVERS)
        for ip in ${arr[@]}
        do
		expect -c "set timeout -1;
	        spawn ssh root@$ip passwd
	        expect {
	            *New* {send -- $PASSWORD\r;exp_continue;}
	            *Retype* {send -- $PASSWORD\r;exp_continue;}
	            *root* {send -- $OLD_PASSWORD\r;exp_continue;}
	            eof        {exit 0;}
	        }";

        done
}



xiupass
