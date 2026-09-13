# ⚡ spring-async-demo

[![Java](https://img.shields.io/badge/java-21%2B-orange)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen)](https://spring.io/projects/spring-boot)
[![Virtual Threads](https://img.shields.io/badge/Virtual%20Threads-Enabled-blueviolet)](https://openjdk.org/jeps/444)
[![Docker](https://img.shields.io/badge/Docker-Dragonwell%2021-blue)](https://github.com/dragonwell-project/dragonwell21)

> Spring 异步任务示例项目，基于 **Spring Boot 4.1.1** + **Java 21 Virtual Threads (虚拟线程)** 构建。

演示 Spring `@Async` 异步执行机制、基于 Java 21 虚拟线程的自定义执行器（`ate`）、Spring Boot 内置任务执行器的虚拟线程配置（`spring.threads.virtual.enabled: true`）以及异步并发结果收集。

## ✨ 特性

- 🧵 **Java 21 虚拟线程 (Loom)**：原生开启 `spring.threads.virtual.enabled: true`，轻量级线程支撑高并发任务
- ⚡ **Spring `@Async` 异步支持**：支持无返回值及带返回值（`CompletableFuture` / `Future`）的异步调用
- ⚙️ **灵活的执行器配置**：配置基于 `SimpleAsyncTaskExecutor` 的虚拟线程池 `ate`，并支持回退切换传统平台线程池
- 🖨️ **服务地址打印**：集成 `print-server-address` 4.0.0 自动打印服务启动地址
- 🐉 **Dragonwell JDK 21**：基于 Alibaba Dragonwell JDK 21 构建与打包，支持容器化部署与 CI/CD

## 🚀 快速开始

### 📥 下载构建产物

前往 [GitHub Releases](../../releases) 页面下载最新的 `spring-async-demo.jar`。

### ▶️ 直接运行

```bash
java -jar spring-async-demo.jar
```

本地默认端口见 `application.yaml` 中的 **48792**；Docker 示例使用 **8080**。均可通过环境变量 `SERVER_PORT` 覆盖。

### 🐳 Docker 运行

先构建 jar 包，再构建镜像：

```bash
mvn clean package -DskipTests
docker build -t spring-async-demo .
```

```bash
docker run -it --rm -p 48792:8080 spring-async-demo
```

自定义 JVM 参数和环境变量：

```bash
docker run -it --rm -p 48792:8080 \
  -e TZ=Asia/Shanghai \
  -e JAVA_OPTS="-XX:+PrintCommandLineFlags" \
  spring-async-demo
```

### 🐙 Docker Compose

```bash
mvn clean package -DskipTests
docker compose up -d
```

## 🔨 构建与测试

项目使用 [Dragonwell JDK 21](https://github.com/dragonwell-project/dragonwell21) 构建。

运行全量测试（包含虚拟线程验证与 MockMvc 接口测试）：

```bash
mvn clean test
```

打包：

```bash
mvn clean package
```

构建产物位于 `target/spring-async-demo.jar`。

本地开发直接运行：

```bash
mvn spring-boot:run
```

## 🔄 CI/CD

推送至 `master` / `main` 分支或创建 `v*` 标签时，GitHub Actions 将自动：

1. 🐉 使用 Dragonwell JDK 21 编译打包并执行单元测试
2. 📦 上传构建产物（可在 Actions 页面登录下载）
3. 🏷️ 创建 `v*` 标签时，自动发布 jar 包到 GitHub Releases
4. 🐳 创建 `v*` 标签时，自动构建镜像并推送到 ghcr.io

## 📚 接口测试与虚拟线程验证

查看 `http/http-requests.http` 发起 HTTP 请求测试（支持选择 `http/http-client.env.json` 环境）：

- `GET /async/taskNoReturnValue`：测试无返回值默认异步任务（基于虚拟线程调度）
- `GET /async/taskReturnValue?count=10`：测试带返回值异步任务（并发调用并收集 Future 结果）
- `GET /async/virtualThreadInfo?count=5`：返回执行当前异步任务的线程详情及 `isVirtual: true` 状态
