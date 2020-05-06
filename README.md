# THEOL 网络教学综合平台 中国海洋大学 (OUC)  
**题库爬取(毛概 or maybe other...)**



## 使用说明

1. IDEA打开项目,安装好依赖

2. 在Parser类的main方法里设置几个参数

   + url :填对应课程点进去的url,参照里面的格式去找

   + 登陆方式(2选1)

     + cookie: 自己在网页登陆,复制cookie里的JSESSIONID
     + 账号密码: 使用账号密码来登录

     

## 自动刷题用法

取消Parser类的main方法里`webReptiler.auto_click_test(); ` 函数的注释,该函数有两个参数,照注释填写即可

```
* @param testid 测试页面的testid,用浏览器F12开发者工具可以查看到
* @param test_num 要自动测试的次数
```

注意这里的自动刷题是随机答的,不保证正确率. 

如果自己 已经答了很多次题,可以注释掉这一行,程序会自动获取已答的题目



## 功能

+ 自动登陆
+ 自动刷题,(瞎答,如果老师说测试分数计入成绩的话慎用)
+ 自动获取已答的题库,解析出题目并持久化到数据库(具有题目去重功能)
+ 从数据库中拿出数据渲染成HTML页面,生成的html代码会直接打印在控制台,复制到一个html文件里即可



## Other

`毛概题库2019春.html` 文件是19年春季学期使用该程序爬的毛概题库,供参考

19年的古董程序,年久失修,不保证能用,网站改动不大的话可以尝试小改一下代码运行

时间很紧,代码丑陋