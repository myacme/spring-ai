#!/bin/bash
# 启动天气Agent脚本

echo "正在启动天气查询Agent..."

# 检查Java版本
java -version

# 编译项目
echo "正在编译项目..."
mvn clean compile

# 启动应用
echo "正在启动应用..."
mvn spring-boot:run