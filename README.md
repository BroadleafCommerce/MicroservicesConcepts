# MicroservicesConcepts
## Demonstrates various extension and dev use cases

- Each concept showcases a discrete customization goal. The goal will be clearly stated in each concept project. Some concept projects will be deeper riffs on concepts from other projects (e.g. simple domain extension in one project, then another project with the same domain extension and additional repository extension).
- The concept project structure will be simple. Min flexpackage alone will be generally favored, avoiding balanced and granular bloat.
- Each concept project will consume framework and demo artifacts from the latest relevant develop branches.
- Each concept project is constructed using the minimal amount of configuration and code for optimum clarity.


## Structure
- ***concepts*** Group of runnable projects that demonstrate a key dev or extension experience using the most streamlined approach available.
- ***docker*** Basic docker configuration to support backend of the tutorial runtime. We can consider not having these items in docker and instead have them be launched by java, or similar. If we did that, the goal would be to reduce tooling required to run the tutorial cases.
- ***script*** - core build script code that supports all instances of `reset-and-run` and `stop-docker`.

## Running
- In the specific concept module, execute the platform specific `reset-and-run` script. `ctrl-c` will terminate the process.
- The `stop-docker` script may be used to take down any running container resulting from the `reset-and-run` script.
- The admin application will be accessible at https://localhost:8446 after all components have completed startup. The browser may complain about the self-signed cert and you may have to make an exception for the cert in your browser. This uses the same admin login and password as your client starter or demo.
- Register your nexus repository credentials with your local maven installation (e.g. `~/.m2/settings.xml`). Add a snippet like this to the servers section:

```xml
<server>
    <id>broadleaf-microservices</id>
    <username>[MyCoUsername Here]</username>
    <password>[MyCoPassword Here]</password>
</server>
```
- Login to Broadleaf's docker registry. `docker login repository.broadleafcommerce.com:5001`. When prompted, enter the same credentials used above for nexus.

## Method
- Each concept project creates a jar that is contributed to a standard Broadleaf Min flexpackage demo
- The jar is contributed via Spring Boot auto configuration.
- When necessary, `@AutoConfigureBefore` and `@AutoConfigureAfter` are employed to favor concept configuration over Broadleaf configuration.
- Integration tests are generally employed in each concept to exercise the service API and demonstrate the customization.
- The customizations are generally Catalog service customizations - specifically riffs on `Product`.

## Terms
- #### <ins>Flexpackage</ins> - A deployable unit of Broadleaf Commerce Microservices. This may contain a single microservice (e.g. the Catalog Service), or it may contain several services bundled together. The advantages of flexpackage bundling are: reduced devops complexity with fewer discrete units running in an environment, smaller deployment footprint with multiple services running under the same JVM/heap allocation (saves $), and a sane local development footprint suitable for developer laptops to run the entire stack. Disadvantages include: less granular scale tuning, and possibly decreased team contribution isolation. It's notable that flexpackage setup is a configuration only concern, and changing later on to a more granular deployment is entirely feasible. The _Balanced_ flexpackage is generally considered the best mix when weighed against all these concerns at the same time.
- #### <ins>Auto Projection</ins> - Broadleaf Commerce Microservices operate with two versions of the domain class representation. The _repository_ domain is generally represented as a JPA annotated POJO that interacts with an ORM to facilitate input/output from database storage. The _projection_ domain is a version of the repository domain that is exposed outside of the service layer and represents the structure that is serialized into JSON as output from the microservice endpoints. Projection and repository domains can share the same structure, but are not required to do so, and Broadleaf provides mapping facilities to customize how data flows between these two representations of the same data. While Broadleaf does allow complexity here, it is not required that extensions of Broadleaf repository domain (or introduction of new repository domain) explicitly declare a projection with explicit mapping. If the needs are simple and direct representation of the data to the business layer is acceptable, then Broadleaf can auto generate a projection for you. Henceforth, you will be able to reference the projection and its fields using the facilities in Broadleaf (demonstrated in several of the concept examples). This serves to simplify configuration and development for standard use cases.
- #### <ins>Data Routing</ins> - This is the technology that primarily facilitates the operation of flexpackage bundling. Data routing is used to detect the datasource that should be utilized for the current flow. As a result, multiple service endpoints housed in the same JVM/classloader can leverage different datasources (and different backing database/schema) to enable their data interactions. This supports proper bounded context per service, while still allowing more interesting deployment options.
- #### <ins>Data Tracking</ins> - This is the technology that supports entity discrimination, sandboxing, and catalog propagation. There are different tracking types based on the maintenance needs of the data. Types of tracking include: catalog, application, sandbox, customercontext, and tenant. These types include interesting grouping, discrimination, preview, audit, and workflow capabilities. Based on your requirements, you can also add these capabilities in a simple way to your own unique domain.
- #### <ins>Fragment (or Repository Fragment)</ins> - This is a Spring Data concept for contributing repository data persistence behavior via interfaces and concrete implementations. The resulting spring component is callable at runtime and exposes the aggregate contract of all the contributed interfaces. Broadleaf provides facilities to contribute additional fragments (interfaces and optional concrete impls) to existing repositories. This is the primary extension mechanism for adding behavior to Broadleaf declared repositories.

