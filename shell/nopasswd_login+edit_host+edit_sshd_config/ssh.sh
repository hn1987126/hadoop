#!/bin/bash
#执行命令：/bin/bash install.sh "113.209.26.122,113.209.26.123,113.209.26.124" root root@2017

# 要免密的服务器ip
SERVERS=$1
# 用户
USER=$2
# 密码
PASSWORD=$3
# 当前脚本所在的位置
THIS_PATH=$(cd `dirname $0`; pwd)

# 安装expect
yum -y install expect


# 创建id_rsa
auto_ssh-keygen() {
    expect -c "set timeout -1;
        spawn ssh-keygen;
        expect {
            *key* {send -- \r;exp_continue;}
            *passphrase* {send -- \r;exp_continue;}
            eof        {exit 0;}
        }";
}

# id_rsa如果不存在则创建
id_rsa_file="/home/${USER}/.ssh/id_rsa.pub"
if [ $USER == "root" ]; then
    id_rsa_file="/root/.ssh/id_rsa.pub"
fi
if [ ! -f "$id_rsa_file" ]; then    
	auto_ssh-keygen
else
	echo "本机id_rsa存在，略过自动创建过程";
fi

# 人机交互输入密码
auto_ssh_copy_id() {
    expect -c "set timeout -1;
        spawn ssh-copy-id $1;
        expect {
            *(yes/no)* {send -- yes\r;exp_continue;}
            *assword:* {send -- $2\r;exp_continue;}
            eof        {exit 0;}
        }";
}

# 拷贝id_rsa.pub到目标机器的authorized_keys
hosts=""
#i=1
ssh_copy_id_to_all() {
    IFS=","
    arr=($SERVERS)
    for ip in ${arr[@]}
    do
        #免密登录
        auto_ssh_copy_id $ip $PASSWORD
        hostname=`ssh $ip hostname`
        hostname_short=`ssh $ip hostname -s`

        #修改主机名
        #ssh $ip sed -i "s/^HOSTNAME/#HOSTNAME/g" /etc/sysconfig/network
        #hostname="s${i}.jcloud.local"
        #ssh $ip 'echo "HOSTNAME=$hostname" >> /etc/sysconfig/network'
        #echo "修改主机名为："$hostname;
        #ssh $ip hostname $hostname

        hosts=${hosts}${ip}":"${hostname}".."${hostname_short}","
        #let i+=1
    done
}



#修改host和sshd_config
ssh_edit_host(){
	IFS=","
        arr=($SERVERS)
	for ip in ${arr[@]}
    do
		scp ${THIS_PATH}/hosts.sh root@$ip:/root
		scp ${THIS_PATH}/sshd_config.sh root@$ip:/root
		ssh root@$ip /bin/bash /root/hosts.sh "$hosts"
		ssh root@$ip /bin/bash /root/sshd_config.sh
    done

	#修改本机host
	/bin/bash ${THIS_PATH}/hosts.sh "$hosts"
	#修改本机sshd_config
	/bin/bash ${THIS_PATH}/sshd_config.sh
}


ssh_copy_id_to_all
#ssh_edit_host
