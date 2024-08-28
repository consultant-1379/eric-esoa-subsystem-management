# Subsystem Management Developer Guide

This section covers developer-related topics for using Subsystem Management.
For more information, review the contents below.

## Contents

- [Developer Guide Subsystem Controller](#subsystem-management-service)
    The Subsystem Management service allows Users to create and configure
    connected systems. Connected systems are the external systems which
    EO-SO will interact with during the life-cycle of a network service.
    EO-SO requires an NFVO and a Domain Manager / EMS system for the
    life-cycle management of network services.

    An instance of a connected system cannot be deleted if is currently in use
    by an active service.
    The service must be deleted prior to deleting the connected system.

    EO-SO supports: Multiple instances of Domain Manager / EMS - unlimited per
    vendor.

- [Developer Guide Subsystem Type Controller](#subsystem-type-service)
    Subsystem Type service manages the types of subsystems EO-SO can connect
    and interwork with.
    Each subsystem type is identified by a unique identifier and a descriptive
    type string.
    The list of connected subsystem types can be retrieved by means of
    SO REST i/f as herein outlined.

- [Developer Guide Subsystem User Controller](#subsystem-users-service)
    Subsystem User service allows to configure the subsystem users associated
    with a specific instance of a subsystem and connection properties.
    An identifier is given by this service to identify uniquely the subsystem
    user being created.
    Methods for creation and deletion of subsystem users are provided.

- [Developer Guide Tenant Mapping Service](#tenant-mapping-service)
    EO-SO provides service level tenancy and user isolation per tenant.
    Connected systems are not isolated per tenant, but access to a connected
    system can be restricted to one or more tenants by using the tenant
    mapping functionality. For example, EO-CM may be added as a connected
    system with multiple connections (i.e. EO-CM tenants or EO-CM users).
    EO-SO can restrict access to these connections on a per tenant basis.
    For NFVO connected systems only, it will not be possible to create a
    network service using that system unless a connection has been mapped
    to your SO tenant. Users with the Provider Admin role are not subject to
    this restriction. A connection to an NFVO system can be mapped to multiple
    SO tenants. For Domain Manager connected systems, it is not required
    to map any connection prior to creating a network service.
    These connections are not restricted in the system.

- [Developer Guide Adapter Links Service](#adapter-links-service)
    Adapter Links Service manages pool of adapters which allow for connectivity
    and interworking between EO-SO Connected Systems. Those adapters are
    specified with unique names.
    SO allows to retrieve the adapters names as based on a specified
    subsystem type.

- [Developer Guide Connection Properties Service](#connection-properties-service)
    Connection Properties service manages the connection properties that
    can be associated to each connected subsystem.
    For each subsystem, there can be sets of connection properties,
    each set and their properties are identified by a unique name.
    Methods for creation, update and deletion of connection properties sets
    are provided, as well as for modification of single properties.

- [Developer Guide Subsystem_Validation Service](#subsystem-validation-service)
  The Subsystem Validation service allows Users to run validation procedures
  against real external Connected System. Specifically URL and credentials are
  validated allowing to verify the network connectivity toward the external system.
  In case of unsuccessful validation, a warning message is reported back to the end
  user. Such validation can be run against Connected Systems yet under creation,
  under editing or within the list view as all provided by the Subsystem Management
  GUI or within an M2M scenario.