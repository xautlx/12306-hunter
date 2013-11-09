12306-hunter
============

Java Swing C/S版本12306订票助手

> 自从有了12306.cn，作为标准程序员闲的没事就有了新的乐趣练练手，本程序完全开放源代码，仅作为技术学习研究交流之用，不得用于任何商业用途

基于HttpClient、Multiple Thread、File I/O等主要技术的Java Swing桌面应用，至于说用途就不多说了，你懂的。

* 直接以HTTP GET/POST发起最小数量必须的订票请求，相比浏览器插件方式更加快速高效；

* 基于多线程多账号登录并发刷票，更高的订票成功率；

* 基于文件记录最后输入的订票数据，提高交互友好体验；

* 该程序只核心关注以最高效快速提交订票请求，不支持诸如自动登录、识别验证码、支付等其他高级功能！

如果有任何问题或建议反馈，请到 https://github.com/xautlx/12306-hunter/issues 提交Issue；也欢迎本着交流进步为目的的改进优化代码并直接提交Pull Request。

### 用法说明

程序采用Java语言编写实现，因此需要安装Java运行环境。理论上Java 5,6,7 版本皆可运行。

如果系统已安装过Java运行环境，则直接执行startup.bat即可。

当然如果不懂Java也没关系，请自行访问Oracle下载安装Java运行环境：

http://www.oracle.com/technetwork/java/javase/downloads/jre7-downloads-1880261.html

选择“Accept License Agreement”，然后点击相应系统版本下载安装即可（可能需要重启系统），之后执行startup.bat即可。


### 界面截图

![Snapshot View](https://raw.github.com/xautlx/12306-hunter/master/snapshot/index.gif)