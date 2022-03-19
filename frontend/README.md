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

```sh
NEXTAUTH_URL=... # url to the auth api endpoint
NEXTAUTH_SECRET=... # Used to encode JWTs
API_ENDPOINT=... # URL of the backend, default should be http://localhost:8080/api
```