# Architectural choices

This document explains why the technologies in the [c4 container diagram](docs/C4_container_diagram.png) were chosen.

## Frontend

### TypeScript
For many years, [Javascript](https://www.javascript.com/) has been **the** programming language for front-end development. But it also has a lot of shortcomings, one of the main ones being that there is no real native type support. [TypeScript](https://www.typescriptlang.org/) is an extension on top of vanilla Javascript that fixes this problem, along with a load more additions. Since TypeScript compiles down to Javascript when used, this only has benefits for the dev team and there are no real downsides to using this. 

### Next.js 12
[Nextjs](https://nextjs.org/) is a [React](https://reactjs.org/) framework that has a lot of really awesome features. These include, but are not limited to, a really easy way to do routing using just the file structure of your application with so called *[pages](https://nextjs.org/docs/basic-features/pages)*; Support for Server-Side Rendering and Static-Site-Generation, which improve the user experience. This is also beneficial for caching, which leads to almost instant page loads on subsequent visits, as if you're just using a native app. Since the release of version 12, Vercel also added the ability to add [middleware](https://nextjs.org/docs/middleware) to the application, which makes authentication and the like a lot easier for us. Lastly it also greatly improves the developer experience, with features like [native Typescript support](https://nextjs.org/docs/basic-features/typescript), [Fast Refresh](https://nextjs.org/docs/basic-features/fast-refresh), easy [testing](https://nextjs.org/docs/testing) support and much more.

### Recoil (still in debate)
React is a stateful framework, but one of the drawbacks is that you sometimes need the same state in multiple components that are in different branches of the component tree. This can be fixed by lifting up the state in the components above it, but this can make the code a lot less clean. 
This is why we want to enforce the use of a global state manager like [Recoil](https://recoiljs.org/), which is built by the team behind React itself. This adds some sort of 'static' object that can hold every state you use in the application, so you can access it from anywhere.

### NextAuth.js
[NextAuth](https://next-auth.js.org/) is a simple authentication framework so you can set up authentication in your Next application in minutes. However, we are planning on routing all the authentication and OAuth2 services through the backend, so this extension will only serve as the messenger between the frontend and the backend and as the keeper of the JWT throughout the session.

### Tailwind CSS
[Tailwind CSS](https://tailwindcss.com/) is a utility CSS framework that makes designing pages a lot easier for people that don't have that much experience using vanilla CSS. It has a lot of great perks:
- It's tiny, it only ships the CSS that is needed for the application to load correctly, which means it has almost no overhead when fetching the page.
- It has effortless support for responsive designs, as it uses a mobile-first approach for adding classnames and removes the need of using the complex media queries that standard CSS uses.
- it's basically inline styling, but without all the drawbacks of traditional inline CSS queries. This also reduces the amount of files you need to create and keeps your file structure as clean as possible

Along with many more attractive features.

## Backend

### Kotlin

[Kotlin](https://kotlinlang.org/) is essentially a more modern version of Java. It has a lot of useful features not included in Java like 
[Null-safety](https://kotlinlang.org/docs/null-safety.html), [String templates](https://kotlinlang.org/docs/basic-types.html#string-templates),
[Properties](https://kotlinlang.org/docs/properties.html), [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
and a *lot* more. This significantly improves the development experience. Kotlin is still very close to Java in a lot of ways,
which makes it very easy to convert Java code to Kotlin (IntelliJ even does this 
[automagically](https://www.jetbrains.com/help/idea/get-started-with-kotlin.html#b13357a)).
All team members had enjoyed using Kotlin for the Software Engineering Lab 1 course last year. 
The choice for Kotlin was unanimous.

### Spring Boot

Spring is without a doubt the most popular web framework for Java/Kotlin. This means issues are easier to fix,
since a lot of developers will most likely have encountered them before and posted about them on sites like StackOverflow.
Spring Boot is essentially an extension of Spring, which takes an opinionated view of Spring and aims to reduce boilerplate code.
The framework also has great support for things like JPA and Keycloak. All of this makes Spring Boot the most obvious choice out there.

### Hibernate

Hibernate is the most popular implementation of the Java Persistence API (JPA), making it the most straightforward choice.
We'll mostly be limiting ourselves to 'vanilla' JPA, but Hibernate does have some useful additional features, 
like automatic generation of UUID's, which we will be using to generate the primary keys in the database.

### PostgreSQL

PostgreSQL is a widely used open-source relation database management system. It works nicely with JPA,
which is mostly why we chose it. Apart from this, all members of the team have some experience with PostgreSQL,
as it was used in the Databases course.
