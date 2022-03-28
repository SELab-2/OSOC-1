# Prerequisites for development

## Documentation

A detailed explanation on how this project is being documented is available [here](documentation_standards.md).

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
Run `yarn` in the frontend directory to install the required files. More information can be found in the [README.md](../frontend/README.md) file in the frontend directory.

## Backend
Run `./mvnw spring-boot:run` in the backend directory to compile and run the backend project.

### Linting Code
Linting is done using [Ktlint](https://github.com/pinterest/ktlint#command-line-usage)
Because the maven plugin is currently broken, it is advised to use the Command Line Interface for this.

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

## Setting up the first admin

Please make sure a user had been created using the register page or using the POST method onto https://localhost:8080/api/users.
This is to create the first admin in the database since there is no other admin that can grant a user the admin role.

Note: Editing the database this way should only be done to create the first admin. 
If you want to anything else, please use the frontend or use the different endpoints.

### Development
It is also possible to follow the [production](#production) instructions for this, as the terminal is used for that.

For development, you can also use the intellij interface.
On the very far right of intellij, you can clik on the database tab.
Next, you need to click on the '+' and then select Data Source > PostgreSQL. You need to fill out the correct information.
If you haven't setup environment variables, postgres should be running on the default values.
The username default is postgres, the password default is postgres and the database name default is osoc.
After this is correctly entered, select apply and ok.
In the database tab, there should now be a PostgreSQL tab. 
If you don't see anthing when you try and open it, you need to right click it and then click refresh.
Next, go to databases > osoc > schemas > public
Then, double click on the account table.
Change the role of the correct row to 0 and the submit using ctrl+enter or using the button on the taskbar.

### Production
#### Without docker
If you are just using postgresql, then you should run and only follow the [lasts few steps](#postgresql-statements).
```shell 
psql -U $OSOC_DB_USERNAME $OSOC_DB_DBNAME -W
```

#### With docker
The container name can be found using
```shell
sudo docker ps
```

Then we want to go into the psql cli.
If you have setup environment variables, then you should only enter the container name. 
Otherwise you need to enter the default values. This should only be done in development since we highly recommend using environment variables in production.
The username default is postgres, the password default is postgres and the database name default is osoc.
```shell
sudo docker exec -it <container-name> psql -U $OSOC_DB_USERNAME $OSOC_DB_DBNAME -W
```
#### PostgreSQL statements
After that, we want to list the users and then assign it the admin role.
```shell
SELECT * FROM account;
UPDATE account SET role=0 WHERE id='<id>';
```