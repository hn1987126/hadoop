#!/bin/bash

sed -i "/^ClientAliveInterval/d" /etc/ssh/sshd_config
sed -i '/^#ClientAliveInterval/a\ClientAliveInterval 60' /etc/ssh/sshd_config
sed -i "/^ClientAliveCountMax/d" /etc/ssh/sshd_config
sed -i '/^#ClientAliveCountMax/a\ClientAliveCountMax 6' /etc/ssh/sshd_config