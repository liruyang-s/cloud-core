# 通用微服务构建镜像（多阶段构建）
# 用法：在流水线中以 MODULE 参数指定模块名，如 --build-arg MODULE=micro-demo
# 各业务项目直接复用，仅需修改镜像名/仓库地址

# ---------- 阶段 1：Maven 构建 ----------
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

# 先拷贝 POM 利用缓存加速依赖下载
COPY pom.xml .
COPY src/modules/micro-common/pom.xml src/modules/micro-common/pom.xml
RUN mvn -B dependency:go-offline -pl src/modules/micro-common -am || true

# 拷贝源码并打包（跳过测试，测试在流水线单独阶段执行）
COPY . .
ARG MODULE=micro-demo
RUN mvn -B clean package -pl src/modules/${MODULE} -am -DskipTests

# ---------- 阶段 2：运行镜像 ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

ARG MODULE=micro-demo
COPY --from=builder /build/src/modules/${MODULE}/target/*.jar app.jar

# JVM 参数通过环境变量注入，dev/test/prod 模板见 deploy/jvm/
ENV JAVA_OPTS="" \
    TZ=Asia/Shanghai

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
