# 使用 JDK 21 作为基础镜像
FROM eclipse-temurin:21-jdk

# 安装运行图形界面所需的 Linux 底层库
RUN apt-get update && \
    apt-get install -y \
    libgtk-3-0 libgl1 libgl1-mesa-dri libx11-6 libxtst6 libxi6 libxrender1 wget unzip && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# 下载适用于 Apple Silicon (ARM64) 的 JavaFX SDK
RUN wget https://download2.gluonhq.com/openjfx/21/openjfx-21_linux-aarch64_bin-sdk.zip -O /tmp/javafx.zip && \
    unzip /tmp/javafx.zip -d /opt && \
    rm /tmp/javafx.zip

WORKDIR /app

# 将你本地 target 文件夹下的 jar 包拷贝进来
# 请确保文件名与你 target 目录下的完全一致
COPY target/assignment2-shopping-cart-ui-1.0-SNAPSHOT.jar /app/app.jar

# 运行命令：手动指定模块路径和需要加载的 JavaFX 模块
ENTRYPOINT ["java", \
    "--module-path", "/opt/javafx-sdk-21/lib", \
    "--add-modules", "javafx.controls,javafx.fxml", \
    "-jar", "app.jar"]