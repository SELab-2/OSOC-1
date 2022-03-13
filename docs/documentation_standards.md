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


## Typescript

Typescript documentation comments start with `/**` and end in `*/`.
For Typescript documentation we follow the [TSDoc](https://tsdoc.org/) standard syntax,
which can then be automatically generated into JSON and eventually into Markdown using [API Extractor/Documenter](https://api-extractor.com/pages/setup/generating_docs/).

For normal functions and classes we're going to rely on the standard TSDoc specs. Following below is an example of such documentation comments, containing some of the most important tags. A list of all tags can be found [here](https://tsdoc.org/pages/tags/alpha/).

The general tags that can be used in every context are `@example`, `@label`, `@link`, `@remarks` and `@see`. The use of `@example` is pretty straight-forward, it describes an example of the function or class and should be used when the use of the item is non-trivial. The `@label` tag is to be used to define an identifier of the item so that it can be referenced in other docs. This can be done using the `@see` and `@link` tags. `@see` is used to build a list of references to an API item or other resource that may be related to the current item. The `@link` tag facilitates this by supporting a way to incorporate hyperlinks, because the use of this tag is non-trivial, it is advised to read the [exact documentation](https://tsdoc.org/pages/tags/link/) on this. Lastly we have the `@remarks` tag, this defines a clause where more information can be written that does not need to be part of the main summary section.

```ts
/**
 * (Brief summary section)
 * 
 * {@label ITEM_IDENTIFIER}
 * 
 * @remarks
 * 
 * More information about the item:
 * @see {@link ANOTHER_ITEM_IDENTIFIER | Another Item Preview Name}
 * @see {@link https://some.url/} for more information about this item
 */
```

For normal method documentation comments the extension is formed by the `@param`, `@throws` and `@returns` tags. The use of these tags is straight-forward and can be seen here.

```ts
/**
 * (Brief summary section)
 * 
 * @param x - the first input variable
 * @param y - the second input variable
 * @returns the result of doing the operation on `x` and `y`
 * 
 * @throws {@link CustomErrorType}
 * This exception is thrown when ...
 */
function doThing(x: type1, y:type2): type3 {
  ...
}
```

For classes and interfaces/types we also have a few extra tags: `@defaultValue`, `@readonly` and `@typeParam`.
```ts
enum WarningStyle {
  DialogBox,
  StatusMessage,
  LogOnly
}

/**
 * (Brief summary)
 * 
 * @typeParam T - Message type
 */
interface IWarningOptions<T> {

  /**
   * (id information)
   * @readonly
   */
  id: string;

  /**
   * Determines how the warning will be displayed.
   *
   * @remarks
   * See {@link WarningStyle| the WarningStyle enum} for more details.
   *
   * @defaultValue `WarningStyle.DialogBox`
   */
  warningStyle: WarningStyle;

  /**
   * Whether the warning can interrupt a user's current activity.
   * @defaultValue
   * The default is `true` unless
   *  `WarningStyle.StatusMessage` was requested.
   */
  cancellable?: boolean;

  /**
   * The warning message
   */
  message: T;
}

```

Because there is no real React documentation standard, we are going to create our own little standard.


# Architectural documentation

The [C4](https://c4model.com/) model is a loose and simple model to document projects in multiple levels. 
We will only be using the first (System Context diagram) and second (Container diagram) levels to have a high level overview of the architecture.
Take a look at the link for a detailed explanation. To create these diagrams, you could use a website like [draw.io](https://app.diagrams.net/).




# API documentation

We will use [Swagger UI](https://swagger.io/tools/swagger-ui/) for this as it seems to be the most popular tool out there.
Sadly it does not seem to be trivial to automatically change the API documentation based on changes in the API code,
but hopefully we won't be changing this code too much anyway. [This](https://www.youtube.com/watch?v=xggucT_xl5U)
tutorial is a good place to get started with Swagger.




# User manual

The user manual will be hosted on this repository's [wiki](https://github.com/SELab-2/OSOC-1/wiki).
The manual should at least contain a 'quick start' guide and explain how to do certain common actions.
