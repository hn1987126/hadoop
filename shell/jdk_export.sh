#!/bin/bash
cat >> /etc/profile << EOF
export JAVA_HOME=/usr/local/java
export PATH=\$PATH:\$JAVA_HOME/bin
EOF