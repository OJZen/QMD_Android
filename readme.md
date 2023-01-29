## 开源说明

QMD于2022.9.14停止服务。

2023.1.18：兑现之前的承诺，将QMD安卓端开源。项目可以正常编译和运行，但是QMD脱离服务器无法正常工作，例如播放和下载歌曲。

之前的图标是我本人的设计，由于这个图标我还有别的用途，所以本项目不会使用原版图标。我在[www.iconfont.cn](https://www.iconfont.cn/collections/detail?spm=a313x.7781069.1998910419.dc64b3430&cid=12553)找到一个比较中意的图标作为替换。

## 项目说明

这个项目基本是我的练手实践的地方，大量代码体现我从一个菜鸟变成不那么菜的菜鸟。我想说的是，项目的代码质量参差不齐，虽然去年我有努力去改善整个项目结构，但每次看到大量垃圾代码总会让我快速丧失动力。最终还是没有改变这是坨屎山代码的事实。

项目当前还处于开发下一个版本的过程中，因此在UI界面上会有一些“未完工”的痕迹。我记得当时准备使用一个超酷的列表框架，并且重做了搜索框，还准备重做顶栏。但突如其来停止维护的决定让我放下了所有开发工作。

项目最开始是由Java语言构建，后期逐步转为Kotlin。

下面大概说一下本项目主要用到的框架。

* 绝大部分页面使用[Navigation](https://developer.android.google.cn/guide/navigation?hl=zh-cn)进行跳转。

* 网络请求使用Retrofit框架 + 协程。

* 数据库使用[Room](https://developer.android.google.cn/training/data-storage/room?hl=zh-cn)。

* 部分页面采用MVVM架构。

* 播放器使用[Exoplayer](https://developer.android.google.cn/guide/topics/media/exoplayer?hl=zh-cn)构建。

* 下载功能使用[Aria](https://github.com/AriaLyy/Aria)库。

我想大家对具体开发没有太多兴趣，主要是对QMD所采用的接口感兴趣。这里就不仔细阐述项目结构了。

## 关于接口

我并没有特地整理过QQ音乐的接口，因为QMD所使用的接口比较少。如果想参考QQ音乐接口，本项目可能并不是特别合适。如果想看比较完善的QQ音乐接口，可以参考[jsososo/QQMusicApi](https://github.com/jsososo/QQMusicApi)。

## 关于服务器端

服务器端不会开源。但我可以说一下服务器端做了什么工作。

首先，服务器端会对客户端的数据进行增删查改，关于这点可以在`app/src/main/java/com/qmd/jzen/api/services/QMDService.kt`里面看到，这是服务器端的所有接口。

其次，服务器端还运行着一个程序用于定时获取绿钻账号的Cookie，并将其保存到数据库。

所以服务器端除了数据库外有两套程序：提供WebAPI的后端程序和自动获取Cookie的程序。

## 关于未来

我有想过把QMD做成自己设置Cookie的QQ音乐下载工具，不依赖服务器运行，但具体可行性还没有测试。如果有比较好的QQ登录方案，我可能会继续维护这个项目，作为开源项目。

## Telegram群组

[QMD交流群](https://t.me/+gc0qPKIJuQg2ZDg1)

