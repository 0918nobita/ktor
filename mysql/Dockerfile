FROM mysql

RUN apt-get update

RUN apt-get install -y tzdata && \
    cp /usr/share/zoneinfo/Asia/Tokyo /etc/localtime

ADD my.cnf /etc/mysql/conf.d/my.cnf

CMD ["mysqld"]
