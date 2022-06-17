FROM postgres
WORKDIR /docker-entrypoint-initdb.d
ADD schema.sql /docker-entrypoint-initdb.d
EXPOSE 5432