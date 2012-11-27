Jesque-Spring
=============

A extension for Jesque that allows interoperability with Spring the Java framework, the integration was designed thingking in two objectives:

1. Build an implememtation of JesqueClient and JesqueExecutor that will be managed by Spring, and available within the Spring context for dependecy injection.
2. Allow the JesqueExecutor to execute jobs that are configured as Spring beans. 