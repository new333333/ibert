ALTER TABLE SSQRTZ_JOB_LISTENERS DROP CONSTRAINT FK_SSQRTZ_JOB_LISTENERS_SSQRTZ_JOB_DETAILS;
ALTER TABLE SSQRTZ_TRIGGERS DROP CONSTRAINT FK_SSQRTZ_TRIGGERS_SSQRTZ_JOB_DETAILS;
ALTER TABLE SSQRTZ_CRON_TRIGGERS DROP CONSTRAINT FK_SSQRTZ_CRON_TRIGGERS_SSQRTZ_TRIGGERS;
ALTER TABLE SSQRTZ_SIMPLE_TRIGGERS DROP CONSTRAINT FK_SSQRTZ_SIMPLE_TRIGGERS_SSQRTZ_TRIGGERS;
ALTER TABLE SSQRTZ_TRIGGER_LISTENERS DROP CONSTRAINT FK_SSQRTZ_TRIGGER_LISTENERS_SSQRTZ_TRIGGERS;
ALTER TABLE SSQRTZ_BLOB_TRIGGERS DROP CONSTRAINT FK_SSQRTZ_BLOB_TRIGGERS_SSQRTZ_TRIGGERS;
DROP TABLE SSQRTZ_CALENDARS;
DROP TABLE SSQRTZ_CRON_TRIGGERS;
DROP TABLE SSQRTZ_BLOB_TRIGGERS;
DROP TABLE SSQRTZ_FIRED_TRIGGERS;
DROP TABLE SSQRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE SSQRTZ_SCHEDULER_STATE;
DROP TABLE SSQRTZ_LOCKS;
DROP TABLE SSQRTZ_JOB_DETAILS;
DROP TABLE SSQRTZ_JOB_LISTENERS;
DROP TABLE SSQRTZ_SIMPLE_TRIGGERS;
DROP TABLE SSQRTZ_TRIGGER_LISTENERS;
DROP TABLE SSQRTZ_TRIGGERS;
CREATE TABLE SSQRTZ_CALENDARS (
  CALENDAR_NAME VARCHAR (100)  NOT NULL ,
  CALENDAR IMAGE NOT NULL, 
  PRIMARY KEY (CALENDAR_NAME));
CREATE TABLE SSQRTZ_CRON_TRIGGERS (
  TRIGGER_NAME VARCHAR (100)  NOT NULL ,
  TRIGGER_GROUP VARCHAR (100)  NOT NULL ,
  CRON_EXPRESSION VARCHAR (120)  NOT NULL ,
  TIME_ZONE_ID VARCHAR (80),
  PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP));
CREATE TABLE SSQRTZ_FIRED_TRIGGERS (
  ENTRY_ID VARCHAR (95)  NOT NULL ,
  TRIGGER_NAME VARCHAR (100)  NOT NULL ,
  TRIGGER_GROUP VARCHAR (100)  NOT NULL ,
  IS_VOLATILE VARCHAR (1)  NOT NULL ,
  INSTANCE_NAME VARCHAR (100)  NOT NULL ,
  FIRED_TIME BIGINT NOT NULL ,
  PRIORITY INTEGER NOT NULL ,
  STATE VARCHAR (16)  NOT NULL,
  JOB_NAME VARCHAR (100)  NULL ,
  JOB_GROUP VARCHAR (100)  NULL ,
  IS_STATEFUL VARCHAR (1)  NULL ,
  REQUESTS_RECOVERY VARCHAR (1)  NULL,
  PRIMARY KEY (ENTRY_ID));
CREATE TABLE SSQRTZ_PAUSED_TRIGGER_GRPS (
  TRIGGER_GROUP VARCHAR (100)  NOT NULL,
  PRIMARY KEY (TRIGGER_GROUP));
CREATE TABLE SSQRTZ_SCHEDULER_STATE (
  INSTANCE_NAME VARCHAR (100)  NOT NULL ,
  LAST_CHECKIN_TIME BIGINT NOT NULL ,
  CHECKIN_INTERVAL BIGINT NOT NULL ,
PRIMARY KEY (INSTANCE_NAME));
CREATE TABLE SSQRTZ_LOCKS (
  LOCK_NAME VARCHAR (40)  NOT NULL,
PRIMARY KEY (LOCK_NAME));
CREATE TABLE SSQRTZ_JOB_DETAILS (
  JOB_NAME VARCHAR (100)  NOT NULL ,
  JOB_GROUP VARCHAR (100)  NOT NULL ,
  DESCRIPTION VARCHAR (250) NULL ,
  JOB_CLASS_NAME VARCHAR (250)  NOT NULL ,
  IS_DURABLE VARCHAR (1)  NOT NULL ,
  IS_VOLATILE VARCHAR (1)  NOT NULL ,
  IS_STATEFUL VARCHAR (1)  NOT NULL ,
  REQUESTS_RECOVERY VARCHAR (1)  NOT NULL ,
  JOB_DATA IMAGE NULL,
  PRIMARY KEY (JOB_NAME,JOB_GROUP));
CREATE TABLE SSQRTZ_JOB_LISTENERS (
  JOB_NAME VARCHAR (100)  NOT NULL ,
  JOB_GROUP VARCHAR (100)  NOT NULL ,
  JOB_LISTENER VARCHAR (100)  NOT NULL,
  PRIMARY KEY (JOB_NAME,JOB_GROUP,JOB_LISTENER));
