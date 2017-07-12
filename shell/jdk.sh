#!/bin/bash

cd /root/soft
#wget http://download.oracle.com/otn-pub/java/jdk/8u131-b11/d54c1d3a095b4ff2b6607d096fa80163/jdk-8u131-linux-x64.tar.gz?AuthParam=1499675958_607b4d05e174a4815f73f3e8ce0401ed
tar -vxzf jdk-8u131-linux-x64.tar.gz
mv jdk1.8.0_131 /usr/local/java
cd /home/hadoop/
/bin/bash jdk_export.sh

SERVERS=(
	s2
	s3
	s4
)
for SERVER in ${SERVERS[@]}
do
	scp -r /usr/local/java root@$SERVER:/usr/local/
	scp jdk_export.sh root@$SERVER:/root
    	ssh root@$SERVER /bin/bash /root/jdk_export.sh
    	ssh root@$SERVER /etc/profile
done