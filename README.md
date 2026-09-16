# micro-cloud-archetype

基于 Spring Cloud Alibaba 2023 最新生态的企业级微服务脚手架，内置权限体系、全套生产级通用组件、多中间件整合，支持 Docker 一键部署与本地零中间件启动，可直接二次开发。

## 技术栈

| 分类 | 技术 | 版本 |
|---|---|---|
| 基础框架 | JDK / Spring Boot | 17 / 3.2.4 |
| 微服务 | Spring Cloud / Spring Cloud Alibaba | 2023.0.1 / 2023.0.1.0 |
| 注册/配置中心 | Nacos | 2.x |
| 网关 | Spring Cloud Gateway | - |
| 认证鉴权 | Sa-Token（Redis 会话共享） | 1.38.0 |
| ORM | MyBatis-Plus | 3.5.5 |
| 缓存/锁 | Redis + Redisson（二级缓存 Caffeine） | 3.27.2 |
| 消息队列 | RocketMQ | 5.x |
| 对象存储 | MinIO | 8.5.7 |
| 分布式事务 | Seata（AT 模式） | 2.0.0 |
| 任务调度 | XXL-Job | 2.4.1 |
| 限流熔断 | Sentinel（网关规则持久化 Nacos） | - |
| 链路追踪 | SkyWalking | 9.7.0 |
| 接口文档 | Knife4j（网关聚合） | 4.3.0 |
| Excel | EasyExcel | 3.3.4 |
| 前端 | Vue 3 + Vite + Element Plus + Pinia | - |

## 架构总览

```
浏览器/小程序 ──> micro-web(3000) ──/dev-api──> micro-gateway(8080)
                                                    │ Sa-Token 统一鉴权 / 限流 / 路由
                    ┌───────────────┬───────────────┼───────────────┐
                micro-auth(9200) micro-user(9201) micro-file(9300) micro-message(9400) micro-demo(9500)
                    │               │                │                │                  │
                    └───────┬───────┴────────────────┴────────────────┴──────────────────┘
                            ▼
        MySQL / Redis / MinIO / RocketMQ / Seata-TC / XXL-Job-Admin
        注册配置中心: Nacos(dev/test/prod 命名空间)   监控: SkyWalking
```

## 目录结构

```
├── src/modules/
│   ├── micro-common/        # 通用组件（core/web/redis/mybatis/sentinel/rocketmq/minio/seata）
│   ├── micro-gateway/       # 网关（8080）
│   ├── micro-auth/          # 认证中心（9200）
│   ├── micro-user/          # 用户权限服务（9201）
│   ├── micro-file/          # 文件服务（9300）
│   ├── micro-message/       # 消息服务（9400）
│   ├── micro-demo/          # 业务示例（分布式事务/定时任务，9500）
│   └── micro-job-client/    # XXL-Job 客户端 starter
├── micro-web/               # Vue3 前端（3000）
├── config/nacos/            # Nacos 配置快照（文件命名：{GROUP}__{dataId}）
├── scripts/                 # 运维脚本（Nacos 配置一键导入）
├── sql/                     # 数据库初始化脚本
├── deploy/jvm/              # JVM 参数模板（dev/test/prod）
├── docker-compose.yml       # 中间件一键拉起
└── .github / .gitlab-ci.yml # CI/CD 流水线模板
```

## 快速开始

### 前置条件

JDK 17、Maven 3.8+、Node 18+、Docker（可选）

### 方式一：完整模式（推荐，体验全部能力）

```bash
# 1. 一键拉起中间件（MySQL/Redis/Nacos/RocketMQ/MinIO/SkyWalking/XXL-Job/Seata）
docker-compose up -d

# 2. 导入 Nacos 配置（自动创建 dev/test/prod 命名空间并发布全部配置）
powershell -ExecutionPolicy Bypass -File scripts/import-nacos-config.ps1

# 3. 编译并按顺序启动：gateway -> auth -> user（其余按需）
mvn clean install -DskipTests

# 4. 启动前端
cd micro-web && npm install && npm run dev
```

访问：前端 `http://127.0.0.1:3000`，网关聚合文档 `http://127.0.0.1:8080/doc.html`

