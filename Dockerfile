# Dockerfile para OSManager
# Build em múltiplos estágios (Multi-stage build)

# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-17 as builder

WORKDIR /app

# Copiar arquivos de configuração do Maven
COPY pom.xml .

# Baixar dependências
RUN mvn dependency:go-offline -B

# Copiar código-fonte
COPY src ./src

# Compilar projeto
RUN mvn clean package -DskipTests

---

# Stage 2: Runtime
FROM tomcat:9.0.87-jdk17-eclipse-temurin

# Definir variáveis de ambiente
ENV CATALINA_HOME=/usr/local/tomcat \
    JAVA_HOME=/usr/local/openjdk-17 \
    TOMCAT_USER=tomcat

# Criar usuário não-root
RUN useradd -m -u 1000 -s /bin/bash $TOMCAT_USER

# Remover aplicações padrão do Tomcat
RUN rm -rf $CATALINA_HOME/webapps/ROOT \
    && rm -rf $CATALINA_HOME/webapps/docs \
    && rm -rf $CATALINA_HOME/webapps/examples \
    && rm -rf $CATALINA_HOME/webapps/manager \
    && rm -rf $CATALINA_HOME/webapps/host-manager

# Copiar WAR do stage anterior
COPY --from=builder --chown=$TOMCAT_USER:$TOMCAT_USER \
    /app/target/osmanager-1.0-SNAPSHOT.war \
    $CATALINA_HOME/webapps/ROOT.war

# Criar diretórios necessários
RUN mkdir -p $CATALINA_HOME/logs \
    && mkdir -p $CATALINA_HOME/work \
    && chown -R $TOMCAT_USER:$TOMCAT_USER $CATALINA_HOME

# Criar setenv.sh para configurar variáveis do Tomcat
RUN echo '#!/bin/bash' > $CATALINA_HOME/bin/setenv.sh \
    && echo 'export JAVA_HOME=/usr/local/openjdk-17' >> $CATALINA_HOME/bin/setenv.sh \
    && echo 'export CATALINA_HOME=/usr/local/tomcat' >> $CATALINA_HOME/bin/setenv.sh \
    && chmod +x $CATALINA_HOME/bin/setenv.sh

# Mudar para usuário não-root
USER $TOMCAT_USER

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/ || exit 1

# Expor porta
EXPOSE 8080

# Iniciar Tomcat
CMD ["catalina.sh", "run"]

