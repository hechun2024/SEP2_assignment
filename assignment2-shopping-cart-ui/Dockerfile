# 使用 JDK 21 基础镜像
FROM eclipse-temurin:21-jdk

# 合并所有安装命令，减少镜像层数，虽然慢点但最稳
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    libgtk-3-0 libgl1 libgl1-mesa-dri libx11-6 libxtst6 libxi6 libxrender1 wget unzip && \
    # 下载 JavaFX SDK
    wget https://download2.gluonhq.com/openjfx/21/openjfx-21_linux-aarch64_bin-sdk.zip -O /tmp/javafx.zip && \
    unzip /tmp/javafx.zip -d /opt && \
    # 清理垃圾，防止镜像大得离谱
    rm /tmp/javafx.zip && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# 拷贝你的 Jar 包
COPY target/assignment2-shopping-cart-ui-1.0-SNAPSHOT.jar /app/app.jar

# 运行
ENTRYPOINT ["java", \
    "--module-path", "/opt/javafx-sdk-21/lib", \
    "--add-modules", "javafx.controls,javafx.fxml", \
    "-jar", "app.jar"]