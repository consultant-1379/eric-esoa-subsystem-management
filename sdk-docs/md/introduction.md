# Subsystem Management Introduction

The Subsystem Management is the SO microservices where connected system
configuration data is being created, permanently stored storage
(into a dedicated ADP PostgreSQL DB instance), read from. It exposes API
for the purpose of:

    creating connected systems configuration data
    modifying connected systems configuration data
    reading connected systems configuration data

That includes connection properties and tenant mapping configuration
data and its storage into a dedicated ADP PostgreSQL DB instance.
For supporting these two last aspects, the Subsystem Management
interfaces southbound toward the Credentials Manager and the Tenant
Management microservices.

## How Subsystem Management Works

Subsystem Manager exposes

    northbound interface:
        an array of REST interfaces
        a Kafka interface

     southbound interface:
        Tenant Management (called during tenancy mapping)
        Credential Manager (called to retrieve information about
        connection property like an encrypted password)

The main purpose of Subsystem Manager is to store the connection
properties for various kind of subsystem that can plag into SO.

Example of Subsystem:

    ENM
    ECM
    Netfusion (Sedona)

Subsystem Manager is responsible to store the connection information
of every external subsystem.
It has its own Postgres database to store these connection properties.
Along with that, it store also information about the tenancy.

![Subsystem_Manager_Components.svg]
  (assets/image/subsystem-management-api/Subsystem_Manager_Components.svg
  "Subsystem Management Architecture")
