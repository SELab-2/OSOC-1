# Uses the postgres:latest image from dockerhub as a base
# You can find the source code for this image at https://github.com/docker-library/postgres/blob/master/14/alpine/Dockerfile
FROM postgres

# Stop docker from creating a second volume as stated in the base postgres image
VOLUME /var/lib/postgresql/data

# Add the init.sql file located in the same directory as a one-time init script to the container
COPY init.sql /docker-entrypoint-initdb.d/