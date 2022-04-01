# Next.js


Start the development server.
```sh
yarn dev
```

Build the app for production.
```sh 
yarn build
```

Run the built app in production mode.
```sh
yarn start
```

## Environment Variables

For development, you should put these variables in a `.env.local` file.
For production a normal `.env` suffices, but this file needs to be instantiated on build time, otherwise it won't get loaded succesfully.
The `NEXT_PUBLIC` prefix exposes the env var to the whole application, not just the NextJS specific items.

```sh
NEXT_PUBLIC_API_ENDPOINT=... # URL of the backend, default should be http://localhost:8080/api
```

## Running linters
While in /frontend run following commands to show style errors
```shell
yarn run eslint .
yarn run prettier --check .
```
Both linters can autofix errors using
```shell
yarn run eslint --fix .
yarn run prettier --write .
```
Linters will only check files in frontend/pages, frontend/public, frontend/styles and frontend/src.\
To add more locations update both frontend/.eslintignore and frontend/.prettierignore.
