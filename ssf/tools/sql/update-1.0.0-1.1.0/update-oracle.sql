connect sitescape/sitescape;
alter table SS_Attachments drop constraint FKA1AD4C3193118767;
alter table SS_Attachments drop constraint FKA1AD4C31DB0761E4;
alter table SS_Attachments drop constraint FKA1AD4C31F93A11B4;
alter table SS_Dashboards drop constraint FKFA9653BE93118767;
alter table SS_Dashboards drop constraint FKFA9653BEDB0761E4;
alter table SS_Definitions drop constraint FK7B56F60193118767;
alter table SS_Definitions drop constraint FK7B56F601DB0761E4;
alter table SS_Events drop constraint FKDE0E53F893118767;
alter table SS_Events drop constraint FKDE0E53F8DB0761E4;
alter table SS_FolderEntries drop constraint FKA6632C83F7719C70;
alter table SS_FolderEntries drop constraint FKA6632C8393118767;
alter table SS_FolderEntries drop constraint FKA6632C83DB0761E4;
alter table SS_FolderEntries drop constraint FKA6632C83A3644438;
alter table SS_Forums drop constraint FKDF668A5193118767;
alter table SS_Forums drop constraint FKDF668A51DB0761E4;
alter table SS_Notifications drop constraint FK9131F9296EADA262;
alter table SS_PrincipalMembership drop constraint FK176F6225AEB5AABF;
alter table SS_Principals drop constraint FK7693816493118767;
alter table SS_Principals drop constraint FK76938164DB0761E4;
alter table SS_WorkflowStates drop constraint FK8FA8AA80A3644438;
alter table SS_Forums drop column upgradeVersion;alter table SS_Attachments add zoneId number(19,0);
alter table SS_AuditTrail add zoneId number(19,0);
alter table SS_AuditTrail add applicationId number(19,0);
alter table SS_CustomAttributes add zoneId number(19,0);
alter table SS_Dashboards add zoneId number(19,0);
alter table SS_Definitions add binderId number(19,0);
create table SS_EmailAddresses (principal number(19,0) not null, type varchar2(64 char) not null, zoneId number(19,0) not null, address varchar2(256 char), primary key (principal, type));
alter table SS_Events add zoneId number(19,0);
alter table SS_Events add calUid varchar2(255 char);
alter table SS_Events add freeBusy varchar2(32 char);
alter table SS_FolderEntries add zoneId number(19,0);
alter table SS_FolderEntries add subscribed number(1,0);
alter table SS_Forums add branding clob;
alter table SS_Forums add postingEnabled number(1,0);
alter table SS_Functions add internalId varchar2(32 char);
alter table SS_Functions add zoneWide number(1,0);
create table SS_IndexNode (id char(32) not null, nodeName varchar2(128 char), indexName varchar2(160 char), zoneId number(19,0), accessMode varchar2(16 char), inSynch number(1,0), primary key (id), unique (nodeName, indexName));
create table SS_IndexingJournal (id number(19,0) not null, zoneId number(19,0), nodeName varchar2(128 char), indexName varchar2(160 char), operationName varchar2(32 char), operationArgs blob, primary key (id));
create table SS_LdapConnectionConfig (id char(32) not null, zoneId number(19,0), url varchar2(255 char), userIdAttribute varchar2(255 char), mappings clob, userSearches clob, groupSearches clob, principal varchar2(255 char), credentials varchar2(255 char), position number(10,0), primary key (id));
alter table SS_LibraryEntries add zoneId number(19,0);
create table SS_NotifyStatus (ownerId number(19,0) not null, zoneId number(19,0), ownerType varchar2(16 char), owningBinderKey varchar2(255 char), owningBinderId number(19,0), lastModified timestamp, lastDigestSent timestamp, lastFullSent timestamp, primary key (ownerId));
alter table SS_Postings add credentials varchar2(64 char);
alter table SS_Principals add skypeId varchar2(64 char);
alter table SS_Principals add twitterId varchar2(64 char);
alter table SS_Principals add status varchar2(256 char);
alter table SS_Principals add statusDate timestamp;
alter table SS_Principals add miniBlogId number(19,0);
alter table SS_Principals add postUrl varchar2(256 char);
alter table SS_Principals add timeout number(10,0);
alter table SS_Principals add trusted number(1,0);
alter table SS_Principals add maxIdleTime number(10,0);
alter table SS_Principals add sameAddrPolicy number(1,0);
alter table SS_Ratings add zoneId number(19,0);
alter table SS_SeenMap add zoneId number(19,0);
create table SS_SharedEntity (id char(32) not null, referer number(19,0), zoneId number(19,0), sharedDate timestamp, accessId number(19,0), accessType number(19,0), entityType varchar2(16 char), entityId number(19,0), primary key (id));
create table SS_SimpleName (zoneId number(19,0) not null, name varchar2(128 char) not null, emailAddress varchar2(128 char), binderId number(19,0), binderType varchar2(16 char), primary key (zoneId, name));
alter table SS_Subscriptions add zoneId number(19,0);
alter table SS_Subscriptions add encodedStyles varchar2(256 char);
alter table SS_Tags add zoneId number(19,0);
create table SS_TokenInfo (id char(32) not null, type char(1 char) not null, zoneId number(19,0), seed varchar2(128 char), userId number(19,0), applicationId number(19,0), binderId number(19,0), binderAccessConstraints number(10,0), clientAddr varchar2(128 char), lastAccessTime timestamp, primary key (id));
alter table SS_UserProperties add zoneId number(19,0);
create table SS_WorkflowHistory (id char(32) not null, zoneId number(19,0), startBy number(19,0), startDate timestamp, endBy number(19,0), endDate timestamp, entityId number(19,0), entityType varchar2(16 char), owningBinderId number(19,0), owningBinderKey varchar2(255 char), tokenId number(19,0), state varchar2(64 char), threadName varchar2(64 char), definitionId varchar2(32 char), ended number(1,0), primary key (id));
alter table SS_WorkflowResponses add zoneId number(19,0);
alter table SS_WorkflowStates add zoneId number(19,0);
create table SS_ZoneConfig (zoneId number(19,0) not null, upgradeVersion number(10,0), postingEnabled number(1,0), simpleUrlPostingEnabled number(1,0), sendMailEnabled number(1,0), allowLocalLogin number(1,0), allowAnonymousAccess number(1,0), allowSelfRegistration number(1,0), lastUpdate number(19,0), primary key (zoneId));
create table SS_ZoneInfo (id char(32) not null, zoneId number(19,0) not null unique, zoneName varchar2(128 char) not null unique, virtualHost varchar2(255 char) unique, primary key (id));
alter table SS_Attachments add constraint FKA1AD4C3158C1A25C foreign key (creation_principal) references SS_Principals;
alter table SS_Attachments add constraint FKA1AD4C31A0B77CD9 foreign key (modification_principal) references SS_Principals;
alter table SS_Attachments add constraint FKA1AD4C31BEEA2CA9 foreign key (filelock_owner) references SS_Principals;
alter table SS_Dashboards add constraint FKFA9653BE58C1A25C foreign key (creation_principal) references SS_Principals;
alter table SS_Dashboards add constraint FKFA9653BEA0B77CD9 foreign key (modification_principal) references SS_Principals;
alter table SS_Definitions add constraint FK7B56F60158C1A25C foreign key (creation_principal) references SS_Principals;
alter table SS_Definitions add constraint FK7B56F601A0B77CD9 foreign key (modification_principal) references SS_Principals;
alter table SS_EmailAddresses add constraint FKC706C3457488E8C7 foreign key (principal) references SS_Principals on delete cascade;
alter table SS_Events add constraint FKDE0E53F858C1A25C foreign key (creation_principal) references SS_Principals;
alter table SS_Events add constraint FKDE0E53F8A0B77CD9 foreign key (modification_principal) references SS_Principals;
alter table SS_FolderEntries add constraint FKA6632C83BD21B765 foreign key (reserved_principal) references SS_Principals;
alter table SS_FolderEntries add constraint FKA6632C8358C1A25C foreign key (creation_principal) references SS_Principals;
alter table SS_FolderEntries add constraint FKA6632C83A0B77CD9 foreign key (modification_principal) references SS_Principals;
alter table SS_FolderEntries add constraint FKA6632C8369145F2D foreign key (wrk_principal) references SS_Principals;
alter table SS_Forums add constraint FKDF668A5158C1A25C foreign key (creation_principal) references SS_Principals;
alter table SS_Forums add constraint FKDF668A51A0B77CD9 foreign key (modification_principal) references SS_Principals;
alter table SS_Notifications add constraint FK9131F929345DBD57 foreign key (principalId) references SS_Principals;
alter table SS_PrincipalMembership add constraint FK176F6225A3FFCD99 foreign key (userId) references SS_Principals;
alter table SS_Principals add constraint FK7693816458C1A25C foreign key (creation_principal) references SS_Principals;
alter table SS_Principals add constraint FK76938164A0B77CD9 foreign key (modification_principal) references SS_Principals;
alter table SS_SharedEntity add constraint FK93426C47F68E5AD foreign key (referer) references SS_Principals;
alter table SS_WorkflowStates add constraint FK8FA8AA8069145F2D foreign key (wrk_principal) references SS_Principals;
create sequence ss_indexingjournal_id_sequence;
alter table SSQRTZ_triggers add PRIORITY NUMBER(13) null;
update SSQRTZ_TRIGGERS set PRIORITY=5 where PRIORITY is null;
alter table SSQRTZ_triggers add JOB_DATA blob null;
alter table SSQRTZ_fired_triggers add PRIORITY NUMBER(13) null;
update SSQRTZ_fired_triggers set PRIORITY=5 where PRIORITY is null;
alter table SSQRTZ_scheduler_state drop column RECOVERER;
alter table SS_Events modify  timeZone varchar2(80 char);
create index owningBinder_audit on SS_AuditTrail (owningBinderId);
create index entityOwner_audit on SS_AuditTrail (entityId, entityType);
create index entityTransaction_audit on SS_AuditTrail (startDate, entityId, entityType, transactionType);
create index owningBinder_clog on SS_ChangeLogs (owningBinderId);
create index entityOwner_clog on SS_ChangeLogs (entityId, entityType);
create index address_email on SS_EmailAddresses (address);
create index indexingJournal_nodeIndex on SS_IndexingJournal (nodeName, indexName);
create index notifyStatus_full on SS_NotifyStatus (zoneId, lastModified, lastFullSent);
create index notifyStatus_digest on SS_NotifyStatus (zoneId, lastModified, lastDigestSent);
create index access_shared on SS_SharedEntity (sharedDate, accessId, accessType);
create index binderId_simpleName on SS_SimpleName (binderId);
create index emailAddress_simpleName on SS_SimpleName (emailAddress);
create index userId_tokenInfoSession on SS_TokenInfo (userId);
create index owningBinder_wfhistory on SS_WorkflowHistory (owningBinderId);
create index entityTransaction_wfhistory on SS_WorkflowHistory (startDate, entityId, entityType);
create index entityOwner_wfhistory on SS_WorkflowHistory (entityId, entityType);
create unique index definition_name on SS_Definitions (zoneId, name, binderId);
