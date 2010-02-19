
delete from SSQRTZ_job_listeners;
delete from SSQRTZ_trigger_listeners;
delete from SSQRTZ_fired_triggers;
delete from SSQRTZ_simple_triggers;
delete from SSQRTZ_cron_triggers;
delete from SSQRTZ_blob_triggers;
delete from SSQRTZ_triggers;
delete from SSQRTZ_job_details;
delete from SSQRTZ_calendars;
delete from SSQRTZ_paused_trigger_grps;
delete from SSQRTZ_locks;
delete from SSQRTZ_scheduler_state;

drop table SSQRTZ_calendars;
drop table SSQRTZ_fired_triggers;
drop table SSQRTZ_trigger_listeners;
drop table SSQRTZ_blob_triggers;
drop table SSQRTZ_cron_triggers;
drop table SSQRTZ_simple_triggers;
drop table SSQRTZ_triggers;
drop table SSQRTZ_job_listeners;
drop table SSQRTZ_job_details;
drop table SSQRTZ_paused_trigger_grps;
drop table SSQRTZ_locks;
drop table SSQRTZ_scheduler_state;


CREATE TABLE SSQRTZ_job_details
  (
    JOB_NAME  VARCHAR2(200) NOT NULL,
    JOB_GROUP VARCHAR2(200) NOT NULL,
    DESCRIPTION VARCHAR2(250) NULL,
    JOB_CLASS_NAME   VARCHAR2(250) NOT NULL, 
    IS_DURABLE VARCHAR2(1) NOT NULL,
    IS_VOLATILE VARCHAR2(1) NOT NULL,
    IS_STATEFUL VARCHAR2(1) NOT NULL,
    REQUESTS_RECOVERY VARCHAR2(1) NOT NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP)
);
CREATE TABLE SSQRTZ_job_listeners
  (
    JOB_NAME  VARCHAR2(200) NOT NULL, 
    JOB_GROUP VARCHAR2(200) NOT NULL,
    JOB_LISTENER VARCHAR2(200) NOT NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP,JOB_LISTENER),
    FOREIGN KEY (JOB_NAME,JOB_GROUP) 
	REFERENCES SSQRTZ_JOB_DETAILS(JOB_NAME,JOB_GROUP)
);
CREATE TABLE SSQRTZ_triggers
  (
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    JOB_NAME  VARCHAR2(200) NOT NULL, 
    JOB_GROUP VARCHAR2(200) NOT NULL,
    IS_VOLATILE VARCHAR2(1) NOT NULL,
    DESCRIPTION VARCHAR2(250) NULL,
    NEXT_FIRE_TIME NUMBER(13) NULL,
    PREV_FIRE_TIME NUMBER(13) NULL,
    PRIORITY NUMBER(13) NULL,
    TRIGGER_STATE VARCHAR2(16) NOT NULL,
    TRIGGER_TYPE VARCHAR2(8) NOT NULL,
    START_TIME NUMBER(13) NOT NULL,
    END_TIME NUMBER(13) NULL,
    CALENDAR_NAME VARCHAR2(200) NULL,
    MISFIRE_INSTR NUMBER(2) NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (JOB_NAME,JOB_GROUP) 
	REFERENCES SSQRTZ_JOB_DETAILS(JOB_NAME,JOB_GROUP) 
);
CREATE TABLE SSQRTZ_simple_triggers
  (
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    REPEAT_COUNT NUMBER(7) NOT NULL,
    REPEAT_INTERVAL NUMBER(12) NOT NULL,
    TIMES_TRIGGERED NUMBER(10) NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP) 
	REFERENCES SSQRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE SSQRTZ_cron_triggers
  (
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    CRON_EXPRESSION VARCHAR2(120) NOT NULL,
    TIME_ZONE_ID VARCHAR2(80),
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP) 
	REFERENCES SSQRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE SSQRTZ_blob_triggers
  (
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    BLOB_DATA BLOB NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP) 
        REFERENCES SSQRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE SSQRTZ_trigger_listeners
  (
    TRIGGER_NAME  VARCHAR2(200) NOT NULL, 
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    TRIGGER_LISTENER VARCHAR2(200) NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_LISTENER),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP) 
	REFERENCES SSQRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE SSQRTZ_calendars
  (
    CALENDAR_NAME  VARCHAR2(200) NOT NULL, 
    CALENDAR BLOB NOT NULL,
    PRIMARY KEY (CALENDAR_NAME)
);
CREATE TABLE SSQRTZ_paused_trigger_grps
  (
    TRIGGER_GROUP  VARCHAR2(200) NOT NULL, 
    PRIMARY KEY (TRIGGER_GROUP)
);
CREATE TABLE SSQRTZ_fired_triggers 
  (
    ENTRY_ID VARCHAR2(95) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    IS_VOLATILE VARCHAR2(1) NOT NULL,
    INSTANCE_NAME VARCHAR2(200) NOT NULL,
    FIRED_TIME NUMBER(13) NOT NULL,
    PRIORITY NUMBER(13) NOT NULL,
    STATE VARCHAR2(16) NOT NULL,
    JOB_NAME VARCHAR2(200) NULL,
    JOB_GROUP VARCHAR2(200) NULL,
    IS_STATEFUL VARCHAR2(1) NULL,
    REQUESTS_RECOVERY VARCHAR2(1) NULL,
    PRIMARY KEY (ENTRY_ID)
);
CREATE TABLE SSQRTZ_scheduler_state 
  (
    INSTANCE_NAME VARCHAR2(200) NOT NULL,
    LAST_CHECKIN_TIME NUMBER(13) NOT NULL,
    CHECKIN_INTERVAL NUMBER(13) NOT NULL,
    PRIMARY KEY (INSTANCE_NAME)
);
CREATE TABLE SSQRTZ_locks
  (
    LOCK_NAME  VARCHAR2(40) NOT NULL, 
    PRIMARY KEY (LOCK_NAME)
);
INSERT INTO SSQRTZ_locks values('TRIGGER_ACCESS');
INSERT INTO SSQRTZ_locks values('JOB_ACCESS');
INSERT INTO SSQRTZ_locks values('CALENDAR_ACCESS');
INSERT INTO SSQRTZ_locks values('STATE_ACCESS');
INSERT INTO SSQRTZ_locks values('MISFIRE_ACCESS');
create index idx_qrtz_j_req_recovery on SSQRTZ_job_details(REQUESTS_RECOVERY);
create index idx_qrtz_t_next_fire_time on SSQRTZ_triggers(NEXT_FIRE_TIME);
create index idx_qrtz_t_state on SSQRTZ_triggers(TRIGGER_STATE);
create index idx_qrtz_t_nft_st on SSQRTZ_triggers(NEXT_FIRE_TIME,TRIGGER_STATE);
create index idx_qrtz_t_volatile on SSQRTZ_triggers(IS_VOLATILE);
create index idx_qrtz_ft_trig_name on SSQRTZ_fired_triggers(TRIGGER_NAME);
create index idx_qrtz_ft_trig_group on SSQRTZ_fired_triggers(TRIGGER_GROUP);
create index idx_qrtz_ft_trig_nm_gp on SSQRTZ_fired_triggers(TRIGGER_NAME,TRIGGER_GROUP);
create index idx_qrtz_ft_trig_volatile on SSQRTZ_fired_triggers(IS_VOLATILE);
create index idx_qrtz_ft_trig_inst_name on SSQRTZ_fired_triggers(INSTANCE_NAME);
create index idx_qrtz_ft_job_name on SSQRTZ_fired_triggers(JOB_NAME);
create index idx_qrtz_ft_job_group on SSQRTZ_fired_triggers(JOB_GROUP);
create index idx_qrtz_ft_job_stateful on SSQRTZ_fired_triggers(IS_STATEFUL);
create index idx_qrtz_ft_job_req_recovery on SSQRTZ_fired_triggers(REQUESTS_RECOVERY);



commit;