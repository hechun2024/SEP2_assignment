# 第一阶段：下载阶段 (命名为 loader)
FROM eclipse-temurin:21-jdk AS loader

# 只需要安装下载工具
RUN apt-get update && apt-get install -y wget unzip

# 下载并解压 JavaFX SDK
RUN wget https://download2.gluonhq.com/openjfx/21/openjfx-21_linux-aarch64_bin-sdk.zip -O /tmp/javafx.zip && \
    unzip /tmp/javafx.zip -d /opt && \
    rm /tmp/javafx.zip

# 第二阶段：最终运行阶段
FROM eclipse-temurin:21-jdk

# 设置环境变量，方便后续维护
ENV JFX_PATH=/opt/javafx-sdk-21/lib

# 安装运行所需的底层库（去掉 wget 和 unzip，减小体积）
# 使用 --no-install-recommends 进一步瘦身
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    libgtk-3-0 libgl1 libgl1-mesa-dri libx11-6 libxtst6 libxi6 libxrender1 && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# 从 loader 阶段把解压好的 SDK 拷贝过来
COPY --from=loader /opt/javafx-sdk-21 /opt/javafx-sdk-21

WORKDIR /app

# 拷贝 Jar 包
COPY target/assignment2-shopping-cart-ui-1.0-SNAPSHOT.jar /app/app.jar

# 运行命令
ENTRYPOINT ["java", \
    "--module-path", "/opt/javafx-sdk-21/lib", \
    "--add-modules", "javafx.controls,javafx.fxml", \
    "-jar", "app.jar"]