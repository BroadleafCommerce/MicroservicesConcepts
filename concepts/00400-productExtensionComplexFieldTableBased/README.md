Description: 

Alters the complex field example to leverage a traditional JPA OneToMany associated collection. The relates to a new table in the database, rather than serializing to JSON.

Goals:

- Show table based complex field support in the JpaProduct extension
- Demonstrate custom mapping to/from projection
- Demonstrate special @ProjectionPostConvert support for setting bi-directional references
- Show supporting admin customization

Builds On:

00200-productExtensionComplexFieldJson