## Applicability
- The Java customization examples should all be applicable to existing and new Broadleaf Microservices projects.
- The maven pom inheritance, maven profiles, run commands, etc... are not currently applicable to existing or new Broadleaf Microservices projects - although advances here will eventually be brought over to real dev projects.
- Leveraging the code patterns and components demonstrated in the concept projects requires recent versions of several Broadleaf Framework libraries. You should set these versions (or newer) at the top of the `dependencyManagement` section of your root pom in your own project before attempting to reproduce or compile against the patterns shown here. If your release train reference is advanced enough to bring these versions (or newer) in by default, then you can skip explicitly declaring them here.

```xml
<dependencyManagement>
    <!-- ↓ These should appear first before the release train ↓ -->
    <dependencies>
        <dependency>
            <groupId>org.broadleafcommerce</groupId>
            <artifactId>spring-frameworkmapping</artifactId>
            <version>0.9.1-GA</version>
        </dependency>
        <dependency>
            <groupId>com.broadleafcommerce.microservices</groupId>
            <artifactId>broadleaf-common-extension</artifactId>
            <version>1.4.13-GA</version>
        </dependency>
        <dependency>
            <groupId>com.broadleafcommerce.microservices</groupId>
            <artifactId>broadleaf-common-jpa</artifactId>
            <version>1.5.7-GA</version>
        </dependency>
        <dependency>
            <groupId>com.broadleafcommerce.microservices</groupId>
            <artifactId>broadleaf-data-tracking-dependencies</artifactId>
            <version>1.7.8-GA</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        ...
    </dependencies>
</dependencyManagement>
```

## Roadmap
- This project represents an ongoing effort to document customization use cases and patterns.
- We will continue to add new concepts as they come up.
- If a new concept requires a framework change or enhancement (e.g. a change to one or more of the libraries above), we will increment the version of this concept project along with noting the version change(s) to the associated common framework libraries.
- Training will also be developed based on these concept materials and will be available separately.

## Index
- ### 00100-productExtensionOnly - Simple extension of JpaProduct adding only a basic field type. Also leverages auto projection, rather than opting for an explicit extended projection.
  - Demonstrate the simplest type of extension
  - Introduce the Projection interface
  - Show full lifecycle support (json in/out) for the endpoint API
  - Show supporting admin customization
  - Show automated testing and the use of `@TestCatalogRouted` (et al.) to handle data routing requirements during the test
- ### 00200-productExtensionComplexFieldJson - Simple extension of JpaProduct adding more complex field types, including collections and maps. The complex types use JPA converters to persist the complex structure as JSON. This example still leverages auto projection and does not declare an explicit extending projection type.
  - Demonstrate more complex field type
  - Demonstrate interaction with Projection interface to expose complex structures for editing
  - Show full lifecycle support (json in/out) for the endpoint API
  - Show supporting admin customization
  - Builds On : _00100-productExtensionOnly_
- ### 00300-productExtensionExplicitProjection - Continues with the complex field example persisted as JSON. However, in this case, an explicit projection type is declared.
  - Demonstrate custom mapping to/from projection
  - Demonstrate response only projection field
  - Show supporting admin customization
  - Demonstrate mapping to synthetic fields
  - Builds On : _00200-productExtensionComplexFieldJson_
- ### 00400-productExtensionComplexFieldTableBased - Alters the complex field example to leverage a traditional JPA OneToMany associated collection. The relates to a new table in the database, rather than serializing to JSON.
  - Show table based complex field support in the JpaProduct extension
  - Demonstrate custom mapping to/from projection
  - Demonstrate special `@ProjectionPostConvert` support for setting bi-directional references
  - Show supporting admin customization
  - Builds On : _00200-productExtensionComplexFieldJson_
- ### 00500-nestedJsonMemberExtension - Extends nested structures that appear arbitrarily deep in the object graph of JpaProduct. The structures appear in various embedded collections and are persisted as JSON.
  - Show several examples of nested structure extension
  - Show supporting admin customization
  - Builds On : _00300-productExtensionExplicitProjection_
