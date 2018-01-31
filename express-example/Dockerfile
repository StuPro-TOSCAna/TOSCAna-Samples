FROM ubuntu:16.04
WORKDIR /redis-root
COPY *.sh /redis-root/
RUN apt-get update  && apt-get install -y sudo
RUN ./install_redis.sh
EXPOSE 6379
ENTRYPOINT ./start_redis.sh
