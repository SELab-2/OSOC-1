# Architectural choices

This document explains why the technologies in the [c4 container diagram](docs/C4_container_diagram.png) were chosen.

## Frontend

### TypeScript

### Next.js


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
