# Uses the postgres:latest image from dockerhub as a base
# You can find the source code for this image at https://github.com/docker-library/postgres/blob/master/14/alpine/Dockerfile
FROM postgres

# Stop docker from creating a second volume as stated in the base postgres image
VOLUME /var/lib/postgresql/data

# Add the init.sql file located in the same directory as a startup script
COPY init.sql /docker-entrypoint-initdb.d/

# Persistent volumes only tested on Ubuntu not on Windows

# Commands to run, assuming terminal location is same directory as Dockerfile and init.sql:
# docker build -t osoc_postgres_image_local_dev:latest .
# docker run -d \
#   --name osoc_postgres_container_local_dev \
#   -p 127.0.0.1:5432:5432 \
#   -v /some/placeholder:/var/lib/postgresql/data \
#   -e POSTGRES_USER=postgres \
#   -e POSTGRES_PASSWORD=postgres \
#   -e POSTGRES_DB=osoc \
#   osoc_postgres_image_local_dev:latest

# To auto remove container after stopping use --rm flag:
# docker run --rm -d \
#   --name osoc_postgres_container_local_dev \
#   -p 127.0.0.1:5432:5432 \
#   -v /some/placeholder:/var/lib/postgresql/data \
#   -e POSTGRES_USER=postgres \
#   -e POSTGRES_PASSWORD=postgres \
#   -e POSTGRES_DB=osoc \
#   osoc_postgres_image_local_dev:latest

# The -v flag makes sure database storage is persistent over different container instances
# Remove this line from the run command for testing with a new temporary database
# You will see docker create a new volume for storage using:
# docker volume ls
# Delete the automatically create volume after removing the container with:
# docker volume prune
# This will remove all volumes created by docker not linked to an existing container
# If you use docker for other purposes as well, use docker volume inspect <volume-name> and docker volume rm <volume-name>

# To start / stop container
# docker container stop osoc_postgres_container_local_dev
# docker container start osoc_postgres_container_local_dev

# To remove container
# docker container rm osoc_postgres_container_local_dev

# To list all containers
# docker ps -a