CREATE TABLE SSQRTZ_SIMPLE_TRIGGERS (
  TRIGGER_NAME VARCHAR (100)  NOT NULL ,
  TRIGGER_GROUP VARCHAR (100)  NOT NULL ,
  REPEAT_COUNT BIGINT NOT NULL ,
  REPEAT_INTERVAL BIGINT NOT NULL ,
  TIMES_TRIGGERED BIGINT NOT NULL,
  PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP));
CREATE TABLE SSQRTZ_BLOB_TRIGGERS (
  TRIGGER_NAME VARCHAR (100)  NOT NULL ,
  TRIGGER_GROUP VARCHAR (100)  NOT NULL ,
  BLOB_DATA IMAGE NULL,
  PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP));
CREATE TABLE SSQRTZ_TRIGGER_LISTENERS (
  TRIGGER_NAME VARCHAR (100)  NOT NULL ,
  TRIGGER_GROUP VARCHAR (100)  NOT NULL ,
  TRIGGER_LISTENER VARCHAR (100)  NOT NULL,
  PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_LISTENER));
CREATE TABLE SSQRTZ_TRIGGERS (
  TRIGGER_NAME VARCHAR (100)  NOT NULL ,
  TRIGGER_GROUP VARCHAR (100)  NOT NULL ,
  JOB_NAME VARCHAR (100)  NOT NULL ,
  JOB_GROUP VARCHAR (100)  NOT NULL ,
  IS_VOLATILE VARCHAR (1)  NOT NULL ,
  DESCRIPTION VARCHAR (250) NULL ,
  NEXT_FIRE_TIME BIGINT NULL ,
  PREV_FIRE_TIME BIGINT NULL ,
  PRIORITY INTEGER NULL ,
  TRIGGER_STATE VARCHAR (16)  NOT NULL ,
  TRIGGER_TYPE VARCHAR (8)  NOT NULL ,
  START_TIME BIGINT NOT NULL ,
  END_TIME BIGINT NULL ,
  CALENDAR_NAME VARCHAR (100)  NULL ,
  MISFIRE_INSTR SMALLINT NULL,
  JOB_DATA IMAGE NULL,
  PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP));

ALTER TABLE SSQRTZ_CRON_TRIGGERS ADD
  CONSTRAINT FK_SSQRTZ_CRON_TRIGGERS_SSQRTZ_TRIGGERS FOREIGN KEY
  (
    TRIGGER_NAME,
    TRIGGER_GROUP
  ) REFERENCES SSQRTZ_TRIGGERS (
    TRIGGER_NAME,
    TRIGGER_GROUP
  ) ON DELETE CASCADE;
ALTER TABLE SSQRTZ_JOB_LISTENERS ADD
  CONSTRAINT FK_SSQRTZ_JOB_LISTENERS_SSQRTZ_JOB_DETAILS FOREIGN KEY
  (
    JOB_NAME,
    JOB_GROUP
  ) REFERENCES SSQRTZ_JOB_DETAILS (
    JOB_NAME,
    JOB_GROUP
  ) ON DELETE CASCADE;
ALTER TABLE SSQRTZ_SIMPLE_TRIGGERS ADD
  CONSTRAINT FK_SSQRTZ_SIMPLE_TRIGGERS_SSQRTZ_TRIGGERS FOREIGN KEY
  (
    TRIGGER_NAME,
    TRIGGER_GROUP
  ) REFERENCES SSQRTZ_TRIGGERS (
    TRIGGER_NAME,
    TRIGGER_GROUP
  ) ON DELETE CASCADE;
ALTER TABLE SSQRTZ_TRIGGER_LISTENERS ADD
  CONSTRAINT FK_SSQRTZ_TRIGGER_LISTENERS_SSQRTZ_TRIGGERS FOREIGN KEY
  (
    TRIGGER_NAME,
    TRIGGER_GROUP
  ) REFERENCES SSQRTZ_TRIGGERS (
    TRIGGER_NAME,
    TRIGGER_GROUP
  ) ON DELETE CASCADE;
ALTER TABLE SSQRTZ_TRIGGERS ADD
  CONSTRAINT FK_SSQRTZ_TRIGGERS_SSQRTZ_JOB_DETAILS FOREIGN KEY
  (
    JOB_NAME,
    JOB_GROUP
  ) REFERENCES SSQRTZ_JOB_DETAILS (
    JOB_NAME,
    JOB_GROUP
  );
 ALTER TABLE SSQRTZ_BLOB_TRIGGERS ADD CONSTRAINT FK_SSQRTZ_BLOB_TRIGGERS_SSQRTZ_TRIGGERS FOREIGN KEY
  (TRIGGER_NAME,TRIGGER_GROUP) REFERENCES SSQRTZ_TRIGGERS (TRIGGER_NAME,TRIGGER_GROUP);

INSERT INTO SSQRTZ_LOCKS VALUES('TRIGGER_ACCESS');
INSERT INTO SSQRTZ_LOCKS VALUES('JOB_ACCESS');
INSERT INTO SSQRTZ_LOCKS VALUES('CALENDAR_ACCESS');
INSERT INTO SSQRTZ_LOCKS VALUES('STATE_ACCESS');
INSERT INTO SSQRTZ_LOCKS VALUES('MISFIRE_ACCESS');
