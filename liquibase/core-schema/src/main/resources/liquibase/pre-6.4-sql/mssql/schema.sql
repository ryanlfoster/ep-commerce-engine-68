
/* ---------------------------------------------------------------------- */
/* JPA_GENERATED_KEYS                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'JPA_GENERATED_KEYS')
BEGIN
     DECLARE @reftable_1 nvarchar(60), @constraintname_1 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'JPA_GENERATED_KEYS'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_1, @constraintname_1
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_1+' drop constraint '+@constraintname_1)
       FETCH NEXT from refcursor into @reftable_1, @constraintname_1
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE JPA_GENERATED_KEYS
END
;

CREATE TABLE JPA_GENERATED_KEYS
(
                ID NVARCHAR (30) NOT NULL,
                LAST_VALUE BIGINT NULL,
);





/* ---------------------------------------------------------------------- */
/* TDIGITALASSETS                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TDIGITALASSETS')
BEGIN
     DECLARE @reftable_2 nvarchar(60), @constraintname_2 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TDIGITALASSETS'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_2, @constraintname_2
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_2+' drop constraint '+@constraintname_2)
       FETCH NEXT from refcursor into @reftable_2, @constraintname_2
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TDIGITALASSETS
END
;

CREATE TABLE TDIGITALASSETS
(
                UIDPK BIGINT NOT NULL,
                FILE_NAME NVARCHAR (255) NOT NULL,
                EXPIRY_DAYS INT NULL,
                MAX_DOWNLOAD_TIMES INT NULL,

    CONSTRAINT TDIGITALASSETS_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_DA_FILE_NAME ON TDIGITALASSETS (FILE_NAME);




/* ---------------------------------------------------------------------- */
/* TTAXCODE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TTAXCODE')
BEGIN
     DECLARE @reftable_3 nvarchar(60), @constraintname_3 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TTAXCODE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_3, @constraintname_3
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_3+' drop constraint '+@constraintname_3)
       FETCH NEXT from refcursor into @reftable_3, @constraintname_3
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TTAXCODE
END
;

CREATE TABLE TTAXCODE
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                CODE NVARCHAR (255) NOT NULL,

    CONSTRAINT TTAXCODE_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TTAXCODE_GUID_UNIQUE UNIQUE (GUID),
    CONSTRAINT TTAXCODE_CODE_UNIQUE UNIQUE (CODE));





/* ---------------------------------------------------------------------- */
/* TTAXJURISDICTION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TTAXJURISDICTION')
BEGIN
     DECLARE @reftable_4 nvarchar(60), @constraintname_4 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TTAXJURISDICTION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_4, @constraintname_4
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_4+' drop constraint '+@constraintname_4)
       FETCH NEXT from refcursor into @reftable_4, @constraintname_4
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TTAXJURISDICTION
END
;

CREATE TABLE TTAXJURISDICTION
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                REGION_CODE NVARCHAR (255) NOT NULL,
                PRICE_CALCULATION_METH INT default 0 NULL,

    CONSTRAINT TTAXJURISDICTION_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TTAXJURISDICTION_GUID_UNIQUE UNIQUE (GUID));





/* ---------------------------------------------------------------------- */
/* TTAXCATEGORY                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TTAXCATEGORY_FK_1')
    ALTER TABLE TTAXCATEGORY DROP CONSTRAINT TTAXCATEGORY_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TTAXCATEGORY')
BEGIN
     DECLARE @reftable_5 nvarchar(60), @constraintname_5 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TTAXCATEGORY'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_5, @constraintname_5
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_5+' drop constraint '+@constraintname_5)
       FETCH NEXT from refcursor into @reftable_5, @constraintname_5
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TTAXCATEGORY
END
;

CREATE TABLE TTAXCATEGORY
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                FIELD_MATCH_TYPE INT NULL,
                TAX_JURISDICTION_UID BIGINT NULL,

    CONSTRAINT TTAXCATEGORY_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TTAXCATEGORY_GUID_UNIQUE UNIQUE (GUID));

CREATE  INDEX I_TAXCAT_TAXJUR_UID ON TTAXCATEGORY (TAX_JURISDICTION_UID);




/* ---------------------------------------------------------------------- */
/* TTAXREGION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TTAXREGION_FK_1')
    ALTER TABLE TTAXREGION DROP CONSTRAINT TTAXREGION_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TTAXREGION')
BEGIN
     DECLARE @reftable_6 nvarchar(60), @constraintname_6 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TTAXREGION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_6, @constraintname_6
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_6+' drop constraint '+@constraintname_6)
       FETCH NEXT from refcursor into @reftable_6, @constraintname_6
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TTAXREGION
END
;

CREATE TABLE TTAXREGION
(
                UIDPK BIGINT NOT NULL,
                REGION_NAME NVARCHAR (255) NOT NULL,
                TAX_CATEGORY_UID BIGINT NULL,

    CONSTRAINT TTAXREGION_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_TAXREG_TAXCAT_UID ON TTAXREGION (TAX_CATEGORY_UID);




/* ---------------------------------------------------------------------- */
/* TTAXVALUE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TTAXVALUE_FK_1')
    ALTER TABLE TTAXVALUE DROP CONSTRAINT TTAXVALUE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TTAXVALUE_FK_2')
    ALTER TABLE TTAXVALUE DROP CONSTRAINT TTAXVALUE_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TTAXVALUE')
BEGIN
     DECLARE @reftable_7 nvarchar(60), @constraintname_7 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TTAXVALUE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_7, @constraintname_7
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_7+' drop constraint '+@constraintname_7)
       FETCH NEXT from refcursor into @reftable_7, @constraintname_7
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TTAXVALUE
END
;

CREATE TABLE TTAXVALUE
(
                UIDPK BIGINT NOT NULL,
                TAX_REGION_UID BIGINT NULL,
                TAX_CODE_UID BIGINT NULL,
                VALUE DECIMAL (19,4) NULL,

    CONSTRAINT TTAXVALUE_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_TAXVAL_TAXREG_UID ON TTAXVALUE (TAX_REGION_UID);
CREATE  INDEX I_TAXVAL_TAXCODE_UID ON TTAXVALUE (TAX_CODE_UID);




/* ---------------------------------------------------------------------- */
/* TCATALOG                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCATALOG')
BEGIN
     DECLARE @reftable_8 nvarchar(60), @constraintname_8 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCATALOG'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_8, @constraintname_8
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_8+' drop constraint '+@constraintname_8)
       FETCH NEXT from refcursor into @reftable_8, @constraintname_8
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCATALOG
END
;

CREATE TABLE TCATALOG
(
                UIDPK BIGINT NOT NULL,
                MASTER INT NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                DEFAULT_LOCALE NVARCHAR (20) NOT NULL,
                CATALOG_CODE NVARCHAR (64) NOT NULL,

    CONSTRAINT TCATALOG_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TCATALOG_CANDIDATE_KEYS UNIQUE (CATALOG_CODE));





/* ---------------------------------------------------------------------- */
/* TATTRIBUTE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TATTRIBUTE')
BEGIN
     DECLARE @reftable_9 nvarchar(60), @constraintname_9 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TATTRIBUTE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_9, @constraintname_9
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_9+' drop constraint '+@constraintname_9)
       FETCH NEXT from refcursor into @reftable_9, @constraintname_9
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TATTRIBUTE
END
;

CREATE TABLE TATTRIBUTE
(
                UIDPK BIGINT NOT NULL,
                ATTRIBUTE_KEY NVARCHAR (255) NOT NULL,
                LOCALE_DEPENDANT INT default 0 NOT NULL,
                ATTRIBUTE_TYPE INT NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                REQUIRED INT default 0 NULL,
                VALUE_LOOKUP_ENABLED INT default 0 NULL,
                MULTI_VALUE_ENABLED INT default 0 NULL,
                ATTRIBUTE_USAGE INT NOT NULL,
                SYSTEM INT default 0 NULL,
                CATALOG_UID BIGINT NULL,
                ATTR_GLOBAL INT NOT NULL,

    CONSTRAINT TATTRIBUTE_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TATTRIBUTE_UNIQUE UNIQUE (ATTRIBUTE_KEY));





/* ---------------------------------------------------------------------- */
/* TSETTINGDEFINITION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSETTINGDEFINITION')
BEGIN
     DECLARE @reftable_10 nvarchar(60), @constraintname_10 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSETTINGDEFINITION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_10, @constraintname_10
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_10+' drop constraint '+@constraintname_10)
       FETCH NEXT from refcursor into @reftable_10, @constraintname_10
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSETTINGDEFINITION
END
;

CREATE TABLE TSETTINGDEFINITION
(
                UIDPK BIGINT NOT NULL,
                PATH NVARCHAR (255) NOT NULL,
                DEFAULT_VALUE NTEXT NULL,
                VALUE_TYPE NVARCHAR (255) NOT NULL,
                DESCRIPTION NTEXT NULL,
                LAST_MODIFIED_DATE DATETIME default CURRENT_TIMESTAMP NOT NULL,
                MAX_OVERRIDE_VALUES BIGINT default 0 NULL,

    CONSTRAINT TSETTINGDEFINITION_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TSETTINGDEFINITION_UNIQUE UNIQUE (PATH));





/* ---------------------------------------------------------------------- */
/* TSETTINGMETADATA                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSETTINGMETADATA_FK_1')
    ALTER TABLE TSETTINGMETADATA DROP CONSTRAINT TSETTINGMETADATA_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSETTINGMETADATA')
BEGIN
     DECLARE @reftable_11 nvarchar(60), @constraintname_11 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSETTINGMETADATA'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_11, @constraintname_11
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_11+' drop constraint '+@constraintname_11)
       FETCH NEXT from refcursor into @reftable_11, @constraintname_11
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSETTINGMETADATA
END
;

CREATE TABLE TSETTINGMETADATA
(
                UIDPK BIGINT NOT NULL,
                SETTING_DEFINITION_UID BIGINT NULL,
                METADATA_KEY NVARCHAR (255) NOT NULL,
                VALUE NVARCHAR (2000) NULL,

    CONSTRAINT TSETTINGMETADATA_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_SETTINGMETADATA_DEF_UID ON TSETTINGMETADATA (SETTING_DEFINITION_UID);




/* ---------------------------------------------------------------------- */
/* TSETTINGVALUE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSETTINGVALUE_FK_1')
    ALTER TABLE TSETTINGVALUE DROP CONSTRAINT TSETTINGVALUE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSETTINGVALUE')
BEGIN
     DECLARE @reftable_12 nvarchar(60), @constraintname_12 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSETTINGVALUE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_12, @constraintname_12
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_12+' drop constraint '+@constraintname_12)
       FETCH NEXT from refcursor into @reftable_12, @constraintname_12
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSETTINGVALUE
END
;

CREATE TABLE TSETTINGVALUE
(
                UIDPK BIGINT NOT NULL,
                SETTING_DEFINITION_UID BIGINT NULL,
                CONTEXT NVARCHAR (255) NULL,
                LAST_MODIFIED_DATE DATETIME default CURRENT_TIMESTAMP NOT NULL,
                CONTEXT_VALUE NTEXT NULL,

    CONSTRAINT TSETTINGVALUE_PK PRIMARY KEY(UIDPK));





/* ---------------------------------------------------------------------- */
/* TSTORE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSTORE_FK_1')
    ALTER TABLE TSTORE DROP CONSTRAINT TSTORE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSTORE')
BEGIN
     DECLARE @reftable_13 nvarchar(60), @constraintname_13 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSTORE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_13, @constraintname_13
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_13+' drop constraint '+@constraintname_13)
       FETCH NEXT from refcursor into @reftable_13, @constraintname_13
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSTORE
END
;

CREATE TABLE TSTORE
(
                UIDPK BIGINT NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                STORECODE NVARCHAR (64) NOT NULL,
                DESCRIPTION NTEXT NULL,
                URL NVARCHAR (255) NULL,
                ENABLED INT default 0 NOT NULL,
                DEFAULT_LOCALE NVARCHAR (20) NULL,
                DEFAULT_CURRENCY CHAR (3) NULL,
                SUB_COUNTRY NVARCHAR (200) NULL,
                COUNTRY NVARCHAR (200) NOT NULL,
                TIMEZONE NVARCHAR (50) NOT NULL,
                STORE_TYPE CHAR (3) NOT NULL,
                CONTENT_ENCODING NVARCHAR (20) default 'utf-8' NULL,
                CREDIT_CARD_CVV2_ENABLED INT default 0 NOT NULL,
                CC_PAYER_AUTH_ENABLED INT default 0 NOT NULL,
                DISPLAY_OUT_OF_STOCK INT default 0 NOT NULL,
                STORE_FULL_CREDIT_CARDS INT default 1 NOT NULL,
                EMAIL_SENDER_NAME NVARCHAR (255) NULL,
                EMAIL_SENDER_ADDRESS NVARCHAR (255) NULL,
                STORE_ADMIN_EMAIL NVARCHAR (255) NULL,
                CATALOG_UID BIGINT NULL,
                STORE_STATE INT default 0 NOT NULL,

    CONSTRAINT TSTORE_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TSTORE_CANDIDATE_KEYS UNIQUE (NAME, STORECODE),
    CONSTRAINT TSTORECODE_UNIQUE UNIQUE (STORECODE));

CREATE  INDEX I_STORE_CATALOG_UID ON TSTORE (CATALOG_UID);




/* ---------------------------------------------------------------------- */
/* TCUSTOMER                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCUSTOMER_FK_1')
    ALTER TABLE TCUSTOMER DROP CONSTRAINT TCUSTOMER_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCUSTOMER')
BEGIN
     DECLARE @reftable_14 nvarchar(60), @constraintname_14 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCUSTOMER'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_14, @constraintname_14
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_14+' drop constraint '+@constraintname_14)
       FETCH NEXT from refcursor into @reftable_14, @constraintname_14
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCUSTOMER
END
;

CREATE TABLE TCUSTOMER
(
                UIDPK BIGINT NOT NULL,
                USER_ID NVARCHAR (255) NOT NULL,
                PREF_BILL_ADDRESS_UID BIGINT NULL,
                PREF_SHIP_ADDRESS_UID BIGINT NULL,
                CREATION_DATE DATETIME NOT NULL,
                LAST_EDIT_DATE DATETIME default CURRENT_TIMESTAMP NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                STATUS INT NOT NULL,
                AUTHENTICATION_UID BIGINT NULL,
                STORE_UID BIGINT NOT NULL,

    CONSTRAINT TCUSTOMER_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TCUSTOMER_UNIQUE UNIQUE (GUID));

CREATE  INDEX I_CUST_STORE_UID ON TCUSTOMER (STORE_UID);
CREATE  INDEX I_C_USERID ON TCUSTOMER (USER_ID);
CREATE  INDEX I_CUST_CR_DATE ON TCUSTOMER (CREATION_DATE);




/* ---------------------------------------------------------------------- */
/* TCUSTOMERAUTHENTICATION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCUSTOMERAUTHENTICATION')
BEGIN
     DECLARE @reftable_15 nvarchar(60), @constraintname_15 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCUSTOMERAUTHENTICATION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_15, @constraintname_15
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_15+' drop constraint '+@constraintname_15)
       FETCH NEXT from refcursor into @reftable_15, @constraintname_15
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCUSTOMERAUTHENTICATION
END
;

CREATE TABLE TCUSTOMERAUTHENTICATION
(
                UIDPK BIGINT NOT NULL,
                PASSWORD NVARCHAR (255) NULL,

    CONSTRAINT TCUSTOMERAUTHENTICATION_PK PRIMARY KEY(UIDPK));





/* ---------------------------------------------------------------------- */
/* TCUSTOMERPROFILEVALUE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCUSTOMERPROFILEVALUE_FK_1')
    ALTER TABLE TCUSTOMERPROFILEVALUE DROP CONSTRAINT TCUSTOMERPROFILEVALUE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCUSTOMERPROFILEVALUE_FK_2')
    ALTER TABLE TCUSTOMERPROFILEVALUE DROP CONSTRAINT TCUSTOMERPROFILEVALUE_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCUSTOMERPROFILEVALUE')
BEGIN
     DECLARE @reftable_16 nvarchar(60), @constraintname_16 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCUSTOMERPROFILEVALUE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_16, @constraintname_16
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_16+' drop constraint '+@constraintname_16)
       FETCH NEXT from refcursor into @reftable_16, @constraintname_16
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCUSTOMERPROFILEVALUE
END
;

CREATE TABLE TCUSTOMERPROFILEVALUE
(
                UIDPK BIGINT NOT NULL,
                ATTRIBUTE_UID BIGINT NOT NULL,
                ATTRIBUTE_TYPE INT NOT NULL,
                LOCALIZED_ATTRIBUTE_KEY NVARCHAR (255) NOT NULL,
                SHORT_TEXT_VALUE NVARCHAR (255) NULL,
                LONG_TEXT_VALUE NTEXT NULL,
                INTEGER_VALUE INT NULL,
                DECIMAL_VALUE DECIMAL (19,2) NULL,
                BOOLEAN_VALUE INT default 0 NULL,
                DATE_VALUE DATETIME NULL,
                CUSTOMER_UID BIGINT NULL,
                LAST_MODIFIED_DATE DATETIME default CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT TCUSTOMERPROFILEVALUE_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_CPV_ATTR_UID ON TCUSTOMERPROFILEVALUE (ATTRIBUTE_UID);
CREATE  INDEX I_CPV_CUID_ATTKEY ON TCUSTOMERPROFILEVALUE (CUSTOMER_UID, LOCALIZED_ATTRIBUTE_KEY);
CREATE  INDEX I_CPV_STV_ATTVALUE ON TCUSTOMERPROFILEVALUE (SHORT_TEXT_VALUE);




/* ---------------------------------------------------------------------- */
/* TCUSTOMERDELETED                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCUSTOMERDELETED')
BEGIN
     DECLARE @reftable_17 nvarchar(60), @constraintname_17 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCUSTOMERDELETED'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_17, @constraintname_17
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_17+' drop constraint '+@constraintname_17)
       FETCH NEXT from refcursor into @reftable_17, @constraintname_17
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCUSTOMERDELETED
END
;

CREATE TABLE TCUSTOMERDELETED
(
                UIDPK BIGINT NOT NULL,
                CUSTOMER_UID BIGINT NOT NULL,
                DELETED_DATE DATETIME NOT NULL,

    CONSTRAINT TCUSTOMERDELETED_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_CUD_DELETED_DATE ON TCUSTOMERDELETED (DELETED_DATE);




/* ---------------------------------------------------------------------- */
/* TADDRESS                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TADDRESS_FK_1')
    ALTER TABLE TADDRESS DROP CONSTRAINT TADDRESS_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TADDRESS')
BEGIN
     DECLARE @reftable_18 nvarchar(60), @constraintname_18 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TADDRESS'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_18, @constraintname_18
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_18+' drop constraint '+@constraintname_18)
       FETCH NEXT from refcursor into @reftable_18, @constraintname_18
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TADDRESS
END
;

CREATE TABLE TADDRESS
(
                UIDPK BIGINT NOT NULL,
                LAST_NAME NVARCHAR (100) NULL,
                FIRST_NAME NVARCHAR (100) NULL,
                PHONE_NUMBER NVARCHAR (50) NULL,
                FAX_NUMBER NVARCHAR (50) NULL,
                STREET_1 NVARCHAR (200) NULL,
                STREET_2 NVARCHAR (200) NULL,
                CITY NVARCHAR (200) NULL,
                SUB_COUNTRY NVARCHAR (200) NULL,
                ZIP_POSTAL_CODE NVARCHAR (50) NULL,
                COUNTRY NVARCHAR (200) NULL,
                COMMERCIAL INT default 0 NULL,
                GUID NVARCHAR (64) NOT NULL,
                CUSTOMER_UID BIGINT NULL,

    CONSTRAINT TADDRESS_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TADDRESS_UNIQUE UNIQUE (GUID));

CREATE  INDEX I_ADDR_C_UID ON TADDRESS (CUSTOMER_UID);




/* ---------------------------------------------------------------------- */
/* TCATALOGSUPPORTEDLOCALE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCATALOGSUPPORTEDLOCALE_FK_1')
    ALTER TABLE TCATALOGSUPPORTEDLOCALE DROP CONSTRAINT TCATALOGSUPPORTEDLOCALE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCATALOGSUPPORTEDLOCALE')
BEGIN
     DECLARE @reftable_19 nvarchar(60), @constraintname_19 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCATALOGSUPPORTEDLOCALE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_19, @constraintname_19
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_19+' drop constraint '+@constraintname_19)
       FETCH NEXT from refcursor into @reftable_19, @constraintname_19
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCATALOGSUPPORTEDLOCALE
END
;

CREATE TABLE TCATALOGSUPPORTEDLOCALE
(
                UIDPK BIGINT NOT NULL,
                LOCALE NVARCHAR (255) NOT NULL,
                CATALOG_UID BIGINT NOT NULL,

    CONSTRAINT TCATALOGSUPPORTEDLOCALE_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_CATSUPLOC_CATALOG_UID ON TCATALOGSUPPORTEDLOCALE (CATALOG_UID);




/* ---------------------------------------------------------------------- */
/* TCATEGORYTYPE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCATEGORYTYPE_FK_1')
    ALTER TABLE TCATEGORYTYPE DROP CONSTRAINT TCATEGORYTYPE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCATEGORYTYPE')
BEGIN
     DECLARE @reftable_20 nvarchar(60), @constraintname_20 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCATEGORYTYPE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_20, @constraintname_20
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_20+' drop constraint '+@constraintname_20)
       FETCH NEXT from refcursor into @reftable_20, @constraintname_20
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCATEGORYTYPE
END
;

CREATE TABLE TCATEGORYTYPE
(
                UIDPK BIGINT NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                TEMPLATE NVARCHAR (255) NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                CATALOG_UID BIGINT NOT NULL,

    CONSTRAINT TCATEGORYTYPE_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TCATEGORYTYPE_UNIQUE UNIQUE (NAME),
    CONSTRAINT TCATEGORYTYPE_GUID_UNIQUE UNIQUE (GUID));

CREATE  INDEX I_CATTYPE_CATALOG_UID ON TCATEGORYTYPE (CATALOG_UID);




/* ---------------------------------------------------------------------- */
/* TCATEGORY                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCATEGORY_FK_1')
    ALTER TABLE TCATEGORY DROP CONSTRAINT TCATEGORY_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCATEGORY_FK_2')
    ALTER TABLE TCATEGORY DROP CONSTRAINT TCATEGORY_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCATEGORY')
BEGIN
     DECLARE @reftable_21 nvarchar(60), @constraintname_21 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCATEGORY'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_21, @constraintname_21
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_21+' drop constraint '+@constraintname_21)
       FETCH NEXT from refcursor into @reftable_21, @constraintname_21
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCATEGORY
END
;

CREATE TABLE TCATEGORY
(
                UIDPK BIGINT NOT NULL,
                TYPE NVARCHAR (255) NOT NULL,
                LAST_MODIFIED_DATE DATETIME default CURRENT_TIMESTAMP NOT NULL,
                PARENT_CATEGORY_UID BIGINT NULL,
                ORDERING INT NULL,
                CATALOG_UID BIGINT NOT NULL,

    CONSTRAINT TCATEGORY_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_CAT_CATALOG_UID ON TCATEGORY (CATALOG_UID);
CREATE  INDEX I_CAT_PARENT_UID ON TCATEGORY (PARENT_CATEGORY_UID);
CREATE  INDEX I_C_MODIFY_DATE ON TCATEGORY (LAST_MODIFIED_DATE);




/* ---------------------------------------------------------------------- */
/* TMASTERCATEGORY                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TMASTERCATEGORY_FK_1')
    ALTER TABLE TMASTERCATEGORY DROP CONSTRAINT TMASTERCATEGORY_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TMASTERCATEGORY')
BEGIN
     DECLARE @reftable_22 nvarchar(60), @constraintname_22 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TMASTERCATEGORY'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_22, @constraintname_22
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_22+' drop constraint '+@constraintname_22)
       FETCH NEXT from refcursor into @reftable_22, @constraintname_22
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TMASTERCATEGORY
END
;

CREATE TABLE TMASTERCATEGORY
(
                UIDPK BIGINT NOT NULL,
                START_DATE DATETIME NOT NULL,
                END_DATE DATETIME NULL,
                HIDDEN INT default 0 NULL,
                CATEGORY_TYPE_UID BIGINT NOT NULL,
                CODE NVARCHAR (64) NOT NULL,
                VIRTUAL INT NULL,

    CONSTRAINT TMASTERCATEGORY_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TCATEGORY_UNIQUE UNIQUE (CODE));

CREATE  INDEX I_CAT_TYPE_UID ON TMASTERCATEGORY (CATEGORY_TYPE_UID);
CREATE  INDEX I_C_SE_DATE ON TMASTERCATEGORY (START_DATE, END_DATE);




/* ---------------------------------------------------------------------- */
/* TLINKEDCATEGORY                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TLINKEDCATEGORY_FK_1')
    ALTER TABLE TLINKEDCATEGORY DROP CONSTRAINT TLINKEDCATEGORY_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TLINKEDCATEGORY')
BEGIN
     DECLARE @reftable_23 nvarchar(60), @constraintname_23 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TLINKEDCATEGORY'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_23, @constraintname_23
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_23+' drop constraint '+@constraintname_23)
       FETCH NEXT from refcursor into @reftable_23, @constraintname_23
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TLINKEDCATEGORY
END
;

CREATE TABLE TLINKEDCATEGORY
(
                UIDPK BIGINT NOT NULL,
                MASTER_CATEGORY_UID BIGINT NULL,
                INCLUDE INT default 1 NULL,

    CONSTRAINT TLINKEDCATEGORY_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_LCAT_MCAT_UID ON TLINKEDCATEGORY (MASTER_CATEGORY_UID);




/* ---------------------------------------------------------------------- */
/* TCATEGORYDELETED                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCATEGORYDELETED')
BEGIN
     DECLARE @reftable_24 nvarchar(60), @constraintname_24 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCATEGORYDELETED'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_24, @constraintname_24
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_24+' drop constraint '+@constraintname_24)
       FETCH NEXT from refcursor into @reftable_24, @constraintname_24
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCATEGORYDELETED
END
;

CREATE TABLE TCATEGORYDELETED
(
                UIDPK BIGINT NOT NULL,
                CATEGORY_UID BIGINT NOT NULL,
                DELETED_DATE DATETIME NOT NULL,

    CONSTRAINT TCATEGORYDELETED_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_CAD_DELETED_DATE ON TCATEGORYDELETED (DELETED_DATE);




/* ---------------------------------------------------------------------- */
/* TCATEGORYATTRIBUTEVALUE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCATEGORYATTRIBUTEVALUE_FK_1')
    ALTER TABLE TCATEGORYATTRIBUTEVALUE DROP CONSTRAINT TCATEGORYATTRIBUTEVALUE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCATEGORYATTRIBUTEVALUE')
BEGIN
     DECLARE @reftable_25 nvarchar(60), @constraintname_25 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCATEGORYATTRIBUTEVALUE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_25, @constraintname_25
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_25+' drop constraint '+@constraintname_25)
       FETCH NEXT from refcursor into @reftable_25, @constraintname_25
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCATEGORYATTRIBUTEVALUE
END
;

CREATE TABLE TCATEGORYATTRIBUTEVALUE
(
                UIDPK BIGINT NOT NULL,
                ATTRIBUTE_UID BIGINT NOT NULL,
                ATTRIBUTE_TYPE INT NOT NULL,
                LOCALIZED_ATTRIBUTE_KEY NVARCHAR (255) NOT NULL,
                SHORT_TEXT_VALUE NVARCHAR (255) NULL,
                LONG_TEXT_VALUE NTEXT NULL,
                INTEGER_VALUE INT NULL,
                DECIMAL_VALUE DECIMAL (19,2) NULL,
                BOOLEAN_VALUE INT default 0 NULL,
                DATE_VALUE DATETIME NULL,
                CATEGORY_UID BIGINT NULL,

    CONSTRAINT TCATEGORYATTRIBUTEVALUE_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_CAV_ATTR_UID ON TCATEGORYATTRIBUTEVALUE (ATTRIBUTE_UID);
CREATE  INDEX I_CAV_CAT_UID ON TCATEGORYATTRIBUTEVALUE (CATEGORY_UID);




/* ---------------------------------------------------------------------- */
/* TCATEGORYLDF                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCATEGORYLDF')
BEGIN
     DECLARE @reftable_26 nvarchar(60), @constraintname_26 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCATEGORYLDF'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_26, @constraintname_26
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_26+' drop constraint '+@constraintname_26)
       FETCH NEXT from refcursor into @reftable_26, @constraintname_26
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCATEGORYLDF
END
;

CREATE TABLE TCATEGORYLDF
(
                UIDPK BIGINT NOT NULL,
                CATEGORY_UID BIGINT NOT NULL,
                URL NVARCHAR (255) NULL,
                KEY_WORDS NVARCHAR (255) NULL,
                DESCRIPTION NVARCHAR (255) NULL,
                TITLE NVARCHAR (255) NULL,
                DISPLAY_NAME NVARCHAR (255) NULL,
                LOCALE NVARCHAR (20) NOT NULL,

    CONSTRAINT TCATEGORYLDF_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_CLDF_CAT_UID ON TCATEGORYLDF (CATEGORY_UID);
CREATE  INDEX I_CLDF_LOCALE_NAME ON TCATEGORYLDF (LOCALE, DISPLAY_NAME);




/* ---------------------------------------------------------------------- */
/* TCATEGORYTYPEATTRIBUTE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCATEGORYTYPEATTRIBUTE_FK_1')
    ALTER TABLE TCATEGORYTYPEATTRIBUTE DROP CONSTRAINT TCATEGORYTYPEATTRIBUTE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCATEGORYTYPEATTRIBUTE_FK_2')
    ALTER TABLE TCATEGORYTYPEATTRIBUTE DROP CONSTRAINT TCATEGORYTYPEATTRIBUTE_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCATEGORYTYPEATTRIBUTE')
BEGIN
     DECLARE @reftable_27 nvarchar(60), @constraintname_27 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCATEGORYTYPEATTRIBUTE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_27, @constraintname_27
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_27+' drop constraint '+@constraintname_27)
       FETCH NEXT from refcursor into @reftable_27, @constraintname_27
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCATEGORYTYPEATTRIBUTE
END
;

CREATE TABLE TCATEGORYTYPEATTRIBUTE
(
                UIDPK BIGINT NOT NULL,
                ORDERING INT NULL,
                ATTRIBUTE_UID BIGINT NOT NULL,
                CATEGORY_TYPE_UID BIGINT NOT NULL,

    CONSTRAINT TCATEGORYTYPEATTRIBUTE_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_CTA_ATTR_UID ON TCATEGORYTYPEATTRIBUTE (ATTRIBUTE_UID);
CREATE  INDEX I_CTA_TYPE_UID ON TCATEGORYTYPEATTRIBUTE (CATEGORY_TYPE_UID);




/* ---------------------------------------------------------------------- */
/* TCMUSER                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCMUSER')
BEGIN
     DECLARE @reftable_28 nvarchar(60), @constraintname_28 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCMUSER'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_28, @constraintname_28
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_28+' drop constraint '+@constraintname_28)
       FETCH NEXT from refcursor into @reftable_28, @constraintname_28
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCMUSER
END
;

CREATE TABLE TCMUSER
(
                UIDPK BIGINT NOT NULL,
                USER_NAME NVARCHAR (255) NOT NULL,
                EMAIL NVARCHAR (255) NOT NULL,
                FIRST_NAME NVARCHAR (100) NULL,
                LAST_NAME NVARCHAR (100) NULL,
                PASSWORD NVARCHAR (255) NOT NULL,
                CREATION_DATE DATETIME NOT NULL,
                LAST_LOGIN_DATE DATETIME NULL,
                LAST_CHANGED_PASSWORD_DATE DATETIME NULL,
                FAILED_LOGIN_ATTEMPTS INT default 0 NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                STATUS INT default 4 NOT NULL,
                ALL_WAREHOUSE_ACCESS INT default 1 NOT NULL,
                ALL_CATALOG_ACCESS INT default 1 NOT NULL,
                ALL_STORE_ACCESS INT default 1 NOT NULL,
                ALL_PRICELIST_ACCESS INT default 1 NOT NULL,
                LAST_MODIFIED_DATE DATETIME default CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT TCMUSER_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TCMUSER_UNIQUE UNIQUE (USER_NAME),
    CONSTRAINT TCMUSER_EMAIL_UNIQUE UNIQUE (EMAIL),
    CONSTRAINT TCMUSER_GUID_UNIQUE UNIQUE (GUID));





/* ---------------------------------------------------------------------- */
/* TPASSWORDHISTORY                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPASSWORDHISTORY_FK_1')
    ALTER TABLE TPASSWORDHISTORY DROP CONSTRAINT TPASSWORDHISTORY_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TPASSWORDHISTORY')
BEGIN
     DECLARE @reftable_29 nvarchar(60), @constraintname_29 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TPASSWORDHISTORY'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_29, @constraintname_29
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_29+' drop constraint '+@constraintname_29)
       FETCH NEXT from refcursor into @reftable_29, @constraintname_29
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TPASSWORDHISTORY
END
;

CREATE TABLE TPASSWORDHISTORY
(
                UIDPK BIGINT NOT NULL,
                OLD_PASSWORD NVARCHAR (255) NOT NULL,
                EXPIRATION_DATE DATETIME NOT NULL,
                CM_USER_UID BIGINT NOT NULL,

    CONSTRAINT TPASSWORDHISTORY_PK PRIMARY KEY(UIDPK));





/* ---------------------------------------------------------------------- */
/* TUSERROLE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TUSERROLE')
BEGIN
     DECLARE @reftable_30 nvarchar(60), @constraintname_30 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TUSERROLE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_30, @constraintname_30
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_30+' drop constraint '+@constraintname_30)
       FETCH NEXT from refcursor into @reftable_30, @constraintname_30
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TUSERROLE
END
;

CREATE TABLE TUSERROLE
(
                UIDPK BIGINT NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                DESCRIPTION NVARCHAR (255) NULL,
                GUID NVARCHAR (64) NOT NULL,

    CONSTRAINT TUSERROLE_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TUSERROLE_UNIQUE UNIQUE (NAME),
    CONSTRAINT TUSERROLE_GUID_UNIQUE UNIQUE (GUID));





/* ---------------------------------------------------------------------- */
/* TCMUSERROLEX                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCMUSERROLEX_FK_1')
    ALTER TABLE TCMUSERROLEX DROP CONSTRAINT TCMUSERROLEX_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCMUSERROLEX_FK_2')
    ALTER TABLE TCMUSERROLEX DROP CONSTRAINT TCMUSERROLEX_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCMUSERROLEX')
BEGIN
     DECLARE @reftable_31 nvarchar(60), @constraintname_31 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCMUSERROLEX'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_31, @constraintname_31
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_31+' drop constraint '+@constraintname_31)
       FETCH NEXT from refcursor into @reftable_31, @constraintname_31
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCMUSERROLEX
END
;

CREATE TABLE TCMUSERROLEX
(
                CM_USER_UID BIGINT NOT NULL,
                USER_ROLE_UID BIGINT NOT NULL,

    CONSTRAINT TCMUSERROLEX_PK PRIMARY KEY(CM_USER_UID,USER_ROLE_UID));

CREATE  INDEX I_CMUSER_ROLE_UID ON TCMUSERROLEX (USER_ROLE_UID);
CREATE  INDEX I_CMUSER_USER_UID ON TCMUSERROLEX (CM_USER_UID);




/* ---------------------------------------------------------------------- */
/* TSHOPPER                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_CUSTOMER')
    ALTER TABLE TSHOPPER DROP CONSTRAINT FK_CUSTOMER;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSHOPPER')
BEGIN
     DECLARE @reftable_32 nvarchar(60), @constraintname_32 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSHOPPER'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_32, @constraintname_32
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_32+' drop constraint '+@constraintname_32)
       FETCH NEXT from refcursor into @reftable_32, @constraintname_32
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSHOPPER
END
;

CREATE TABLE TSHOPPER
(
                UIDPK BIGINT NOT NULL,
                TYPE NVARCHAR (255) default 'SHOPPER' NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                CUSTOMER_GUID NVARCHAR (64) NULL,
                STORECODE NVARCHAR (64) NULL,

    CONSTRAINT TSHOPPER_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TSHOPPER_GUID_UNIQUE UNIQUE (GUID));





/* ---------------------------------------------------------------------- */
/* TCUSTOMERGROUP                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCUSTOMERGROUP')
BEGIN
     DECLARE @reftable_33 nvarchar(60), @constraintname_33 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCUSTOMERGROUP'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_33, @constraintname_33
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_33+' drop constraint '+@constraintname_33)
       FETCH NEXT from refcursor into @reftable_33, @constraintname_33
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCUSTOMERGROUP
END
;

CREATE TABLE TCUSTOMERGROUP
(
                UIDPK BIGINT NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                GUID NVARCHAR (64) NOT NULL,

    CONSTRAINT TCUSTOMERGROUP_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TCUSTOMERGROUP_UNIQUE UNIQUE (NAME),
    CONSTRAINT TCUSTOMERGROUP_GUID_UNIQUE UNIQUE (GUID));





/* ---------------------------------------------------------------------- */
/* TCUSTOMERGROUPROLEX                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCUSTOMERGROUPROLEX_FK_1')
    ALTER TABLE TCUSTOMERGROUPROLEX DROP CONSTRAINT TCUSTOMERGROUPROLEX_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCUSTOMERGROUPROLEX')
BEGIN
     DECLARE @reftable_34 nvarchar(60), @constraintname_34 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCUSTOMERGROUPROLEX'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_34, @constraintname_34
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_34+' drop constraint '+@constraintname_34)
       FETCH NEXT from refcursor into @reftable_34, @constraintname_34
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCUSTOMERGROUPROLEX
END
;

CREATE TABLE TCUSTOMERGROUPROLEX
(
                UIDPK BIGINT NOT NULL,
                CUSTOMER_GROUP_UID BIGINT NOT NULL,
                CUSTOMER_ROLE NVARCHAR (255) NULL,

    CONSTRAINT TCUSTOMERGROUPROLEX_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_CGRX_GROUP_UID ON TCUSTOMERGROUPROLEX (CUSTOMER_GROUP_UID);




/* ---------------------------------------------------------------------- */
/* TCUSTOMERGROUPX                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCUSTOMERGROUPX_FK_1')
    ALTER TABLE TCUSTOMERGROUPX DROP CONSTRAINT TCUSTOMERGROUPX_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCUSTOMERGROUPX_FK_2')
    ALTER TABLE TCUSTOMERGROUPX DROP CONSTRAINT TCUSTOMERGROUPX_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCUSTOMERGROUPX')
BEGIN
     DECLARE @reftable_35 nvarchar(60), @constraintname_35 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCUSTOMERGROUPX'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_35, @constraintname_35
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_35+' drop constraint '+@constraintname_35)
       FETCH NEXT from refcursor into @reftable_35, @constraintname_35
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCUSTOMERGROUPX
END
;

CREATE TABLE TCUSTOMERGROUPX
(
                CUSTOMER_UID BIGINT NOT NULL,
                CUSTOMERGROUP_UID BIGINT NOT NULL,
);

CREATE  INDEX I_CGX_GROUP_UID ON TCUSTOMERGROUPX (CUSTOMERGROUP_UID);
CREATE  INDEX I_CGX_CUSTOMER_UID ON TCUSTOMERGROUPX (CUSTOMER_UID);




/* ---------------------------------------------------------------------- */
/* TCUSTOMERSESSION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCUSTOMERSESSION_FK_1')
    ALTER TABLE TCUSTOMERSESSION DROP CONSTRAINT TCUSTOMERSESSION_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCUSTOMERSESSION')
BEGIN
     DECLARE @reftable_36 nvarchar(60), @constraintname_36 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCUSTOMERSESSION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_36, @constraintname_36
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_36+' drop constraint '+@constraintname_36)
       FETCH NEXT from refcursor into @reftable_36, @constraintname_36
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCUSTOMERSESSION
END
;

CREATE TABLE TCUSTOMERSESSION
(
                UIDPK BIGINT NOT NULL,
                CREATION_DATE DATETIME NOT NULL,
                LAST_ACCESSED_DATE DATETIME default CURRENT_TIMESTAMP NOT NULL,
                SHOPPER_UID BIGINT NULL,
                LOCALE NVARCHAR (255) NULL,
                CURRENCY NVARCHAR (3) NULL,
                GUID NVARCHAR (64) NOT NULL,
                IP_ADDRESS NVARCHAR (255) NULL,

    CONSTRAINT TCUSTOMERSESSION_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TCUSTOMERSESSION_UNIQUE UNIQUE (GUID));

CREATE  INDEX I_CS_SHOPPER_UID ON TCUSTOMERSESSION (SHOPPER_UID);
CREATE  INDEX I_CS_ACS_DATE ON TCUSTOMERSESSION (LAST_ACCESSED_DATE);
CREATE  INDEX I_CS_CRT_DATE ON TCUSTOMERSESSION (CREATION_DATE);




/* ---------------------------------------------------------------------- */
/* TCUSTOMERCREDITCARD                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCUSTOMERCREDITCARD_FK_1')
    ALTER TABLE TCUSTOMERCREDITCARD DROP CONSTRAINT TCUSTOMERCREDITCARD_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCUSTOMERCREDITCARD_FK_2')
    ALTER TABLE TCUSTOMERCREDITCARD DROP CONSTRAINT TCUSTOMERCREDITCARD_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCUSTOMERCREDITCARD')
BEGIN
     DECLARE @reftable_37 nvarchar(60), @constraintname_37 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCUSTOMERCREDITCARD'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_37, @constraintname_37
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_37+' drop constraint '+@constraintname_37)
       FETCH NEXT from refcursor into @reftable_37, @constraintname_37
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCUSTOMERCREDITCARD
END
;

CREATE TABLE TCUSTOMERCREDITCARD
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                CARD_TYPE NVARCHAR (50) NOT NULL,
                CARD_HOLDER_NAME NVARCHAR (100) NOT NULL,
                CARD_NUMBER NVARCHAR (255) NOT NULL,
                EXPIRY_YEAR NVARCHAR (4) NOT NULL,
                EXPIRY_MONTH NVARCHAR (2) NOT NULL,
                START_YEAR NVARCHAR (4) NULL,
                START_MONTH NVARCHAR (2) NULL,
                ISSUE_NUMBER INT NULL,
                DEFAULT_CARD INT NULL,
                CUSTOMER_UID BIGINT NOT NULL,
                BILLING_ADDRESS_UID BIGINT NULL,

    CONSTRAINT TCUSTOMERCREDITCARD_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TCUSTCCRD_GUID_UNIQUE UNIQUE (GUID));

CREATE  INDEX I_CCC_CUSTOMER_UID ON TCUSTOMERCREDITCARD (CUSTOMER_UID);
CREATE  INDEX I_CCC_BA_UID ON TCUSTOMERCREDITCARD (BILLING_ADDRESS_UID);




/* ---------------------------------------------------------------------- */
/* TWAREHOUSEADDRESS                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TWAREHOUSEADDRESS')
BEGIN
     DECLARE @reftable_38 nvarchar(60), @constraintname_38 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TWAREHOUSEADDRESS'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_38, @constraintname_38
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_38+' drop constraint '+@constraintname_38)
       FETCH NEXT from refcursor into @reftable_38, @constraintname_38
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TWAREHOUSEADDRESS
END
;

CREATE TABLE TWAREHOUSEADDRESS
(
                UIDPK BIGINT NOT NULL,
                STREET_1 NVARCHAR (200) NOT NULL,
                STREET_2 NVARCHAR (200) NULL,
                CITY NVARCHAR (200) NOT NULL,
                SUB_COUNTRY NVARCHAR (200) NULL,
                ZIP_POSTAL_CODE NVARCHAR (50) NOT NULL,
                COUNTRY NVARCHAR (200) NOT NULL,

    CONSTRAINT TWAREHOUSEADDRESS_PK PRIMARY KEY(UIDPK));





/* ---------------------------------------------------------------------- */
/* TWAREHOUSE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TWAREHOUSE_FK_1')
    ALTER TABLE TWAREHOUSE DROP CONSTRAINT TWAREHOUSE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TWAREHOUSE')
BEGIN
     DECLARE @reftable_39 nvarchar(60), @constraintname_39 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TWAREHOUSE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_39, @constraintname_39
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_39+' drop constraint '+@constraintname_39)
       FETCH NEXT from refcursor into @reftable_39, @constraintname_39
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TWAREHOUSE
END
;

CREATE TABLE TWAREHOUSE
(
                UIDPK BIGINT NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                PICK_DELAY INT NULL,
                ADDRESS_UID BIGINT NULL,
                CODE NVARCHAR (64) NOT NULL,

    CONSTRAINT TWAREHOUSE_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TWAREHOUSE_CODE_UNIQUE UNIQUE (CODE));

CREATE  INDEX I_WAREHOUSE_ADDRESS_UID ON TWAREHOUSE (ADDRESS_UID);




/* ---------------------------------------------------------------------- */
/* TIMPORTJOB                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TIMPORTJOB_FK_1')
    ALTER TABLE TIMPORTJOB DROP CONSTRAINT TIMPORTJOB_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TIMPORTJOB_FK_2')
    ALTER TABLE TIMPORTJOB DROP CONSTRAINT TIMPORTJOB_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TIMPORTJOB_FK_3')
    ALTER TABLE TIMPORTJOB DROP CONSTRAINT TIMPORTJOB_FK_3;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TIMPORTJOB')
BEGIN
     DECLARE @reftable_40 nvarchar(60), @constraintname_40 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TIMPORTJOB'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_40, @constraintname_40
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_40+' drop constraint '+@constraintname_40)
       FETCH NEXT from refcursor into @reftable_40, @constraintname_40
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TIMPORTJOB
END
;

CREATE TABLE TIMPORTJOB
(
                UIDPK BIGINT NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                CSV_FILE_NAME NVARCHAR (255) NOT NULL,
                COL_DELIMETER CHAR (1) NULL,
                TEXT_QUALIFIER CHAR (1) NULL,
                DATA_TYPE_NAME NVARCHAR (255) NOT NULL,
                IMPORT_TYPE INT NOT NULL,
                MAX_ALLOW_ERRORS INT NOT NULL,
                CATALOG_UID BIGINT NULL,
                STORE_UID BIGINT NULL,
                WAREHOUSE_UID BIGINT NULL,
                DEPENDENT_OBJ_GUID NVARCHAR (64) NULL,
                GUID NVARCHAR (64) NOT NULL,

    CONSTRAINT TIMPORTJOB_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TIMPORTJOB_UNIQUE UNIQUE (NAME),
    CONSTRAINT TIMPORTJOB_GUID_UNIQUE UNIQUE (GUID));

CREATE  INDEX I_IMPORTJOB_CATALOG_UID ON TIMPORTJOB (CATALOG_UID);
CREATE  INDEX I_IMPORTJOB_STORE_UID ON TIMPORTJOB (STORE_UID);
CREATE  INDEX I_IMPORTJOB_WAREHOUSE_UID ON TIMPORTJOB (WAREHOUSE_UID);




/* ---------------------------------------------------------------------- */
/* TIMPORTMAPPINGS                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TIMPORTMAPPINGS_FK_1')
    ALTER TABLE TIMPORTMAPPINGS DROP CONSTRAINT TIMPORTMAPPINGS_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TIMPORTMAPPINGS')
BEGIN
     DECLARE @reftable_41 nvarchar(60), @constraintname_41 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TIMPORTMAPPINGS'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_41, @constraintname_41
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_41+' drop constraint '+@constraintname_41)
       FETCH NEXT from refcursor into @reftable_41, @constraintname_41
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TIMPORTMAPPINGS
END
;

CREATE TABLE TIMPORTMAPPINGS
(
                UIDPK BIGINT NOT NULL,
                IMPORT_JOB_UID BIGINT NOT NULL,
                COL_NUMBER INT NOT NULL,
                IMPORT_FIELD_NAME NVARCHAR (255) NOT NULL,

    CONSTRAINT TIMPORTMAPPINGS_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_IMAP_JOB_UID ON TIMPORTMAPPINGS (IMPORT_JOB_UID);




/* ---------------------------------------------------------------------- */
/* TORDERADDRESS                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TORDERADDRESS')
BEGIN
     DECLARE @reftable_42 nvarchar(60), @constraintname_42 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TORDERADDRESS'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_42, @constraintname_42
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_42+' drop constraint '+@constraintname_42)
       FETCH NEXT from refcursor into @reftable_42, @constraintname_42
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TORDERADDRESS
END
;

CREATE TABLE TORDERADDRESS
(
                UIDPK BIGINT NOT NULL,
                LAST_NAME NVARCHAR (100) NULL,
                FIRST_NAME NVARCHAR (100) NULL,
                PHONE_NUMBER NVARCHAR (50) NULL,
                FAX_NUMBER NVARCHAR (50) NULL,
                STREET_1 NVARCHAR (200) NULL,
                STREET_2 NVARCHAR (200) NULL,
                CITY NVARCHAR (200) NULL,
                SUB_COUNTRY NVARCHAR (200) NULL,
                ZIP_POSTAL_CODE NVARCHAR (50) NULL,
                COUNTRY NVARCHAR (200) NULL,
                COMMERCIAL INT default 0 NULL,
                GUID NVARCHAR (64) NOT NULL,

    CONSTRAINT TORDERADDRESS_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TORDERADDRESS_UNIQUE UNIQUE (GUID));

CREATE  INDEX I_ORDERADDRESS_FIRST_NAME ON TORDERADDRESS (FIRST_NAME );
CREATE  INDEX I_ORDERADDRESS_LAST_NAME ON TORDERADDRESS (LAST_NAME );
CREATE  INDEX I_ORDERADDRESS_ZIP_POSTAL_CODE ON TORDERADDRESS (ZIP_POSTAL_CODE );
CREATE  INDEX I_ORDERADDRESS_PHONE_NUMBER ON TORDERADDRESS (PHONE_NUMBER );




/* ---------------------------------------------------------------------- */
/* TORDER                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TORDER_FK_1')
    ALTER TABLE TORDER DROP CONSTRAINT TORDER_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TORDER_FK_2')
    ALTER TABLE TORDER DROP CONSTRAINT TORDER_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TORDER_FK_3')
    ALTER TABLE TORDER DROP CONSTRAINT TORDER_FK_3;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TORDER')
BEGIN
     DECLARE @reftable_43 nvarchar(60), @constraintname_43 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TORDER'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_43, @constraintname_43
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_43+' drop constraint '+@constraintname_43)
       FETCH NEXT from refcursor into @reftable_43, @constraintname_43
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TORDER
END
;

CREATE TABLE TORDER
(
                UIDPK BIGINT NOT NULL,
                LAST_MODIFIED_DATE DATETIME default CURRENT_TIMESTAMP NOT NULL,
                CREATED_DATE DATETIME NOT NULL,
                IP_ADDRESS NVARCHAR (255) NULL,
                ORDER_BILLING_ADDRESS_UID BIGINT NULL,
                TOTAL DECIMAL (19,2) NULL,
                STATUS NVARCHAR (20) NULL,
                ORDER_NUMBER NVARCHAR (64) NOT NULL,
                EXTERNAL_ORDER_NUMBER NVARCHAR (64) NULL,
                CUSTOMER_UID BIGINT NULL,
                LOCALE NVARCHAR (5) NOT NULL,
                CURRENCY NVARCHAR (3) NULL,
                STORE_UID BIGINT NULL,
                CREATED_BY BIGINT NULL,
                ORDER_SOURCE NVARCHAR (100) NULL,
                EXCHANGE_ORDER INT default 0 NULL,

    CONSTRAINT TORDER_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TORDER_UNIQUE UNIQUE (ORDER_NUMBER));

CREATE  INDEX I_O_STORE_UID ON TORDER (STORE_UID);
CREATE  INDEX I_O_OBA_UID ON TORDER (ORDER_BILLING_ADDRESS_UID);
CREATE  INDEX I_O_CUSTOMER_UID ON TORDER (CUSTOMER_UID);
CREATE  INDEX I_O_MODIFY_DATE ON TORDER (LAST_MODIFIED_DATE);
CREATE  INDEX I_O_CREATED_DATE ON TORDER (CREATED_DATE);
CREATE  INDEX I_O_STATUS ON TORDER (STATUS);
CREATE  INDEX I_O_TOTAL ON TORDER (CURRENCY, TOTAL);




/* ---------------------------------------------------------------------- */
/* TORDERNUMBERGENERATOR                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TORDERNUMBERGENERATOR')
BEGIN
     DECLARE @reftable_44 nvarchar(60), @constraintname_44 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TORDERNUMBERGENERATOR'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_44, @constraintname_44
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_44+' drop constraint '+@constraintname_44)
       FETCH NEXT from refcursor into @reftable_44, @constraintname_44
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TORDERNUMBERGENERATOR
END
;

CREATE TABLE TORDERNUMBERGENERATOR
(
                UIDPK BIGINT default 1 NOT NULL,
                NEXT_ORDER_NUMBER NVARCHAR (100) NOT NULL,

    CONSTRAINT TORDERNUMBERGENERATOR_PK PRIMARY KEY(UIDPK));





/* ---------------------------------------------------------------------- */
/* TORDERLOCK                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TORDERLOCK_FK_1')
    ALTER TABLE TORDERLOCK DROP CONSTRAINT TORDERLOCK_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TORDERLOCK_FK_2')
    ALTER TABLE TORDERLOCK DROP CONSTRAINT TORDERLOCK_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TORDERLOCK')
BEGIN
     DECLARE @reftable_45 nvarchar(60), @constraintname_45 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TORDERLOCK'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_45, @constraintname_45
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_45+' drop constraint '+@constraintname_45)
       FETCH NEXT from refcursor into @reftable_45, @constraintname_45
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TORDERLOCK
END
;

CREATE TABLE TORDERLOCK
(
                UIDPK BIGINT NOT NULL,
                ORDER_UID BIGINT NOT NULL,
                USER_UID BIGINT NOT NULL,
                CREATED_DATE BIGINT NOT NULL,

    CONSTRAINT TORDERLOCK_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TORDERLOCK_UNIQUE UNIQUE (ORDER_UID));

CREATE  INDEX I_ORDERLOCK_USER_UID ON TORDERLOCK (USER_UID);




/* ---------------------------------------------------------------------- */
/* TORDERAUDIT                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TORDERAUDIT_FK_1')
    ALTER TABLE TORDERAUDIT DROP CONSTRAINT TORDERAUDIT_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TORDERAUDIT_FK_2')
    ALTER TABLE TORDERAUDIT DROP CONSTRAINT TORDERAUDIT_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TORDERAUDIT')
BEGIN
     DECLARE @reftable_46 nvarchar(60), @constraintname_46 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TORDERAUDIT'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_46, @constraintname_46
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_46+' drop constraint '+@constraintname_46)
       FETCH NEXT from refcursor into @reftable_46, @constraintname_46
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TORDERAUDIT
END
;

CREATE TABLE TORDERAUDIT
(
                UIDPK BIGINT NOT NULL,
                CREATED_DATE DATETIME NOT NULL,
                CREATED_BY BIGINT NULL,
                DETAIL NTEXT NULL,
                ORDER_UID BIGINT NULL,
                ORIGINATOR_TYPE NVARCHAR (30) NOT NULL,
                TITLE NVARCHAR (255) NOT NULL,
                LAST_MODIFIED_DATE DATETIME default CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT TORDERAUDIT_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_ON_ORDER_UID ON TORDERAUDIT (ORDER_UID);
CREATE  INDEX I_ON_USER_UID ON TORDERAUDIT (CREATED_BY);




/* ---------------------------------------------------------------------- */
/* TGIFTCERTIFICATE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TGIFTCERTIFICATE_FK_1')
    ALTER TABLE TGIFTCERTIFICATE DROP CONSTRAINT TGIFTCERTIFICATE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TGIFTCERTIFICATE_FK_2')
    ALTER TABLE TGIFTCERTIFICATE DROP CONSTRAINT TGIFTCERTIFICATE_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TGIFTCERTIFICATE')
BEGIN
     DECLARE @reftable_47 nvarchar(60), @constraintname_47 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TGIFTCERTIFICATE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_47, @constraintname_47
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_47+' drop constraint '+@constraintname_47)
       FETCH NEXT from refcursor into @reftable_47, @constraintname_47
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TGIFTCERTIFICATE
END
;

CREATE TABLE TGIFTCERTIFICATE
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                GIFT_CERTIFICATE_CODE NVARCHAR (64) NULL,
                CREATED_DATE DATETIME NOT NULL,
                LAST_MODIFIED_DATE DATETIME default CURRENT_TIMESTAMP NOT NULL,
                RECIPIENT_NAME NVARCHAR (255) NULL,
                SENDER_NAME NVARCHAR (255) NULL,
                MESSAGE NVARCHAR (255) NULL,
                THEME NVARCHAR (255) NULL,
                PURCHASE_AMOUNT DECIMAL (19,2) NULL,
                CURRENCY NVARCHAR (255) NULL,
                RECEPIENT_EMAIL NVARCHAR (255) NULL,
                CUSTOMER_UID BIGINT NULL,
                STORE_UID BIGINT NOT NULL,
                ORDER_GUID NVARCHAR (64) NULL,

    CONSTRAINT TGIFTCERTIFICATE_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_GCERT_CUSTOMER_UID ON TGIFTCERTIFICATE (CUSTOMER_UID);
CREATE  INDEX I_GCERT_STORE_UID ON TGIFTCERTIFICATE (STORE_UID);
CREATE  INDEX I_P_GCERT_CODE ON TGIFTCERTIFICATE (GIFT_CERTIFICATE_CODE);
CREATE  INDEX I_ORDER_GUID ON TGIFTCERTIFICATE (ORDER_GUID);




/* ---------------------------------------------------------------------- */
/* TSHIPPINGREGION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSHIPPINGREGION')
BEGIN
     DECLARE @reftable_48 nvarchar(60), @constraintname_48 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSHIPPINGREGION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_48, @constraintname_48
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_48+' drop constraint '+@constraintname_48)
       FETCH NEXT from refcursor into @reftable_48, @constraintname_48
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSHIPPINGREGION
END
;

CREATE TABLE TSHIPPINGREGION
(
                UIDPK BIGINT NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                REGION_STR NVARCHAR (2000) NULL,
                GUID NVARCHAR (64) NOT NULL,

    CONSTRAINT TSHIPPINGREGION_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TSHIPPINGREGION_UNIQUE UNIQUE (NAME),
    CONSTRAINT TSHIPPINGREGION_GUID_UNIQUE UNIQUE (GUID));





/* ---------------------------------------------------------------------- */
/* TSHIPPINGCOSTCALCULATIONMETHOD                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSHIPPINGCOSTCALCULATIONMETHOD')
BEGIN
     DECLARE @reftable_49 nvarchar(60), @constraintname_49 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSHIPPINGCOSTCALCULATIONMETHOD'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_49, @constraintname_49
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_49+' drop constraint '+@constraintname_49)
       FETCH NEXT from refcursor into @reftable_49, @constraintname_49
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSHIPPINGCOSTCALCULATIONMETHOD
END
;

CREATE TABLE TSHIPPINGCOSTCALCULATIONMETHOD
(
                UIDPK BIGINT NOT NULL,
                TYPE NVARCHAR (255) NOT NULL,

    CONSTRAINT TSHIPPINGCOSTCALCULATIONMETHOD_PK PRIMARY KEY(UIDPK));





/* ---------------------------------------------------------------------- */
/* TSHIPPINGCOSTCALCULATIONPARAM                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSHIPPINGCOSTCALCULATIONP_FK_1')
    ALTER TABLE TSHIPPINGCOSTCALCULATIONPARAM DROP CONSTRAINT TSHIPPINGCOSTCALCULATIONP_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSHIPPINGCOSTCALCULATIONPARAM')
BEGIN
     DECLARE @reftable_50 nvarchar(60), @constraintname_50 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSHIPPINGCOSTCALCULATIONPARAM'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_50, @constraintname_50
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_50+' drop constraint '+@constraintname_50)
       FETCH NEXT from refcursor into @reftable_50, @constraintname_50
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSHIPPINGCOSTCALCULATIONPARAM
END
;

CREATE TABLE TSHIPPINGCOSTCALCULATIONPARAM
(
                UIDPK BIGINT NOT NULL,
                PARAM_KEY NVARCHAR (255) NOT NULL,
                VALUE NVARCHAR (255) NOT NULL,
                DISPLAY_TEXT NVARCHAR (255) NOT NULL,
                SCCM_UID BIGINT NULL,
                CURRENCY CHAR (3) NULL,

    CONSTRAINT TSHIPPINGCOSTCALCULATIONPARAM_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_SCCP_SCCM_UID ON TSHIPPINGCOSTCALCULATIONPARAM (SCCM_UID);




/* ---------------------------------------------------------------------- */
/* TSHIPPINGSERVICELEVEL                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSHIPPINGSERVICELEVEL_FK_1')
    ALTER TABLE TSHIPPINGSERVICELEVEL DROP CONSTRAINT TSHIPPINGSERVICELEVEL_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSHIPPINGSERVICELEVEL_FK_2')
    ALTER TABLE TSHIPPINGSERVICELEVEL DROP CONSTRAINT TSHIPPINGSERVICELEVEL_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSHIPPINGSERVICELEVEL_FK_3')
    ALTER TABLE TSHIPPINGSERVICELEVEL DROP CONSTRAINT TSHIPPINGSERVICELEVEL_FK_3;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSHIPPINGSERVICELEVEL')
BEGIN
     DECLARE @reftable_51 nvarchar(60), @constraintname_51 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSHIPPINGSERVICELEVEL'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_51, @constraintname_51
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_51+' drop constraint '+@constraintname_51)
       FETCH NEXT from refcursor into @reftable_51, @constraintname_51
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSHIPPINGSERVICELEVEL
END
;

CREATE TABLE TSHIPPINGSERVICELEVEL
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                SHIPPING_REGION_UID BIGINT NOT NULL,
                STORE_UID BIGINT NOT NULL,
                SCCM_UID BIGINT NOT NULL,
                CARRIER NVARCHAR (255) NULL,
                CODE NVARCHAR (64) NOT NULL,
                DEFAULT_COST DECIMAL (19,2) NULL,
                ENABLED INT NOT NULL,
                LAST_MODIFIED_DATE DATETIME default CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT TSHIPPINGSERVICELEVEL_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TSHIPPINGSERVICELEVEL_UNIQUE UNIQUE (GUID),
    CONSTRAINT TSHIPPINGSRVLEVEL_CODE_UNIQUE UNIQUE (CODE));

CREATE  INDEX I_SSL_STORE_UID ON TSHIPPINGSERVICELEVEL (STORE_UID);
CREATE  INDEX I_SSL_SR_UID ON TSHIPPINGSERVICELEVEL (SHIPPING_REGION_UID);
CREATE  INDEX I_SSL_SCCM_UID ON TSHIPPINGSERVICELEVEL (SCCM_UID);
CREATE  INDEX I_SSL_MODIFY_DATE ON TSHIPPINGSERVICELEVEL (LAST_MODIFIED_DATE);




/* ---------------------------------------------------------------------- */
/* TPICKLIST                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPICKLIST_FK_1')
    ALTER TABLE TPICKLIST DROP CONSTRAINT TPICKLIST_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPICKLIST_FK_2')
    ALTER TABLE TPICKLIST DROP CONSTRAINT TPICKLIST_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TPICKLIST')
BEGIN
     DECLARE @reftable_52 nvarchar(60), @constraintname_52 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TPICKLIST'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_52, @constraintname_52
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_52+' drop constraint '+@constraintname_52)
       FETCH NEXT from refcursor into @reftable_52, @constraintname_52
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TPICKLIST
END
;

CREATE TABLE TPICKLIST
(
                UIDPK BIGINT NOT NULL,
                CREATED_DATE DATETIME NOT NULL,
                WAREHOUSE_UID BIGINT NULL,
                CREATED_BY BIGINT NULL,
                ACTIVE INT default 0 NULL,

    CONSTRAINT TPICKLIST_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_PL_WAREHOUSE_UID ON TPICKLIST (WAREHOUSE_UID);
CREATE  INDEX I_PL_CREATED_BY ON TPICKLIST (CREATED_BY);




/* ---------------------------------------------------------------------- */
/* TORDERSHIPMENT                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TORDERSHIPMENT_FK_1')
    ALTER TABLE TORDERSHIPMENT DROP CONSTRAINT TORDERSHIPMENT_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TORDERSHIPMENT_FK_2')
    ALTER TABLE TORDERSHIPMENT DROP CONSTRAINT TORDERSHIPMENT_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TORDERSHIPMENT_FK_3')
    ALTER TABLE TORDERSHIPMENT DROP CONSTRAINT TORDERSHIPMENT_FK_3;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TORDERSHIPMENT_FK_4')
    ALTER TABLE TORDERSHIPMENT DROP CONSTRAINT TORDERSHIPMENT_FK_4;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TORDERSHIPMENT')
BEGIN
     DECLARE @reftable_53 nvarchar(60), @constraintname_53 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TORDERSHIPMENT'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_53, @constraintname_53
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_53+' drop constraint '+@constraintname_53)
       FETCH NEXT from refcursor into @reftable_53, @constraintname_53
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TORDERSHIPMENT
END
;

CREATE TABLE TORDERSHIPMENT
(
                UIDPK BIGINT NOT NULL,
                TYPE NVARCHAR (255) NOT NULL,
                STATUS NVARCHAR (20) NULL,
                LAST_MODIFIED_DATE DATETIME default CURRENT_TIMESTAMP NOT NULL,
                CREATED_DATE DATETIME NOT NULL,
                SHIPMENT_DATE DATETIME NULL,
                CARRIER NVARCHAR (255) NULL,
                SERVICE_LEVEL NVARCHAR (255) NULL,
                TRACKING_CODE NVARCHAR (255) NULL,
                ITEM_SUBTOTAL DECIMAL (19,2) NULL,
                BEFORE_TAX_SHIPPING_COST DECIMAL (19,2) NULL,
                ITEM_TAX DECIMAL (19,2) NULL,
                SUBTOTAL_DISCOUNT DECIMAL (19,2) NULL,
                SHIPPING_COST DECIMAL (19,2) NULL,
                SHIPPING_TAX DECIMAL (19,2) NULL,
                SHIPPING_SUBTOTAL DECIMAL (19,2) NULL,
                INCLUSIVE_TAX INT NULL,
                ORDER_ADDRESS_UID BIGINT NULL,
                ORDER_UID BIGINT NULL,
                SERVICE_LEVEL_UID BIGINT NULL,
                PICKLIST_UID BIGINT NULL,
                SHIPMENT_NUMBER NVARCHAR (64) NOT NULL,

    CONSTRAINT TORDERSHIPMENT_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TORDERSHIPMENT_UNIQUE UNIQUE (SHIPMENT_NUMBER));

CREATE  INDEX I_OSHIP_SHIPLEVSERV ON TORDERSHIPMENT (SERVICE_LEVEL_UID);
CREATE  INDEX I_OSHIP_OA_UID ON TORDERSHIPMENT (ORDER_ADDRESS_UID);
CREATE  INDEX I_OSHIP_ORDER_UID ON TORDERSHIPMENT (ORDER_UID);
CREATE  INDEX I_OSHIP_PICK_LIST ON TORDERSHIPMENT (PICKLIST_UID);
CREATE  INDEX I_OSHIP_MDFY_DATE ON TORDERSHIPMENT (LAST_MODIFIED_DATE);
CREATE  INDEX I_OSHIP_STATUS ON TORDERSHIPMENT (STATUS);




/* ---------------------------------------------------------------------- */
/* TORDERPAYMENT                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TORDERPAYMENT_FK_1')
    ALTER TABLE TORDERPAYMENT DROP CONSTRAINT TORDERPAYMENT_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TORDERPAYMENT_FK_2')
    ALTER TABLE TORDERPAYMENT DROP CONSTRAINT TORDERPAYMENT_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TORDERPAYMENT_FK_3')
    ALTER TABLE TORDERPAYMENT DROP CONSTRAINT TORDERPAYMENT_FK_3;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TORDERPAYMENT')
BEGIN
     DECLARE @reftable_54 nvarchar(60), @constraintname_54 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TORDERPAYMENT'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_54, @constraintname_54
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_54+' drop constraint '+@constraintname_54)
       FETCH NEXT from refcursor into @reftable_54, @constraintname_54
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TORDERPAYMENT
END
;

CREATE TABLE TORDERPAYMENT
(
                UIDPK BIGINT NOT NULL,
                CREATED_DATE DATETIME NOT NULL,
                CARD_TYPE NVARCHAR (50) NULL,
                CARD_HOLDER_NAME NVARCHAR (100) NULL,
                CARD_NUMBER NVARCHAR (255) NULL,
                MASKED_CARD_NUMBER NVARCHAR (255) NULL,
                EXPIRY_YEAR NVARCHAR (4) NULL,
                EXPIRY_MONTH NVARCHAR (2) NULL,
                START_DATE DATETIME NULL,
                ISSUE_NUMBER NVARCHAR (100) NULL,
                PAYMENT_GATEWAY NVARCHAR (100) NULL,
                AMOUNT DECIMAL (19,2) NULL,
                REFERENCE_ID NVARCHAR (50) NULL,
                REQUEST_TOKEN NVARCHAR (255) NULL,
                AUTHORIZATION_CODE NVARCHAR (50) NULL,
                TRANSACTION_TYPE NVARCHAR (25) NULL,
                CURRENCY NVARCHAR (10) NULL,
                EMAIL NVARCHAR (100) NULL,
                STATUS NVARCHAR (20) NULL,
                ORDER_UID BIGINT NULL,
                GIFTCERTIFICATE_UID BIGINT NULL,
                ORDERSHIPMENT_UID BIGINT NULL,
                PAYMENT_FOR_SUBSCRIPTIONS INT NULL,
                LAST_MODIFIED_DATE DATETIME default CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT TORDERPAYMENT_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_OP_GCERT_BY ON TORDERPAYMENT (GIFTCERTIFICATE_UID);
CREATE  INDEX I_OP_ORDERSHIPMENT_BY ON TORDERPAYMENT (ORDERSHIPMENT_UID);
CREATE  INDEX I_OP_ORDER_UID ON TORDERPAYMENT (ORDER_UID);




/* ---------------------------------------------------------------------- */
/* TGIFTCERTIFICATETRANSACTION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TGIFTCERTIFICATETRANSACTI_FK_1')
    ALTER TABLE TGIFTCERTIFICATETRANSACTION DROP CONSTRAINT TGIFTCERTIFICATETRANSACTI_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TGIFTCERTIFICATETRANSACTION')
BEGIN
     DECLARE @reftable_55 nvarchar(60), @constraintname_55 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TGIFTCERTIFICATETRANSACTION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_55, @constraintname_55
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_55+' drop constraint '+@constraintname_55)
       FETCH NEXT from refcursor into @reftable_55, @constraintname_55
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TGIFTCERTIFICATETRANSACTION
END
;

CREATE TABLE TGIFTCERTIFICATETRANSACTION
(
                UIDPK BIGINT NOT NULL,
                CREATED_DATE DATETIME NOT NULL,
                AMOUNT DECIMAL (19,2) NULL,
                AUTHORIZATION_CODE NVARCHAR (50) NULL,
                TRANSACTION_TYPE NVARCHAR (25) NULL,
                GIFTCERTIFICATE_UID BIGINT NULL,

    CONSTRAINT TGIFTCERTIFICATETRANSACTION_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_GCT_GCERT_BY ON TGIFTCERTIFICATETRANSACTION (GIFTCERTIFICATE_UID);




/* ---------------------------------------------------------------------- */
/* TRMAGENERATOR                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TRMAGENERATOR')
BEGIN
     DECLARE @reftable_56 nvarchar(60), @constraintname_56 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TRMAGENERATOR'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_56, @constraintname_56
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_56+' drop constraint '+@constraintname_56)
       FETCH NEXT from refcursor into @reftable_56, @constraintname_56
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TRMAGENERATOR
END
;

CREATE TABLE TRMAGENERATOR
(
                UIDPK BIGINT default 1 NOT NULL,
                NEXT_RMA NVARCHAR (100) NOT NULL,

    CONSTRAINT TRMAGENERATOR_PK PRIMARY KEY(UIDPK));





/* ---------------------------------------------------------------------- */
/* TORDERRETURN                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TORDERRETURN_FK_1')
    ALTER TABLE TORDERRETURN DROP CONSTRAINT TORDERRETURN_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TORDERRETURN_FK_2')
    ALTER TABLE TORDERRETURN DROP CONSTRAINT TORDERRETURN_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TORDERRETURN_FK_3')
    ALTER TABLE TORDERRETURN DROP CONSTRAINT TORDERRETURN_FK_3;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TORDERRETURN_FK_4')
    ALTER TABLE TORDERRETURN DROP CONSTRAINT TORDERRETURN_FK_4;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TORDERRETURN_FK_5')
    ALTER TABLE TORDERRETURN DROP CONSTRAINT TORDERRETURN_FK_5;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TORDERRETURN')
BEGIN
     DECLARE @reftable_57 nvarchar(60), @constraintname_57 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TORDERRETURN'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_57, @constraintname_57
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_57+' drop constraint '+@constraintname_57)
       FETCH NEXT from refcursor into @reftable_57, @constraintname_57
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TORDERRETURN
END
;

CREATE TABLE TORDERRETURN
(
                UIDPK BIGINT NOT NULL,
                CREATED_DATE DATETIME NOT NULL,
                RMA_CODE NVARCHAR (255) NULL,
                RETURN_COMMENT NVARCHAR (2000) NULL,
                ORDER_UID BIGINT NULL,
                CREATED_BY BIGINT NULL,
                STATUS NVARCHAR (50) NULL,
                RETURN_TYPE NVARCHAR (255) NULL,
                PHYSICAL_RETURN INT default 0 NULL,
                EXCHANGE_ORDER_UID BIGINT NULL,
                ORDER_PAYMENT_UID BIGINT NULL,
                LESS_RESTOCK_AMOUNT DECIMAL (19,2) NULL,
                SHIPPING_COST DECIMAL (19,2) default 0 NOT NULL,
                LAST_MODIFIED_DATE DATETIME default CURRENT_TIMESTAMP NOT NULL,
                RECEIVED_BY BIGINT NULL,
                VERSION INT NULL,
                ORDER_RETURN_ADDRESS_UID BIGINT NULL,

    CONSTRAINT TORDERRETURN_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_OR_ORDER_UID ON TORDERRETURN (ORDER_UID);
CREATE  INDEX I_OR_CREATED_BY ON TORDERRETURN (CREATED_BY);
CREATE  INDEX I_OR_RECEIVED_BY ON TORDERRETURN (RECEIVED_BY);
CREATE  INDEX I_OR_EXCHANGE_ORDER_UID ON TORDERRETURN (EXCHANGE_ORDER_UID);
CREATE  INDEX I_OR_RETURN_ADDRESS ON TORDERRETURN (ORDER_RETURN_ADDRESS_UID);
CREATE  INDEX I_OR_RMA_CODE ON TORDERRETURN (RMA_CODE);




/* ---------------------------------------------------------------------- */
/* TSHIPMENTTAX                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSHIPMENTTAX_FK_1')
    ALTER TABLE TSHIPMENTTAX DROP CONSTRAINT TSHIPMENTTAX_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSHIPMENTTAX_FK_2')
    ALTER TABLE TSHIPMENTTAX DROP CONSTRAINT TSHIPMENTTAX_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSHIPMENTTAX')
BEGIN
     DECLARE @reftable_58 nvarchar(60), @constraintname_58 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSHIPMENTTAX'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_58, @constraintname_58
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_58+' drop constraint '+@constraintname_58)
       FETCH NEXT from refcursor into @reftable_58, @constraintname_58
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSHIPMENTTAX
END
;

CREATE TABLE TSHIPMENTTAX
(
                UIDPK BIGINT NOT NULL,
                TAX_CATEGORY_NAME NVARCHAR (255) NOT NULL,
                TAX_CATEGORY_DISPLAY_NAME NVARCHAR (255) NOT NULL,
                VALUE DECIMAL (19,2) NULL,
                ORDER_SHIPMENT_UID BIGINT NULL,
                ORDER_RETURN_UID BIGINT NULL,

    CONSTRAINT TSHIPMENTTAX_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_ST_ORDER_SHIPMENT_UID ON TSHIPMENTTAX (ORDER_SHIPMENT_UID);
CREATE  INDEX I_ST_OR_UID ON TSHIPMENTTAX (ORDER_RETURN_UID);




/* ---------------------------------------------------------------------- */
/* TPRODUCTTYPE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCTTYPE_FK_1')
    ALTER TABLE TPRODUCTTYPE DROP CONSTRAINT TPRODUCTTYPE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCTTYPE_FK_2')
    ALTER TABLE TPRODUCTTYPE DROP CONSTRAINT TPRODUCTTYPE_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TPRODUCTTYPE')
BEGIN
     DECLARE @reftable_59 nvarchar(60), @constraintname_59 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TPRODUCTTYPE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_59, @constraintname_59
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_59+' drop constraint '+@constraintname_59)
       FETCH NEXT from refcursor into @reftable_59, @constraintname_59
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TPRODUCTTYPE
END
;

CREATE TABLE TPRODUCTTYPE
(
                UIDPK BIGINT NOT NULL,
                WITH_MULTIPLE_SKUS INT default 0 NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                TEMPLATE NVARCHAR (255) NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                TAX_CODE_UID BIGINT NOT NULL,
                CATALOG_UID BIGINT NOT NULL,
                EXCLUDE_FROM_DISCOUNT INT default 0 NOT NULL,

    CONSTRAINT TPRODUCTTYPE_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TPRODUCTTYPE_UNIQUE UNIQUE (NAME),
    CONSTRAINT TPRODUCTTYPE_GUID_UNIQUE UNIQUE (GUID));

CREATE  INDEX I_PRODTYPE_CATALOG_UID ON TPRODUCTTYPE (CATALOG_UID);
CREATE  INDEX I_PRODTYPE_TAXCODE_UID ON TPRODUCTTYPE (TAX_CODE_UID);




/* ---------------------------------------------------------------------- */
/* TBRAND                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TBRAND_FK_1')
    ALTER TABLE TBRAND DROP CONSTRAINT TBRAND_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TBRAND')
BEGIN
     DECLARE @reftable_60 nvarchar(60), @constraintname_60 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TBRAND'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_60, @constraintname_60
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_60+' drop constraint '+@constraintname_60)
       FETCH NEXT from refcursor into @reftable_60, @constraintname_60
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TBRAND
END
;

CREATE TABLE TBRAND
(
                UIDPK BIGINT NOT NULL,
                CODE NVARCHAR (255) NOT NULL,
                IMAGE_URL NVARCHAR (255) NULL,
                CATALOG_UID BIGINT NOT NULL,

    CONSTRAINT TBRAND_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TBRAND_UNIQUE UNIQUE (CODE));

CREATE  INDEX I_B_CATALOG_UID ON TBRAND (CATALOG_UID);




/* ---------------------------------------------------------------------- */
/* TPRODUCT                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCT_FK_1')
    ALTER TABLE TPRODUCT DROP CONSTRAINT TPRODUCT_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCT_FK_2')
    ALTER TABLE TPRODUCT DROP CONSTRAINT TPRODUCT_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCT_FK_3')
    ALTER TABLE TPRODUCT DROP CONSTRAINT TPRODUCT_FK_3;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TPRODUCT')
BEGIN
     DECLARE @reftable_61 nvarchar(60), @constraintname_61 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TPRODUCT'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_61, @constraintname_61
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_61+' drop constraint '+@constraintname_61)
       FETCH NEXT from refcursor into @reftable_61, @constraintname_61
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TPRODUCT
END
;

CREATE TABLE TPRODUCT
(
                UIDPK BIGINT NOT NULL,
                LAST_MODIFIED_DATE DATETIME default CURRENT_TIMESTAMP NOT NULL,
                START_DATE DATETIME NOT NULL,
                END_DATE DATETIME NULL,
                IMAGE NVARCHAR (255) NULL,
                PRODUCT_TYPE_UID BIGINT NOT NULL,
                BRAND_UID BIGINT NULL,
                DEFAULT_SKU_UID BIGINT NULL,
                CODE NVARCHAR (64) NOT NULL,
                MIN_QUANTITY INT default 1 NOT NULL,
                EXPECTED_RELEASE_DATE DATETIME NULL,
                HIDDEN INT default 0 NULL,
                SALES_COUNT INT default 0 NULL,
                TAX_CODE_UID BIGINT NULL,
                PRE_OR_BACK_ORDER_LIMIT INT NULL,
                AVAILABILITY_CRITERIA NVARCHAR (30) NULL,
                TYPE NVARCHAR (255) NULL,
                NOT_SOLD_SEPARATELY INT default 0 NULL,
                CALCULATED INT default 0 NULL,

    CONSTRAINT TPRODUCT_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TPRODUCT_UNIQUE UNIQUE (CODE));

CREATE  INDEX I_P_TYPE_UID ON TPRODUCT (PRODUCT_TYPE_UID);
CREATE  INDEX I_P_BRAND_UID ON TPRODUCT (BRAND_UID);
CREATE  INDEX I_P_TAXCODE_UID ON TPRODUCT (TAX_CODE_UID);
CREATE  INDEX I_P_MODIFY_DATE ON TPRODUCT (LAST_MODIFIED_DATE);
CREATE  INDEX I_P_SE_DATE ON TPRODUCT (START_DATE, END_DATE);




/* ---------------------------------------------------------------------- */
/* TPRODUCTATTRIBUTEVALUE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCTATTRIBUTEVALUE_FK_1')
    ALTER TABLE TPRODUCTATTRIBUTEVALUE DROP CONSTRAINT TPRODUCTATTRIBUTEVALUE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCTATTRIBUTEVALUE_FK_2')
    ALTER TABLE TPRODUCTATTRIBUTEVALUE DROP CONSTRAINT TPRODUCTATTRIBUTEVALUE_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TPRODUCTATTRIBUTEVALUE')
BEGIN
     DECLARE @reftable_62 nvarchar(60), @constraintname_62 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TPRODUCTATTRIBUTEVALUE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_62, @constraintname_62
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_62+' drop constraint '+@constraintname_62)
       FETCH NEXT from refcursor into @reftable_62, @constraintname_62
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TPRODUCTATTRIBUTEVALUE
END
;

CREATE TABLE TPRODUCTATTRIBUTEVALUE
(
                UIDPK BIGINT NOT NULL,
                ATTRIBUTE_UID BIGINT NOT NULL,
                ATTRIBUTE_TYPE INT NOT NULL,
                LOCALIZED_ATTRIBUTE_KEY NVARCHAR (255) NOT NULL,
                SHORT_TEXT_VALUE NVARCHAR (255) NULL,
                LONG_TEXT_VALUE NTEXT NULL,
                INTEGER_VALUE INT NULL,
                DECIMAL_VALUE DECIMAL (19,2) NULL,
                BOOLEAN_VALUE INT default 0 NULL,
                DATE_VALUE DATETIME NULL,
                PRODUCT_UID BIGINT NULL,

    CONSTRAINT TPRODUCTATTRIBUTEVALUE_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_PAV_ATTR_UID ON TPRODUCTATTRIBUTEVALUE (ATTRIBUTE_UID);
CREATE  INDEX I_PAV_PROD_UID ON TPRODUCTATTRIBUTEVALUE (PRODUCT_UID);




/* ---------------------------------------------------------------------- */
/* TPRODUCTCATEGORY                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCTCATEGORY_FK_1')
    ALTER TABLE TPRODUCTCATEGORY DROP CONSTRAINT TPRODUCTCATEGORY_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCTCATEGORY_FK_2')
    ALTER TABLE TPRODUCTCATEGORY DROP CONSTRAINT TPRODUCTCATEGORY_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TPRODUCTCATEGORY')
BEGIN
     DECLARE @reftable_63 nvarchar(60), @constraintname_63 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TPRODUCTCATEGORY'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_63, @constraintname_63
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_63+' drop constraint '+@constraintname_63)
       FETCH NEXT from refcursor into @reftable_63, @constraintname_63
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TPRODUCTCATEGORY
END
;

CREATE TABLE TPRODUCTCATEGORY
(
                UIDPK BIGINT NOT NULL,
                PRODUCT_UID BIGINT NOT NULL,
                CATEGORY_UID BIGINT NOT NULL,
                FEAT_PRODUCT_ORDER INT default 0 NULL,
                DEFAULT_CATEGORY INT NULL,

    CONSTRAINT TPRODUCTCATEGORY_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TPRODUCTCATEGORY_UNIQUE UNIQUE (PRODUCT_UID, CATEGORY_UID));

CREATE  INDEX I_PC_PUID ON TPRODUCTCATEGORY (PRODUCT_UID);
CREATE  INDEX I_PC_CUID ON TPRODUCTCATEGORY (CATEGORY_UID);




/* ---------------------------------------------------------------------- */
/* TPRODUCTLDF                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCTLDF_FK_1')
    ALTER TABLE TPRODUCTLDF DROP CONSTRAINT TPRODUCTLDF_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TPRODUCTLDF')
BEGIN
     DECLARE @reftable_64 nvarchar(60), @constraintname_64 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TPRODUCTLDF'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_64, @constraintname_64
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_64+' drop constraint '+@constraintname_64)
       FETCH NEXT from refcursor into @reftable_64, @constraintname_64
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TPRODUCTLDF
END
;

CREATE TABLE TPRODUCTLDF
(
                UIDPK BIGINT NOT NULL,
                PRODUCT_UID BIGINT NOT NULL,
                URL NVARCHAR (255) NULL,
                KEY_WORDS NVARCHAR (255) NULL,
                DESCRIPTION NVARCHAR (255) NULL,
                TITLE NVARCHAR (255) NULL,
                DISPLAY_NAME NVARCHAR (255) NULL,
                LOCALE NVARCHAR (20) NOT NULL,

    CONSTRAINT TPRODUCTLDF_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_PLDF_PUID ON TPRODUCTLDF (PRODUCT_UID);
CREATE  INDEX I_PLDF_LOCALE_NAME ON TPRODUCTLDF (LOCALE, DISPLAY_NAME);




/* ---------------------------------------------------------------------- */
/* TPRODUCTSKU                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCTSKU_FK_1')
    ALTER TABLE TPRODUCTSKU DROP CONSTRAINT TPRODUCTSKU_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCTSKU_FK_2')
    ALTER TABLE TPRODUCTSKU DROP CONSTRAINT TPRODUCTSKU_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TPRODUCTSKU')
BEGIN
     DECLARE @reftable_65 nvarchar(60), @constraintname_65 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TPRODUCTSKU'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_65, @constraintname_65
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_65+' drop constraint '+@constraintname_65)
       FETCH NEXT from refcursor into @reftable_65, @constraintname_65
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TPRODUCTSKU
END
;

CREATE TABLE TPRODUCTSKU
(
                UIDPK BIGINT NOT NULL,
                START_DATE DATETIME NOT NULL,
                END_DATE DATETIME NULL,
                SKUCODE NVARCHAR (255) NOT NULL,
                GUID NVARCHAR (255) NOT NULL,
                IMAGE NVARCHAR (255) NULL,
                PRODUCT_UID BIGINT NOT NULL,
                SHIPPABLE INT default 1 NULL,
                WEIGHT DECIMAL (19,2) default 0 NULL,
                HEIGHT DECIMAL (19,2) default 0 NULL,
                WIDTH DECIMAL (19,2) default 0 NULL,
                LENGTH DECIMAL (19,2) default 0 NULL,
                PRE_OR_BACK_ORDERED_QUANTITY INT NULL,
                DIGITAL INT default 0 NULL,
                DIGITAL_ASSET_UID BIGINT NULL,
                LAST_MODIFIED_DATE DATETIME default CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT TPRODUCTSKU_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TPRODUCTSKU_UNIQUE UNIQUE (SKUCODE),
    CONSTRAINT TPRODUCTSKU_GUID UNIQUE (GUID));

CREATE  INDEX I_PS_PRODUCT_UID ON TPRODUCTSKU (PRODUCT_UID);
CREATE  INDEX I_PS_DIGASSET_UID ON TPRODUCTSKU (DIGITAL_ASSET_UID);
CREATE  INDEX I_PS_SE_DATE ON TPRODUCTSKU (START_DATE, END_DATE);




/* ---------------------------------------------------------------------- */
/* TINVENTORY                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TINVENTORY')
BEGIN
     DECLARE @reftable_66 nvarchar(60), @constraintname_66 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TINVENTORY'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_66, @constraintname_66
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_66+' drop constraint '+@constraintname_66)
       FETCH NEXT from refcursor into @reftable_66, @constraintname_66
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TINVENTORY
END
;

CREATE TABLE TINVENTORY
(
                UIDPK BIGINT NOT NULL,
                QUANTITY_ON_HAND INT NULL,
                RESERVED_QUANTITY INT NULL,
                REORDER_MINIMUM INT default 0 NULL,
                REORDER_QUANTITY INT default 0 NULL,
                RESTOCK_DATE DATETIME NULL,
                ALLOCATED_QUANTITY INT NULL,
                WAREHOUSE_UID BIGINT NOT NULL,
                PRODUCTSKU_SKUCODE NVARCHAR (255) NOT NULL,

    CONSTRAINT TINVENTORY_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TINVENTORY_UNIQUE UNIQUE (WAREHOUSE_UID, PRODUCTSKU_SKUCODE));

CREATE  INDEX I_INVENTORY_WAREHOUSE_UID ON TINVENTORY (WAREHOUSE_UID);
CREATE  INDEX I_INVENTORY_SKUCODE ON TINVENTORY (PRODUCTSKU_SKUCODE);




/* ---------------------------------------------------------------------- */
/* TINVENTORYJOURNAL                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TINVENTORYJOURNAL')
BEGIN
     DECLARE @reftable_67 nvarchar(60), @constraintname_67 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TINVENTORYJOURNAL'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_67, @constraintname_67
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_67+' drop constraint '+@constraintname_67)
       FETCH NEXT from refcursor into @reftable_67, @constraintname_67
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TINVENTORYJOURNAL
END
;

CREATE TABLE TINVENTORYJOURNAL
(
                UIDPK BIGINT NOT NULL,
                ALLOCATED_QUANTITY_DELTA INT NOT NULL,
                QUANTITY_ON_HAND_DELTA INT NOT NULL,
                SKUCODE NVARCHAR (255) NOT NULL,
                WAREHOUSE_UID BIGINT NOT NULL,

    CONSTRAINT TINVENTORYJOURNAL_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_INV_JOURNAL_SKU_WAREHOUSE ON TINVENTORYJOURNAL (SKUCODE, WAREHOUSE_UID);




/* ---------------------------------------------------------------------- */
/* TINVENTORYJOURNALLOCK                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TINVENTORYJOURNALLOCK')
BEGIN
     DECLARE @reftable_68 nvarchar(60), @constraintname_68 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TINVENTORYJOURNALLOCK'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_68, @constraintname_68
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_68+' drop constraint '+@constraintname_68)
       FETCH NEXT from refcursor into @reftable_68, @constraintname_68
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TINVENTORYJOURNALLOCK
END
;

CREATE TABLE TINVENTORYJOURNALLOCK
(
                UIDPK BIGINT NOT NULL,
                SKUCODE NVARCHAR (255) NOT NULL,
                WAREHOUSE_UID BIGINT NOT NULL,
                LOCKCOUNT INT NOT NULL,
                VERSION INT NOT NULL,

    CONSTRAINT TINVENTORYJOURNALLOCK_PK PRIMARY KEY(UIDPK),
    CONSTRAINT U_INV_JNL_LOCK_CANDIDATE_KEYS UNIQUE (SKUCODE, WAREHOUSE_UID));





/* ---------------------------------------------------------------------- */
/* TORDERSKU                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TORDERSKU_FK_1')
    ALTER TABLE TORDERSKU DROP CONSTRAINT TORDERSKU_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TOSKU_FK_TOSKU')
    ALTER TABLE TORDERSKU DROP CONSTRAINT TOSKU_FK_TOSKU;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TORDERSKU_FK_3')
    ALTER TABLE TORDERSKU DROP CONSTRAINT TORDERSKU_FK_3;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TORDERSKU')
BEGIN
     DECLARE @reftable_69 nvarchar(60), @constraintname_69 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TORDERSKU'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_69, @constraintname_69
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_69+' drop constraint '+@constraintname_69)
       FETCH NEXT from refcursor into @reftable_69, @constraintname_69
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TORDERSKU
END
;

CREATE TABLE TORDERSKU
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                PARENT_ITEM_UID BIGINT NULL,
                LAST_MODIFIED_DATE DATETIME default CURRENT_TIMESTAMP NOT NULL,
                CREATED_DATE DATETIME NOT NULL,
                SKUCODE NVARCHAR (255) NOT NULL,
                TAXCODE NVARCHAR (255) NOT NULL,
                PRODUCT_SKU_UID BIGINT NULL,
                ORDER_SHIPMENT_UID BIGINT NULL,
                QUANTITY INT NULL,
                DISPLAY_NAME NVARCHAR (255) NOT NULL,
                AMOUNT DECIMAL (19,2) NULL,
                TAX_AMOUNT DECIMAL (19,2) NULL,
                LIST_UNIT_PRICE DECIMAL (19,2) NULL,
                SALE_UNIT_PRICE DECIMAL (19,2) NULL,
                PROMO_UNIT_PRICE DECIMAL (19,2) NULL,
                UNIT_PRICE DECIMAL (19,2) NULL,
                DISCOUNT_AMOUNT DECIMAL (19,2) NULL,
                DISPLAY_SKU_OPTIONS NVARCHAR (255) NULL,
                IMAGE NVARCHAR (255) NULL,
                WEIGHT INT default 0 NULL,
                DIGITAL_ASSET_UID BIGINT NULL,
                ALLOCATED_QUANTITY BIGINT NULL,
                CURRENCY NVARCHAR (3) NULL,
                ORDERING INT default 0 NOT NULL,

    CONSTRAINT TORDERSKU_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TORDERSKU_GUID_UNIQUE UNIQUE (GUID));

CREATE  INDEX I_OS_SHIPMENT_UID ON TORDERSKU (ORDER_SHIPMENT_UID);
CREATE  INDEX I_OS_PARENT_ITEM_UID ON TORDERSKU (PARENT_ITEM_UID);
CREATE  INDEX I_OS_PRODUCT_SKU_UID ON TORDERSKU (PRODUCT_SKU_UID);
CREATE  INDEX I_OSHIP_PRODUCTSKU ON TORDERSKU (SKUCODE);
CREATE  INDEX I_OS_DIGITALASSET_UID ON TORDERSKU (DIGITAL_ASSET_UID);




/* ---------------------------------------------------------------------- */
/* TORDERRETURNSKU                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TORDERRETURNSKU_FK_1')
    ALTER TABLE TORDERRETURNSKU DROP CONSTRAINT TORDERRETURNSKU_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TORDERRETURNSKU_FK_2')
    ALTER TABLE TORDERRETURNSKU DROP CONSTRAINT TORDERRETURNSKU_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TORDERRETURNSKU')
BEGIN
     DECLARE @reftable_70 nvarchar(60), @constraintname_70 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TORDERRETURNSKU'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_70, @constraintname_70
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_70+' drop constraint '+@constraintname_70)
       FETCH NEXT from refcursor into @reftable_70, @constraintname_70
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TORDERRETURNSKU
END
;

CREATE TABLE TORDERRETURNSKU
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                ORDER_SKU_UID BIGINT NULL,
                ORDER_RETURN_UID BIGINT NULL,
                QUANTITY INT NULL,
                RETURN_AMOUNT DECIMAL (19,2) NULL,
                RECEIVED_QUANTITY INT NULL,
                RECEIVED_STATE NVARCHAR (255) NULL,
                RETURN_REASON NVARCHAR (255) NULL,

    CONSTRAINT TORDERRETURNSKU_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_ORS_OR_UID ON TORDERRETURNSKU (ORDER_RETURN_UID);
CREATE  INDEX I_ORS_OS_UID ON TORDERRETURNSKU (ORDER_SKU_UID);




/* ---------------------------------------------------------------------- */
/* TPRODUCTASSOCIATION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCTASSOCIATION_FK_1')
    ALTER TABLE TPRODUCTASSOCIATION DROP CONSTRAINT TPRODUCTASSOCIATION_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCTASSOCIATION_FK_2')
    ALTER TABLE TPRODUCTASSOCIATION DROP CONSTRAINT TPRODUCTASSOCIATION_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCTASSOCIATION_FK_3')
    ALTER TABLE TPRODUCTASSOCIATION DROP CONSTRAINT TPRODUCTASSOCIATION_FK_3;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TPRODUCTASSOCIATION')
BEGIN
     DECLARE @reftable_71 nvarchar(60), @constraintname_71 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TPRODUCTASSOCIATION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_71, @constraintname_71
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_71+' drop constraint '+@constraintname_71)
       FETCH NEXT from refcursor into @reftable_71, @constraintname_71
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TPRODUCTASSOCIATION
END
;

CREATE TABLE TPRODUCTASSOCIATION
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                ASSOCIATION_TYPE INT NOT NULL,
                SOURCE_PRODUCT_UID BIGINT NOT NULL,
                TARGET_PRODUCT_UID BIGINT NOT NULL,
                CATALOG_UID BIGINT NULL,
                START_DATE DATETIME NOT NULL,
                END_DATE DATETIME NULL,
                DEFAULT_QUANTITY INT default 1 NOT NULL,
                SOURCE_PRODUCT_DEPENDENT INT default 0 NULL,
                ORDERING INT default 0 NULL,

    CONSTRAINT TPRODUCTASSOCIATION_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_PA_SRCPROD_UID ON TPRODUCTASSOCIATION (SOURCE_PRODUCT_UID);
CREATE  INDEX I_PA_TGTPROD_UID ON TPRODUCTASSOCIATION (TARGET_PRODUCT_UID);
CREATE  INDEX I_PA_CATALOG_UID ON TPRODUCTASSOCIATION (CATALOG_UID);
CREATE  INDEX I_PR_SE_DATE ON TPRODUCTASSOCIATION (START_DATE, END_DATE);




/* ---------------------------------------------------------------------- */
/* TPRODUCTSKUATTRIBUTEVALUE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCTSKUATTRIBUTEVALUE_FK_1')
    ALTER TABLE TPRODUCTSKUATTRIBUTEVALUE DROP CONSTRAINT TPRODUCTSKUATTRIBUTEVALUE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCTSKUATTRIBUTEVALUE_FK_2')
    ALTER TABLE TPRODUCTSKUATTRIBUTEVALUE DROP CONSTRAINT TPRODUCTSKUATTRIBUTEVALUE_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TPRODUCTSKUATTRIBUTEVALUE')
BEGIN
     DECLARE @reftable_72 nvarchar(60), @constraintname_72 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TPRODUCTSKUATTRIBUTEVALUE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_72, @constraintname_72
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_72+' drop constraint '+@constraintname_72)
       FETCH NEXT from refcursor into @reftable_72, @constraintname_72
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TPRODUCTSKUATTRIBUTEVALUE
END
;

CREATE TABLE TPRODUCTSKUATTRIBUTEVALUE
(
                UIDPK BIGINT NOT NULL,
                ATTRIBUTE_UID BIGINT NOT NULL,
                ATTRIBUTE_TYPE INT NOT NULL,
                LOCALIZED_ATTRIBUTE_KEY NVARCHAR (255) NOT NULL,
                SHORT_TEXT_VALUE NVARCHAR (255) NULL,
                LONG_TEXT_VALUE NTEXT NULL,
                INTEGER_VALUE INT NULL,
                DECIMAL_VALUE DECIMAL (19,2) NULL,
                BOOLEAN_VALUE INT default 0 NULL,
                DATE_VALUE DATETIME NULL,
                PRODUCT_SKU_UID BIGINT NULL,

    CONSTRAINT TPRODUCTSKUATTRIBUTEVALUE_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_PSAV_SKU_UID ON TPRODUCTSKUATTRIBUTEVALUE (PRODUCT_SKU_UID);
CREATE  INDEX I_PSAV_ATTR_UID ON TPRODUCTSKUATTRIBUTEVALUE (ATTRIBUTE_UID);




/* ---------------------------------------------------------------------- */
/* TPRODUCTTYPEATTRIBUTE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCTTYPEATTRIBUTE_FK_1')
    ALTER TABLE TPRODUCTTYPEATTRIBUTE DROP CONSTRAINT TPRODUCTTYPEATTRIBUTE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCTTYPEATTRIBUTE_FK_2')
    ALTER TABLE TPRODUCTTYPEATTRIBUTE DROP CONSTRAINT TPRODUCTTYPEATTRIBUTE_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TPRODUCTTYPEATTRIBUTE')
BEGIN
     DECLARE @reftable_73 nvarchar(60), @constraintname_73 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TPRODUCTTYPEATTRIBUTE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_73, @constraintname_73
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_73+' drop constraint '+@constraintname_73)
       FETCH NEXT from refcursor into @reftable_73, @constraintname_73
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TPRODUCTTYPEATTRIBUTE
END
;

CREATE TABLE TPRODUCTTYPEATTRIBUTE
(
                UIDPK BIGINT NOT NULL,
                ORDERING INT NULL,
                ATTRIBUTE_UID BIGINT NOT NULL,
                PRODUCT_TYPE_UID BIGINT NOT NULL,

    CONSTRAINT TPRODUCTTYPEATTRIBUTE_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_PTA_ATTR_UID ON TPRODUCTTYPEATTRIBUTE (ATTRIBUTE_UID);
CREATE  INDEX I_PTA_TYPE_UID ON TPRODUCTTYPEATTRIBUTE (PRODUCT_TYPE_UID);




/* ---------------------------------------------------------------------- */
/* TPRODUCTTYPESKUATTRIBUTE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCTTYPESKUATTRIBUTE_FK_1')
    ALTER TABLE TPRODUCTTYPESKUATTRIBUTE DROP CONSTRAINT TPRODUCTTYPESKUATTRIBUTE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCTTYPESKUATTRIBUTE_FK_2')
    ALTER TABLE TPRODUCTTYPESKUATTRIBUTE DROP CONSTRAINT TPRODUCTTYPESKUATTRIBUTE_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TPRODUCTTYPESKUATTRIBUTE')
BEGIN
     DECLARE @reftable_74 nvarchar(60), @constraintname_74 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TPRODUCTTYPESKUATTRIBUTE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_74, @constraintname_74
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_74+' drop constraint '+@constraintname_74)
       FETCH NEXT from refcursor into @reftable_74, @constraintname_74
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TPRODUCTTYPESKUATTRIBUTE
END
;

CREATE TABLE TPRODUCTTYPESKUATTRIBUTE
(
                UIDPK BIGINT NOT NULL,
                ORDERING INT NULL,
                ATTRIBUTE_UID BIGINT NOT NULL,
                PRODUCT_TYPE_UID BIGINT NOT NULL,

    CONSTRAINT TPRODUCTTYPESKUATTRIBUTE_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_PTSA_ATTR_UID ON TPRODUCTTYPESKUATTRIBUTE (ATTRIBUTE_UID);
CREATE  INDEX I_PTSA_TYPE_UID ON TPRODUCTTYPESKUATTRIBUTE (PRODUCT_TYPE_UID);




/* ---------------------------------------------------------------------- */
/* TSELLINGCONTEXT                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSELLINGCONTEXT')
BEGIN
     DECLARE @reftable_75 nvarchar(60), @constraintname_75 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSELLINGCONTEXT'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_75, @constraintname_75
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_75+' drop constraint '+@constraintname_75)
       FETCH NEXT from refcursor into @reftable_75, @constraintname_75
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSELLINGCONTEXT
END
;

CREATE TABLE TSELLINGCONTEXT
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                DESCRIPTION NVARCHAR (255) NULL,
                PRIORITY INT NOT NULL,
                TYPE NVARCHAR (100) NOT NULL,

    CONSTRAINT TSELLINGCONTEXT_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TSELLINGCONTEXT_UNIQUE UNIQUE (GUID));





/* ---------------------------------------------------------------------- */
/* TRULESET                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TRULESET')
BEGIN
     DECLARE @reftable_76 nvarchar(60), @constraintname_76 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TRULESET'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_76, @constraintname_76
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_76+' drop constraint '+@constraintname_76)
       FETCH NEXT from refcursor into @reftable_76, @constraintname_76
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TRULESET
END
;

CREATE TABLE TRULESET
(
                UIDPK BIGINT NOT NULL,
                LAST_MODIFIED_DATE DATETIME default CURRENT_TIMESTAMP NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                SCENARIO INT NOT NULL,

    CONSTRAINT TRULESET_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TRULESET_UNIQUE UNIQUE (NAME));





/* ---------------------------------------------------------------------- */
/* TRULE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='SCRULE_FK')
    ALTER TABLE TRULE DROP CONSTRAINT SCRULE_FK;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TRULE_FK_2')
    ALTER TABLE TRULE DROP CONSTRAINT TRULE_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TRULE_FK_3')
    ALTER TABLE TRULE DROP CONSTRAINT TRULE_FK_3;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TRULE_FK_4')
    ALTER TABLE TRULE DROP CONSTRAINT TRULE_FK_4;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TRULE')
BEGIN
     DECLARE @reftable_77 nvarchar(60), @constraintname_77 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TRULE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_77, @constraintname_77
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_77+' drop constraint '+@constraintname_77)
       FETCH NEXT from refcursor into @reftable_77, @constraintname_77
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TRULE
END
;

CREATE TABLE TRULE
(
                UIDPK BIGINT NOT NULL,
                LAST_MODIFIED_DATE DATETIME default CURRENT_TIMESTAMP NOT NULL,
                RULECODE NVARCHAR (64) NOT NULL,
                START_DATE DATETIME NULL,
                END_DATE DATETIME NULL,
                ELIGIBILITY_OPERATOR INT default 0 NULL,
                CONDITION_OPERATOR INT default 0 NULL,
                NAME NVARCHAR (255) NOT NULL,
                DESCRIPTION NVARCHAR (255) NULL,
                RULE_SET_UID BIGINT NULL,
                STORE_UID BIGINT NULL,
                CATALOG_UID BIGINT NULL,
                CM_USER_UID BIGINT NULL,
                ENABLED INT NOT NULL,
                CURRENT_LUP_NUMBER BIGINT NULL,
                SELLING_CTX_UID BIGINT NULL,

    CONSTRAINT TRULE_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TRULE_UNIQUE UNIQUE (NAME),
    CONSTRAINT TRULE_CODE_UNIQUE UNIQUE (RULECODE));

CREATE  INDEX I_R_SET_STORE_UID ON TRULE (STORE_UID);
CREATE  INDEX I_R_SET_CATALOG_UID ON TRULE (CATALOG_UID);
CREATE  INDEX I_R_SET_UID ON TRULE (RULE_SET_UID);
CREATE  INDEX I_R_SE_DATE ON TRULE (START_DATE, END_DATE);




/* ---------------------------------------------------------------------- */
/* TRULEELEMENT                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TRULEELEMENT_FK_1')
    ALTER TABLE TRULEELEMENT DROP CONSTRAINT TRULEELEMENT_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TRULEELEMENT')
BEGIN
     DECLARE @reftable_78 nvarchar(60), @constraintname_78 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TRULEELEMENT'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_78, @constraintname_78
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_78+' drop constraint '+@constraintname_78)
       FETCH NEXT from refcursor into @reftable_78, @constraintname_78
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TRULEELEMENT
END
;

CREATE TABLE TRULEELEMENT
(
                UIDPK BIGINT NOT NULL,
                TYPE NVARCHAR (255) NOT NULL,
                KIND NVARCHAR (255) NOT NULL,
                RULE_UID BIGINT NULL,

    CONSTRAINT TRULEELEMENT_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_RE_RULE_UID ON TRULEELEMENT (RULE_UID);




/* ---------------------------------------------------------------------- */
/* TRULEEXCEPTION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TRULEEXCEPTION_FK_1')
    ALTER TABLE TRULEEXCEPTION DROP CONSTRAINT TRULEEXCEPTION_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TRULEEXCEPTION')
BEGIN
     DECLARE @reftable_79 nvarchar(60), @constraintname_79 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TRULEEXCEPTION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_79, @constraintname_79
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_79+' drop constraint '+@constraintname_79)
       FETCH NEXT from refcursor into @reftable_79, @constraintname_79
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TRULEEXCEPTION
END
;

CREATE TABLE TRULEEXCEPTION
(
                UIDPK BIGINT NOT NULL,
                TYPE NVARCHAR (255) NOT NULL,
                RULE_ELEMENT_UID BIGINT NULL,

    CONSTRAINT TRULEEXCEPTION_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_REXP_RE_UID ON TRULEEXCEPTION (RULE_ELEMENT_UID);




/* ---------------------------------------------------------------------- */
/* TRULEPARAMETER                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TRULEPARAMETER_FK_1')
    ALTER TABLE TRULEPARAMETER DROP CONSTRAINT TRULEPARAMETER_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TRULEPARAMETER_FK_2')
    ALTER TABLE TRULEPARAMETER DROP CONSTRAINT TRULEPARAMETER_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TRULEPARAMETER')
BEGIN
     DECLARE @reftable_80 nvarchar(60), @constraintname_80 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TRULEPARAMETER'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_80, @constraintname_80
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_80+' drop constraint '+@constraintname_80)
       FETCH NEXT from refcursor into @reftable_80, @constraintname_80
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TRULEPARAMETER
END
;

CREATE TABLE TRULEPARAMETER
(
                UIDPK BIGINT NOT NULL,
                PARAM_KEY NVARCHAR (255) NOT NULL,
                PARAM_VALUE NVARCHAR (255) NOT NULL,
                DISPLAY_TEXT NVARCHAR (255) NULL,
                RULE_ELEMENT_UID BIGINT NULL,
                RULE_EXCEPTION_UID BIGINT NULL,

    CONSTRAINT TRULEPARAMETER_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_RP_RE_UID ON TRULEPARAMETER (RULE_ELEMENT_UID);
CREATE  INDEX I_RP_REXP_UID ON TRULEPARAMETER (RULE_EXCEPTION_UID);




/* ---------------------------------------------------------------------- */
/* TSHOPPINGCART                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSHOPPINGCART_FK_1')
    ALTER TABLE TSHOPPINGCART DROP CONSTRAINT TSHOPPINGCART_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSHOPPINGCART_FK_SHOPPER')
    ALTER TABLE TSHOPPINGCART DROP CONSTRAINT TSHOPPINGCART_FK_SHOPPER;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSHOPPINGCART')
BEGIN
     DECLARE @reftable_81 nvarchar(60), @constraintname_81 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSHOPPINGCART'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_81, @constraintname_81
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_81+' drop constraint '+@constraintname_81)
       FETCH NEXT from refcursor into @reftable_81, @constraintname_81
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSHOPPINGCART
END
;

CREATE TABLE TSHOPPINGCART
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (100) NOT NULL,
                STORE_UID BIGINT NOT NULL,
                SHOPPER_UID BIGINT NOT NULL,

    CONSTRAINT TSHOPPINGCART_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TSHOPPINGCART_UNIQUE UNIQUE (GUID));

CREATE  INDEX I_SHOPCART_STORE_UID ON TSHOPPINGCART (STORE_UID);
CREATE  INDEX I_SHOPCART_SHOPPER_UID ON TSHOPPINGCART (SHOPPER_UID);




/* ---------------------------------------------------------------------- */
/* TCARTITEM                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCARTITEM_FK_TCARTITEM')
    ALTER TABLE TCARTITEM DROP CONSTRAINT TCARTITEM_FK_TCARTITEM;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCARTITEM_FK_2')
    ALTER TABLE TCARTITEM DROP CONSTRAINT TCARTITEM_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCARTITEM')
BEGIN
     DECLARE @reftable_82 nvarchar(60), @constraintname_82 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCARTITEM'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_82, @constraintname_82
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_82+' drop constraint '+@constraintname_82)
       FETCH NEXT from refcursor into @reftable_82, @constraintname_82
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCARTITEM
END
;

CREATE TABLE TCARTITEM
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                SKU_UID BIGINT NOT NULL,
                QUANTITY INT NOT NULL,
                CURRENCY NVARCHAR (3) NULL,
                LIST_UNIT_PRICE DECIMAL (19,2) NULL,
                SALE_UNIT_PRICE DECIMAL (19,2) NULL,
                PROMO_UNIT_PRICE DECIMAL (19,2) NULL,
                DISCOUNT_AMOUNT DECIMAL (19,2) NULL,
                TAX_AMOUNT DECIMAL (19,2) NULL,
                PARENT_ITEM_UID BIGINT NULL,
                LAST_MODIFIED_DATE DATETIME default CURRENT_TIMESTAMP NOT NULL,
                ORDERING INT default 0 NOT NULL,

    CONSTRAINT TCARTITEM_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_CARTI_SKU_UID ON TCARTITEM (SKU_UID);
CREATE  INDEX I_CARTITEM_GUID ON TCARTITEM (GUID);
CREATE  INDEX I_CARTITEM_PARENT_ITEM_UID ON TCARTITEM (PARENT_ITEM_UID);




/* ---------------------------------------------------------------------- */
/* TSHOPPINGITEMRECURRINGPRICE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSHOPPINGITEMRECURRINGPRI_FK_1')
    ALTER TABLE TSHOPPINGITEMRECURRINGPRICE DROP CONSTRAINT TSHOPPINGITEMRECURRINGPRI_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSHOPPINGITEMRECURRINGPRI_FK_2')
    ALTER TABLE TSHOPPINGITEMRECURRINGPRICE DROP CONSTRAINT TSHOPPINGITEMRECURRINGPRI_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSHOPPINGITEMRECURRINGPRICE')
BEGIN
     DECLARE @reftable_83 nvarchar(60), @constraintname_83 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSHOPPINGITEMRECURRINGPRICE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_83, @constraintname_83
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_83+' drop constraint '+@constraintname_83)
       FETCH NEXT from refcursor into @reftable_83, @constraintname_83
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSHOPPINGITEMRECURRINGPRICE
END
;

CREATE TABLE TSHOPPINGITEMRECURRINGPRICE
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                PAYMENT_SCHEDULE_NAME NVARCHAR (255) NOT NULL,
                FREQ_AMOUNT DECIMAL (19,8) NOT NULL,
                FREQ_UNIT NVARCHAR (255) NOT NULL,
                DURATION_AMOUNT DECIMAL (19,8) NULL,
                DURATION_UNIT NVARCHAR (255) NULL,
                CARTITEM_UID BIGINT NULL,
                ORDERSKU_UID BIGINT NULL,
                LIST_UNIT_PRICE DECIMAL (19,2) NULL,
                SALE_UNIT_PRICE DECIMAL (19,2) NULL,
                PROMO_UNIT_PRICE DECIMAL (19,2) NULL,

    CONSTRAINT TSHOPPINGITEMRECURRINGPRICE_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_SIRP_CARTITEM_UID ON TSHOPPINGITEMRECURRINGPRICE (CARTITEM_UID);
CREATE  INDEX I_SIRP_ORDERSKU_UID ON TSHOPPINGITEMRECURRINGPRICE (ORDERSKU_UID);




/* ---------------------------------------------------------------------- */
/* TUSERROLEPERMISSIONX                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TUSERROLEPERMISSIONX_FK_1')
    ALTER TABLE TUSERROLEPERMISSIONX DROP CONSTRAINT TUSERROLEPERMISSIONX_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TUSERROLEPERMISSIONX')
BEGIN
     DECLARE @reftable_84 nvarchar(60), @constraintname_84 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TUSERROLEPERMISSIONX'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_84, @constraintname_84
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_84+' drop constraint '+@constraintname_84)
       FETCH NEXT from refcursor into @reftable_84, @constraintname_84
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TUSERROLEPERMISSIONX
END
;

CREATE TABLE TUSERROLEPERMISSIONX
(
                UIDPK BIGINT NOT NULL,
                ROLE_UID BIGINT NOT NULL,
                USER_PERMISSION NVARCHAR (255) NULL,

    CONSTRAINT TUSERROLEPERMISSIONX_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_URPXI_ROLE_UID ON TUSERROLEPERMISSIONX (ROLE_UID);




/* ---------------------------------------------------------------------- */
/* TSKUOPTION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSKUOPTION_FK_1')
    ALTER TABLE TSKUOPTION DROP CONSTRAINT TSKUOPTION_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSKUOPTION')
BEGIN
     DECLARE @reftable_85 nvarchar(60), @constraintname_85 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSKUOPTION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_85, @constraintname_85
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_85+' drop constraint '+@constraintname_85)
       FETCH NEXT from refcursor into @reftable_85, @constraintname_85
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSKUOPTION
END
;

CREATE TABLE TSKUOPTION
(
                UIDPK BIGINT NOT NULL,
                OPTION_KEY NVARCHAR (100) NOT NULL,
                CATALOG_UID BIGINT NOT NULL,

    CONSTRAINT TSKUOPTION_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TSKUOPTION_UNIQUE UNIQUE (OPTION_KEY));

CREATE  INDEX I_SKUOPT_CATALOG_UID ON TSKUOPTION (CATALOG_UID);




/* ---------------------------------------------------------------------- */
/* TSKUOPTIONVALUE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSKUOPTIONVALUE_FK_1')
    ALTER TABLE TSKUOPTIONVALUE DROP CONSTRAINT TSKUOPTIONVALUE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSKUOPTIONVALUE')
BEGIN
     DECLARE @reftable_86 nvarchar(60), @constraintname_86 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSKUOPTIONVALUE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_86, @constraintname_86
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_86+' drop constraint '+@constraintname_86)
       FETCH NEXT from refcursor into @reftable_86, @constraintname_86
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSKUOPTIONVALUE
END
;

CREATE TABLE TSKUOPTIONVALUE
(
                UIDPK BIGINT NOT NULL,
                OPTION_VALUE_KEY NVARCHAR (255) NOT NULL,
                ORDERING INT NULL,
                SKU_OPTION_UID BIGINT NOT NULL,
                IMAGE NVARCHAR (255) NULL,

    CONSTRAINT TSKUOPTIONVALUE_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TSKUOPTVAL_UNIQ UNIQUE (OPTION_VALUE_KEY));

CREATE  INDEX I_SOV_SO_UID ON TSKUOPTIONVALUE (SKU_OPTION_UID);




/* ---------------------------------------------------------------------- */
/* TPRODUCTTYPESKUOPTION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCTTYPESKUOPTION_FK_1')
    ALTER TABLE TPRODUCTTYPESKUOPTION DROP CONSTRAINT TPRODUCTTYPESKUOPTION_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCTTYPESKUOPTION_FK_2')
    ALTER TABLE TPRODUCTTYPESKUOPTION DROP CONSTRAINT TPRODUCTTYPESKUOPTION_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TPRODUCTTYPESKUOPTION')
BEGIN
     DECLARE @reftable_87 nvarchar(60), @constraintname_87 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TPRODUCTTYPESKUOPTION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_87, @constraintname_87
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_87+' drop constraint '+@constraintname_87)
       FETCH NEXT from refcursor into @reftable_87, @constraintname_87
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TPRODUCTTYPESKUOPTION
END
;

CREATE TABLE TPRODUCTTYPESKUOPTION
(
                PRODUCT_TYPE_UID BIGINT NOT NULL,
                SKU_OPTION_UID BIGINT NOT NULL,
);

CREATE  INDEX I_PTSO_PT_UID ON TPRODUCTTYPESKUOPTION (PRODUCT_TYPE_UID);
CREATE  INDEX I_PTSO_SO_UID ON TPRODUCTTYPESKUOPTION (SKU_OPTION_UID);




/* ---------------------------------------------------------------------- */
/* TPRODUCTSKUOPTIONVALUE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCTSKUOPTIONVALUE_FK_1')
    ALTER TABLE TPRODUCTSKUOPTIONVALUE DROP CONSTRAINT TPRODUCTSKUOPTIONVALUE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPRODUCTSKUOPTIONVALUE_FK_2')
    ALTER TABLE TPRODUCTSKUOPTIONVALUE DROP CONSTRAINT TPRODUCTSKUOPTIONVALUE_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TPRODUCTSKUOPTIONVALUE')
BEGIN
     DECLARE @reftable_88 nvarchar(60), @constraintname_88 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TPRODUCTSKUOPTIONVALUE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_88, @constraintname_88
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_88+' drop constraint '+@constraintname_88)
       FETCH NEXT from refcursor into @reftable_88, @constraintname_88
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TPRODUCTSKUOPTIONVALUE
END
;

CREATE TABLE TPRODUCTSKUOPTIONVALUE
(
                UIDPK BIGINT NOT NULL,
                PRODUCT_SKU_UID BIGINT NOT NULL,
                OPTION_KEY NVARCHAR (100) NOT NULL,
                OPTION_VALUE_UID BIGINT NOT NULL,

    CONSTRAINT TPRODUCTSKUOPTIONVALUE_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_PSSO_PS_UID ON TPRODUCTSKUOPTIONVALUE (PRODUCT_SKU_UID);
CREATE  INDEX I_PSSO_SKUOV_UID ON TPRODUCTSKUOPTIONVALUE (OPTION_VALUE_UID);




/* ---------------------------------------------------------------------- */
/* TPRODUCTDELETED                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TPRODUCTDELETED')
BEGIN
     DECLARE @reftable_89 nvarchar(60), @constraintname_89 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TPRODUCTDELETED'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_89, @constraintname_89
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_89+' drop constraint '+@constraintname_89)
       FETCH NEXT from refcursor into @reftable_89, @constraintname_89
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TPRODUCTDELETED
END
;

CREATE TABLE TPRODUCTDELETED
(
                UIDPK BIGINT NOT NULL,
                PRODUCT_UID BIGINT NOT NULL,
                DELETED_DATE DATETIME NOT NULL,

    CONSTRAINT TPRODUCTDELETED_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_PD_DELETED_DATE ON TPRODUCTDELETED (DELETED_DATE);




/* ---------------------------------------------------------------------- */
/* TOBJECTDELETED                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TOBJECTDELETED')
BEGIN
     DECLARE @reftable_90 nvarchar(60), @constraintname_90 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TOBJECTDELETED'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_90, @constraintname_90
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_90+' drop constraint '+@constraintname_90)
       FETCH NEXT from refcursor into @reftable_90, @constraintname_90
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TOBJECTDELETED
END
;

CREATE TABLE TOBJECTDELETED
(
                UIDPK BIGINT NOT NULL,
                OBJECT_TYPE NVARCHAR (255) NOT NULL,
                OBJECT_UID BIGINT NOT NULL,
                DELETED_DATE DATETIME NOT NULL,

    CONSTRAINT TOBJECTDELETED_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_OD_DELETED_DATE ON TOBJECTDELETED (DELETED_DATE);




/* ---------------------------------------------------------------------- */
/* TLOCALIZEDPROPERTIES                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TLOCALIZEDPROPERTIES')
BEGIN
     DECLARE @reftable_91 nvarchar(60), @constraintname_91 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TLOCALIZEDPROPERTIES'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_91, @constraintname_91
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_91+' drop constraint '+@constraintname_91)
       FETCH NEXT from refcursor into @reftable_91, @constraintname_91
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TLOCALIZEDPROPERTIES
END
;

CREATE TABLE TLOCALIZEDPROPERTIES
(
                UIDPK BIGINT NOT NULL,
                OBJECT_UID BIGINT NULL,
                LOCALIZED_PROPERTY_KEY NVARCHAR (255) NOT NULL,
                VALUE NVARCHAR (255) NOT NULL,
                TYPE NVARCHAR (31) NOT NULL,

    CONSTRAINT TLOCALIZEDPROPERTIES_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_LP_OBJECT_UID ON TLOCALIZEDPROPERTIES (OBJECT_UID);




/* ---------------------------------------------------------------------- */
/* TDIGITALASSETAUDIT                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TDIGITALASSETAUDIT')
BEGIN
     DECLARE @reftable_92 nvarchar(60), @constraintname_92 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TDIGITALASSETAUDIT'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_92, @constraintname_92
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_92+' drop constraint '+@constraintname_92)
       FETCH NEXT from refcursor into @reftable_92, @constraintname_92
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TDIGITALASSETAUDIT
END
;

CREATE TABLE TDIGITALASSETAUDIT
(
                UIDPK BIGINT NOT NULL,
                ORDERSKU_UID BIGINT NOT NULL,
                DIGITALASSET_UID BIGINT NOT NULL,
                DOWNLOAD_TIME DATETIME NOT NULL,
                IP_ADDRESS NVARCHAR (255) NULL,

    CONSTRAINT TDIGITALASSETAUDIT_PK PRIMARY KEY(UIDPK));





/* ---------------------------------------------------------------------- */
/* TAPPLIEDRULE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TAPPLIEDRULE')
BEGIN
     DECLARE @reftable_93 nvarchar(60), @constraintname_93 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TAPPLIEDRULE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_93, @constraintname_93
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_93+' drop constraint '+@constraintname_93)
       FETCH NEXT from refcursor into @reftable_93, @constraintname_93
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TAPPLIEDRULE
END
;

CREATE TABLE TAPPLIEDRULE
(
                UIDPK BIGINT NOT NULL,
                ORDER_UID BIGINT NOT NULL,
                RULE_UID BIGINT NOT NULL,
                RULE_NAME NVARCHAR (255) NOT NULL,
                RULE_CODE NTEXT NOT NULL,

    CONSTRAINT TAPPLIEDRULE_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_TAR_ORDER_UID ON TAPPLIEDRULE (ORDER_UID);
CREATE  INDEX I_TAR_RULE_UID ON TAPPLIEDRULE (RULE_UID);




/* ---------------------------------------------------------------------- */
/* TAPPLIEDRULECOUPONCODE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TAPPLIEDRULECOUPONCODE_FK_1')
    ALTER TABLE TAPPLIEDRULECOUPONCODE DROP CONSTRAINT TAPPLIEDRULECOUPONCODE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TAPPLIEDRULECOUPONCODE')
BEGIN
     DECLARE @reftable_94 nvarchar(60), @constraintname_94 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TAPPLIEDRULECOUPONCODE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_94, @constraintname_94
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_94+' drop constraint '+@constraintname_94)
       FETCH NEXT from refcursor into @reftable_94, @constraintname_94
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TAPPLIEDRULECOUPONCODE
END
;

CREATE TABLE TAPPLIEDRULECOUPONCODE
(
                UIDPK BIGINT NOT NULL,
                APPLIED_RULE_UID BIGINT NOT NULL,
                COUPONCODE NVARCHAR (255) NOT NULL,
                USECOUNT INT default 0 NULL,

    CONSTRAINT TAPPLIEDRULECOUPONCODE_PK PRIMARY KEY(UIDPK));





/* ---------------------------------------------------------------------- */
/* TTOPSELLER                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TTOPSELLER')
BEGIN
     DECLARE @reftable_95 nvarchar(60), @constraintname_95 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TTOPSELLER'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_95, @constraintname_95
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_95+' drop constraint '+@constraintname_95)
       FETCH NEXT from refcursor into @reftable_95, @constraintname_95
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TTOPSELLER
END
;

CREATE TABLE TTOPSELLER
(
                UIDPK BIGINT NOT NULL,
                CATEGORY_UID BIGINT NOT NULL,

    CONSTRAINT TTOPSELLER_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TTOPSELLER_UNIQUE UNIQUE (CATEGORY_UID));





/* ---------------------------------------------------------------------- */
/* TTOPSELLERPRODUCTS                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TTOPSELLERPRODUCTS_FK_1')
    ALTER TABLE TTOPSELLERPRODUCTS DROP CONSTRAINT TTOPSELLERPRODUCTS_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TTOPSELLERPRODUCTS')
BEGIN
     DECLARE @reftable_96 nvarchar(60), @constraintname_96 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TTOPSELLERPRODUCTS'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_96, @constraintname_96
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_96+' drop constraint '+@constraintname_96)
       FETCH NEXT from refcursor into @reftable_96, @constraintname_96
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TTOPSELLERPRODUCTS
END
;

CREATE TABLE TTOPSELLERPRODUCTS
(
                UIDPK BIGINT NOT NULL,
                TOP_SELLER_UID BIGINT NOT NULL,
                PRODUCT_UID BIGINT NOT NULL,
                SALES_COUNT INT NOT NULL,

    CONSTRAINT TTOPSELLERPRODUCTS_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TTOPSELLERPRODUCTS_UNIQUE UNIQUE (TOP_SELLER_UID, PRODUCT_UID));

CREATE  INDEX I_TSP_TS_UID ON TTOPSELLERPRODUCTS (TOP_SELLER_UID);




/* ---------------------------------------------------------------------- */
/* TSFSEARCHLOG                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSFSEARCHLOG')
BEGIN
     DECLARE @reftable_97 nvarchar(60), @constraintname_97 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSFSEARCHLOG'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_97, @constraintname_97
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_97+' drop constraint '+@constraintname_97)
       FETCH NEXT from refcursor into @reftable_97, @constraintname_97
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSFSEARCHLOG
END
;

CREATE TABLE TSFSEARCHLOG
(
                UIDPK BIGINT NOT NULL,
                SEARCH_TIME DATETIME NOT NULL,
                KEYWORDS NVARCHAR (255) NULL,
                RESULT_COUNT INT NOT NULL,
                SUGGESTIONS_GENERATED INT default 0 NULL,
                CATEGORY_RESTRICTION BIGINT default 0 NOT NULL,

    CONSTRAINT TSFSEARCHLOG_PK PRIMARY KEY(UIDPK));





/* ---------------------------------------------------------------------- */
/* TSTOREWAREHOUSE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSTOREWAREHOUSE_FK_1')
    ALTER TABLE TSTOREWAREHOUSE DROP CONSTRAINT TSTOREWAREHOUSE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSTOREWAREHOUSE_FK_2')
    ALTER TABLE TSTOREWAREHOUSE DROP CONSTRAINT TSTOREWAREHOUSE_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSTOREWAREHOUSE')
BEGIN
     DECLARE @reftable_98 nvarchar(60), @constraintname_98 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSTOREWAREHOUSE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_98, @constraintname_98
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_98+' drop constraint '+@constraintname_98)
       FETCH NEXT from refcursor into @reftable_98, @constraintname_98
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSTOREWAREHOUSE
END
;

CREATE TABLE TSTOREWAREHOUSE
(
                STORE_UID BIGINT NOT NULL,
                WAREHOUSE_UID BIGINT NOT NULL,
);

CREATE  INDEX I_WAREHOUSE_WH_UID ON TSTOREWAREHOUSE (WAREHOUSE_UID);
CREATE  INDEX I_WAREHOUSE_STORE_UID ON TSTOREWAREHOUSE (STORE_UID);




/* ---------------------------------------------------------------------- */
/* TSTORECREDITCARDTYPE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSTORECREDITCARDTYPE_FK_1')
    ALTER TABLE TSTORECREDITCARDTYPE DROP CONSTRAINT TSTORECREDITCARDTYPE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSTORECREDITCARDTYPE')
BEGIN
     DECLARE @reftable_99 nvarchar(60), @constraintname_99 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSTORECREDITCARDTYPE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_99, @constraintname_99
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_99+' drop constraint '+@constraintname_99)
       FETCH NEXT from refcursor into @reftable_99, @constraintname_99
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSTORECREDITCARDTYPE
END
;

CREATE TABLE TSTORECREDITCARDTYPE
(
                UIDPK BIGINT NOT NULL,
                TYPE NVARCHAR (255) NULL,
                STORE_UID BIGINT NOT NULL,

    CONSTRAINT TSTORECREDITCARDTYPE_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_CREDITCARDTYPES_STORE_UID ON TSTORECREDITCARDTYPE (STORE_UID);




/* ---------------------------------------------------------------------- */
/* TSTORETAXCODE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSTORETAXCODE_FK_1')
    ALTER TABLE TSTORETAXCODE DROP CONSTRAINT TSTORETAXCODE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSTORETAXCODE_FK_2')
    ALTER TABLE TSTORETAXCODE DROP CONSTRAINT TSTORETAXCODE_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSTORETAXCODE')
BEGIN
     DECLARE @reftable_100 nvarchar(60), @constraintname_100 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSTORETAXCODE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_100, @constraintname_100
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_100+' drop constraint '+@constraintname_100)
       FETCH NEXT from refcursor into @reftable_100, @constraintname_100
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSTORETAXCODE
END
;

CREATE TABLE TSTORETAXCODE
(
                STORE_UID BIGINT NOT NULL,
                TAXCODE_UID BIGINT NOT NULL,
);

CREATE  INDEX I_TAXCODE_TAXCODE_UID ON TSTORETAXCODE (TAXCODE_UID);
CREATE  INDEX I_TAXCODE_STORE_UID ON TSTORETAXCODE (STORE_UID);




/* ---------------------------------------------------------------------- */
/* TSTORETAXJURISDICTION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSTORETAXJURISDICTION_FK_1')
    ALTER TABLE TSTORETAXJURISDICTION DROP CONSTRAINT TSTORETAXJURISDICTION_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSTORETAXJURISDICTION_FK_2')
    ALTER TABLE TSTORETAXJURISDICTION DROP CONSTRAINT TSTORETAXJURISDICTION_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSTORETAXJURISDICTION')
BEGIN
     DECLARE @reftable_101 nvarchar(60), @constraintname_101 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSTORETAXJURISDICTION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_101, @constraintname_101
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_101+' drop constraint '+@constraintname_101)
       FETCH NEXT from refcursor into @reftable_101, @constraintname_101
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSTORETAXJURISDICTION
END
;

CREATE TABLE TSTORETAXJURISDICTION
(
                STORE_UID BIGINT NOT NULL,
                TAXJURISDICTION_UID BIGINT NOT NULL,
);

CREATE  INDEX I_TAXJURISDICTION_JUR_UID ON TSTORETAXJURISDICTION (TAXJURISDICTION_UID);
CREATE  INDEX I_TAXJURISDICTION_STORE_UID ON TSTORETAXJURISDICTION (STORE_UID);




/* ---------------------------------------------------------------------- */
/* TPAYMENTGATEWAY                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TPAYMENTGATEWAY')
BEGIN
     DECLARE @reftable_102 nvarchar(60), @constraintname_102 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TPAYMENTGATEWAY'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_102, @constraintname_102
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_102+' drop constraint '+@constraintname_102)
       FETCH NEXT from refcursor into @reftable_102, @constraintname_102
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TPAYMENTGATEWAY
END
;

CREATE TABLE TPAYMENTGATEWAY
(
                UIDPK BIGINT NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                TYPE NVARCHAR (255) NOT NULL,

    CONSTRAINT TPAYMENTGATEWAY_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TPAYMENTGATEWAY_NAME UNIQUE (NAME));





/* ---------------------------------------------------------------------- */
/* TSTOREPAYMENTGATEWAY                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSTOREPAYMENTGATEWAY_FK_1')
    ALTER TABLE TSTOREPAYMENTGATEWAY DROP CONSTRAINT TSTOREPAYMENTGATEWAY_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSTOREPAYMENTGATEWAY_FK_2')
    ALTER TABLE TSTOREPAYMENTGATEWAY DROP CONSTRAINT TSTOREPAYMENTGATEWAY_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSTOREPAYMENTGATEWAY')
BEGIN
     DECLARE @reftable_103 nvarchar(60), @constraintname_103 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSTOREPAYMENTGATEWAY'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_103, @constraintname_103
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_103+' drop constraint '+@constraintname_103)
       FETCH NEXT from refcursor into @reftable_103, @constraintname_103
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSTOREPAYMENTGATEWAY
END
;

CREATE TABLE TSTOREPAYMENTGATEWAY
(
                STORE_UID BIGINT NOT NULL,
                GATEWAY_UID BIGINT NOT NULL,
);

CREATE  INDEX I_PAYMENTGATEWAY_PGW ON TSTOREPAYMENTGATEWAY (GATEWAY_UID);
CREATE  INDEX I_PAYMENTGATEWAY_STORE ON TSTOREPAYMENTGATEWAY (STORE_UID);




/* ---------------------------------------------------------------------- */
/* TPAYMENTGATEWAYPROPERTIES                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TPAYMENTGATEWAYPROPERTIES_FK_1')
    ALTER TABLE TPAYMENTGATEWAYPROPERTIES DROP CONSTRAINT TPAYMENTGATEWAYPROPERTIES_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TPAYMENTGATEWAYPROPERTIES')
BEGIN
     DECLARE @reftable_104 nvarchar(60), @constraintname_104 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TPAYMENTGATEWAYPROPERTIES'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_104, @constraintname_104
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_104+' drop constraint '+@constraintname_104)
       FETCH NEXT from refcursor into @reftable_104, @constraintname_104
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TPAYMENTGATEWAYPROPERTIES
END
;

CREATE TABLE TPAYMENTGATEWAYPROPERTIES
(
                UIDPK BIGINT NOT NULL,
                PROPKEY NVARCHAR (255) NOT NULL,
                PROPVALUE NVARCHAR (255) NULL,
                PAYMENTGATEWAY_UID BIGINT NOT NULL,

    CONSTRAINT TPAYMENTGATEWAYPROPERTIES_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_PGPROPS_PG_UID ON TPAYMENTGATEWAYPROPERTIES (PAYMENTGATEWAY_UID);




/* ---------------------------------------------------------------------- */
/* TSTORECATALOG                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSTORECATALOG_FK_1')
    ALTER TABLE TSTORECATALOG DROP CONSTRAINT TSTORECATALOG_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSTORECATALOG_FK_2')
    ALTER TABLE TSTORECATALOG DROP CONSTRAINT TSTORECATALOG_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSTORECATALOG')
BEGIN
     DECLARE @reftable_105 nvarchar(60), @constraintname_105 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSTORECATALOG'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_105, @constraintname_105
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_105+' drop constraint '+@constraintname_105)
       FETCH NEXT from refcursor into @reftable_105, @constraintname_105
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSTORECATALOG
END
;

CREATE TABLE TSTORECATALOG
(
                UIDPK BIGINT NOT NULL,
                STORE_UID BIGINT NOT NULL,
                CATALOG_UID BIGINT NOT NULL,

    CONSTRAINT TSTORECATALOG_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_STORECAT_CATALOG_UID ON TSTORECATALOG (CATALOG_UID);
CREATE  INDEX I_STORECAT_STORE_UID ON TSTORECATALOG (STORE_UID);




/* ---------------------------------------------------------------------- */
/* TCMUSERSTORE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCMUSERSTORE_FK_1')
    ALTER TABLE TCMUSERSTORE DROP CONSTRAINT TCMUSERSTORE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCMUSERSTORE_FK_2')
    ALTER TABLE TCMUSERSTORE DROP CONSTRAINT TCMUSERSTORE_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCMUSERSTORE')
BEGIN
     DECLARE @reftable_106 nvarchar(60), @constraintname_106 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCMUSERSTORE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_106, @constraintname_106
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_106+' drop constraint '+@constraintname_106)
       FETCH NEXT from refcursor into @reftable_106, @constraintname_106
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCMUSERSTORE
END
;

CREATE TABLE TCMUSERSTORE
(
                USER_UID BIGINT NOT NULL,
                STORE_UID BIGINT NOT NULL,

    CONSTRAINT TCMUSERSTORE_PK PRIMARY KEY(USER_UID,STORE_UID));

CREATE  INDEX I_CMUSERSTORE_STORE_UID ON TCMUSERSTORE (STORE_UID);
CREATE  INDEX I_CMUSERSTORE__USER_UID ON TCMUSERSTORE (USER_UID);




/* ---------------------------------------------------------------------- */
/* TCMUSERWAREHOUSE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCMUSERWAREHOUSE_FK_1')
    ALTER TABLE TCMUSERWAREHOUSE DROP CONSTRAINT TCMUSERWAREHOUSE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCMUSERWAREHOUSE_FK_2')
    ALTER TABLE TCMUSERWAREHOUSE DROP CONSTRAINT TCMUSERWAREHOUSE_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCMUSERWAREHOUSE')
BEGIN
     DECLARE @reftable_107 nvarchar(60), @constraintname_107 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCMUSERWAREHOUSE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_107, @constraintname_107
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_107+' drop constraint '+@constraintname_107)
       FETCH NEXT from refcursor into @reftable_107, @constraintname_107
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCMUSERWAREHOUSE
END
;

CREATE TABLE TCMUSERWAREHOUSE
(
                USER_UID BIGINT NOT NULL,
                WAREHOUSE_UID BIGINT NOT NULL,

    CONSTRAINT TCMUSERWAREHOUSE_PK PRIMARY KEY(USER_UID,WAREHOUSE_UID));

CREATE  INDEX I_CMUSERWH_WAREHOUSE_UID ON TCMUSERWAREHOUSE (WAREHOUSE_UID);
CREATE  INDEX I_CMUSERWH__USER_UID ON TCMUSERWAREHOUSE (USER_UID);




/* ---------------------------------------------------------------------- */
/* TCMUSERCATALOG                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCMUSERCATALOG_FK_1')
    ALTER TABLE TCMUSERCATALOG DROP CONSTRAINT TCMUSERCATALOG_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCMUSERCATALOG_FK_2')
    ALTER TABLE TCMUSERCATALOG DROP CONSTRAINT TCMUSERCATALOG_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCMUSERCATALOG')
BEGIN
     DECLARE @reftable_108 nvarchar(60), @constraintname_108 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCMUSERCATALOG'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_108, @constraintname_108
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_108+' drop constraint '+@constraintname_108)
       FETCH NEXT from refcursor into @reftable_108, @constraintname_108
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCMUSERCATALOG
END
;

CREATE TABLE TCMUSERCATALOG
(
                USER_UID BIGINT NOT NULL,
                CATALOG_UID BIGINT NOT NULL,

    CONSTRAINT TCMUSERCATALOG_PK PRIMARY KEY(USER_UID,CATALOG_UID));

CREATE  INDEX I_CMUSERCATALOG_CATALOG_UID ON TCMUSERCATALOG (CATALOG_UID);
CREATE  INDEX I_CMUSERCATALOG__USER_UID ON TCMUSERCATALOG (USER_UID);




/* ---------------------------------------------------------------------- */
/* TSTOREASSOCIATION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSTOREASSOCIATION_FK_1')
    ALTER TABLE TSTOREASSOCIATION DROP CONSTRAINT TSTOREASSOCIATION_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSTOREASSOCIATION_FK_2')
    ALTER TABLE TSTOREASSOCIATION DROP CONSTRAINT TSTOREASSOCIATION_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSTOREASSOCIATION')
BEGIN
     DECLARE @reftable_109 nvarchar(60), @constraintname_109 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSTOREASSOCIATION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_109, @constraintname_109
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_109+' drop constraint '+@constraintname_109)
       FETCH NEXT from refcursor into @reftable_109, @constraintname_109
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSTOREASSOCIATION
END
;

CREATE TABLE TSTOREASSOCIATION
(
                STORE_UID BIGINT NOT NULL,
                ASSOCIATED_STORE_UID BIGINT NOT NULL,

    CONSTRAINT TSTOREASSOCIATION_PK PRIMARY KEY(STORE_UID,ASSOCIATED_STORE_UID));

CREATE  INDEX I_STORE_ASSOCIATE_UID ON TSTOREASSOCIATION (STORE_UID);
CREATE  INDEX I_STORE_ASSOCIATION_UID ON TSTOREASSOCIATION (ASSOCIATED_STORE_UID);




/* ---------------------------------------------------------------------- */
/* TSYNONYMGROUPS                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSYNONYMGROUPS_FK_1')
    ALTER TABLE TSYNONYMGROUPS DROP CONSTRAINT TSYNONYMGROUPS_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSYNONYMGROUPS')
BEGIN
     DECLARE @reftable_110 nvarchar(60), @constraintname_110 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSYNONYMGROUPS'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_110, @constraintname_110
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_110+' drop constraint '+@constraintname_110)
       FETCH NEXT from refcursor into @reftable_110, @constraintname_110
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSYNONYMGROUPS
END
;

CREATE TABLE TSYNONYMGROUPS
(
                UIDPK BIGINT NOT NULL,
                CONCEPT_TERM NVARCHAR (255) NOT NULL,
                LOCALE NVARCHAR (20) NOT NULL,
                CATALOG_UID BIGINT NOT NULL,

    CONSTRAINT TSYNONYMGROUPS_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_SYNONYMGR_CATLOG_UID ON TSYNONYMGROUPS (CATALOG_UID);




/* ---------------------------------------------------------------------- */
/* TSYNONYM                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSYNONYM_FK_1')
    ALTER TABLE TSYNONYM DROP CONSTRAINT TSYNONYM_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSYNONYM')
BEGIN
     DECLARE @reftable_111 nvarchar(60), @constraintname_111 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSYNONYM'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_111, @constraintname_111
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_111+' drop constraint '+@constraintname_111)
       FETCH NEXT from refcursor into @reftable_111, @constraintname_111
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSYNONYM
END
;

CREATE TABLE TSYNONYM
(
                UIDPK BIGINT NOT NULL,
                SYNONYM_UID BIGINT NOT NULL,
                SYNONYM_WORD NVARCHAR (255) NOT NULL,

    CONSTRAINT TSYNONYM_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_SYNONYM_SYNONYMGR_UID ON TSYNONYM (SYNONYM_UID);




/* ---------------------------------------------------------------------- */
/* TINDEXNOTIFY                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TINDEXNOTIFY')
BEGIN
     DECLARE @reftable_112 nvarchar(60), @constraintname_112 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TINDEXNOTIFY'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_112, @constraintname_112
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_112+' drop constraint '+@constraintname_112)
       FETCH NEXT from refcursor into @reftable_112, @constraintname_112
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TINDEXNOTIFY
END
;

CREATE TABLE TINDEXNOTIFY
(
                UIDPK BIGINT NOT NULL,
                INDEX_TYPE NVARCHAR (100) NOT NULL,
                UPDATE_TYPE NVARCHAR (64) NOT NULL,
                AFFECTED_UID BIGINT NULL,
                ENTITY_TYPE NVARCHAR (64) NULL,
                QUERY_STRING NTEXT NULL,

    CONSTRAINT TINDEXNOTIFY_PK PRIMARY KEY(UIDPK));





/* ---------------------------------------------------------------------- */
/* TRULESTORAGE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TRULESTORAGE_FK_1')
    ALTER TABLE TRULESTORAGE DROP CONSTRAINT TRULESTORAGE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TRULESTORAGE_FK_2')
    ALTER TABLE TRULESTORAGE DROP CONSTRAINT TRULESTORAGE_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TRULESTORAGE')
BEGIN
     DECLARE @reftable_113 nvarchar(60), @constraintname_113 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TRULESTORAGE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_113, @constraintname_113
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_113+' drop constraint '+@constraintname_113)
       FETCH NEXT from refcursor into @reftable_113, @constraintname_113
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TRULESTORAGE
END
;

CREATE TABLE TRULESTORAGE
(
                UIDPK BIGINT NOT NULL,
                STORE_UID BIGINT NULL,
                CATALOG_UID BIGINT NULL,
                SCENARIO INT NOT NULL,
                LAST_MODIFIED_DATE DATETIME default CURRENT_TIMESTAMP NOT NULL,
                RULEBASE IMAGE NOT NULL,

    CONSTRAINT TRULESTORAGE_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_RS_STRE_UID ON TRULESTORAGE (STORE_UID);
CREATE  INDEX I_RS_CAT_UID ON TRULESTORAGE (CATALOG_UID);




/* ---------------------------------------------------------------------- */
/* TADVANCEDSEARCHQUERY                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TADVANCEDSEARCHQUERY_FK_1')
    ALTER TABLE TADVANCEDSEARCHQUERY DROP CONSTRAINT TADVANCEDSEARCHQUERY_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TADVANCEDSEARCHQUERY')
BEGIN
     DECLARE @reftable_114 nvarchar(60), @constraintname_114 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TADVANCEDSEARCHQUERY'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_114, @constraintname_114
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_114+' drop constraint '+@constraintname_114)
       FETCH NEXT from refcursor into @reftable_114, @constraintname_114
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TADVANCEDSEARCHQUERY
END
;

CREATE TABLE TADVANCEDSEARCHQUERY
(
                UIDPK BIGINT NOT NULL,
                QUERY_ID BIGINT NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                DESCRIPTION NTEXT NULL,
                QUERY_VISIBILITY NVARCHAR (20) NOT NULL,
                OWNER_ID BIGINT NULL,
                QUERY_TYPE NVARCHAR (20) NOT NULL,
                QUERY_CONTENT NTEXT NULL,

    CONSTRAINT TADVANCEDSEARCHQUERY_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_ON_OWNER_UID ON TADVANCEDSEARCHQUERY (OWNER_ID);




/* ---------------------------------------------------------------------- */
/* TSTORESUPPORTEDCURRENCY                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSTORESUPPORTEDCURRENCY_FK_1')
    ALTER TABLE TSTORESUPPORTEDCURRENCY DROP CONSTRAINT TSTORESUPPORTEDCURRENCY_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSTORESUPPORTEDCURRENCY')
BEGIN
     DECLARE @reftable_115 nvarchar(60), @constraintname_115 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSTORESUPPORTEDCURRENCY'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_115, @constraintname_115
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_115+' drop constraint '+@constraintname_115)
       FETCH NEXT from refcursor into @reftable_115, @constraintname_115
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSTORESUPPORTEDCURRENCY
END
;

CREATE TABLE TSTORESUPPORTEDCURRENCY
(
                UIDPK BIGINT NOT NULL,
                CURRENCY NVARCHAR (255) NOT NULL,
                STORE_UID BIGINT NOT NULL,

    CONSTRAINT TSTORESUPPORTEDCURRENCY_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_STORESUPCURR_STORE_UID ON TSTORESUPPORTEDCURRENCY (STORE_UID);




/* ---------------------------------------------------------------------- */
/* TSTORESUPPORTEDLOCALE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TSTORESUPPORTEDLOCALE_FK_1')
    ALTER TABLE TSTORESUPPORTEDLOCALE DROP CONSTRAINT TSTORESUPPORTEDLOCALE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSTORESUPPORTEDLOCALE')
BEGIN
     DECLARE @reftable_116 nvarchar(60), @constraintname_116 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSTORESUPPORTEDLOCALE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_116, @constraintname_116
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_116+' drop constraint '+@constraintname_116)
       FETCH NEXT from refcursor into @reftable_116, @constraintname_116
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSTORESUPPORTEDLOCALE
END
;

CREATE TABLE TSTORESUPPORTEDLOCALE
(
                UIDPK BIGINT NOT NULL,
                LOCALE NVARCHAR (255) NOT NULL,
                STORE_UID BIGINT NOT NULL,

    CONSTRAINT TSTORESUPPORTEDLOCALE_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_STORESUPLOC_STORE_UID ON TSTORESUPPORTEDLOCALE (STORE_UID);




/* ---------------------------------------------------------------------- */
/* TINDEXBUILDSTATUS                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TINDEXBUILDSTATUS')
BEGIN
     DECLARE @reftable_117 nvarchar(60), @constraintname_117 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TINDEXBUILDSTATUS'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_117, @constraintname_117
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_117+' drop constraint '+@constraintname_117)
       FETCH NEXT from refcursor into @reftable_117, @constraintname_117
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TINDEXBUILDSTATUS
END
;

CREATE TABLE TINDEXBUILDSTATUS
(
                UIDPK BIGINT NOT NULL,
                INDEX_TYPE NVARCHAR (100) NOT NULL,
                LAST_BUILD_DATE DATETIME NULL,
                INDEX_STATUS NVARCHAR (100) default 'MISSING' NOT NULL,
                TOTAL_RECORDS INT default -1 NULL,
                PROCESSED_RECORDS INT default -1 NULL,
                OPERATION_START_DATE DATETIME NULL,
                LAST_MODIFIED_DATE DATETIME default CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT TINDEXBUILDSTATUS_PK PRIMARY KEY(UIDPK));





/* ---------------------------------------------------------------------- */
/* TPRICELIST                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TPRICELIST')
BEGIN
     DECLARE @reftable_118 nvarchar(60), @constraintname_118 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TPRICELIST'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_118, @constraintname_118
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_118+' drop constraint '+@constraintname_118)
       FETCH NEXT from refcursor into @reftable_118, @constraintname_118
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TPRICELIST
END
;

CREATE TABLE TPRICELIST
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                CURRENCY NVARCHAR (255) NOT NULL,
                DESCRIPTION NTEXT NULL,
                HIDDEN INT default 0 NULL,

    CONSTRAINT TPRICELIST_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TPRICELIST_GUID_UNIQUE UNIQUE (GUID),
    CONSTRAINT TPRICELIST_NAME_UNIQUE UNIQUE (NAME));





/* ---------------------------------------------------------------------- */
/* TBASEAMOUNT                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TBASEAMOUNT_FK_1')
    ALTER TABLE TBASEAMOUNT DROP CONSTRAINT TBASEAMOUNT_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TBASEAMOUNT')
BEGIN
     DECLARE @reftable_119 nvarchar(60), @constraintname_119 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TBASEAMOUNT'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_119, @constraintname_119
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_119+' drop constraint '+@constraintname_119)
       FETCH NEXT from refcursor into @reftable_119, @constraintname_119
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TBASEAMOUNT
END
;

CREATE TABLE TBASEAMOUNT
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                OBJECT_GUID NVARCHAR (64) NOT NULL,
                OBJECT_TYPE NVARCHAR (100) NOT NULL,
                QUANTITY DECIMAL (19,2) NOT NULL,
                LIST DECIMAL (19,2) NULL,
                SALE DECIMAL (19,2) NULL,
                PRICE_LIST_GUID NVARCHAR (64) NOT NULL,

    CONSTRAINT TBASEAMOUNT_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TBASEAMOUNT_UNIQUE UNIQUE (OBJECT_GUID, OBJECT_TYPE, QUANTITY, PRICE_LIST_GUID),
    CONSTRAINT TBASEAMOUNT_GUID_UNIQUE UNIQUE (GUID));

CREATE  INDEX I_TBASEAMOUNT_FK_GUID ON TBASEAMOUNT (PRICE_LIST_GUID);
CREATE  INDEX I_TBASEAMOUNT_OBJECTS ON TBASEAMOUNT (PRICE_LIST_GUID, OBJECT_GUID);




/* ---------------------------------------------------------------------- */
/* TOBJECTGROUPMEMBER                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TOBJECTGROUPMEMBER')
BEGIN
     DECLARE @reftable_120 nvarchar(60), @constraintname_120 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TOBJECTGROUPMEMBER'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_120, @constraintname_120
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_120+' drop constraint '+@constraintname_120)
       FETCH NEXT from refcursor into @reftable_120, @constraintname_120
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TOBJECTGROUPMEMBER
END
;

CREATE TABLE TOBJECTGROUPMEMBER
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                OBJECT_GROUP_ID NVARCHAR (64) NOT NULL,
                OBJECT_TYPE NVARCHAR (100) NOT NULL,
                OBJECT_IDENTIFIER NVARCHAR (100) NOT NULL,

    CONSTRAINT TOBJECTGROUPMEMBER_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TOBJECTGROUP_UNIQUE UNIQUE (OBJECT_GROUP_ID, OBJECT_TYPE, OBJECT_IDENTIFIER),
    CONSTRAINT TOBJECTGROUP_GUID_UNIQUE UNIQUE (GUID));





/* ---------------------------------------------------------------------- */
/* TCHANGESET                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCHANGESET')
BEGIN
     DECLARE @reftable_121 nvarchar(60), @constraintname_121 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCHANGESET'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_121, @constraintname_121
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_121+' drop constraint '+@constraintname_121)
       FETCH NEXT from refcursor into @reftable_121, @constraintname_121
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCHANGESET
END
;

CREATE TABLE TCHANGESET
(
                UIDPK BIGINT NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                DESCRIPTION NVARCHAR (255) NULL,
                OBJECT_GROUP_ID NVARCHAR (64) NOT NULL,
                CREATED_DATE DATETIME NOT NULL,
                CREATED_BY_USER_GUID NVARCHAR (64) NOT NULL,
                STATE_CODE NVARCHAR (64) NOT NULL,

    CONSTRAINT TCHANGESET_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TCHANGESET_GROUP_ID_UNIQUE UNIQUE (OBJECT_GROUP_ID));





/* ---------------------------------------------------------------------- */
/* TOBJECTMETADATA                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TOBJECTMETADATA_FK_1')
    ALTER TABLE TOBJECTMETADATA DROP CONSTRAINT TOBJECTMETADATA_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TOBJECTMETADATA')
BEGIN
     DECLARE @reftable_122 nvarchar(60), @constraintname_122 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TOBJECTMETADATA'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_122, @constraintname_122
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_122+' drop constraint '+@constraintname_122)
       FETCH NEXT from refcursor into @reftable_122, @constraintname_122
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TOBJECTMETADATA
END
;

CREATE TABLE TOBJECTMETADATA
(
                UIDPK BIGINT NOT NULL,
                OBJECT_GROUP_MEMBER_UID BIGINT NOT NULL,
                METADATA_KEY NVARCHAR (255) NOT NULL,
                METADATA_VALUE NVARCHAR (255) NOT NULL,

    CONSTRAINT TOBJECTMETADATA_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_TOBJECTMETADATA_MBR_UID ON TOBJECTMETADATA (OBJECT_GROUP_MEMBER_UID);




/* ---------------------------------------------------------------------- */
/* TCHANGESETUSER                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCHANGESETUSER_FK_1')
    ALTER TABLE TCHANGESETUSER DROP CONSTRAINT TCHANGESETUSER_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCHANGESETUSER')
BEGIN
     DECLARE @reftable_123 nvarchar(60), @constraintname_123 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCHANGESETUSER'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_123, @constraintname_123
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_123+' drop constraint '+@constraintname_123)
       FETCH NEXT from refcursor into @reftable_123, @constraintname_123
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCHANGESETUSER
END
;

CREATE TABLE TCHANGESETUSER
(
                UIDPK BIGINT NOT NULL,
                USER_GUID NVARCHAR (255) NOT NULL,
                CHANGESET_UID BIGINT NOT NULL,

    CONSTRAINT TCHANGESETUSER_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_TCHANGESETUSER_CS_UID ON TCHANGESETUSER (CHANGESET_UID);
CREATE  INDEX I_TCHANGESETUSER_USER_GUID ON TCHANGESETUSER (USER_GUID);




/* ---------------------------------------------------------------------- */
/* TTAGDICTIONARY                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TTAGDICTIONARY')
BEGIN
     DECLARE @reftable_124 nvarchar(60), @constraintname_124 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TTAGDICTIONARY'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_124, @constraintname_124
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_124+' drop constraint '+@constraintname_124)
       FETCH NEXT from refcursor into @reftable_124, @constraintname_124
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TTAGDICTIONARY
END
;

CREATE TABLE TTAGDICTIONARY
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                PURPOSE NVARCHAR (255) NOT NULL,

    CONSTRAINT TTAGDICTIONARY_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TAGDICTIONARY_UNIQUE UNIQUE (GUID));





/* ---------------------------------------------------------------------- */
/* TTAGCONDITION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TTAGCONDITION')
BEGIN
     DECLARE @reftable_125 nvarchar(60), @constraintname_125 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TTAGCONDITION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_125, @constraintname_125
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_125+' drop constraint '+@constraintname_125)
       FETCH NEXT from refcursor into @reftable_125, @constraintname_125
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TTAGCONDITION
END
;

CREATE TABLE TTAGCONDITION
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                DESCRIPTION NVARCHAR (4000) NULL,
                CONDITION_STRING NVARCHAR (4000) NOT NULL,
                TAGDICTIONARY_GUID NVARCHAR (64) NULL,
                NAMED INT default 0 NOT NULL,

    CONSTRAINT TTAGCONDITION_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TAGCONDITION_UNIQUE UNIQUE (GUID));

CREATE  INDEX I_TAGDICTIONARY_FK ON TTAGCONDITION (TAGDICTIONARY_GUID);




/* ---------------------------------------------------------------------- */
/* TSELLINGCONTEXTCONDITION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_SELLCOND_SELLCTX')
    ALTER TABLE TSELLINGCONTEXTCONDITION DROP CONSTRAINT FK_SELLCOND_SELLCTX;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSELLINGCONTEXTCONDITION')
BEGIN
     DECLARE @reftable_126 nvarchar(60), @constraintname_126 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSELLINGCONTEXTCONDITION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_126, @constraintname_126
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_126+' drop constraint '+@constraintname_126)
       FETCH NEXT from refcursor into @reftable_126, @constraintname_126
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSELLINGCONTEXTCONDITION
END
;

CREATE TABLE TSELLINGCONTEXTCONDITION
(
                SELLING_CONTEXT_UID BIGINT NOT NULL,
                CONDITION_GUID NVARCHAR (64) NOT NULL,
);





/* ---------------------------------------------------------------------- */
/* TCSDYNAMICCONTENT                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCSDYNAMICCONTENT')
BEGIN
     DECLARE @reftable_127 nvarchar(60), @constraintname_127 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCSDYNAMICCONTENT'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_127, @constraintname_127
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_127+' drop constraint '+@constraintname_127)
       FETCH NEXT from refcursor into @reftable_127, @constraintname_127
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCSDYNAMICCONTENT
END
;

CREATE TABLE TCSDYNAMICCONTENT
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                CONTENT_WRAPPER_ID NVARCHAR (255) NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                DESCRIPTION NVARCHAR (4000) NULL,

    CONSTRAINT TCSDYNAMICCONTENT_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TCSDYNAMICCONTENT_UNIQUE UNIQUE (GUID),
    CONSTRAINT TCSDYNAMICCONTENT_UNIQUE_NAME UNIQUE (NAME));





/* ---------------------------------------------------------------------- */
/* TCSDYNAMICCONTENTDELIVERY                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_DELIVERY_DCONTENT')
    ALTER TABLE TCSDYNAMICCONTENTDELIVERY DROP CONSTRAINT FK_DELIVERY_DCONTENT;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_SELLING_CONTEXT')
    ALTER TABLE TCSDYNAMICCONTENTDELIVERY DROP CONSTRAINT FK_SELLING_CONTEXT;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCSDYNAMICCONTENTDELIVERY')
BEGIN
     DECLARE @reftable_128 nvarchar(60), @constraintname_128 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCSDYNAMICCONTENTDELIVERY'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_128, @constraintname_128
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_128+' drop constraint '+@constraintname_128)
       FETCH NEXT from refcursor into @reftable_128, @constraintname_128
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCSDYNAMICCONTENTDELIVERY
END
;

CREATE TABLE TCSDYNAMICCONTENTDELIVERY
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                DESCRIPTION NVARCHAR (255) NULL,
                PRIORITY INT NOT NULL,
                CSDC_CONTENT_UID BIGINT NOT NULL,
                SELLING_CONTEXT_GUID NVARCHAR (64) NULL,

    CONSTRAINT TCSDYNAMICCONTENTDELIVERY_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TCSDCA_UNIQUE UNIQUE (GUID));





/* ---------------------------------------------------------------------- */
/* TCSCONTENTSPACE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCSCONTENTSPACE')
BEGIN
     DECLARE @reftable_129 nvarchar(60), @constraintname_129 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCSCONTENTSPACE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_129, @constraintname_129
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_129+' drop constraint '+@constraintname_129)
       FETCH NEXT from refcursor into @reftable_129, @constraintname_129
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCSCONTENTSPACE
END
;

CREATE TABLE TCSCONTENTSPACE
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                TARGET_ID NVARCHAR (255) NOT NULL,
                DESCRIPTION NVARCHAR (255) NULL,

    CONSTRAINT TCSCONTENTSPACE_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TCSCONTENTSPACE_UNIQUE UNIQUE (GUID),
    CONSTRAINT TCSCONTENTSPACE_UNIQUE_TARGET UNIQUE (TARGET_ID));





/* ---------------------------------------------------------------------- */
/* TCSPARAMETERVALUE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_PARAMVAL_DCONTENT')
    ALTER TABLE TCSPARAMETERVALUE DROP CONSTRAINT FK_PARAMVAL_DCONTENT;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCSPARAMETERVALUE')
BEGIN
     DECLARE @reftable_130 nvarchar(60), @constraintname_130 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCSPARAMETERVALUE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_130, @constraintname_130
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_130+' drop constraint '+@constraintname_130)
       FETCH NEXT from refcursor into @reftable_130, @constraintname_130
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCSPARAMETERVALUE
END
;

CREATE TABLE TCSPARAMETERVALUE
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                CSDYNAMICCONTENT_UID BIGINT NOT NULL,
                PARAMETER_NAME NVARCHAR (255) NOT NULL,
                LOCALIZABLE INT default 0 NOT NULL,
                DESCRIPTION NVARCHAR (4000) NULL,

    CONSTRAINT TCSPARAMETERVALUE_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TCSPARAMETERVALUE_UNIQUE UNIQUE (GUID));





/* ---------------------------------------------------------------------- */
/* TCSPARAMETERVALUELDF                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_PARAMVALLOCALE_PARAMVAL')
    ALTER TABLE TCSPARAMETERVALUELDF DROP CONSTRAINT FK_PARAMVALLOCALE_PARAMVAL;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCSPARAMETERVALUELDF')
BEGIN
     DECLARE @reftable_131 nvarchar(60), @constraintname_131 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCSPARAMETERVALUELDF'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_131, @constraintname_131
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_131+' drop constraint '+@constraintname_131)
       FETCH NEXT from refcursor into @reftable_131, @constraintname_131
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCSPARAMETERVALUELDF
END
;

CREATE TABLE TCSPARAMETERVALUELDF
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                LOCALE NVARCHAR (20) NOT NULL,
                LDVALUE NVARCHAR (4000) NOT NULL,
                CSPARAMETERVALUE_UID BIGINT NOT NULL,

    CONSTRAINT TCSPARAMETERVALUELDF_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TCSPARAMETERVALUELDF_UNIQUE UNIQUE (GUID));





/* ---------------------------------------------------------------------- */
/* TCSDYNAMICCONTENTSPACE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_DELSPACE_DELIVERY')
    ALTER TABLE TCSDYNAMICCONTENTSPACE DROP CONSTRAINT FK_DELSPACE_DELIVERY;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_DELSPACE_CONTENTSPACE')
    ALTER TABLE TCSDYNAMICCONTENTSPACE DROP CONSTRAINT FK_DELSPACE_CONTENTSPACE;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCSDYNAMICCONTENTSPACE')
BEGIN
     DECLARE @reftable_132 nvarchar(60), @constraintname_132 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCSDYNAMICCONTENTSPACE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_132, @constraintname_132
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_132+' drop constraint '+@constraintname_132)
       FETCH NEXT from refcursor into @reftable_132, @constraintname_132
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCSDYNAMICCONTENTSPACE
END
;

CREATE TABLE TCSDYNAMICCONTENTSPACE
(
                DC_DELIVERY_UID BIGINT NOT NULL,
                DC_CONTENTSPACE_UID BIGINT NOT NULL,

    CONSTRAINT TCSDYNAMICCONTENTSPACE_CK_1 UNIQUE (DC_DELIVERY_UID, DC_CONTENTSPACE_UID));





/* ---------------------------------------------------------------------- */
/* TCHANGETRANSACTION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCHANGETRANSACTION')
BEGIN
     DECLARE @reftable_133 nvarchar(60), @constraintname_133 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCHANGETRANSACTION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_133, @constraintname_133
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_133+' drop constraint '+@constraintname_133)
       FETCH NEXT from refcursor into @reftable_133, @constraintname_133
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCHANGETRANSACTION
END
;

CREATE TABLE TCHANGETRANSACTION
(
                UIDPK BIGINT NOT NULL,
                TRANSACTION_ID NVARCHAR (255) NOT NULL,
                CHANGE_DATE DATETIME NOT NULL,

    CONSTRAINT TCHANGETRANSACTION_PK PRIMARY KEY(UIDPK));





/* ---------------------------------------------------------------------- */
/* TCHANGETRANSACTIONMETADATA                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCHANGETRANSACTIONMETADAT_FK_1')
    ALTER TABLE TCHANGETRANSACTIONMETADATA DROP CONSTRAINT TCHANGETRANSACTIONMETADAT_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCHANGETRANSACTIONMETADATA')
BEGIN
     DECLARE @reftable_134 nvarchar(60), @constraintname_134 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCHANGETRANSACTIONMETADATA'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_134, @constraintname_134
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_134+' drop constraint '+@constraintname_134)
       FETCH NEXT from refcursor into @reftable_134, @constraintname_134
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCHANGETRANSACTIONMETADATA
END
;

CREATE TABLE TCHANGETRANSACTIONMETADATA
(
                UIDPK BIGINT NOT NULL,
                CHANGE_TRANSACTION_UID BIGINT NOT NULL,
                METADATA_KEY NVARCHAR (255) NOT NULL,
                METADATA_VALUE NVARCHAR (255) NOT NULL,

    CONSTRAINT TCHANGETRANSACTIONMETADATA_PK PRIMARY KEY(UIDPK));





/* ---------------------------------------------------------------------- */
/* TCHANGEOPERATION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCHANGEOPERATION_FK_1')
    ALTER TABLE TCHANGEOPERATION DROP CONSTRAINT TCHANGEOPERATION_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCHANGEOPERATION')
BEGIN
     DECLARE @reftable_135 nvarchar(60), @constraintname_135 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCHANGEOPERATION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_135, @constraintname_135
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_135+' drop constraint '+@constraintname_135)
       FETCH NEXT from refcursor into @reftable_135, @constraintname_135
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCHANGEOPERATION
END
;

CREATE TABLE TCHANGEOPERATION
(
                UIDPK BIGINT NOT NULL,
                OPERATION_ORDER INT NOT NULL,
                ROOT_OBJECT_NAME NVARCHAR (255) NULL,
                ROOT_OBJECT_UID BIGINT NULL,
                ROOT_OBJECT_GUID NVARCHAR (255) NULL,
                CHANGE_TYPE NVARCHAR (255) NOT NULL,
                CHANGE_TRANSACTION_UID BIGINT NOT NULL,
                QUERY_STRING NVARCHAR (1000) NULL,
                QUERY_PARAMETERS NVARCHAR (255) NULL,
                TYPE NVARCHAR (20) NULL,

    CONSTRAINT TCHANGEOPERATION_PK PRIMARY KEY(UIDPK));





/* ---------------------------------------------------------------------- */
/* TTAGVALUETYPE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TTAGVALUETYPE')
BEGIN
     DECLARE @reftable_136 nvarchar(60), @constraintname_136 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TTAGVALUETYPE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_136, @constraintname_136
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_136+' drop constraint '+@constraintname_136)
       FETCH NEXT from refcursor into @reftable_136, @constraintname_136
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TTAGVALUETYPE
END
;

CREATE TABLE TTAGVALUETYPE
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                JAVA_TYPE NVARCHAR (50) NULL,
                UI_PICKER_KEY NVARCHAR (50) NULL,

    CONSTRAINT TTAGVALUETYPE_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TAGVALUETYPE_UNIQUE UNIQUE (GUID));





/* ---------------------------------------------------------------------- */
/* TTAGGROUP                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TTAGGROUP')
BEGIN
     DECLARE @reftable_137 nvarchar(60), @constraintname_137 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TTAGGROUP'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_137, @constraintname_137
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_137+' drop constraint '+@constraintname_137)
       FETCH NEXT from refcursor into @reftable_137, @constraintname_137
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TTAGGROUP
END
;

CREATE TABLE TTAGGROUP
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,

    CONSTRAINT TTAGGROUP_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TAGGROUP_UNIQUE UNIQUE (GUID));





/* ---------------------------------------------------------------------- */
/* TTAGDEFINITION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_TTAGVALUETYPE')
    ALTER TABLE TTAGDEFINITION DROP CONSTRAINT FK_TTAGVALUETYPE;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_TTAGGROUP')
    ALTER TABLE TTAGDEFINITION DROP CONSTRAINT FK_TTAGGROUP;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TTAGDEFINITION')
BEGIN
     DECLARE @reftable_138 nvarchar(60), @constraintname_138 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TTAGDEFINITION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_138, @constraintname_138
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_138+' drop constraint '+@constraintname_138)
       FETCH NEXT from refcursor into @reftable_138, @constraintname_138
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TTAGDEFINITION
END
;

CREATE TABLE TTAGDEFINITION
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                DESCRIPTION NVARCHAR (255) NULL,
                TAGVALUETYPE_GUID NVARCHAR (64) NOT NULL,
                TAGGROUP_UID BIGINT NULL,

    CONSTRAINT TTAGDEFINITION_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TAGDEFINITION_UNIQUE UNIQUE (GUID));





/* ---------------------------------------------------------------------- */
/* TTAGALLOWEDVALUE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_TAGALLOWEDVAL_TAGTYPE')
    ALTER TABLE TTAGALLOWEDVALUE DROP CONSTRAINT FK_TAGALLOWEDVAL_TAGTYPE;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TTAGALLOWEDVALUE')
BEGIN
     DECLARE @reftable_139 nvarchar(60), @constraintname_139 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TTAGALLOWEDVALUE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_139, @constraintname_139
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_139+' drop constraint '+@constraintname_139)
       FETCH NEXT from refcursor into @reftable_139, @constraintname_139
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TTAGALLOWEDVALUE
END
;

CREATE TABLE TTAGALLOWEDVALUE
(
                UIDPK BIGINT NOT NULL,
                VALUE NVARCHAR (255) NOT NULL,
                TAGVALUETYPE_GUID NVARCHAR (64) NOT NULL,
                DESCRIPTION NVARCHAR (4000) NULL,
                ORDERING INT default 0 NOT NULL,

    CONSTRAINT TTAGALLOWEDVALUE_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TAGALLOWEDVALUE_UNIQUE UNIQUE (VALUE, TAGVALUETYPE_GUID),
    CONSTRAINT TAGALLOWEDVALUE_UNIQUE2 UNIQUE (ORDERING, TAGVALUETYPE_GUID));





/* ---------------------------------------------------------------------- */
/* TDATACHANGED                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TDATACHANGED_FK_1')
    ALTER TABLE TDATACHANGED DROP CONSTRAINT TDATACHANGED_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TDATACHANGED')
BEGIN
     DECLARE @reftable_140 nvarchar(60), @constraintname_140 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TDATACHANGED'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_140, @constraintname_140
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_140+' drop constraint '+@constraintname_140)
       FETCH NEXT from refcursor into @reftable_140, @constraintname_140
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TDATACHANGED
END
;

CREATE TABLE TDATACHANGED
(
                UIDPK BIGINT NOT NULL,
                CHANGE_TYPE NVARCHAR (255) NOT NULL,
                FIELD_NAME NVARCHAR (255) NULL,
                FIELD_OLD_VALUE NTEXT NULL,
                FIELD_NEW_VALUE NTEXT NULL,
                OBJECT_NAME NVARCHAR (255) NULL,
                OBJECT_UID BIGINT NULL,
                OBJECT_GUID NVARCHAR (255) NULL,
                CHANGE_OPERATION_UID BIGINT NOT NULL,

    CONSTRAINT TDATACHANGED_PK PRIMARY KEY(UIDPK));





/* ---------------------------------------------------------------------- */
/* TTAGDICTIONARYTAGDEFINITION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TTAGDICTIONARYTAGDEFINITION')
BEGIN
     DECLARE @reftable_141 nvarchar(60), @constraintname_141 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TTAGDICTIONARYTAGDEFINITION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_141, @constraintname_141
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_141+' drop constraint '+@constraintname_141)
       FETCH NEXT from refcursor into @reftable_141, @constraintname_141
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TTAGDICTIONARYTAGDEFINITION
END
;

CREATE TABLE TTAGDICTIONARYTAGDEFINITION
(
                TAGDICTIONARY_GUID NVARCHAR (64) NOT NULL,
                TAGDEFINITION_GUID NVARCHAR (64) NOT NULL,

    CONSTRAINT TAGDICTIONARYTAGDEF_UNIQUE UNIQUE (TAGDICTIONARY_GUID, TAGDEFINITION_GUID));





/* ---------------------------------------------------------------------- */
/* TTAGOPERATOR                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TTAGOPERATOR')
BEGIN
     DECLARE @reftable_142 nvarchar(60), @constraintname_142 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TTAGOPERATOR'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_142, @constraintname_142
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_142+' drop constraint '+@constraintname_142)
       FETCH NEXT from refcursor into @reftable_142, @constraintname_142
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TTAGOPERATOR
END
;

CREATE TABLE TTAGOPERATOR
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,

    CONSTRAINT TTAGOPERATOR_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TAGOPERATOR_UNIQUE UNIQUE (GUID));





/* ---------------------------------------------------------------------- */
/* TTAGVALUETYPEOPERATOR                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_TAGOPERATOR_TAGVALUETYPE')
    ALTER TABLE TTAGVALUETYPEOPERATOR DROP CONSTRAINT FK_TAGOPERATOR_TAGVALUETYPE;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_TAGVALUETYPE_TAGOPERATOR')
    ALTER TABLE TTAGVALUETYPEOPERATOR DROP CONSTRAINT FK_TAGVALUETYPE_TAGOPERATOR;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TTAGVALUETYPEOPERATOR')
BEGIN
     DECLARE @reftable_143 nvarchar(60), @constraintname_143 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TTAGVALUETYPEOPERATOR'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_143, @constraintname_143
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_143+' drop constraint '+@constraintname_143)
       FETCH NEXT from refcursor into @reftable_143, @constraintname_143
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TTAGVALUETYPEOPERATOR
END
;

CREATE TABLE TTAGVALUETYPEOPERATOR
(
                TAGVALUETYPE_GUID NVARCHAR (64) NOT NULL,
                TAGOPERATOR_GUID NVARCHAR (64) NOT NULL,

    CONSTRAINT TTAGVALUETYPEOPERATOR_UNIQUE UNIQUE (TAGVALUETYPE_GUID, TAGOPERATOR_GUID));





/* ---------------------------------------------------------------------- */
/* TVALIDATIONCONSTRAINTS                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TVALIDATIONCONSTRAINTS')
BEGIN
     DECLARE @reftable_144 nvarchar(60), @constraintname_144 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TVALIDATIONCONSTRAINTS'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_144, @constraintname_144
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_144+' drop constraint '+@constraintname_144)
       FETCH NEXT from refcursor into @reftable_144, @constraintname_144
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TVALIDATIONCONSTRAINTS
END
;

CREATE TABLE TVALIDATIONCONSTRAINTS
(
                UIDPK BIGINT NOT NULL,
                OBJECT_UID BIGINT NULL,
                ERROR_MESSAGE_KEY NVARCHAR (255) NOT NULL,
                VALIDATION_CONSTRAINT NVARCHAR (4000) NOT NULL,
                TYPE NVARCHAR (31) NOT NULL,

    CONSTRAINT TVALIDATIONCONSTRAINTS_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_VC_OBJECT_UID ON TVALIDATIONCONSTRAINTS (OBJECT_UID);




/* ---------------------------------------------------------------------- */
/* TBUNDLECONSTITUENTX                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TBCX_TPROD_FK')
    ALTER TABLE TBUNDLECONSTITUENTX DROP CONSTRAINT TBCX_TPROD_FK;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TBCX_TPROD_C_FK')
    ALTER TABLE TBUNDLECONSTITUENTX DROP CONSTRAINT TBCX_TPROD_C_FK;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TBCX_TSKU_C_FK')
    ALTER TABLE TBUNDLECONSTITUENTX DROP CONSTRAINT TBCX_TSKU_C_FK;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TBUNDLECONSTITUENTX')
BEGIN
     DECLARE @reftable_145 nvarchar(60), @constraintname_145 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TBUNDLECONSTITUENTX'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_145, @constraintname_145
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_145+' drop constraint '+@constraintname_145)
       FETCH NEXT from refcursor into @reftable_145, @constraintname_145
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TBUNDLECONSTITUENTX
END
;

CREATE TABLE TBUNDLECONSTITUENTX
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                BUNDLE_UID BIGINT NOT NULL,
                CONSTITUENT_UID BIGINT NULL,
                CONSTITUENT_SKU_UID BIGINT NULL,
                QUANTITY INT default 1 NOT NULL,
                ORDERING INT default 0 NOT NULL,

    CONSTRAINT TBUNDLECONSTITUENTX_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TBCX_GUID_UNIQUE UNIQUE (GUID));

CREATE  INDEX I_BCX_BUNDLE_UID ON TBUNDLECONSTITUENTX (BUNDLE_UID);
CREATE  INDEX I_BCX_CONSTITUENT_UID ON TBUNDLECONSTITUENTX (CONSTITUENT_UID);
CREATE  INDEX I_BCX_CONSTITUENT_SKU_UID ON TBUNDLECONSTITUENTX (CONSTITUENT_SKU_UID);




/* ---------------------------------------------------------------------- */
/* TPRICEADJUSTMENT                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_TPRICEADJUST_CONST_GUID')
    ALTER TABLE TPRICEADJUSTMENT DROP CONSTRAINT FK_TPRICEADJUST_CONST_GUID;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TPRICEADJUSTMENT')
BEGIN
     DECLARE @reftable_146 nvarchar(60), @constraintname_146 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TPRICEADJUSTMENT'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_146, @constraintname_146
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_146+' drop constraint '+@constraintname_146)
       FETCH NEXT from refcursor into @reftable_146, @constraintname_146
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TPRICEADJUSTMENT
END
;

CREATE TABLE TPRICEADJUSTMENT
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                CONSTITUENT_GUID NVARCHAR (64) NOT NULL,
                AMOUNT DECIMAL (19,2) NULL,
                PRICE_LIST_GUID NVARCHAR (64) NOT NULL,

    CONSTRAINT TPRICEADJUSTMENT_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TPRICEADJUSTMENT_GUID_UNIQUE UNIQUE (GUID));

CREATE  INDEX I_TPRICEADJUSTMENT_FK_PL_GUID ON TPRICEADJUSTMENT (PRICE_LIST_GUID);
CREATE  INDEX I_TPRICEADJUSTMENT_FK_BCX_GUID ON TPRICEADJUSTMENT (CONSTITUENT_GUID);
CREATE  INDEX I_TPRICEADJUSTMENT_KEYS ON TPRICEADJUSTMENT (PRICE_LIST_GUID, CONSTITUENT_GUID);




/* ---------------------------------------------------------------------- */
/* TBUNDLESELECTIONRULE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TBSR_TPROD_FK')
    ALTER TABLE TBUNDLESELECTIONRULE DROP CONSTRAINT TBSR_TPROD_FK;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TBUNDLESELECTIONRULE')
BEGIN
     DECLARE @reftable_147 nvarchar(60), @constraintname_147 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TBUNDLESELECTIONRULE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_147, @constraintname_147
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_147+' drop constraint '+@constraintname_147)
       FETCH NEXT from refcursor into @reftable_147, @constraintname_147
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TBUNDLESELECTIONRULE
END
;

CREATE TABLE TBUNDLESELECTIONRULE
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                PARAMETER INT NOT NULL,
                BUNDLE_UID BIGINT NOT NULL,

    CONSTRAINT TBUNDLESELECTIONRULE_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_BSR_BUNDLE_UID ON TBUNDLESELECTIONRULE (BUNDLE_UID);




/* ---------------------------------------------------------------------- */
/* TSHOPPINGITEMDATA                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCARTITEM_FK')
    ALTER TABLE TSHOPPINGITEMDATA DROP CONSTRAINT TCARTITEM_FK;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSHOPPINGITEMDATA')
BEGIN
     DECLARE @reftable_148 nvarchar(60), @constraintname_148 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSHOPPINGITEMDATA'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_148, @constraintname_148
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_148+' drop constraint '+@constraintname_148)
       FETCH NEXT from refcursor into @reftable_148, @constraintname_148
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSHOPPINGITEMDATA
END
;

CREATE TABLE TSHOPPINGITEMDATA
(
                UIDPK BIGINT NOT NULL,
                ITEM_KEY NTEXT NOT NULL,
                ITEM_VALUE NTEXT NULL,
                CARTITEM_UID BIGINT NOT NULL,

    CONSTRAINT TSHOPPINGITEMDATA_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_CARTITEM_UID ON TSHOPPINGITEMDATA (CARTITEM_UID);




/* ---------------------------------------------------------------------- */
/* TORDERITEMDATA                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TORDERSKU_FK')
    ALTER TABLE TORDERITEMDATA DROP CONSTRAINT TORDERSKU_FK;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TORDERITEMDATA')
BEGIN
     DECLARE @reftable_149 nvarchar(60), @constraintname_149 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TORDERITEMDATA'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_149, @constraintname_149
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_149+' drop constraint '+@constraintname_149)
       FETCH NEXT from refcursor into @reftable_149, @constraintname_149
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TORDERITEMDATA
END
;

CREATE TABLE TORDERITEMDATA
(
                UIDPK BIGINT NOT NULL,
                ITEM_KEY NTEXT NOT NULL,
                ITEM_VALUE NTEXT NULL,
                ORDERSKU_UID BIGINT NOT NULL,

    CONSTRAINT TORDERITEMDATA_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_ORDERSKU_UID ON TORDERITEMDATA (ORDERSKU_UID);




/* ---------------------------------------------------------------------- */
/* TPRICELISTASSIGNMENT                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='CATPLA_FK')
    ALTER TABLE TPRICELISTASSIGNMENT DROP CONSTRAINT CATPLA_FK;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='PLDPLA_FK')
    ALTER TABLE TPRICELISTASSIGNMENT DROP CONSTRAINT PLDPLA_FK;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='SCPLA_FK')
    ALTER TABLE TPRICELISTASSIGNMENT DROP CONSTRAINT SCPLA_FK;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TPRICELISTASSIGNMENT')
BEGIN
     DECLARE @reftable_150 nvarchar(60), @constraintname_150 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TPRICELISTASSIGNMENT'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_150, @constraintname_150
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_150+' drop constraint '+@constraintname_150)
       FETCH NEXT from refcursor into @reftable_150, @constraintname_150
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TPRICELISTASSIGNMENT
END
;

CREATE TABLE TPRICELISTASSIGNMENT
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                DESCRIPTION NVARCHAR (4000) NULL,
                PRIORITY INT default 1 NOT NULL,
                CATALOG_UID BIGINT NOT NULL,
                PRLISTDSCR_UID BIGINT NOT NULL,
                SELLING_CTX_UID BIGINT NULL,
                HIDDEN INT default 0 NULL,

    CONSTRAINT TPRICELISTASSIGNMENT_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TPLA_GUID_UNIQUE UNIQUE (GUID),
    CONSTRAINT TPLA_NAME_UNIQUE UNIQUE (NAME));

CREATE  INDEX I_CATALOG_UID ON TPRICELISTASSIGNMENT (CATALOG_UID);
CREATE  INDEX I_PRLISTDSCR_UID ON TPRICELISTASSIGNMENT (PRLISTDSCR_UID);




/* ---------------------------------------------------------------------- */
/* TIMPORTNOTIFICATION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='IN_IMPORTJOB_FK')
    ALTER TABLE TIMPORTNOTIFICATION DROP CONSTRAINT IN_IMPORTJOB_FK;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='IN_CMUSER_FK')
    ALTER TABLE TIMPORTNOTIFICATION DROP CONSTRAINT IN_CMUSER_FK;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TIMPORTNOTIFICATION')
BEGIN
     DECLARE @reftable_151 nvarchar(60), @constraintname_151 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TIMPORTNOTIFICATION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_151, @constraintname_151
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_151+' drop constraint '+@constraintname_151)
       FETCH NEXT from refcursor into @reftable_151, @constraintname_151
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TIMPORTNOTIFICATION
END
;

CREATE TABLE TIMPORTNOTIFICATION
(
                UIDPK BIGINT NOT NULL,
                REQUEST_ID NVARCHAR (64) NOT NULL,
                ACTION NVARCHAR (64) NOT NULL,
                IMPORT_JOB_UID BIGINT NOT NULL,
                DATE_CREATED DATETIME default CURRENT_TIMESTAMP NOT NULL,
                CMUSER_UID BIGINT NOT NULL,
                NOTIFICATION_STATE NVARCHAR (64) NOT NULL,
                CHANGESET_GUID NVARCHAR (64) NULL,

    CONSTRAINT TIMPORTNOTIFICATION_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_IN_IMPORTJOB_FK ON TIMPORTNOTIFICATION (IMPORT_JOB_UID);
CREATE  INDEX I_IN_CMUSER_FK ON TIMPORTNOTIFICATION (CMUSER_UID);




/* ---------------------------------------------------------------------- */
/* TIMPORTNOTIFICATIONMETADATA                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='INM_IMPORTNOTIFICATION_FK')
    ALTER TABLE TIMPORTNOTIFICATIONMETADATA DROP CONSTRAINT INM_IMPORTNOTIFICATION_FK;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TIMPORTNOTIFICATIONMETADATA')
BEGIN
     DECLARE @reftable_152 nvarchar(60), @constraintname_152 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TIMPORTNOTIFICATIONMETADATA'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_152, @constraintname_152
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_152+' drop constraint '+@constraintname_152)
       FETCH NEXT from refcursor into @reftable_152, @constraintname_152
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TIMPORTNOTIFICATIONMETADATA
END
;

CREATE TABLE TIMPORTNOTIFICATIONMETADATA
(
                UIDPK BIGINT NOT NULL,
                METADATA_KEY NVARCHAR (255) NULL,
                METADATA_VALUE NVARCHAR (255) NULL,
                IMPORT_NOTIFICATION_UID BIGINT NOT NULL,

    CONSTRAINT TIMPORTNOTIFICATIONMETADATA_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_INM_IMPORTNOTIFICATION_FK ON TIMPORTNOTIFICATIONMETADATA (IMPORT_NOTIFICATION_UID);




/* ---------------------------------------------------------------------- */
/* TIMPORTJOBSTATUS                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='IJS_IMPORTJOB_FK')
    ALTER TABLE TIMPORTJOBSTATUS DROP CONSTRAINT IJS_IMPORTJOB_FK;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='IJS_CMUSER_FK')
    ALTER TABLE TIMPORTJOBSTATUS DROP CONSTRAINT IJS_CMUSER_FK;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TIMPORTJOBSTATUS')
BEGIN
     DECLARE @reftable_153 nvarchar(60), @constraintname_153 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TIMPORTJOBSTATUS'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_153, @constraintname_153
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_153+' drop constraint '+@constraintname_153)
       FETCH NEXT from refcursor into @reftable_153, @constraintname_153
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TIMPORTJOBSTATUS
END
;

CREATE TABLE TIMPORTJOBSTATUS
(
                UIDPK BIGINT NOT NULL,
                PROCESS_ID NVARCHAR (64) NOT NULL,
                IMPORT_JOB_UID BIGINT NOT NULL,
                CMUSER_UID BIGINT NOT NULL,
                START_DATE DATETIME NULL,
                END_DATE DATETIME NULL,
                TOTAL_ROWS INT NULL,
                FAILED_ROWS INT NULL,
                CURRENT_ROW INT NULL,
                STATE NVARCHAR (64) NOT NULL,
                LAST_MODIFIED_DATE DATETIME default CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT TIMPORTJOBSTATUS_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_IJS_IMPORTJOB_FK ON TIMPORTJOBSTATUS (IMPORT_JOB_UID);
CREATE  INDEX I_IJS_CMUSER_FK ON TIMPORTJOBSTATUS (CMUSER_UID);




/* ---------------------------------------------------------------------- */
/* TIMPORTBADROW                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='IBR_IMPORTJOBSTATUS_FK')
    ALTER TABLE TIMPORTBADROW DROP CONSTRAINT IBR_IMPORTJOBSTATUS_FK;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TIMPORTBADROW')
BEGIN
     DECLARE @reftable_154 nvarchar(60), @constraintname_154 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TIMPORTBADROW'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_154, @constraintname_154
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_154+' drop constraint '+@constraintname_154)
       FETCH NEXT from refcursor into @reftable_154, @constraintname_154
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TIMPORTBADROW
END
;

CREATE TABLE TIMPORTBADROW
(
                UIDPK BIGINT NOT NULL,
                IMPORT_JOB_STATUS_UID BIGINT NOT NULL,
                ROW_NUMBER INT NOT NULL,
                ROW_DATA NTEXT NOT NULL,

    CONSTRAINT TIMPORTBADROW_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_IBR_IMPORTJOBSTATUS_FK ON TIMPORTBADROW (IMPORT_JOB_STATUS_UID);




/* ---------------------------------------------------------------------- */
/* TIMPORTFAULT                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='IF_IMPORTBADROW_FK')
    ALTER TABLE TIMPORTFAULT DROP CONSTRAINT IF_IMPORTBADROW_FK;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TIMPORTFAULT')
BEGIN
     DECLARE @reftable_155 nvarchar(60), @constraintname_155 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TIMPORTFAULT'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_155, @constraintname_155
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_155+' drop constraint '+@constraintname_155)
       FETCH NEXT from refcursor into @reftable_155, @constraintname_155
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TIMPORTFAULT
END
;

CREATE TABLE TIMPORTFAULT
(
                UIDPK BIGINT NOT NULL,
                IMPORT_BAD_ROW_UID BIGINT NOT NULL,
                LEVEL_NUMBER INT NULL,
                CODE NVARCHAR (255) NULL,
                SOURCE_MESSAGE NTEXT NULL,
                ARGS NVARCHAR (255) NULL,

    CONSTRAINT TIMPORTFAULT_PK PRIMARY KEY(UIDPK));

CREATE  INDEX I_IF_IMPORTBADROW_FK ON TIMPORTFAULT (IMPORT_BAD_ROW_UID);




/* ---------------------------------------------------------------------- */
/* TCMUSERPRICELIST                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCMUSERPRICELIST_FK_1')
    ALTER TABLE TCMUSERPRICELIST DROP CONSTRAINT TCMUSERPRICELIST_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCMUSERPRICELIST_FK_2')
    ALTER TABLE TCMUSERPRICELIST DROP CONSTRAINT TCMUSERPRICELIST_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCMUSERPRICELIST')
BEGIN
     DECLARE @reftable_156 nvarchar(60), @constraintname_156 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCMUSERPRICELIST'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_156, @constraintname_156
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_156+' drop constraint '+@constraintname_156)
       FETCH NEXT from refcursor into @reftable_156, @constraintname_156
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCMUSERPRICELIST
END
;

CREATE TABLE TCMUSERPRICELIST
(
                USER_UID BIGINT NOT NULL,
                PRICELIST_GUID NVARCHAR (64) NOT NULL,

    CONSTRAINT TCMUSERPRICELIST_PK PRIMARY KEY(USER_UID,PRICELIST_GUID));

CREATE  INDEX I_CMUSERPL_GUID ON TCMUSERPRICELIST (PRICELIST_GUID);
CREATE  INDEX I_CMUSERPL_USER_UID ON TCMUSERPRICELIST (USER_UID);




/* ---------------------------------------------------------------------- */
/* TCOUPONCONFIG                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCOUPONCONFIG_FK_1')
    ALTER TABLE TCOUPONCONFIG DROP CONSTRAINT TCOUPONCONFIG_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCOUPONCONFIG')
BEGIN
     DECLARE @reftable_157 nvarchar(60), @constraintname_157 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCOUPONCONFIG'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_157, @constraintname_157
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_157+' drop constraint '+@constraintname_157)
       FETCH NEXT from refcursor into @reftable_157, @constraintname_157
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCOUPONCONFIG
END
;

CREATE TABLE TCOUPONCONFIG
(
                UIDPK BIGINT NOT NULL,
                RULECODE NVARCHAR (64) NOT NULL,
                USAGE_LIMIT INT NOT NULL,
                USAGE_TYPE NVARCHAR (255) NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                LIMITED_DURATION INT default 0 NOT NULL,
                DURATION_DAYS INT NULL,
                MULTI_USE_PER_ORDER INT default 0 NOT NULL,

    CONSTRAINT TCOUPONCONFIG_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TCOUPONCONFIG_GUID_UNQ UNIQUE (GUID),
    CONSTRAINT TCOUPONCONFIG_RULECODE_UNQ UNIQUE (RULECODE));





/* ---------------------------------------------------------------------- */
/* TCOUPON                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCOUPON_FK_1')
    ALTER TABLE TCOUPON DROP CONSTRAINT TCOUPON_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCOUPON')
BEGIN
     DECLARE @reftable_158 nvarchar(60), @constraintname_158 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCOUPON'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_158, @constraintname_158
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_158+' drop constraint '+@constraintname_158)
       FETCH NEXT from refcursor into @reftable_158, @constraintname_158
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCOUPON
END
;

CREATE TABLE TCOUPON
(
                UIDPK BIGINT NOT NULL,
                COUPONCODE NVARCHAR (255) NOT NULL,
                COUPON_CONFIG_UID BIGINT NOT NULL,
                SUSPENDED INT default 0 NOT NULL,

    CONSTRAINT TCOUPON_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TCOUPON_RULE_COUPON_UNQ UNIQUE (COUPONCODE));





/* ---------------------------------------------------------------------- */
/* TCOUPONUSAGE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TCOUPONUSAGE_FK_1')
    ALTER TABLE TCOUPONUSAGE DROP CONSTRAINT TCOUPONUSAGE_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCOUPONUSAGE')
BEGIN
     DECLARE @reftable_159 nvarchar(60), @constraintname_159 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCOUPONUSAGE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_159, @constraintname_159
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_159+' drop constraint '+@constraintname_159)
       FETCH NEXT from refcursor into @reftable_159, @constraintname_159
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCOUPONUSAGE
END
;

CREATE TABLE TCOUPONUSAGE
(
                UIDPK BIGINT NOT NULL,
                COUPON_UID BIGINT NOT NULL,
                USECOUNT INT NOT NULL,
                ACTIVE_IN_CART INT default 0 NOT NULL,
                CUSTOMER_EMAIL_ADDRESS NVARCHAR (255) NOT NULL,
                LIMITED_DURATION_END_DATE DATETIME NULL,
                SUSPENDED INT default 0 NOT NULL,

    CONSTRAINT TCOUPONUSAGE_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TCOUPON_EMAIL_COUPON_UNQ UNIQUE (COUPON_UID, CUSTOMER_EMAIL_ADDRESS));





/* ---------------------------------------------------------------------- */
/* TWISHLIST                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_WISHLIST_SHOPPER')
    ALTER TABLE TWISHLIST DROP CONSTRAINT FK_WISHLIST_SHOPPER;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_STORE')
    ALTER TABLE TWISHLIST DROP CONSTRAINT FK_STORE;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TWISHLIST')
BEGIN
     DECLARE @reftable_160 nvarchar(60), @constraintname_160 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TWISHLIST'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_160, @constraintname_160
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_160+' drop constraint '+@constraintname_160)
       FETCH NEXT from refcursor into @reftable_160, @constraintname_160
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TWISHLIST
END
;

CREATE TABLE TWISHLIST
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (100) NOT NULL,
                STORECODE NVARCHAR (64) NULL,
                SHOPPER_UID BIGINT NOT NULL,

    CONSTRAINT TWISHLIST_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TWISHLIST_UNIQUE UNIQUE (GUID));

CREATE  INDEX I_TWISHLIST_SHOPPER_UID ON TWISHLIST (SHOPPER_UID);




/* ---------------------------------------------------------------------- */
/* TWISHLISTITEMS                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TWISHLISTITEMS_FK_1')
    ALTER TABLE TWISHLISTITEMS DROP CONSTRAINT TWISHLISTITEMS_FK_1;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='TWISHLISTITEMS_FK_2')
    ALTER TABLE TWISHLISTITEMS DROP CONSTRAINT TWISHLISTITEMS_FK_2;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TWISHLISTITEMS')
BEGIN
     DECLARE @reftable_161 nvarchar(60), @constraintname_161 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TWISHLISTITEMS'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_161, @constraintname_161
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_161+' drop constraint '+@constraintname_161)
       FETCH NEXT from refcursor into @reftable_161, @constraintname_161
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TWISHLISTITEMS
END
;

CREATE TABLE TWISHLISTITEMS
(
                WISHLIST_UID BIGINT NOT NULL,
                ITEM_UID BIGINT NOT NULL,

    CONSTRAINT TWISHLISTITEMS_PK PRIMARY KEY(WISHLIST_UID,ITEM_UID));





/* ---------------------------------------------------------------------- */
/* TSHOPPINGCARTITEMS                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TSHOPPINGCARTITEMS')
BEGIN
     DECLARE @reftable_162 nvarchar(60), @constraintname_162 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TSHOPPINGCARTITEMS'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_162, @constraintname_162
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_162+' drop constraint '+@constraintname_162)
       FETCH NEXT from refcursor into @reftable_162, @constraintname_162
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TSHOPPINGCARTITEMS
END
;

CREATE TABLE TSHOPPINGCARTITEMS
(
                SHOPPING_CART_UID BIGINT NOT NULL,
                ITEM_UID BIGINT NOT NULL,
);

CREATE  INDEX I_TSCI_CART_UID ON TSHOPPINGCARTITEMS (SHOPPING_CART_UID);




/* ---------------------------------------------------------------------- */
/* TCAMPAIGN                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCAMPAIGN')
BEGIN
     DECLARE @reftable_163 nvarchar(60), @constraintname_163 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCAMPAIGN'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_163, @constraintname_163
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_163+' drop constraint '+@constraintname_163)
       FETCH NEXT from refcursor into @reftable_163, @constraintname_163
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCAMPAIGN
END
;

CREATE TABLE TCAMPAIGN
(
                UIDPK BIGINT NOT NULL,
                THIRD_PARTY_ID NVARCHAR (255) NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                START_DATE DATETIME NULL,
                END_DATE DATETIME NULL,
                STATE INT default 0 NOT NULL,

    CONSTRAINT TCAMPAIGN_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TCAMPAIGN_NAME_UNQ UNIQUE (NAME),
    CONSTRAINT TCAMPAIGN_THIRD_PARTY_ID_UNQ UNIQUE (THIRD_PARTY_ID));





/* ---------------------------------------------------------------------- */
/* TEXPERIENCE                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_CAMPAIGN_TEXPERIENCE_UID')
    ALTER TABLE TEXPERIENCE DROP CONSTRAINT FK_CAMPAIGN_TEXPERIENCE_UID;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TEXPERIENCE')
BEGIN
     DECLARE @reftable_164 nvarchar(60), @constraintname_164 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TEXPERIENCE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_164, @constraintname_164
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_164+' drop constraint '+@constraintname_164)
       FETCH NEXT from refcursor into @reftable_164, @constraintname_164
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TEXPERIENCE
END
;

CREATE TABLE TEXPERIENCE
(
                UIDPK BIGINT NOT NULL,
                THIRD_PARTY_ID NVARCHAR (255) NOT NULL,
                NAME NVARCHAR (255) NOT NULL,
                GUID NVARCHAR (100) NOT NULL,
                CAMPAIGN_UID BIGINT NOT NULL,

    CONSTRAINT TEXPERIENCE_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TEXPERIENCE_GUID_UNIQ UNIQUE (GUID));





/* ---------------------------------------------------------------------- */
/* TMARKETTESTINGOFFER                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_OFFER_STORE_UID')
    ALTER TABLE TMARKETTESTINGOFFER DROP CONSTRAINT FK_OFFER_STORE_UID;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TMARKETTESTINGOFFER')
BEGIN
     DECLARE @reftable_165 nvarchar(60), @constraintname_165 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TMARKETTESTINGOFFER'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_165, @constraintname_165
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_165+' drop constraint '+@constraintname_165)
       FETCH NEXT from refcursor into @reftable_165, @constraintname_165
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TMARKETTESTINGOFFER
END
;

CREATE TABLE TMARKETTESTINGOFFER
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (100) NOT NULL,
                NAME NVARCHAR (255) NULL,
                STORE_UID BIGINT NULL,
                OFFER_VALUE_ENTITY_UID BIGINT NULL,
                OFFER_TYPE NVARCHAR (255) NULL,
                LAST_SYNC_DATE DATETIME NULL,
                OFFER_TYPE_NAME NVARCHAR (255) NULL,
                CAMPAIGN_STATE INT default 1 NOT NULL,
                EXTERNAL_ID BIGINT NULL,

    CONSTRAINT TMARKETTESTINGOFFER_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TMARKETOFFER_GUID_UNQ UNIQUE (GUID),
    CONSTRAINT TMARKETTESTINGOFFER_NAME_UNQ UNIQUE (NAME));





/* ---------------------------------------------------------------------- */
/* TEXPERIENCEOFFERLOCATION                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_TEOL_EXPERIENCE_UID')
    ALTER TABLE TEXPERIENCEOFFERLOCATION DROP CONSTRAINT FK_TEOL_EXPERIENCE_UID;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type ='RI' AND name='FK_TEOL_OFFER_UID')
    ALTER TABLE TEXPERIENCEOFFERLOCATION DROP CONSTRAINT FK_TEOL_OFFER_UID;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TEXPERIENCEOFFERLOCATION')
BEGIN
     DECLARE @reftable_166 nvarchar(60), @constraintname_166 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TEXPERIENCEOFFERLOCATION'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_166, @constraintname_166
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_166+' drop constraint '+@constraintname_166)
       FETCH NEXT from refcursor into @reftable_166, @constraintname_166
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TEXPERIENCEOFFERLOCATION
END
;

CREATE TABLE TEXPERIENCEOFFERLOCATION
(
                UIDPK BIGINT NOT NULL,
                EXPERIENCE_UID BIGINT NOT NULL,
                OFFER_UID BIGINT NOT NULL,
                OFFER_LOCATION NVARCHAR (255) NOT NULL,

    CONSTRAINT TEXPERIENCEOFFERLOCATION_PK PRIMARY KEY(UIDPK),
    CONSTRAINT TEOL_ALL_COLUMNS_UNQ UNIQUE (EXPERIENCE_UID, OFFER_UID, OFFER_LOCATION));





/* ---------------------------------------------------------------------- */
/* TCARTORDER                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TCARTORDER')
BEGIN
     DECLARE @reftable_167 nvarchar(60), @constraintname_167 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TCARTORDER'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_167, @constraintname_167
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_167+' drop constraint '+@constraintname_167)
       FETCH NEXT from refcursor into @reftable_167, @constraintname_167
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TCARTORDER
END
;

CREATE TABLE TCARTORDER
(
                UIDPK BIGINT NOT NULL,
                GUID NVARCHAR (64) NOT NULL,
                BILLING_GUID NVARCHAR (64) NULL,
                SHOPPINGCART_GUID NVARCHAR (100) NOT NULL,

    CONSTRAINT TCARTORDER_PK PRIMARY KEY(UIDPK),
    CONSTRAINT UNQ_CARTORDER_GUID UNIQUE (GUID),
    CONSTRAINT UNQ_CARTORDER_SC_GUID UNIQUE (SHOPPINGCART_GUID));





/* ---------------------------------------------------------------------- */
/* TOAUTHACCESSTOKEN                                                      */
/* ---------------------------------------------------------------------- */

IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'TOAUTHACCESSTOKEN')
BEGIN
     DECLARE @reftable_168 nvarchar(60), @constraintname_168 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'TOAUTHACCESSTOKEN'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_168, @constraintname_168
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_168+' drop constraint '+@constraintname_168)
       FETCH NEXT from refcursor into @reftable_168, @constraintname_168
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE TOAUTHACCESSTOKEN
END
;

CREATE TABLE TOAUTHACCESSTOKEN
(
                UIDPK BIGINT NOT NULL,
                TOKEN_ID NVARCHAR (255) NOT NULL,
                EXPIRY_DATE DATETIME NOT NULL,
                TOKEN_TYPE NVARCHAR (255) NOT NULL,
                USERNAME NVARCHAR (255) NOT NULL,
                CREDENTIALS NVARCHAR (255) NOT NULL,
                STORECODE NVARCHAR (255) NOT NULL,
                CLIENT_ID NVARCHAR (255) NOT NULL,
                CLIENT_SECRET NVARCHAR (255) NULL,

    CONSTRAINT TOAUTHACCESSTOKEN_PK PRIMARY KEY(UIDPK),
    CONSTRAINT UNQ_TOAT_TOKEN_TOKEN_ID UNIQUE (TOKEN_ID));

CREATE  INDEX I_TOAT_EXPIRY_DATE ON TOAUTHACCESSTOKEN (EXPIRY_DATE);




