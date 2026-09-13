# 开发规范

本文档供 **AI 编码助手** 阅读。

## 环境

- Java 21、Maven 3.9.6、Spring Boot 4.1.1
- 虚拟线程（Virtual Threads）：`spring.threads.virtual.enabled: true`
- 本地默认端口：`48792`（见 `application.yaml`）
- Docker 容器端口：`8080`（映射主机端口 `48792`）

## 命令

```bash
mvn spring-boot:run
mvn test
mvn clean package
```

构建产物：`target/spring-async-demo.jar`。CI 构建使用 Dragonwell JDK 21。

## 目录结构

```
src/main/java/cn/netbuffer/spring/async/demo/
├── SpringAsyncDemoApplication.java       # 应用启动入口（@EnableAsync）
├── config/
│   └── AsyncConfig.java                  # 异步线程池配置（ate，支持虚拟线程/平台线程）
├── controller/
│   └── AsyncController.java              # 异步任务测试接口（含虚拟线程状态接口）
└── service/
    └── TaskService.java                  # 异步任务具体实现
src/main/resources/
└── application.yaml                      # 核心配置文件（虚拟线程开启与 task.execution 配置）
src/test/java/                             # SpringBootTest & MockMvc 完整测试用例
http/                                      # HTTP 请求示例与环境配置 (http-client.env.json)
```

## 核心约定

- 命名风格：`*Controller`、`*Service`、`*Config`、`*Application`、`*ApplicationTests`
- 异步与虚拟线程：通过 `@EnableAsync` 开启 Spring 异步支持；默认启用 Java 21 虚拟线程执行器 `SimpleAsyncTaskExecutor`
- 保持轻量示例规模：避免无必要引入复杂中间件或安全组件
- 不要随意改动 Java/Spring Boot 大版本与基础包结构

## 联改规则

| 改动 | 同步更新 |
|------|----------|
| 端口改动 | `application.yaml`、`Dockerfile`、`docker-compose.yml`、`README.md`、`http/http-requests.http`、`http/http-client.env.json` |
| 依赖与插件变更 | `pom.xml`、`README.md` |

## 提交规范

遵循 Conventional Commits：`<type>: <说明>`（英文祈使句、小写开头、无句末句号）。

类型推荐：`feat` `fix` `docs` `refactor` `test` `chore` `perf`

- 一事一提交；相关文档/HTTP 示例可在同个提交中更新
- 用户未明确要求时禁止直接 `git commit` / `git push`
- 禁止提交任何生产密钥、Token 或敏感账号信息

## CI / 发布

`.github/workflows/build.yml`：
- 推送或 PR 到 `master` / `main` 分支时触发构建、单元测试并上传 jar 包 Artifact。
- 推送 `v*` 标签时自动创建 GitHub Release 并构建推送镜像到 `ghcr.io`。

## 安全与边界

- 不把真实密钥/敏感信息硬编码到代码或配置文件中
- 保持改动聚焦，严禁脱离需求的非必要大重构
