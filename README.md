# Replication package for: "Enhancing Interoperability of HL7 Resources Using Namespaces in Graph Databases"

This repository is used as a replication package for the work on "Enhancing Interoperability of HL7 Resources Using Namespaces in Graph Databases".

The project itself is configured as a Maven Java project using Maven 3.8.6 in combination with OpenJDK 11. The main entrypoint
of the project is the Neo4jOnFhirMain class. Executing this class will read data from a hapi fhir server and will store
it in a Neo4j graph database hosted on localhost:7687.

## Requirements

### Hapi Fhir Server

It is necessary to have a running Hapi FHIR Server (we used version: 6.2.2) with FHIR Version 4.0.1, which contains a list
of patient and AuditEvent data. The data that we used can be found as JSON in the folder `/data` (cf. patients.json and audit_events.json).
The data can be imported using the following command:

```cmd
curl -X POST --location "http://serverBaseURL/fhir/" \
    -H "Content-Type: application/fhir+json" \
    -d @foldername/patients.json
```

### Neo4j database
A running Neo4j database setup is required in order to store the data. The following docker-compose file could be used 
to create a running instance:

```yaml
version: '2'
services:
  db:
    image: neo4j:4.2.19-community
    ports:
      - 7474:7474
      - 7687:7687
    volumes:
      - "localpath/data:/data"
      - "localpath/conf:/var/lib/neo4j/conf"
      - "localpath/plugins:/var/lib/neo4j/plugins"
```

Please make sure, that both `localpath/conf` and `localphat/plugins` need to include a configuration for CyFHIR.
The folder can be copied from the data folder using the folders: `conf` and `plugins`.

Importing the data using CyFHIR can be done using the following command and replace `<json_string>` with the content of
the file `data/patients_filtered.json`.

```cypher
CALL cyfhir.bundle.load('<json_string>')
```

Note: For our example we used two separate Neo4j database instances, for storing FHIR resources using CyFHIR and our 
namespace approach to avoid conflicts.

## Contributing

**First make sure to read our [general contribution guidelines](https://fhooeaist.github.io/CONTRIBUTING.html).**
   
## License

Copyright (c) 2023 the original author or authors.
DO NOT ALTER OR REMOVE COPYRIGHT NOTICES.

This Source Code Form is subject to the terms of the Mozilla Public
License, v. 2.0. If a copy of the MPL was not distributed with this
file, You can obtain one at https://mozilla.org/MPL/2.0/.

Files contained in the data/plugins folder are third party apps and may be distributed under different license:
 - CyFHIR.jar: GPL-3.0
 - apoc-4.2.0.10-all.jar: Apache-2.0

## Research

If you are going to use this project as part of a research paper, we would ask you to reference this project by citing
it. 

<TODO zenodo doi>
