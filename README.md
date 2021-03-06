# Distribution-Sale

# 前言

分销，对于初创产品的前期推广和和业务营销都是很有必要的。有了分销才有分润，在分润的激励下，在产品前期用户的积极性将得到极大的提升。

针对单个产品，本系统提供分销服务，以作为产品的**分销解决方案**
。利用熟人社交，人脉等方式进行产品推广、商品销售等，利用这种裂变方式，可以达到精准投放的效果，使产品能够得到良好的收益和推广，同时也为产品积累用户和活跃度，帮助产品度过最为艰难的初期，**最终实现盈利**。

针对多个产品，本系统**支持多租户**（本系统中体现为“平台”），支持数据库行级别的数据隔离。您可以作为您产品体系中的分销中心，最大限度的复用分销能力的同时节省服务器资源和运维人力资源。

# 开源说明

**GITEE：**[**https://gitee.com/fufeii/distribution-sale**](https://gitee.com/fufeii/distribution-sale)
**版权声明：**[**Apache License 2.0**](https://gitee.com/fufeii/distribution-sale/blob/master/LICENSE)

# 系统依赖

| **名称** | **版本** | **作用** |
| --- | --- | --- |
| java | 8 | java环境支持 |
| mysql | 8 | 数据持久化 |
| redis | 6 | 数据缓存、分布式锁 |

# 项目亮点

1. 支持多租户，行级别的租户数据隔离
1. 会员账户多样，支持金钱账户、积分账户
1. 内置多种分润，包括邀请好友注册分润、会员段位升级分润、商品金钱交易分润
1. 业务易于扩展，支持分润全链路的扩展，例如自定义的业务分润策略、分润事件推送策略
1. 完善的安全机制，基于HmacSHA256的签名机制和~~RSA2048的加密机制~~，有效保障API的安全性
1. 自研管理后台，提供可视化界面操作、~~运营数据统计~~
1. 部署简单，提供多种部署方案脚本

# 系统介绍

## 工程结构

| 名称 | 类型 | 作用 |
| --- | --- | --- |
| ds-admin | jar | WEB界面管理端服务 |
| ds-common | module | 公共模块 |
| ds-repository | module | 实体、实体仓库操作模块 |
| ds-server | jar | Server后端服务 |

注：`ds-admin`
服务的前端UI采用[Pear-Admin-Layui](https://gitee.com/pear-admin/Pear-Admin-Layui)实现，admin后端接口为restful接口。若需改造为前后分离模式，则直接结合页面接口和swagger文档即可。

## 系统API

> 注意：此API来自`ds-server`服务，业务系统后台通过HTTP协议在不同的场景下调用分销系统

| **类型** | **名称** | **描述** |
| --- | --- | --- |
| 会员API | 创建会员 | 创建会员，可以理解为同步主系统的会员 |
| 会员API | 查询会员详情 | 查询会员详情，包括段位、身份、账户信息等 |
| 会员API | 分页查询会员团队 | 分页查询给定级别的下的会员树数据 |
| 会员API | 更新会员身份 | 同步主系统的会员身份 |
| 会员API | 更新会员状态 | 同步主系统的会员状态 |
| 账户API | 发起账户变动 | 针对金钱账户和积分账户的增加、减少、冻结操作（提现功能等） |
| 账户API | 分页查询会员账户变动记录 | 分页查询会员的账户流水记录数据 |
| 分润API | 发起金钱交易 | 发起一笔需要进行分润的交易，针对此交易进行分润（如果可以） |
| 分润API | 查询分润事件 | 查询指定分销事件的详情，包括获得分润会员的分润数据 |
| 分润API | 分页查询会员分润记录 | 分润查询给定会员的分润流水数据 |

## 核心交互

注意：仅展示了核心功能的交互流程图
> **会员注册**
![UML-会员注册](./assert/image/UML-流程-会员注册.jpg)

> **会员交易**
![UML-会员交易](./assert/image/UML-流程-会员交易.jpg)

## 核心API

注意：仅展示了核心API的交互流程图
> **会员注册**
![UML-时序-会员注册.jpg](./assert/image/UML-时序-会员注册.jpg)

> **会员交易**
![UML-时序-会员交易.jpg](./assert/image/UML-时序-会员交易.jpg)

# 快速开始

注意：本次以`ds-server`为示例进行构建

## 部署项目

确保redis可用，mysql可用

```sql
CREATE DATABASE `ds` DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;
```

### 本地启动

选择一：使用IDEA启动

选择二：使用jar包启动

```bash
java -jar ds-server-x.x.x-SNAPSHOT.jar
```

### 使用dockfile

确保 Dockerfile文件（dockerfile-server）和server服务jar包（ds-server-x.x.x-SNAPSHOT.jar）在同一目录

```bash
# 构建镜像
docker build --no-cache -f ./dockerfile-server -t ds-server:1.0 ./
```

通过`-e ACTIVE=your_active`指定环境，此配置等价于`spring.profiles.active`，若需要映射日志文件则增加`-v ./log:/runtime/app_server_log`

```shell
# 运行实例
docker run -d -p 8081:8081 -e ACTIVE=loc --name ds-server ds-server:1.0
```

访问控制台输出的系统地址

### 使用docker-compose

> 注意：推荐使用此方法进行**演示**，不需要自己准备mysql和redis环境

将项目中的`docker-compose.yaml`和`dockerfile-server`以及`server服务jar`和`dockerfile-admin`以及`admin服务jar`和`initdb.sql`放在同一文件夹下

```sql
-- initdb.sql
CREATE DATABASE `ds` DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;
```

```shell
# 构建镜像
docker-compose build --no-cache
```

```shell
# 运行实例
docker-compose up -d
```

访问控制台输出的系统地址

## 分润准备

> 第零步：创建平台

启动项目，包括`admin服务`和`server服务`
> 第一步：创建平台

使用ds超管账号 `admin/123456789`登录admin服务 在 用户管理 => 平台管理 创建一个平台 在 系统设置 => 创建一个平台用户账号
> 第二步：设置分润配置

登录上一步设置的平台管理员账号 在 分润管理 => 分润配置 创建分润配置 在 分润管理 => 段位配置 创建段位配置 注：此模块可以根据业务场景设置不同的参数
> 第三步：模拟后台交互调用

选择一：将项目中的`ds.postman_collection.json`文件导入postman软件中，查看接口说明

选择二：访问`server`服务的接口文档，查看接口详情，接口作用可以参考[系统API](#系统API)章节

# 免责声明

**本项目仅提供一种参考和示例，您使用本项目及其衍生项目（基于本项目修改）用于任何用途时，请遵守国家法律法规，若您因此产生任何违法行为，均与本人无关， 特此声明。**