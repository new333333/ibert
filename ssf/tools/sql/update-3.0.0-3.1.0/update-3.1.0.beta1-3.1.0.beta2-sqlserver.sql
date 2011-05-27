use sitescape;
drop index entityOwner_audit on SS_AuditTrail;
drop index entityTransaction_audit on SS_AuditTrail;
drop index diskSpaceUsed_bquota on SS_BinderQuota;
drop index diskSpaceUsedCumulative_bquota on SS_BinderQuota;
drop index diskQuota_bquota on SS_BinderQuota;
drop index entityOwner_clog on SS_ChangeLogs;
drop index internalId_Definition on SS_Definitions;
drop index index_emaillog on SS_EmailLog;
drop index internalId_Binder on SS_Forums;
drop index diskQuota_principal on SS_Principals; 
drop index access_shared on SS_SharedEntity;
drop index entityTransaction_wfhistory on SS_WorkflowHistory;
alter table SS_SeenMap add pruneDays bigint;
alter table SS_ZoneConfig alter column holidays varchar(4000); 
alter table SS_ZoneConfig alter column weekendDays varchar(128);
create index entityOwner_audit on SS_AuditTrail (entityType, entityId);
create index search_audit on SS_AuditTrail (zoneId, startDate, startBy);
create index entityOwner_clog on SS_ChangeLogs (entityType, entityId);
create index operationDate_clog on SS_ChangeLogs (zoneId, operationDate);
create index binder_Dash on SS_Dashboards (binderId);
create index internal_Definition on SS_Definitions (zoneId, internalId, type);
create index sendDate_emaillog on SS_EmailLog (zoneId, sendDate);
create index entryDef_fEntry on SS_FolderEntries (entryDef);
create index internal_Binder on SS_Forums (zoneId, internalId);
create index snapshot_lstats on SS_LicenseStats (zoneId, snapshotDate);
create index email_postings on SS_Postings (zoneId, emailAddress);
create index type_principal on SS_Principals (zoneId, type);
create index entryDef_principal on SS_Principals (entryDef);
create index ldapGuid_principal on SS_Principals (ldapGuid);
create index internal_principal on SS_Principals (zoneId, internalId);
create index entity_Ratings on SS_Ratings (entityId, entityType);
create index access_shared on SS_SharedEntity (accessType, accessId, sharedDate);
create index entity_shared on SS_SharedEntity (entityType, entityId);
create index entity_Subscriptions on SS_Subscriptions (entityId, entityType);
create index entity_tag on SS_Tags (entity_type, entity_id);
create index owner_tag on SS_Tags (owner_type, owner_id);
create index startDate_wfhistory on SS_WorkflowHistory (zoneId, startDate);
