#!/bin/bash

# 要免密的服务器ip
SERVERS=(
	s1
	s2
	s3
	s4
)
# 密码
PASSWORD=iloveme

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
id_rsa_file="/root/.ssh/id_rsa.pub"
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
ssh_copy_id_to_all() {
    for SERVER in ${SERVERS[@]}
    do
        auto_ssh_copy_id $SERVER $PASSWORD
    done
}

ssh_copy_id_to_all

