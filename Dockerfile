FROM adoptopenjdk/openjdk11:alpine-jre

# Refer to Maven build -> finalName
ARG JAR_FILE=target/test/telegrambot-0.0.1-SNAPSHOT.jar

# cd /opt/app
WORKDIR /build
COPY . .
RUN wget https://dl.google.com/cloudsql/cloud_sql_proxy.linux.amd64 -O /build/cloud_sql_proxy
RUN chmod +x /build/cloud_sql_proxy
COPY run.sh /build/run.sh
RUN chmod +x /build/run.sh
RUN apk --no-cache add ca-certificates
COPY credentials.json /build/credentials.json
RUN ls
# java -jar /opt/app/app.jar
CMD ["./run.sh"]