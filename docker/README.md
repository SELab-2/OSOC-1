## Docker instructions:

### Bash Script
Instead of manually running these commands you can run the bash script `rund`. To see all the commands run:
```shell
./rund help
```

### Build docker image
First create a docker image based upon the Dockerfile, this will also couple init.sql to the docker container.
Changes to either the Dockerfile or init.sql means you will need to remove and then rebuild the container. \
The following command assumes you are located in the folder where this readme is located.
Also keep in mind these commands won't work on the server since all these docker names end in \_dev.
```shell
docker build -t osoc_postgres_image_local_dev:latest .
```

### Run docker container
After building the docker image you can run it as a container with the following command:
```shell
docker run -d \
  --name osoc_postgres_container_local_dev \
  -p 5432:5432 \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=osoc \
  osoc_postgres_image_local_dev:latest
```
Note: Persistent volumes were only tested on Ubuntu not on Windows.
On Windows you should first share the folder via docker desktop.

### Container commands:
You can start, stop or remove a container with the following commands:
```shell
docker container start osoc_postgres_container_local_dev
docker container stop osoc_postgres_container_local_dev
docker container rm osoc_postgres_container_local_dev
```

### Script init.sql
The script init.sql will be run only once, when you create the container via docker run. \
Be aware that you can delete or overwrite data already in the database from a previous container!

### Non-persistent docker container for testing
When you do not want the database files to be persistent across container instances
and want to automatically remove the container after you are done;
use the --rm flag and remove the -v flag.
```shell
docker run --rm -d \
  --name osoc_postgres_container_local_dev \
  -p 127.0.0.1:5432:5432 \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=osoc \
  osoc_postgres_image_local_dev:latest
```
Not specifying the -v flag means docker will create a new volume to store the database,
you can remove all volumes not linked to an existing container via
```shell
docker volume prune
```


### Docker information
List all existing containers: ```docker ps -a``` or ```docker container ls -a``` \
List all existing images: ```docker image ls -a``` \
List all docker created volumes: ```docker volume ls``` \
Note: using an existing folder as a volume will not show up in this list. \
Any of the results from the above commands can be inspected using ```docker inspect <id>```
where id can be an image, name, repository, volume, ... \
And as always --help is your friend.