/* ---------------------------------------------------------------------- */
/* TOAUTHACCESSTOKEN                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* JPA_GENERATED_KEYS                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TDIGITALASSETS                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TTAXCODE                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TTAXJURISDICTION                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TTAXCATEGORY
    ADD CONSTRAINT TTAXCATEGORY_FK_1 FOREIGN KEY (TAX_JURISDICTION_UID)
    REFERENCES TTAXJURISDICTION (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TTAXCATEGORY                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TTAXREGION
    ADD CONSTRAINT TTAXREGION_FK_1 FOREIGN KEY (TAX_CATEGORY_UID)
    REFERENCES TTAXCATEGORY (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TTAXREGION                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TTAXVALUE
    ADD CONSTRAINT TTAXVALUE_FK_1 FOREIGN KEY (TAX_REGION_UID)
    REFERENCES TTAXREGION (UIDPK)
END
;

BEGIN
ALTER TABLE TTAXVALUE
    ADD CONSTRAINT TTAXVALUE_FK_2 FOREIGN KEY (TAX_CODE_UID)
    REFERENCES TTAXCODE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TTAXVALUE                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TCATALOG                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TATTRIBUTE                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TSETTINGDEFINITION                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TSETTINGMETADATA
    ADD CONSTRAINT TSETTINGMETADATA_FK_1 FOREIGN KEY (SETTING_DEFINITION_UID)
    REFERENCES TSETTINGDEFINITION (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TSETTINGMETADATA                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TSETTINGVALUE
    ADD CONSTRAINT TSETTINGVALUE_FK_1 FOREIGN KEY (SETTING_DEFINITION_UID)
    REFERENCES TSETTINGDEFINITION (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TSETTINGVALUE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TSTORE
    ADD CONSTRAINT TSTORE_FK_1 FOREIGN KEY (CATALOG_UID)
    REFERENCES TCATALOG (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TSTORE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCUSTOMER
    ADD CONSTRAINT TCUSTOMER_FK_1 FOREIGN KEY (STORE_UID)
    REFERENCES TSTORE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCUSTOMER                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TCUSTOMERAUTHENTICATION                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCUSTOMERPROFILEVALUE
    ADD CONSTRAINT TCUSTOMERPROFILEVALUE_FK_1 FOREIGN KEY (ATTRIBUTE_UID)
    REFERENCES TATTRIBUTE (UIDPK)
END
;

BEGIN
ALTER TABLE TCUSTOMERPROFILEVALUE
    ADD CONSTRAINT TCUSTOMERPROFILEVALUE_FK_2 FOREIGN KEY (CUSTOMER_UID)
    REFERENCES TCUSTOMER (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCUSTOMERPROFILEVALUE                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TCUSTOMERDELETED                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TADDRESS
    ADD CONSTRAINT TADDRESS_FK_1 FOREIGN KEY (CUSTOMER_UID)
    REFERENCES TCUSTOMER (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TADDRESS                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCATALOGSUPPORTEDLOCALE
    ADD CONSTRAINT TCATALOGSUPPORTEDLOCALE_FK_1 FOREIGN KEY (CATALOG_UID)
    REFERENCES TCATALOG (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCATALOGSUPPORTEDLOCALE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCATEGORYTYPE
    ADD CONSTRAINT TCATEGORYTYPE_FK_1 FOREIGN KEY (CATALOG_UID)
    REFERENCES TCATALOG (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCATEGORYTYPE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCATEGORY
    ADD CONSTRAINT TCATEGORY_FK_1 FOREIGN KEY (CATALOG_UID)
    REFERENCES TCATALOG (UIDPK)
END
;

BEGIN
ALTER TABLE TCATEGORY
    ADD CONSTRAINT TCATEGORY_FK_2 FOREIGN KEY (PARENT_CATEGORY_UID)
    REFERENCES TCATEGORY (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCATEGORY                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TMASTERCATEGORY
    ADD CONSTRAINT TMASTERCATEGORY_FK_1 FOREIGN KEY (CATEGORY_TYPE_UID)
    REFERENCES TCATEGORYTYPE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TMASTERCATEGORY                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TLINKEDCATEGORY
    ADD CONSTRAINT TLINKEDCATEGORY_FK_1 FOREIGN KEY (MASTER_CATEGORY_UID)
    REFERENCES TCATEGORY (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TLINKEDCATEGORY                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TCATEGORYDELETED                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCATEGORYATTRIBUTEVALUE
    ADD CONSTRAINT TCATEGORYATTRIBUTEVALUE_FK_1 FOREIGN KEY (ATTRIBUTE_UID)
    REFERENCES TATTRIBUTE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCATEGORYATTRIBUTEVALUE                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TCATEGORYLDF                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCATEGORYTYPEATTRIBUTE
    ADD CONSTRAINT TCATEGORYTYPEATTRIBUTE_FK_1 FOREIGN KEY (ATTRIBUTE_UID)
    REFERENCES TATTRIBUTE (UIDPK)
END
;

BEGIN
ALTER TABLE TCATEGORYTYPEATTRIBUTE
    ADD CONSTRAINT TCATEGORYTYPEATTRIBUTE_FK_2 FOREIGN KEY (CATEGORY_TYPE_UID)
    REFERENCES TCATEGORYTYPE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCATEGORYTYPEATTRIBUTE                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TCMUSER                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TPASSWORDHISTORY
    ADD CONSTRAINT TPASSWORDHISTORY_FK_1 FOREIGN KEY (CM_USER_UID)
    REFERENCES TCMUSER (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TPASSWORDHISTORY                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TUSERROLE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCMUSERROLEX
    ADD CONSTRAINT TCMUSERROLEX_FK_1 FOREIGN KEY (USER_ROLE_UID)
    REFERENCES TUSERROLE (UIDPK)
END
;

BEGIN
ALTER TABLE TCMUSERROLEX
    ADD CONSTRAINT TCMUSERROLEX_FK_2 FOREIGN KEY (CM_USER_UID)
    REFERENCES TCMUSER (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCMUSERROLEX                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TSHOPPER
    ADD CONSTRAINT FK_CUSTOMER FOREIGN KEY (CUSTOMER_GUID)
    REFERENCES TCUSTOMER (GUID)
END
;




/* ---------------------------------------------------------------------- */
/* TSHOPPER                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TCUSTOMERGROUP                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCUSTOMERGROUPROLEX
    ADD CONSTRAINT TCUSTOMERGROUPROLEX_FK_1 FOREIGN KEY (CUSTOMER_GROUP_UID)
    REFERENCES TCUSTOMERGROUP (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCUSTOMERGROUPROLEX                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCUSTOMERGROUPX
    ADD CONSTRAINT TCUSTOMERGROUPX_FK_1 FOREIGN KEY (CUSTOMERGROUP_UID)
    REFERENCES TCUSTOMERGROUP (UIDPK)
END
;

BEGIN
ALTER TABLE TCUSTOMERGROUPX
    ADD CONSTRAINT TCUSTOMERGROUPX_FK_2 FOREIGN KEY (CUSTOMER_UID)
    REFERENCES TCUSTOMER (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCUSTOMERGROUPX                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCUSTOMERSESSION
    ADD CONSTRAINT TCUSTOMERSESSION_FK_1 FOREIGN KEY (SHOPPER_UID)
    REFERENCES TSHOPPER (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCUSTOMERSESSION                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCUSTOMERCREDITCARD
    ADD CONSTRAINT TCUSTOMERCREDITCARD_FK_1 FOREIGN KEY (CUSTOMER_UID)
    REFERENCES TCUSTOMER (UIDPK)
END
;

BEGIN
ALTER TABLE TCUSTOMERCREDITCARD
    ADD CONSTRAINT TCUSTOMERCREDITCARD_FK_2 FOREIGN KEY (BILLING_ADDRESS_UID)
    REFERENCES TADDRESS (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCUSTOMERCREDITCARD                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TWAREHOUSEADDRESS                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TWAREHOUSE
    ADD CONSTRAINT TWAREHOUSE_FK_1 FOREIGN KEY (ADDRESS_UID)
    REFERENCES TWAREHOUSEADDRESS (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TWAREHOUSE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TIMPORTJOB
    ADD CONSTRAINT TIMPORTJOB_FK_1 FOREIGN KEY (CATALOG_UID)
    REFERENCES TCATALOG (UIDPK)
END
;

BEGIN
ALTER TABLE TIMPORTJOB
    ADD CONSTRAINT TIMPORTJOB_FK_2 FOREIGN KEY (STORE_UID)
    REFERENCES TSTORE (UIDPK)
END
;

BEGIN
ALTER TABLE TIMPORTJOB
    ADD CONSTRAINT TIMPORTJOB_FK_3 FOREIGN KEY (WAREHOUSE_UID)
    REFERENCES TWAREHOUSE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TIMPORTJOB                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TIMPORTMAPPINGS
    ADD CONSTRAINT TIMPORTMAPPINGS_FK_1 FOREIGN KEY (IMPORT_JOB_UID)
    REFERENCES TIMPORTJOB (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TIMPORTMAPPINGS                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TORDERADDRESS                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TORDER
    ADD CONSTRAINT TORDER_FK_1 FOREIGN KEY (STORE_UID)
    REFERENCES TSTORE (UIDPK)
END
;

BEGIN
ALTER TABLE TORDER
    ADD CONSTRAINT TORDER_FK_2 FOREIGN KEY (ORDER_BILLING_ADDRESS_UID)
    REFERENCES TORDERADDRESS (UIDPK)
END
;

BEGIN
ALTER TABLE TORDER
    ADD CONSTRAINT TORDER_FK_3 FOREIGN KEY (CUSTOMER_UID)
    REFERENCES TCUSTOMER (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TORDER                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TORDERNUMBERGENERATOR                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TORDERLOCK
    ADD CONSTRAINT TORDERLOCK_FK_1 FOREIGN KEY (ORDER_UID)
    REFERENCES TORDER (UIDPK)
END
;

BEGIN
ALTER TABLE TORDERLOCK
    ADD CONSTRAINT TORDERLOCK_FK_2 FOREIGN KEY (USER_UID)
    REFERENCES TCMUSER (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TORDERLOCK                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TORDERAUDIT
    ADD CONSTRAINT TORDERAUDIT_FK_1 FOREIGN KEY (ORDER_UID)
    REFERENCES TORDER (UIDPK)
END
;

BEGIN
ALTER TABLE TORDERAUDIT
    ADD CONSTRAINT TORDERAUDIT_FK_2 FOREIGN KEY (CREATED_BY)
    REFERENCES TCMUSER (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TORDERAUDIT                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TGIFTCERTIFICATE
    ADD CONSTRAINT TGIFTCERTIFICATE_FK_1 FOREIGN KEY (CUSTOMER_UID)
    REFERENCES TCUSTOMER (UIDPK)
END
;

BEGIN
ALTER TABLE TGIFTCERTIFICATE
    ADD CONSTRAINT TGIFTCERTIFICATE_FK_2 FOREIGN KEY (STORE_UID)
    REFERENCES TSTORE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TGIFTCERTIFICATE                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TSHIPPINGREGION                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TSHIPPINGCOSTCALCULATIONMETHOD                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TSHIPPINGCOSTCALCULATIONPARAM
    ADD CONSTRAINT TSHIPPINGCOSTCALCULATIONP_FK_1 FOREIGN KEY (SCCM_UID)
    REFERENCES TSHIPPINGCOSTCALCULATIONMETHOD (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TSHIPPINGCOSTCALCULATIONPARAM                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TSHIPPINGSERVICELEVEL
    ADD CONSTRAINT TSHIPPINGSERVICELEVEL_FK_1 FOREIGN KEY (SHIPPING_REGION_UID)
    REFERENCES TSHIPPINGREGION (UIDPK)
END
;

BEGIN
ALTER TABLE TSHIPPINGSERVICELEVEL
    ADD CONSTRAINT TSHIPPINGSERVICELEVEL_FK_2 FOREIGN KEY (STORE_UID)
    REFERENCES TSTORE (UIDPK)
END
;

BEGIN
ALTER TABLE TSHIPPINGSERVICELEVEL
    ADD CONSTRAINT TSHIPPINGSERVICELEVEL_FK_3 FOREIGN KEY (SCCM_UID)
    REFERENCES TSHIPPINGCOSTCALCULATIONMETHOD (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TSHIPPINGSERVICELEVEL                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TPICKLIST
    ADD CONSTRAINT TPICKLIST_FK_1 FOREIGN KEY (WAREHOUSE_UID)
    REFERENCES TWAREHOUSE (UIDPK)
END
;

BEGIN
ALTER TABLE TPICKLIST
    ADD CONSTRAINT TPICKLIST_FK_2 FOREIGN KEY (CREATED_BY)
    REFERENCES TCMUSER (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TPICKLIST                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TORDERSHIPMENT
    ADD CONSTRAINT TORDERSHIPMENT_FK_1 FOREIGN KEY (ORDER_ADDRESS_UID)
    REFERENCES TORDERADDRESS (UIDPK)
END
;

BEGIN
ALTER TABLE TORDERSHIPMENT
    ADD CONSTRAINT TORDERSHIPMENT_FK_2 FOREIGN KEY (SERVICE_LEVEL_UID)
    REFERENCES TSHIPPINGSERVICELEVEL (UIDPK)
END
;

BEGIN
ALTER TABLE TORDERSHIPMENT
    ADD CONSTRAINT TORDERSHIPMENT_FK_3 FOREIGN KEY (ORDER_UID)
    REFERENCES TORDER (UIDPK)
END
;

BEGIN
ALTER TABLE TORDERSHIPMENT
    ADD CONSTRAINT TORDERSHIPMENT_FK_4 FOREIGN KEY (PICKLIST_UID)
    REFERENCES TPICKLIST (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TORDERSHIPMENT                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TORDERPAYMENT
    ADD CONSTRAINT TORDERPAYMENT_FK_1 FOREIGN KEY (ORDER_UID)
    REFERENCES TORDER (UIDPK)
END
;

BEGIN
ALTER TABLE TORDERPAYMENT
    ADD CONSTRAINT TORDERPAYMENT_FK_2 FOREIGN KEY (GIFTCERTIFICATE_UID)
    REFERENCES TGIFTCERTIFICATE (UIDPK)
END
;

BEGIN
ALTER TABLE TORDERPAYMENT
    ADD CONSTRAINT TORDERPAYMENT_FK_3 FOREIGN KEY (ORDERSHIPMENT_UID)
    REFERENCES TORDERSHIPMENT (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TORDERPAYMENT                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TGIFTCERTIFICATETRANSACTION
    ADD CONSTRAINT TGIFTCERTIFICATETRANSACTI_FK_1 FOREIGN KEY (GIFTCERTIFICATE_UID)
    REFERENCES TGIFTCERTIFICATE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TGIFTCERTIFICATETRANSACTION                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TRMAGENERATOR                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TORDERRETURN
    ADD CONSTRAINT TORDERRETURN_FK_1 FOREIGN KEY (ORDER_UID)
    REFERENCES TORDER (UIDPK)
END
;

BEGIN
ALTER TABLE TORDERRETURN
    ADD CONSTRAINT TORDERRETURN_FK_2 FOREIGN KEY (CREATED_BY)
    REFERENCES TCMUSER (UIDPK)
END
;

BEGIN
ALTER TABLE TORDERRETURN
    ADD CONSTRAINT TORDERRETURN_FK_3 FOREIGN KEY (RECEIVED_BY)
    REFERENCES TCMUSER (UIDPK)
END
;

BEGIN
ALTER TABLE TORDERRETURN
    ADD CONSTRAINT TORDERRETURN_FK_4 FOREIGN KEY (EXCHANGE_ORDER_UID)
    REFERENCES TORDER (UIDPK)
END
;

BEGIN
ALTER TABLE TORDERRETURN
    ADD CONSTRAINT TORDERRETURN_FK_5 FOREIGN KEY (ORDER_RETURN_ADDRESS_UID)
    REFERENCES TORDERADDRESS (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TORDERRETURN                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TSHIPMENTTAX
    ADD CONSTRAINT TSHIPMENTTAX_FK_1 FOREIGN KEY (ORDER_SHIPMENT_UID)
    REFERENCES TORDERSHIPMENT (UIDPK)
END
;

BEGIN
ALTER TABLE TSHIPMENTTAX
    ADD CONSTRAINT TSHIPMENTTAX_FK_2 FOREIGN KEY (ORDER_RETURN_UID)
    REFERENCES TORDERRETURN (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TSHIPMENTTAX                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TPRODUCTTYPE
    ADD CONSTRAINT TPRODUCTTYPE_FK_1 FOREIGN KEY (CATALOG_UID)
    REFERENCES TCATALOG (UIDPK)
END
;

BEGIN
ALTER TABLE TPRODUCTTYPE
    ADD CONSTRAINT TPRODUCTTYPE_FK_2 FOREIGN KEY (TAX_CODE_UID)
    REFERENCES TTAXCODE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TPRODUCTTYPE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TBRAND
    ADD CONSTRAINT TBRAND_FK_1 FOREIGN KEY (CATALOG_UID)
    REFERENCES TCATALOG (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TBRAND                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TPRODUCT
    ADD CONSTRAINT TPRODUCT_FK_1 FOREIGN KEY (PRODUCT_TYPE_UID)
    REFERENCES TPRODUCTTYPE (UIDPK)
END
;

BEGIN
ALTER TABLE TPRODUCT
    ADD CONSTRAINT TPRODUCT_FK_2 FOREIGN KEY (BRAND_UID)
    REFERENCES TBRAND (UIDPK)
END
;

BEGIN
ALTER TABLE TPRODUCT
    ADD CONSTRAINT TPRODUCT_FK_3 FOREIGN KEY (TAX_CODE_UID)
    REFERENCES TTAXCODE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TPRODUCT                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TPRODUCTATTRIBUTEVALUE
    ADD CONSTRAINT TPRODUCTATTRIBUTEVALUE_FK_1 FOREIGN KEY (ATTRIBUTE_UID)
    REFERENCES TATTRIBUTE (UIDPK)
END
;

BEGIN
ALTER TABLE TPRODUCTATTRIBUTEVALUE
    ADD CONSTRAINT TPRODUCTATTRIBUTEVALUE_FK_2 FOREIGN KEY (PRODUCT_UID)
    REFERENCES TPRODUCT (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TPRODUCTATTRIBUTEVALUE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TPRODUCTCATEGORY
    ADD CONSTRAINT TPRODUCTCATEGORY_FK_1 FOREIGN KEY (PRODUCT_UID)
    REFERENCES TPRODUCT (UIDPK)
END
;

BEGIN
ALTER TABLE TPRODUCTCATEGORY
    ADD CONSTRAINT TPRODUCTCATEGORY_FK_2 FOREIGN KEY (CATEGORY_UID)
    REFERENCES TCATEGORY (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TPRODUCTCATEGORY                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TPRODUCTLDF
    ADD CONSTRAINT TPRODUCTLDF_FK_1 FOREIGN KEY (PRODUCT_UID)
    REFERENCES TPRODUCT (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TPRODUCTLDF                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TPRODUCTSKU
    ADD CONSTRAINT TPRODUCTSKU_FK_1 FOREIGN KEY (PRODUCT_UID)
    REFERENCES TPRODUCT (UIDPK)
END
;

BEGIN
ALTER TABLE TPRODUCTSKU
    ADD CONSTRAINT TPRODUCTSKU_FK_2 FOREIGN KEY (DIGITAL_ASSET_UID)
    REFERENCES TDIGITALASSETS (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TPRODUCTSKU                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TINVENTORY                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TINVENTORYJOURNAL                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TINVENTORYJOURNALLOCK                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TORDERSKU
    ADD CONSTRAINT TORDERSKU_FK_1 FOREIGN KEY (ORDER_SHIPMENT_UID)
    REFERENCES TORDERSHIPMENT (UIDPK)
END
;

BEGIN
ALTER TABLE TORDERSKU
    ADD CONSTRAINT TOSKU_FK_TOSKU FOREIGN KEY (PARENT_ITEM_UID)
    REFERENCES TORDERSKU (UIDPK)
END
;

BEGIN
ALTER TABLE TORDERSKU
    ADD CONSTRAINT TORDERSKU_FK_3 FOREIGN KEY (PRODUCT_SKU_UID)
    REFERENCES TPRODUCTSKU (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TORDERSKU                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TORDERRETURNSKU
    ADD CONSTRAINT TORDERRETURNSKU_FK_1 FOREIGN KEY (ORDER_RETURN_UID)
    REFERENCES TORDERRETURN (UIDPK)
END
;

BEGIN
ALTER TABLE TORDERRETURNSKU
    ADD CONSTRAINT TORDERRETURNSKU_FK_2 FOREIGN KEY (ORDER_SKU_UID)
    REFERENCES TORDERSKU (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TORDERRETURNSKU                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TPRODUCTASSOCIATION
    ADD CONSTRAINT TPRODUCTASSOCIATION_FK_1 FOREIGN KEY (SOURCE_PRODUCT_UID)
    REFERENCES TPRODUCT (UIDPK)
END
;

BEGIN
ALTER TABLE TPRODUCTASSOCIATION
    ADD CONSTRAINT TPRODUCTASSOCIATION_FK_2 FOREIGN KEY (TARGET_PRODUCT_UID)
    REFERENCES TPRODUCT (UIDPK)
END
;

BEGIN
ALTER TABLE TPRODUCTASSOCIATION
    ADD CONSTRAINT TPRODUCTASSOCIATION_FK_3 FOREIGN KEY (CATALOG_UID)
    REFERENCES TCATALOG (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TPRODUCTASSOCIATION                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TPRODUCTSKUATTRIBUTEVALUE
    ADD CONSTRAINT TPRODUCTSKUATTRIBUTEVALUE_FK_1 FOREIGN KEY (PRODUCT_SKU_UID)
    REFERENCES TPRODUCTSKU (UIDPK)
END
;

BEGIN
ALTER TABLE TPRODUCTSKUATTRIBUTEVALUE
    ADD CONSTRAINT TPRODUCTSKUATTRIBUTEVALUE_FK_2 FOREIGN KEY (ATTRIBUTE_UID)
    REFERENCES TATTRIBUTE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TPRODUCTSKUATTRIBUTEVALUE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TPRODUCTTYPEATTRIBUTE
    ADD CONSTRAINT TPRODUCTTYPEATTRIBUTE_FK_1 FOREIGN KEY (ATTRIBUTE_UID)
    REFERENCES TATTRIBUTE (UIDPK)
END
;

BEGIN
ALTER TABLE TPRODUCTTYPEATTRIBUTE
    ADD CONSTRAINT TPRODUCTTYPEATTRIBUTE_FK_2 FOREIGN KEY (PRODUCT_TYPE_UID)
    REFERENCES TPRODUCTTYPE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TPRODUCTTYPEATTRIBUTE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TPRODUCTTYPESKUATTRIBUTE
    ADD CONSTRAINT TPRODUCTTYPESKUATTRIBUTE_FK_1 FOREIGN KEY (ATTRIBUTE_UID)
    REFERENCES TATTRIBUTE (UIDPK)
END
;

BEGIN
ALTER TABLE TPRODUCTTYPESKUATTRIBUTE
    ADD CONSTRAINT TPRODUCTTYPESKUATTRIBUTE_FK_2 FOREIGN KEY (PRODUCT_TYPE_UID)
    REFERENCES TPRODUCTTYPE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TPRODUCTTYPESKUATTRIBUTE                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TSELLINGCONTEXT                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TRULESET                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TRULE
    ADD CONSTRAINT SCRULE_FK FOREIGN KEY (SELLING_CTX_UID)
    REFERENCES TSELLINGCONTEXT (UIDPK)
END
;

BEGIN
ALTER TABLE TRULE
    ADD CONSTRAINT TRULE_FK_2 FOREIGN KEY (STORE_UID)
    REFERENCES TSTORE (UIDPK)
END
;

BEGIN
ALTER TABLE TRULE
    ADD CONSTRAINT TRULE_FK_3 FOREIGN KEY (CATALOG_UID)
    REFERENCES TCATALOG (UIDPK)
END
;

BEGIN
ALTER TABLE TRULE
    ADD CONSTRAINT TRULE_FK_4 FOREIGN KEY (RULE_SET_UID)
    REFERENCES TRULESET (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TRULE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TRULEELEMENT
    ADD CONSTRAINT TRULEELEMENT_FK_1 FOREIGN KEY (RULE_UID)
    REFERENCES TRULE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TRULEELEMENT                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TRULEEXCEPTION
    ADD CONSTRAINT TRULEEXCEPTION_FK_1 FOREIGN KEY (RULE_ELEMENT_UID)
    REFERENCES TRULEELEMENT (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TRULEEXCEPTION                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TRULEPARAMETER
    ADD CONSTRAINT TRULEPARAMETER_FK_1 FOREIGN KEY (RULE_ELEMENT_UID)
    REFERENCES TRULEELEMENT (UIDPK)
END
;

BEGIN
ALTER TABLE TRULEPARAMETER
    ADD CONSTRAINT TRULEPARAMETER_FK_2 FOREIGN KEY (RULE_EXCEPTION_UID)
    REFERENCES TRULEEXCEPTION (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TRULEPARAMETER                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TSHOPPINGCART
    ADD CONSTRAINT TSHOPPINGCART_FK_1 FOREIGN KEY (STORE_UID)
    REFERENCES TSTORE (UIDPK)
END
;

BEGIN
ALTER TABLE TSHOPPINGCART
    ADD CONSTRAINT TSHOPPINGCART_FK_SHOPPER FOREIGN KEY (SHOPPER_UID)
    REFERENCES TSHOPPER (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TSHOPPINGCART                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCARTITEM
    ADD CONSTRAINT TCARTITEM_FK_TCARTITEM FOREIGN KEY (PARENT_ITEM_UID)
    REFERENCES TCARTITEM (UIDPK)
END
;

BEGIN
ALTER TABLE TCARTITEM
    ADD CONSTRAINT TCARTITEM_FK_2 FOREIGN KEY (SKU_UID)
    REFERENCES TPRODUCTSKU (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCARTITEM                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TSHOPPINGITEMRECURRINGPRICE
    ADD CONSTRAINT TSHOPPINGITEMRECURRINGPRI_FK_1 FOREIGN KEY (CARTITEM_UID)
    REFERENCES TCARTITEM (UIDPK)
END
;

BEGIN
ALTER TABLE TSHOPPINGITEMRECURRINGPRICE
    ADD CONSTRAINT TSHOPPINGITEMRECURRINGPRI_FK_2 FOREIGN KEY (ORDERSKU_UID)
    REFERENCES TORDERSKU (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TSHOPPINGITEMRECURRINGPRICE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TUSERROLEPERMISSIONX
    ADD CONSTRAINT TUSERROLEPERMISSIONX_FK_1 FOREIGN KEY (ROLE_UID)
    REFERENCES TUSERROLE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TUSERROLEPERMISSIONX                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TSKUOPTION
    ADD CONSTRAINT TSKUOPTION_FK_1 FOREIGN KEY (CATALOG_UID)
    REFERENCES TCATALOG (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TSKUOPTION                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TSKUOPTIONVALUE
    ADD CONSTRAINT TSKUOPTIONVALUE_FK_1 FOREIGN KEY (SKU_OPTION_UID)
    REFERENCES TSKUOPTION (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TSKUOPTIONVALUE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TPRODUCTTYPESKUOPTION
    ADD CONSTRAINT TPRODUCTTYPESKUOPTION_FK_1 FOREIGN KEY (PRODUCT_TYPE_UID)
    REFERENCES TPRODUCTTYPE (UIDPK)
END
;

BEGIN
ALTER TABLE TPRODUCTTYPESKUOPTION
    ADD CONSTRAINT TPRODUCTTYPESKUOPTION_FK_2 FOREIGN KEY (SKU_OPTION_UID)
    REFERENCES TSKUOPTION (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TPRODUCTTYPESKUOPTION                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TPRODUCTSKUOPTIONVALUE
    ADD CONSTRAINT TPRODUCTSKUOPTIONVALUE_FK_1 FOREIGN KEY (PRODUCT_SKU_UID)
    REFERENCES TPRODUCTSKU (UIDPK)
END
;

BEGIN
ALTER TABLE TPRODUCTSKUOPTIONVALUE
    ADD CONSTRAINT TPRODUCTSKUOPTIONVALUE_FK_2 FOREIGN KEY (OPTION_VALUE_UID)
    REFERENCES TSKUOPTIONVALUE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TPRODUCTSKUOPTIONVALUE                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TPRODUCTDELETED                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TOBJECTDELETED                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TLOCALIZEDPROPERTIES                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TDIGITALASSETAUDIT                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TAPPLIEDRULE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TAPPLIEDRULECOUPONCODE
    ADD CONSTRAINT TAPPLIEDRULECOUPONCODE_FK_1 FOREIGN KEY (APPLIED_RULE_UID)
    REFERENCES TAPPLIEDRULE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TAPPLIEDRULECOUPONCODE                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TTOPSELLER                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TTOPSELLERPRODUCTS
    ADD CONSTRAINT TTOPSELLERPRODUCTS_FK_1 FOREIGN KEY (TOP_SELLER_UID)
    REFERENCES TTOPSELLER (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TTOPSELLERPRODUCTS                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TSFSEARCHLOG                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TSTOREWAREHOUSE
    ADD CONSTRAINT TSTOREWAREHOUSE_FK_1 FOREIGN KEY (STORE_UID)
    REFERENCES TSTORE (UIDPK)
END
;

BEGIN
ALTER TABLE TSTOREWAREHOUSE
    ADD CONSTRAINT TSTOREWAREHOUSE_FK_2 FOREIGN KEY (WAREHOUSE_UID)
    REFERENCES TWAREHOUSE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TSTOREWAREHOUSE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TSTORECREDITCARDTYPE
    ADD CONSTRAINT TSTORECREDITCARDTYPE_FK_1 FOREIGN KEY (STORE_UID)
    REFERENCES TSTORE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TSTORECREDITCARDTYPE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TSTORETAXCODE
    ADD CONSTRAINT TSTORETAXCODE_FK_1 FOREIGN KEY (STORE_UID)
    REFERENCES TSTORE (UIDPK)
END
;

BEGIN
ALTER TABLE TSTORETAXCODE
    ADD CONSTRAINT TSTORETAXCODE_FK_2 FOREIGN KEY (TAXCODE_UID)
    REFERENCES TTAXCODE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TSTORETAXCODE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TSTORETAXJURISDICTION
    ADD CONSTRAINT TSTORETAXJURISDICTION_FK_1 FOREIGN KEY (STORE_UID)
    REFERENCES TSTORE (UIDPK)
END
;

BEGIN
ALTER TABLE TSTORETAXJURISDICTION
    ADD CONSTRAINT TSTORETAXJURISDICTION_FK_2 FOREIGN KEY (TAXJURISDICTION_UID)
    REFERENCES TTAXJURISDICTION (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TSTORETAXJURISDICTION                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TPAYMENTGATEWAY                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TSTOREPAYMENTGATEWAY
    ADD CONSTRAINT TSTOREPAYMENTGATEWAY_FK_1 FOREIGN KEY (STORE_UID)
    REFERENCES TSTORE (UIDPK)
END
;

BEGIN
ALTER TABLE TSTOREPAYMENTGATEWAY
    ADD CONSTRAINT TSTOREPAYMENTGATEWAY_FK_2 FOREIGN KEY (GATEWAY_UID)
    REFERENCES TPAYMENTGATEWAY (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TSTOREPAYMENTGATEWAY                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TPAYMENTGATEWAYPROPERTIES
    ADD CONSTRAINT TPAYMENTGATEWAYPROPERTIES_FK_1 FOREIGN KEY (PAYMENTGATEWAY_UID)
    REFERENCES TPAYMENTGATEWAY (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TPAYMENTGATEWAYPROPERTIES                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TSTORECATALOG
    ADD CONSTRAINT TSTORECATALOG_FK_1 FOREIGN KEY (STORE_UID)
    REFERENCES TSTORE (UIDPK)
END
;

BEGIN
ALTER TABLE TSTORECATALOG
    ADD CONSTRAINT TSTORECATALOG_FK_2 FOREIGN KEY (CATALOG_UID)
    REFERENCES TCATALOG (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TSTORECATALOG                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCMUSERSTORE
    ADD CONSTRAINT TCMUSERSTORE_FK_1 FOREIGN KEY (STORE_UID)
    REFERENCES TSTORE (UIDPK)
END
;

BEGIN
ALTER TABLE TCMUSERSTORE
    ADD CONSTRAINT TCMUSERSTORE_FK_2 FOREIGN KEY (USER_UID)
    REFERENCES TCMUSER (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCMUSERSTORE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCMUSERWAREHOUSE
    ADD CONSTRAINT TCMUSERWAREHOUSE_FK_1 FOREIGN KEY (WAREHOUSE_UID)
    REFERENCES TWAREHOUSE (UIDPK)
END
;

BEGIN
ALTER TABLE TCMUSERWAREHOUSE
    ADD CONSTRAINT TCMUSERWAREHOUSE_FK_2 FOREIGN KEY (USER_UID)
    REFERENCES TCMUSER (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCMUSERWAREHOUSE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCMUSERCATALOG
    ADD CONSTRAINT TCMUSERCATALOG_FK_1 FOREIGN KEY (CATALOG_UID)
    REFERENCES TCATALOG (UIDPK)
END
;

BEGIN
ALTER TABLE TCMUSERCATALOG
    ADD CONSTRAINT TCMUSERCATALOG_FK_2 FOREIGN KEY (USER_UID)
    REFERENCES TCMUSER (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCMUSERCATALOG                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TSTOREASSOCIATION
    ADD CONSTRAINT TSTOREASSOCIATION_FK_1 FOREIGN KEY (STORE_UID)
    REFERENCES TSTORE (UIDPK)
END
;

BEGIN
ALTER TABLE TSTOREASSOCIATION
    ADD CONSTRAINT TSTOREASSOCIATION_FK_2 FOREIGN KEY (ASSOCIATED_STORE_UID)
    REFERENCES TSTORE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TSTOREASSOCIATION                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TSYNONYMGROUPS
    ADD CONSTRAINT TSYNONYMGROUPS_FK_1 FOREIGN KEY (CATALOG_UID)
    REFERENCES TCATALOG (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TSYNONYMGROUPS                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TSYNONYM
    ADD CONSTRAINT TSYNONYM_FK_1 FOREIGN KEY (SYNONYM_UID)
    REFERENCES TSYNONYMGROUPS (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TSYNONYM                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TINDEXNOTIFY                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TRULESTORAGE
    ADD CONSTRAINT TRULESTORAGE_FK_1 FOREIGN KEY (STORE_UID)
    REFERENCES TSTORE (UIDPK)
END
;

BEGIN
ALTER TABLE TRULESTORAGE
    ADD CONSTRAINT TRULESTORAGE_FK_2 FOREIGN KEY (CATALOG_UID)
    REFERENCES TCATALOG (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TRULESTORAGE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TADVANCEDSEARCHQUERY
    ADD CONSTRAINT TADVANCEDSEARCHQUERY_FK_1 FOREIGN KEY (OWNER_ID)
    REFERENCES TCMUSER (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TADVANCEDSEARCHQUERY                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TSTORESUPPORTEDCURRENCY
    ADD CONSTRAINT TSTORESUPPORTEDCURRENCY_FK_1 FOREIGN KEY (STORE_UID)
    REFERENCES TSTORE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TSTORESUPPORTEDCURRENCY                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TSTORESUPPORTEDLOCALE
    ADD CONSTRAINT TSTORESUPPORTEDLOCALE_FK_1 FOREIGN KEY (STORE_UID)
    REFERENCES TSTORE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TSTORESUPPORTEDLOCALE                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TINDEXBUILDSTATUS                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TPRICELIST                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TBASEAMOUNT
    ADD CONSTRAINT TBASEAMOUNT_FK_1 FOREIGN KEY (PRICE_LIST_GUID)
    REFERENCES TPRICELIST (GUID)
END
;




/* ---------------------------------------------------------------------- */
/* TBASEAMOUNT                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TOBJECTGROUPMEMBER                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TCHANGESET                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TOBJECTMETADATA
    ADD CONSTRAINT TOBJECTMETADATA_FK_1 FOREIGN KEY (OBJECT_GROUP_MEMBER_UID)
    REFERENCES TOBJECTGROUPMEMBER (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TOBJECTMETADATA                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCHANGESETUSER
    ADD CONSTRAINT TCHANGESETUSER_FK_1 FOREIGN KEY (CHANGESET_UID)
    REFERENCES TCHANGESET (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCHANGESETUSER                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TTAGDICTIONARY                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TTAGCONDITION                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TSELLINGCONTEXTCONDITION
    ADD CONSTRAINT FK_SELLCOND_SELLCTX FOREIGN KEY (SELLING_CONTEXT_UID)
    REFERENCES TSELLINGCONTEXT (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TSELLINGCONTEXTCONDITION                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TCSDYNAMICCONTENT                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCSDYNAMICCONTENTDELIVERY
    ADD CONSTRAINT FK_DELIVERY_DCONTENT FOREIGN KEY (CSDC_CONTENT_UID)
    REFERENCES TCSDYNAMICCONTENT (UIDPK)
END
;

BEGIN
ALTER TABLE TCSDYNAMICCONTENTDELIVERY
    ADD CONSTRAINT FK_SELLING_CONTEXT FOREIGN KEY (SELLING_CONTEXT_GUID)
    REFERENCES TSELLINGCONTEXT (GUID)
END
;




/* ---------------------------------------------------------------------- */
/* TCSDYNAMICCONTENTDELIVERY                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TCSCONTENTSPACE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCSPARAMETERVALUE
    ADD CONSTRAINT FK_PARAMVAL_DCONTENT FOREIGN KEY (CSDYNAMICCONTENT_UID)
    REFERENCES TCSDYNAMICCONTENT (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCSPARAMETERVALUE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCSPARAMETERVALUELDF
    ADD CONSTRAINT FK_PARAMVALLOCALE_PARAMVAL FOREIGN KEY (CSPARAMETERVALUE_UID)
    REFERENCES TCSPARAMETERVALUE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCSPARAMETERVALUELDF                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCSDYNAMICCONTENTSPACE
    ADD CONSTRAINT FK_DELSPACE_DELIVERY FOREIGN KEY (DC_DELIVERY_UID)
    REFERENCES TCSDYNAMICCONTENTDELIVERY (UIDPK)
END
;

BEGIN
ALTER TABLE TCSDYNAMICCONTENTSPACE
    ADD CONSTRAINT FK_DELSPACE_CONTENTSPACE FOREIGN KEY (DC_CONTENTSPACE_UID)
    REFERENCES TCSCONTENTSPACE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCSDYNAMICCONTENTSPACE                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TCHANGETRANSACTION                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCHANGETRANSACTIONMETADATA
    ADD CONSTRAINT TCHANGETRANSACTIONMETADAT_FK_1 FOREIGN KEY (CHANGE_TRANSACTION_UID)
    REFERENCES TCHANGETRANSACTION (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCHANGETRANSACTIONMETADATA                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCHANGEOPERATION
    ADD CONSTRAINT TCHANGEOPERATION_FK_1 FOREIGN KEY (CHANGE_TRANSACTION_UID)
    REFERENCES TCHANGETRANSACTION (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCHANGEOPERATION                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TTAGVALUETYPE                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TTAGGROUP                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TTAGDEFINITION
    ADD CONSTRAINT FK_TTAGVALUETYPE FOREIGN KEY (TAGVALUETYPE_GUID)
    REFERENCES TTAGVALUETYPE (GUID)
END
;

BEGIN
ALTER TABLE TTAGDEFINITION
    ADD CONSTRAINT FK_TTAGGROUP FOREIGN KEY (TAGGROUP_UID)
    REFERENCES TTAGGROUP (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TTAGDEFINITION                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TTAGALLOWEDVALUE
    ADD CONSTRAINT FK_TAGALLOWEDVAL_TAGTYPE FOREIGN KEY (TAGVALUETYPE_GUID)
    REFERENCES TTAGVALUETYPE (GUID)
END
;




/* ---------------------------------------------------------------------- */
/* TTAGALLOWEDVALUE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TDATACHANGED
    ADD CONSTRAINT TDATACHANGED_FK_1 FOREIGN KEY (CHANGE_OPERATION_UID)
    REFERENCES TCHANGEOPERATION (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TDATACHANGED                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TTAGDICTIONARYTAGDEFINITION                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TTAGOPERATOR                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TTAGVALUETYPEOPERATOR
    ADD CONSTRAINT FK_TAGOPERATOR_TAGVALUETYPE FOREIGN KEY (TAGOPERATOR_GUID)
    REFERENCES TTAGOPERATOR (GUID)
END
;

BEGIN
ALTER TABLE TTAGVALUETYPEOPERATOR
    ADD CONSTRAINT FK_TAGVALUETYPE_TAGOPERATOR FOREIGN KEY (TAGVALUETYPE_GUID)
    REFERENCES TTAGVALUETYPE (GUID)
END
;




/* ---------------------------------------------------------------------- */
/* TTAGVALUETYPEOPERATOR                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TVALIDATIONCONSTRAINTS                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TBUNDLECONSTITUENTX
    ADD CONSTRAINT TBCX_TPROD_FK FOREIGN KEY (BUNDLE_UID)
    REFERENCES TPRODUCT (UIDPK)
END
;

BEGIN
ALTER TABLE TBUNDLECONSTITUENTX
    ADD CONSTRAINT TBCX_TPROD_C_FK FOREIGN KEY (CONSTITUENT_UID)
    REFERENCES TPRODUCT (UIDPK)
END
;

BEGIN
ALTER TABLE TBUNDLECONSTITUENTX
    ADD CONSTRAINT TBCX_TSKU_C_FK FOREIGN KEY (CONSTITUENT_SKU_UID)
    REFERENCES TPRODUCTSKU (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TBUNDLECONSTITUENTX                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TPRICEADJUSTMENT
    ADD CONSTRAINT FK_TPRICEADJUST_CONST_GUID FOREIGN KEY (CONSTITUENT_GUID)
    REFERENCES TBUNDLECONSTITUENTX (GUID)
END
;




/* ---------------------------------------------------------------------- */
/* TPRICEADJUSTMENT                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TBUNDLESELECTIONRULE
    ADD CONSTRAINT TBSR_TPROD_FK FOREIGN KEY (BUNDLE_UID)
    REFERENCES TPRODUCT (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TBUNDLESELECTIONRULE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TSHOPPINGITEMDATA
    ADD CONSTRAINT TCARTITEM_FK FOREIGN KEY (CARTITEM_UID)
    REFERENCES TCARTITEM (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TSHOPPINGITEMDATA                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TORDERITEMDATA
    ADD CONSTRAINT TORDERSKU_FK FOREIGN KEY (ORDERSKU_UID)
    REFERENCES TORDERSKU (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TORDERITEMDATA                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TPRICELISTASSIGNMENT
    ADD CONSTRAINT CATPLA_FK FOREIGN KEY (CATALOG_UID)
    REFERENCES TCATALOG (UIDPK)
END
;

BEGIN
ALTER TABLE TPRICELISTASSIGNMENT
    ADD CONSTRAINT PLDPLA_FK FOREIGN KEY (PRLISTDSCR_UID)
    REFERENCES TPRICELIST (UIDPK)
END
;

BEGIN
ALTER TABLE TPRICELISTASSIGNMENT
    ADD CONSTRAINT SCPLA_FK FOREIGN KEY (SELLING_CTX_UID)
    REFERENCES TSELLINGCONTEXT (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TPRICELISTASSIGNMENT                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TIMPORTNOTIFICATION
    ADD CONSTRAINT IN_IMPORTJOB_FK FOREIGN KEY (IMPORT_JOB_UID)
    REFERENCES TIMPORTJOB (UIDPK)
END
;

BEGIN
ALTER TABLE TIMPORTNOTIFICATION
    ADD CONSTRAINT IN_CMUSER_FK FOREIGN KEY (CMUSER_UID)
    REFERENCES TCMUSER (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TIMPORTNOTIFICATION                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TIMPORTNOTIFICATIONMETADATA
    ADD CONSTRAINT INM_IMPORTNOTIFICATION_FK FOREIGN KEY (IMPORT_NOTIFICATION_UID)
    REFERENCES TIMPORTNOTIFICATION (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TIMPORTNOTIFICATIONMETADATA                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TIMPORTJOBSTATUS
    ADD CONSTRAINT IJS_IMPORTJOB_FK FOREIGN KEY (IMPORT_JOB_UID)
    REFERENCES TIMPORTJOB (UIDPK)
END
;

BEGIN
ALTER TABLE TIMPORTJOBSTATUS
    ADD CONSTRAINT IJS_CMUSER_FK FOREIGN KEY (CMUSER_UID)
    REFERENCES TCMUSER (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TIMPORTJOBSTATUS                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TIMPORTBADROW
    ADD CONSTRAINT IBR_IMPORTJOBSTATUS_FK FOREIGN KEY (IMPORT_JOB_STATUS_UID)
    REFERENCES TIMPORTJOBSTATUS (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TIMPORTBADROW                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TIMPORTFAULT
    ADD CONSTRAINT IF_IMPORTBADROW_FK FOREIGN KEY (IMPORT_BAD_ROW_UID)
    REFERENCES TIMPORTBADROW (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TIMPORTFAULT                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCMUSERPRICELIST
    ADD CONSTRAINT TCMUSERPRICELIST_FK_1 FOREIGN KEY (PRICELIST_GUID)
    REFERENCES TPRICELIST (GUID)
END
;

BEGIN
ALTER TABLE TCMUSERPRICELIST
    ADD CONSTRAINT TCMUSERPRICELIST_FK_2 FOREIGN KEY (USER_UID)
    REFERENCES TCMUSER (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCMUSERPRICELIST                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCOUPONCONFIG
    ADD CONSTRAINT TCOUPONCONFIG_FK_1 FOREIGN KEY (RULECODE)
    REFERENCES TRULE (RULECODE)
END
;




/* ---------------------------------------------------------------------- */
/* TCOUPONCONFIG                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCOUPON
    ADD CONSTRAINT TCOUPON_FK_1 FOREIGN KEY (COUPON_CONFIG_UID)
    REFERENCES TCOUPONCONFIG (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCOUPON                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TCOUPONUSAGE
    ADD CONSTRAINT TCOUPONUSAGE_FK_1 FOREIGN KEY (COUPON_UID)
    REFERENCES TCOUPON (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TCOUPONUSAGE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TWISHLIST
    ADD CONSTRAINT FK_WISHLIST_SHOPPER FOREIGN KEY (SHOPPER_UID)
    REFERENCES TSHOPPER (UIDPK)
END
;

BEGIN
ALTER TABLE TWISHLIST
    ADD CONSTRAINT FK_STORE FOREIGN KEY (STORECODE)
    REFERENCES TSTORE (STORECODE)
END
;




/* ---------------------------------------------------------------------- */
/* TWISHLIST                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TWISHLISTITEMS
    ADD CONSTRAINT TWISHLISTITEMS_FK_1 FOREIGN KEY (WISHLIST_UID)
    REFERENCES TWISHLIST (UIDPK)
END
;

BEGIN
ALTER TABLE TWISHLISTITEMS
    ADD CONSTRAINT TWISHLISTITEMS_FK_2 FOREIGN KEY (ITEM_UID)
    REFERENCES TCARTITEM (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TWISHLISTITEMS                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TSHOPPINGCARTITEMS                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TCAMPAIGN                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TEXPERIENCE
    ADD CONSTRAINT FK_CAMPAIGN_TEXPERIENCE_UID FOREIGN KEY (CAMPAIGN_UID)
    REFERENCES TCAMPAIGN (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TEXPERIENCE                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TMARKETTESTINGOFFER
    ADD CONSTRAINT FK_OFFER_STORE_UID FOREIGN KEY (STORE_UID)
    REFERENCES TSTORE (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TMARKETTESTINGOFFER                                                      */
/* ---------------------------------------------------------------------- */

BEGIN
ALTER TABLE TEXPERIENCEOFFERLOCATION
    ADD CONSTRAINT FK_TEOL_EXPERIENCE_UID FOREIGN KEY (EXPERIENCE_UID)
    REFERENCES TEXPERIENCE (UIDPK)
END
;

BEGIN
ALTER TABLE TEXPERIENCEOFFERLOCATION
    ADD CONSTRAINT FK_TEOL_OFFER_UID FOREIGN KEY (OFFER_UID)
    REFERENCES TMARKETTESTINGOFFER (UIDPK)
END
;




/* ---------------------------------------------------------------------- */
/* TEXPERIENCEOFFERLOCATION                                                      */
/* ---------------------------------------------------------------------- */




/* ---------------------------------------------------------------------- */
/* TCARTORDER                                                      */
/* ---------------------------------------------------------------------- */



