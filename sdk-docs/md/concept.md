# Subsystem Management Concepts

This section describes the main concepts and terminology you need to understand
to use Subsystem Management effectively. Where possible, they are presented in
the order that you will most likely encounter them.

The Subsystem Management resource model consists of concepts like subsystem,
subsystem type, subsystem user, connection properties, adapter links and tenant
mappings.

- [Subsystem](#Subsystem)
- [Subsystem Type](#Subsystem-type)
- [Subsystem User](#Subsystem-user)
- [Connection Properties](#connection-properties)
- [Adapter Links](#adapter-links)
- [Tenant Mapping](#tenant-mapping)
- [Subsystem Validation](#subsystem-validation)

## Subsystem

A software system which is external to your software application and to which
your application needs interaction particularly on but not limited to the
southbound.

### Subsystem Examples

    Domain Manager
    The software system that manages FCAPS (Fault, Configuration, Accounting,
    Performance and Security) of a telecommunication network.
    e.g. ENM - Ericsson Network Manager

    NFVO
    NFV (Network Function Virtualization) Orchestrator performs resource
    orchestration and network service orchestration, as well as other
    functions. The NFVO is a central component of an NFV-based solution.
    It brings together different functions to make a single orchestration
    service that encompasses the whole framework and has well-organized
    resource use. e.g. EO-CM - Cloud Manager

## Subsystem Type

The type of Subsystem EO-SO can connect and operate with, eg. Domain
Manager, NFVO, Domain Orchestrator or EAI etc.

## Subsystem User

Any Service or Resource within your software application that uses a
particular connection property to interact with Subsystem is the Subsystem
User. The users are created during creation of a service and deleted
during decommissioning.

## Connection Properties

The credentials (username/password/tenancy) to access the subsystem.
Multiple connection properties are supported by Subsystem manager to allow
access for multi-user and multi-tenancy within the Subsystem.

## Adapter Links

Adapters which allow for connectivity and interworking between EO-SO Connected
Systems. SO allows to retrieve the adapters names as based on a specified
subsystem type.

## Tenant Mapping

Provides a mapping between SO tenants and Connection Properties.
Determines which tenants have access to particular Connection Properties.

## Subsystem Validation

Allows Users to run validation procedures against real external Connected System.
Currently, the validations of URL, username, password and tenant name are supported.
That is accomplished on a Tenant basis when required. This feature is currently available
only for Ericsson Domain Manager and NFVO Connected Systems scenarios.