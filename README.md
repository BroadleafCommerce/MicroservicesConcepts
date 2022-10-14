# MicroservicesTutorialResearch
R &amp; D around various extension and dev use cases. Proving ground for concepts.

- The idea is to create various demo projects that showcase a vision for the simplest path to achieve a dev goal. The goal will be clearly stated in each demo project. Some demo projects will be deeper riffs on concepts from other projects (e.g. simple domain extension in one project, then another project with the same domain extension and additional repository extension).
- The demo project structure will be simple. Min flexpackage alone will be generally favored, avoiding balanced and granular bloat.
- Will consume framework and demo artifacts from the latest relevant develop branches.
- One of the key goals will be to lazer focus on the interesting code extension and customization topics. We want to reduce as much as possible additional boilerplate setup files. This makes it easier to focus on the key topic, reduces project structure bloat that needs to be understood right away, and simplifies training/explanation by reducing the number of items that need to be explained.


Structure

- ***base*** Similar to the min flexpackage pom, except this is actually a pom project, instead of a jar project. This is an example of something we could make available via nexus and remove from here.
- ***root*** Root parent from which all extension projects derive. This is an example of something we could make available via nexus and remove from here.
- ***concepts*** Group of runnable projects that demonstrate a key dev or extension experience using the most streamlined approach available.
- ***core*** This project removes the need to include the boilerplate application launch hook point. This is an example of something we could make available on nexus to simplify tutorial setup and remove from here.
- ***docker*** Basic docker configuration to support backend of the tutorial runtime. We can consider not having these items in docker and instead have them be launched by java, or similar. If we did that, the goal would be to reduce tooling required to run the tutorial cases.
- ***utilities*** Utility classes to help with build simplification are included here. This serves duties like automatic generation of liquibase schema and spring configuration overrides to point to the generated liquibase changelogs. This removes more boilerplate that a student needs to create on their own. This is an example of something we could make available on nexus to simplify tutorial setup and remove from here.

Running
- In the `app` directory of a concept, execute `mvn spring-boot:run`.
