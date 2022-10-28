# MicroservicesTutorialResearch
R &amp; D around various extension and dev use cases. Proving ground for concepts.

- The idea is to create various demo projects that showcase a vision for the simplest path to achieve a dev goal. The goal will be clearly stated in each demo project. Some demo projects will be deeper riffs on concepts from other projects (e.g. simple domain extension in one project, then another project with the same domain extension and additional repository extension).
- The demo project structure will be simple. Min flexpackage alone will be generally favored, avoiding balanced and granular bloat.
- Will consume framework and demo artifacts from the latest relevant develop branches.
- One of the key goals will be to laser focus on the interesting code extension and customization topics. We want to reduce as much as possible additional boilerplate setup files. This makes it easier to focus on the key topic, reduces project structure that needs to be understood right away, and simplifies training/explanation by reducing the number of items that need to be explained.


Structure

- ***concepts*** Group of runnable projects that demonstrate a key dev or extension experience using the most streamlined approach available.
- ***docker*** Basic docker configuration to support backend of the tutorial runtime. We can consider not having these items in docker and instead have them be launched by java, or similar. If we did that, the goal would be to reduce tooling required to run the tutorial cases.
- ***script*** - core build script code that supports all instances of `reset-and-run` and `stop-docker`.

Running
- In the specific concept module, execute the platform specific `reset-and-run` script. `ctrl-c` will terminate the process.
- The `stop-docker` script may be used to take down any running container resulting from the `reset-and-run` script.
