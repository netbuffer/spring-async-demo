FROM alibabadragonwell/dragonwell:21-ubuntu
LABEL author="netbuffer"
WORKDIR /
COPY target/spring-async-demo.jar /
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /spring-async-demo.jar"]
