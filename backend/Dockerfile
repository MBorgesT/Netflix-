FROM ubuntu:22.04

RUN apt-get update
RUN apt-get install -y openjdk-21-jdk
RUN java -version

RUN apt-get -y update && apt-get -y upgrade && apt-get install -y --no-install-recommends ffmpeg

# RUN mkdir /data/
RUN mkdir /app/
# RUN mkdir openjdk-21/

# COPY openjdk-21/ /openjdk-21/

COPY out/artifacts/backend_jar/ /app/
COPY packaging.sh /app/packaging.sh

CMD ["java", "-jar", "/app/backend.jar"]
# CMD ["openjdk-21/bin/java", "-jar", "/app/backend.jar"]