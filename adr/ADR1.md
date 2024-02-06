# ADR #1 Tech Stack

**Status** : Accepted

**Context**

We needed to decide on a tech stack to use, it should support the development
of a web application, capable of eventing and REST Api integration. We exclude UI, as this is out of scope.

**Solutions**

We considered the following technologies:

Language (Only real options were java and kotlin, since we need robust object orientation support and have experience with them):

- Java
- Kotlin
- Some scripting language (Python, Ruby, etc.)

Framework:

- Spring Boot - widely used, good support, but can be heavy and difficult to learn
- Quarkus - modern java / kotlin framework, native container support and promises to be lightweight

Database:

- MySQL - widely used
- PostgreSQL - also widely used, does come with some extra features
- H2 - lightweight, in-memory, good for testing

**Decision**

We decided to use Kotlin, Spring Boot and Mysql. We had experience with Kotlin and wanted to try if the support for it got better in the latest Spring framework.
Spring Boot was our choice, again because of some experience and the availability of community tutorials. For the database,
we chose MySQL, but were a bit indifferent, since we use JPA anyways to abstract the DB logic.