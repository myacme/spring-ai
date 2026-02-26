@echo off
echo 正在启动快递查询Agent服务...
echo 请确保已配置以下环境变量：
echo DEEPSEEK_API_KEY=your_deepseek_api_key
echo KUAIDI100_KEY=your_kuaidi100_key
echo KUAIDI100_CUSTOMER=your_kuaidi100_customer
echo.

REM 检查Java版本
java -version
echo.

REM 使用简化版pom.xml编译运行
mvn -f pom-simple.xml clean compile spring-boot:run

pause