- ### 00600-nestedTableBasedMemberExtension - Extends nested structures that appear arbitrarily deep in the object graph of JpaProduct. The structures appear in OneToMany table based collections.
  - Show example of nested OneToMany table based structure extension
  - Show supporting admin customization
  - Builds On : _00400-productExtensionComplexFieldTableBased_
- ### 00700-repositoryCustomizationOverride - Adds a new repository implementation fragment overriding out-of-the-box behavior of JpaTrackableRepository.
  - Show concrete fragment contribution example overriding JpaTrackableRepository methods for JpaProductRepository
  - Demonstrate the use of JpaTrackableRepositoryDelegateSupplier to use in the fragment for extension via composition
  - Builds On : _0700-repositoryCustomizationOverride, 00200-productExtensionComplexFieldJson_
- ### 00800-repositoryCustomizationContribution - Introduces new repository methods that contribute new persistence related behavior. This take the form of either dynamic query method fragments, or concrete implementation fragments.
  - Demonstrate new query method fragment contribution (interface only)
  - Demonstrate new concrete method implementation fragment contribution
  - Show concrete fragment contribution example overriding JpaTrackableRepository methods for JpaProductRepository
  - Demonstrate the use of JpaTrackableRepositoryDelegateSupplier to use in the fragment for extension via composition
  - Builds On : _00200-productExtensionComplexFieldJson_
- ### 00900-businessLogicCustomization - Uses a simple customization of the DefaultProductService.
  - Show a minor customization of the business logic of DefaultProductService
- ### 01000-businessLogicCustomizationAutoProjection - Business logic customization that leverages a customized repository and extended domain with auto projection.
  - Show DefaultProductService call the customized repository to search by a new extended field
  - Demonstrate how to use the Projection interface to interact with the service API
  - Builds On : _00800-repositoryCustomizationContribution_
- ### 01100-businessLogicCustomizationExplicitProjection - Business logic customization that leverages a customized repository and extended domain with explicit projection.
  - Show complete lifecycle in/out of the endpoint with extended field information
  - Demonstrate handling of the customized repository and domain
  - Builds On : _00300-productExtensionExplicitProjection_
- ### 01200-endpointCustomization - Simple customization of out-of-the-box ProductEndpoint.
  - Demonstrate a behavior tweak of a single endpoint method
- ### 01300-endpointCustomizationAutoProjection - Customization of an endpoint method in ProductEndpoint leveraging a customized service, repository, auto-projection, and domain.
  - Demonstrate a behavior tweak of a single endpoint method
  - Show leveraging a completely customized flow through to persistence
  - Demonstrate working with an auto projection in the endpoint
  - Builds On : _01000-businessLogicCustomizationAutoProjection_
- ### 01400-endpointCustomizationExplicitProjection - Customization of an endpoint method in ProductEndpoint using an extended explicit projection and domain.
  - Demonstrate a behavior tweak of a single endpoint method
  - Demonstrate working with an explicit projection in the endpoint
  - Builds On : _01100-businessLogicCustomizationExplicitProjection_
- ### 01500-newDomain - Introduction of new domain without explicit projection or any other explicit plumbing like repository, service, or endpoint.
  - Demonstrate the simplest type of domain introduction
  - Show full lifecycle support (json in/out) for the endpoint API
  - Builds On : _00100-productExtensionOnly_
- ### 01600-newDomainComplexField - Introduction of new domain including complex field structures.
  - Demonstrate domain introduction with embedded json collection fields
  - Demonstrate domain introduction with nested JPA OneToMany collection fields
  - Builds On : _01500-newDomain_
- ### 01700-newDomainFineTuneAutoProjection - Introduction of new domain with auto projection output fine tuned through customization.
  - Demonstrate customization of auto projection with the ExplicitProjectionFieldConfiguration annotation
  - Demonstrate removing a field from the projection
  - Demonstrate limiting a field to response only during update/replace
  - Demonstrate altering deserialization/serialization (e.g. to/from MonetaryAmount for a BigDecimal field)
  - Builds On : _01600-newDomainComplexField_
- ### 01800-newDomainExplicitProjection - Introduction of new domain including explicit projection declaration.
  - Demonstrate explicit projection declaration
  - Demonstrate projection customizations
  - Demonstrate custom JSON deserialization/serialization for a projection field
  - Demonstrate maintenance to/from a synthetic map to a different JPA domain structure
  - Builds On : _01600-newDomainComplexField, 00300-productExtensionExplicitProjection_
