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

### Linting Code
Linting is done using the [Ktlint Maven Plugin](https://github.com/gantsign/ktlint-maven-plugin)
There are 2 possible ways to lint your code, one is through the command line and the other through IntelliJ IDEA.

Command Line:
- Checking code for style violations
```sh 
mvn ktlint:check
```
- Format code
```sh 
mvn ktlint:format
```
- Generate project report
```sh 
mvn ktlint:ktlint
```

These commands are also available in IntelliJ. To navigate to these commands follow `Maven (top right) > Plugins > ktlint`.
A .editorconfig file is also added to ensure everyone uses the same coding style, make sure to enable the editorconfig plugin in IntelliJ to use this!