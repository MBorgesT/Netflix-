FROM openjdk:21

RUN mkdir /data/
RUN mkdir /app/

COPY out/artifacts/backend_jar/ /app/
COPY packaging.sh /app/packaging.sh

CMD ["java", "-jar", "/app/backend.jar"]