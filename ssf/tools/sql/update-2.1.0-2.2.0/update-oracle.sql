connect sitescape/sitescape;
alter table SS_LdapConnectionConfig add ldapGuidAttribute varchar2(255 char);
alter table SS_Principals add ldapGuid varchar2(128 char);
create index ldapGuid_principal on SS_Principals (ldapGuid);
ALTER TABLE SSQRTZ_job_details MODIFY JOB_NAME VARCHAR2(200);
ALTER TABLE SSQRTZ_job_details MODIFY JOB_GROUP VARCHAR2(200);
ALTER TABLE SSQRTZ_job_details MODIFY DESCRIPTION VARCHAR2(250);
ALTER TABLE SSQRTZ_job_details MODIFY JOB_CLASS_NAME VARCHAR2(250);
ALTER TABLE SSQRTZ_job_listeners MODIFY JOB_NAME VARCHAR2(200);
ALTER TABLE SSQRTZ_job_listeners MODIFY JOB_GROUP VARCHAR2(200);
ALTER TABLE SSQRTZ_job_listeners MODIFY JOB_LISTENER VARCHAR2(200);
ALTER TABLE SSQRTZ_triggers MODIFY TRIGGER_NAME VARCHAR2(200);
ALTER TABLE SSQRTZ_triggers MODIFY TRIGGER_GROUP VARCHAR2(200);
ALTER TABLE SSQRTZ_triggers MODIFY JOB_NAME VARCHAR2(200);
ALTER TABLE SSQRTZ_triggers MODIFY JOB_GROUP VARCHAR2(200);
ALTER TABLE SSQRTZ_triggers MODIFY DESCRIPTION VARCHAR2(250);
ALTER TABLE SSQRTZ_triggers MODIFY CALENDAR_NAME VARCHAR2(200);
ALTER TABLE SSQRTZ_simple_triggers MODIFY TRIGGER_NAME VARCHAR2(200);
ALTER TABLE SSQRTZ_simple_triggers MODIFY TRIGGER_GROUP VARCHAR2(200);
ALTER TABLE SSQRTZ_simple_triggers MODIFY TIMES_TRIGGERED NUMBER(10);
ALTER TABLE SSQRTZ_cron_triggers MODIFY TRIGGER_NAME VARCHAR2(200);
ALTER TABLE SSQRTZ_cron_triggers MODIFY TRIGGER_GROUP VARCHAR2(200);
ALTER TABLE SSQRTZ_cron_triggers MODIFY CRON_EXPRESSION VARCHAR2(120);
ALTER TABLE SSQRTZ_blob_triggers MODIFY TRIGGER_NAME VARCHAR2(200);
ALTER TABLE SSQRTZ_blob_triggers MODIFY TRIGGER_GROUP VARCHAR2(200);
ALTER TABLE SSQRTZ_trigger_listeners MODIFY TRIGGER_NAME VARCHAR2(200);
ALTER TABLE SSQRTZ_trigger_listeners MODIFY TRIGGER_GROUP VARCHAR2(200);
ALTER TABLE SSQRTZ_trigger_listeners MODIFY TRIGGER_LISTENER VARCHAR2(200);
ALTER TABLE SSQRTZ_calendars MODIFY CALENDAR_NAME VARCHAR2(200);
ALTER TABLE SSQRTZ_paused_trigger_grps MODIFY TRIGGER_GROUP VARCHAR2(200);
ALTER TABLE SSQRTZ_fired_triggers MODIFY TRIGGER_NAME VARCHAR2(200);
ALTER TABLE SSQRTZ_fired_triggers MODIFY TRIGGER_GROUP VARCHAR2(200);
ALTER TABLE SSQRTZ_fired_triggers MODIFY INSTANCE_NAME VARCHAR2(200);
ALTER TABLE SSQRTZ_fired_triggers MODIFY JOB_NAME VARCHAR2(200);
ALTER TABLE SSQRTZ_fired_triggers MODIFY JOB_GROUP VARCHAR2(200);
ALTER TABLE SSQRTZ_scheduler_state MODIFY INSTANCE_NAME VARCHAR2(200);
INSERT INTO SS_SchemaInfo values (7);
