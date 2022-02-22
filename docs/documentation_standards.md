This document defines how to document the different parts of the project.

# Code comments

## Kotlin

Kotlin documentation comments start with `/**` and end in `*/`. 
When writing such documentation you should follow the [KDoc](https://kotlinlang.org/docs/kotlin-doc.html#kdoc-syntax) syntax,
which is the equivalent of JavaDocs for Kotlin. This allows for documentation generation with [Dokka](https://github.com/Kotlin/dokka).

KDoc is in many ways equivalent to JavaDoc, but importantly [discourages](https://kotlinlang.org/docs/coding-conventions.html#documentation-comments)
the use of `@param` and `@returns`. Instead, incorporate these description in the main body of the documentation comment and add 
[links](https://kotlinlang.org/docs/kotlin-doc.html#links-to-elements) to parameters when they are mentioned.
It's still fine to use `@param` and `@returns` when a lengthy description is required which doesn't fit into the flow of the main text.
This means that standard documentation comment templates should not include `@param` or `@returns`.

There are other tags that are useful and recommended, however. The `@throws` or `@exception` tags indicate that a method could throw
a certain exception. Since Kotlin does not have [checked exceptions](https://beginnersbook.com/2013/04/java-checked-unchecked-exceptions-with-examples/),
this is very useful to document methods that are expected to throw errors when, for example, the method fails to connect to the server.
In the spirit of the previous paragraph, we will only use these tags when their description wouldn't fit in the flow of the main text.

A final useful tag for methods is the `@see` tag, which is a handy way to link to another relevant piece of code.

Therefore we will use the following template for methods:
```kotlin
/**
 * (Method description)
 * 
 * Optional:
 *    @param tags
 *    @returns tag
 *    @throws tags
 *    @see tags
 */
```

For classes there are a couple additional optional tags, namely the `@constructor` and `@property` tags. Again, try to include
the description of these tags in the main text if possible instead of using these tags. So for classes we have the following template:
```kotlin
/**
 * (Class description)
 *
 * Optional:
 *    @param tags
 *    @see tags
 *    @constructor tag
 *    @property tags
 */
```

[Here](https://github.com/Kotlin/dokka/blob/master/examples/gradle/dokka-gradle-example/src/main/kotlin/demo/HelloWorld.kt)
is an example of how you could document a class. From these comments, Dokka generates 
[this](https://kotlin.github.io/dokka/examples/dokka-gradle-example/html/-dokka%20-gradle%20-example/demo/index.html).




# Architectural documentation

The [C4](https://c4model.com/) model is a loose and simple model to document projects in multiple levels. 
We will only be using the first (System Context diagram) and second (Container diagram) levels to have a high level overview of the architecture.
Take a look at the link for a detailed explanation. To create these diagrams, you could use a website like [draw.io](https://app.diagrams.net/).
