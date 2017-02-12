# java-china

Java中国是一款开源免费的论坛程序，致力于打造一个简洁优质的Java程序员论坛。

演示地址 [http://java-china.org](http://java-china.org)

有任何问题可以发 [issues](https://github.com/junicorn/java-china/issues/new)

## 特性

- 界面简洁清爽，对移动端友好
- 支持markdown语法
- 支持Emoji表情输入
- 支持Github账户登录
- 支持@用户
- 支持在线播放音乐(小彩蛋)
- 每日励志名言
- 更多功能还在开发...

## 开发环境使用

1. 建立数据库javachina，编码为utf－8,导入 `javachina.sql`
2. 导入maven工程，启动 `Application.java` 的 `main` 函数
3. 访问 http://127.0.0.1:8099

## 配置 [app.properties]

- `server.port`：web服务端口
- `app.dev`：是否是开发者模式(生产环境建议关闭)
- `app.aes_salt`：AES盐值
- `famous.key`：名人名言接口密钥
- `app.site_url`：你的网站地址
- `app.version`：当前版本，用户清除静态资源缓存
- `qiniu.*`：七牛空间配置
- `mail.*`：邮箱配置，不配置无法注册

数据库配置在 `druid.properties` 配置文件。

## 预览图 

![alt](http://7xsk2r.com2.z0.glb.clouddn.com/QQ20160417-0.png)

## 提醒

由于Emoji图片太多，我把它存在 [这里](https://github.com/biezhi/emojis) ，如果你需要可以在这儿下载，本项目中删除了。

## 开源协议

[Apache2](https://github.com/junicorn/java-china/blob/master/LICENSE)

## 捐赠我们

![alt](http://7xsk2r.com2.z0.glb.clouddn.com/alipay.png)
