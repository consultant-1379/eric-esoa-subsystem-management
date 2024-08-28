# Subsystem Management Use Cases

This section provides an overview of common use cases for Subsystem Management.

- [SO-OSS01: Onboard Connected System](#so-oss01-onboard-connected-system)
- [SO-ESS01: Edit Connected System](#so-ess01-edit-connected-system)
- [SO-DSS01: Delete Connected System](#so-dss01-delete-connected-system)
- [SO-VSS-01: View System](#so-vss-01-view-system)
- [SO-CTM-01: Create Tenant Mapping](#so-ctm-01-create-tenant-mapping)
- [SO-UTM-01: Update Tenant Mapping](#so-utm-01-update-tenant-mapping)
- [SO-DTM-01: Delete Tenant Mapping](#so-dtm-01-delete-tenant-mapping)
- [SO-RTM-01: Read Tenant Mapping](#so-rtm-01-read-tenant-mapping)

## SO-OSS01: Onboard Connected System

On-board Connected System on Service Orchestration.

When the connected system is of type NFVO, multiple connection properties can be
added with predefined fields; while when the connected system is of type
Domain Manager, a single connection property is available with predefined fields.

**Precondition:**

- You must have SO Admin access rights.
- The specified Connected System does not exist in Service Orchestration.

**Successful End Condition:** The Connected System is created on Service
Orchestration.

## SO-ESS01: Edit Connected System

Update a Connected System for Service Orchestration.

Used only to update the name, url, and connectionProperties of the subsystem.
Include the required properties to update all connectionProperties in the
requestbody.

**Precondition:**

- You must have SO Admin access rights.
- The specified Connected System exists in Service Orchestration.

**Successful End Condition:** The Connected System is updated on Service
Orchestration.

## SO-DSS01: Delete Connected System

Remove one or a pool of Connected Systems from Service Orchestration.

**Precondition:**

- You must have SO Admin access rights.
- The specified Connected Systems exist in Service Orchestration.
- There are no active Services created using the specified Connected Systems.

**Successful End Condition:** The Connected Systems are deleted from
Service Orchestration.

## SO-VSS-01: View System

- View details of a Connected System.

   Reads a Connected System information identified by its subsystem identifier.
   The "select" keyword query can be used to return all items of type specified
   in the selectfield name, for example, "id".

   **Precondition:** The specified Connected System exists in Service Orchestration.

   **Successful End Condition:** Details of the Connected System are displayed.

- View the list of all Connected Systems, or a filtered list of Connected Systems,
  supporting paginated response data.

  Reads the whole set of Connected Systems filtered out by query parameters or
  retrieves the Connected Systems mappings in a paginated way to specifically
  support GUI operations by means of query parameters.

## SO-CTM-01: Create Tenant Mapping

Creates a Tenant Connection Mapping between a Connected System and SO Tenant.

**Precondition:**

- You have Provider Admin access rights.
- You have created SO tenants using the Tenant Management function in the
  User Administration application.
- You have created Connected Systems with types that can be restricted for
  tenants, for example NFVO type.

**Successful End Condition:** View the added connection mappings on the updated
Tenant Connections table.

## SO-UTM-01: Update Tenant Mapping

Modifies the list of Connection Properties in an existing Tenant Connection
Mapping.

**Precondition:**

- You must have Provider Admin access rights.
- You have previously created a Tenant Mapping.

**Successful End Condition:** View the updated connection mappings on the
updated Tenant Connections table.

## SO-DTM-01: Delete Tenant Mapping

Deletes the Tenant Connection Mapping with the tenant name and subsystem Id.
In case the subsystemId is not specified, deletes all the tenants.

**Precondition:**

- You must have Provider Admin access rights.
- You have previously created a Tenant Mapping.

**Successful End Condition:** The deleted connection mapping is not present
anymore on the updated Tenant Connections table.

## SO-RTM-01: Read Tenant Mapping

- Retrieves the Tenant Connection Mapping given the tenant name and subsystem
  identifier.

  **Precondition:**

  - You must have Provider Admin access rights.
  - Tenant Connection mapping has been created to view.

  **Successful End Condition:** Details of the Tenant Connection Mapping are
  displayed.

- Retrieves Tenant Connection Mappings list in a paginated way to specifically support
  GUI operations by means of query parameters.

  **Precondition:**

  - You must have Provider Admin access rights.
  - Tenant Connection mappings have been created to view.
