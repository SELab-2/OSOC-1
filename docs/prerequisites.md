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
Linting is done using [Ktlint](https://github.com/pinterest/ktlint#command-line-usage)
Because the maven plugin is currently broken(?), it is advised to use the Command Line Interface for this.

Command Line:
- Checking code for style violations
```sh 
# Check the style of all Kotlin files (ending with '.kt' or '.kts') inside the current dir (recursively).
# Hidden folders will be skipped.
$ ktlint
```
- Format code
```sh 
# Auto-correct style violations.
# If some errors cannot be fixed automatically they will be printed to stderr. 
$ ktlint -F "src/**/*.kt"
```
- Generate project report
```sh 
# Print style violations as usual + create report in checkstyle format, specifying report location. 
$ ktlint --reporter=plain --reporter=checkstyle,output=ktlint-report-in-checkstyle-format.xml
```

A .editorconfig file is also added to ensure everyone uses the same coding style, make sure to enable the editorconfig plugin in IntelliJ to use this!