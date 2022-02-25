# Prerequisites for development

## Setting up Husky and commit linting

Husky is dependent on NodeJS, for this we use version 16.14.0 as it's the latest stable version. Make sure it's installed on your system, or do so like this:

### Windows
Install NodeJS over at https://nodejs.org/en/download/. Make sure to install the right version.

### Ubuntu
```bash
sudo apt install curl
curl -sL https://deb.nodesource.com/setup_16.x | sudo -E bash -
sudo apt install nodejs
node -v
> v16.14.0
```

Next, you should just run `npm install` in the main directory of the project (same location as the .husky directory). This should fix the installation for you.


## Frontend
Run `npm install` in the frontend directory to install the required files. More information can be found in the [README.md](../frontend/README.md) file in the frontend directory.

## Backend
Run `./mvnw spring-boot:run` in the backend directory to compile and run the backend project.