### 方式二：本地模式（零 Nacos，快速验证）

只需 MySQL + Redis，通过环境变量关闭 Nacos：

```bash
# 关闭 Nacos 配置拉取与注册（使用本地 application.yml 兜底配置）
NACOS_CONFIG_ENABLED=false NACOS_DISCOVERY_ENABLED=false
```

在 IDE 启动参数或环境变量中设置上述两项后，依次启动 `micro-gateway`、`micro-auth`、`micro-user` 即可。
注意：本地模式下网关 `lb://` 路由不可用（无注册中心），适合单服务调试；完整链路请用方式一。

## 端口清单

| 服务/组件 | 端口 | 说明 |
|---|---|---|
| micro-web | 3000 | 前端（/dev-api 代理到网关） |
| micro-gateway | 8080 | 网关 |
| micro-auth | 9200 | 认证中心 |
| micro-user | 9201 | 用户权限 |
| micro-file | 9300 | 文件服务 |
| micro-message | 9400 | 消息服务 |
| micro-demo | 9500 | 业务示例 |
| Nacos | 8848 / 9848 | 控制台 / gRPC |
| MySQL | 3306 | root / micro123456 |
| Redis | 6379 | 密码 micro123456 |
| MinIO | 9000 / 9001 | API / 控制台（admin / micro123456） |
| RocketMQ | 9876 / 10911 | NameServer / Broker |
| XXL-Job Admin | 8081 | admin / 123456 |
| Seata Server | 8091 / 7091 | TC / 控制台 |
| SkyWalking | 12800 / 11800 | UI / OAP |

## 默认账号

| 入口 | 账号 | 密码 |
|---|---|---|
| 前端系统 | admin | admin123 |
| Nacos 控制台 | nacos | nacos |
| MySQL | root | micro123456 |

## 常用环境变量

所有配置均支持环境变量覆盖（`${ENV:默认值}` 占位符）：

| 变量 | 默认值 | 说明 |
|---|---|---|
| NACOS_SERVER_ADDR | 127.0.0.1:8848 | Nacos 地址 |
| NACOS_NAMESPACE | dev | 命名空间 |
| NACOS_USERNAME / NACOS_PASSWORD | nacos / nacos | Nacos 鉴权 |
| NACOS_CONFIG_ENABLED | true | false=不拉远程配置（本地模式） |
| NACOS_DISCOVERY_ENABLED | true | false=不注册（本地模式） |
| MYSQL_HOST / MYSQL_PORT | 127.0.0.1 / 3306 | MySQL |
| MYSQL_USERNAME / MYSQL_PASSWORD | root / micro123456 | MySQL 账号 |
| REDIS_HOST / REDIS_PORT | 127.0.0.1 / 6379 | Redis |
| REDIS_PASSWORD | micro123456 | Redis 密码 |
| MINIO_ENDPOINT | http://127.0.0.1:9000 | MinIO |
| ROCKETMQ_CONSUMER_ENABLED | false | RocketMQ 消费者开关 |
| SEATA_ENABLED | true | Seata 开关 |

## 开发约束

- 新增业务模块：复制 `micro-demo` 为模板，修改 `spring.application.name`、端口、库名
- 不要修改 common / gateway / auth / user / file 底座模块，能力扩展通过新增 starter 实现
- 配置变更：修改 `config/nacos/` 下对应文件后重新执行导入脚本，保持仓库与 Nacos 一致
- 每个业务模块自管自己的数据库（建库与业务表 SQL 放模块内 `sql/` 目录）

## FAQ

**Q: 启动报 Nacos 连接失败？**
A: 确认 Nacos 已启动且 `8848/9848` 端口可达；开启了鉴权时确认 `NACOS_USERNAME/NACOS_PASSWORD` 正确；或改用本地模式。

**Q: 网关 503 / Feign 调不通？**
A: 检查目标服务是否已注册到 Nacos 的 `dev` 命名空间（控制台 → 服务列表）。

**Q: 修改了 Nacos 配置不生效？**
A: 带 `@RefreshScope` 的配置自动刷新；数据源等启动期配置需重启服务。
