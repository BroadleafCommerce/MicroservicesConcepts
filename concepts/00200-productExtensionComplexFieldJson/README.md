Description: 

Simple extension of JpaProduct adding more complex field types, including collections and maps. The complex types use JPA converters to persist the complex structure as JSON. This example still leverages auto projection and does not declare an explicit extending projection type.

Goals:

- Demonstrate more complex field type
- Demonstrate interaction with Projection interface to expose complex structures for editing
- Show full lifecycle support (json in/out) for the endpoint API
- Show supporting admin customization

Builds On:

00100-productExtensionOnly

