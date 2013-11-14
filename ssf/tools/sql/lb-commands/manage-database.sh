#!/bin/sh

displayUsage () {
echo "Usage: manage-database [db type] [command]"
echo ""
echo "db types:"
echo "   mysql           MySQL"
echo "   oracle          Oracle"
echo "   sqlserver       MS SQL Server"
echo ""
echo "standard commands (used for updating database):"
echo "   updateDatabase  Update database to current version by applying un-run change"
echo "                   sets to the database."
echo "   generateSqlToUpdateDatabase"
echo "                   Apply un-run change sets to generate SQL in a file which can"
echo "                   later be executed to update database to current version."
echo "   mark33DatabaseAsUpdated"
echo "                   Mark all change sets as ran against the 3.3 database."
echo "                   Used for one-time transition from 3.3 schema to later version."
echo "   generateSqlToMark33DatabaseAsUpdated"
echo "                   Generate SQL in a file which can later be executed to mark"
echo "                   all change sets as ran against the 3.3 database."
echo ""
echo "troubleshooting commands (useful for verification, testing, or troubleshooting):"
echo "   exportSchema    Export schema from existing database into a change log file"
echo "                   which can later be executed to create identical schema in"
echo "                   another database."
echo "   exportData      Export data from existing database into a change log file"
echo "                   which can later be executed to import identical data into"
echo "                   another database. This does not scale or handle all data types."
echo "   diffDatabases   Output schema differences between two databases into a change"
echo "                   log file which can later be executed to upgrade the schema of"
echo "                   the first database to that of the second (reference) database."
echo "                   There are limitations with this function."
echo "   validate        Checks the change log for errors. Useful after making manual"
echo "                   edits to the change log file (which is not recommended)."
echo "   markDatabaseAsUpdated"
echo "                   Mark the database schema as current with the most up-to-date"
echo "                   schema definition. Can be useful when rebuilding Liquibase"
echo "                   meta data after manual update or fix-up of the schema."
echo "                   Used for one-time transition from 3.3 schema to later version."
echo "   generateSqlToMarkDatabaseAsUpdated"
echo "                   Generate SQL in a file which can later be executed to mark"
echo "                   the database schema as current."
echo ""
echo "Note: Additional parameters are read in from [db type]-liquibase.properties file."
}

CLASSPATH=./lib/mysql-connector.jar:./lib/ojdbc6.jar:./lib/jtds.jar

# Set default log level. Valid values are debug, info, warning, severe, off.
LOG_LEVEL=info

CONTEXTS=schema,data

JAVA_OPTS=

if [ "$1" == "" ] || [ "$2" == "" ]; then
  displayUsage
  exit 1
fi

if [ "$1" != "mysql" ] && [ "$1" != "oracle" ] && [ "$1" != "sqlserver" ]; then
  echo "Errors:"
  echo "   Invalid database type"
  displayUsage
  exit 1
fi

if [ "$2" = "updateDatabase" ]; then
  java -jar "./lib/liquibase.jar" --logLevel="$LOG_LEVEL" --defaultsFile="./$1-liquibase.properties" --classpath="$CLASSPATH" --changeLogFile="scripts/changelog/$1-changelog-master.xml" --contexts="$CONTEXTS" update
elif [ "$2" = "generateSqlToUpdateDatabase" ]; then
  java -jar "./lib/liquibase.jar" --logLevel="$LOG_LEVEL" --defaultsFile="./$1-liquibase.properties" --classpath="$CLASSPATH" --changeLogFile="scripts/changelog/$1-changelog-master.xml" --contexts="$CONTEXTS" updateSQL > "./$1-update.sql"
elif [ "$2" = "markDatabaseAsUpdated" ]; then
  java -jar "./lib/liquibase.jar" --logLevel="$LOG_LEVEL" --defaultsFile="./$1-liquibase.properties" --classpath="$CLASSPATH" --changeLogFile="scripts/changelog/$1-changelog-master.xml" --contexts="$CONTEXTS" changeLogSync
elif [ "$2" = "generateSqlToMarkDatabaseAsUpdated" ]; then
  java -jar "./lib/liquibase.jar" --logLevel="$LOG_LEVEL" --defaultsFile="./$1-liquibase.properties" --classpath="$CLASSPATH" --changeLogFile="scripts/changelog/$1-changelog-master.xml" --contexts="$CONTEXTS" changeLogSyncSQL > "./$1-markasupdated.sql"
elif [ "$2" = "mark33DatabaseAsUpdated" ]; then
  java -jar "./lib/liquibase.jar" --logLevel="$LOG_LEVEL" --defaultsFile="./$1-liquibase.properties" --classpath="$CLASSPATH" --changeLogFile="scripts/changelog/$1-changelog-3.3.xml" --contexts="$CONTEXTS" changeLogSync
elif [ "$2" = "generateSqlToMark33DatabaseAsUpdated" ]; then
  java -jar "./lib/liquibase.jar" --logLevel="$LOG_LEVEL" --defaultsFile="./$1-liquibase.properties" --classpath="$CLASSPATH" --changeLogFile="scripts/changelog/$1-changelog-3.3.xml" --contexts="$CONTEXTS" changeLogSyncSQL > "./$1-markasupdated-3.3.sql"
elif [ "$2" = "exportSchema" ]; then
  java -jar "./lib/liquibase.jar" --logLevel="$LOG_LEVEL" --defaultsFile="./$1-liquibase.properties" --classpath="$CLASSPATH" generateChangeLog > "./$1-schema-changelog.xml"
elif [ "$2" = "exportData" ]; then
  java -jar "./lib/liquibase.jar" --logLevel="$LOG_LEVEL" --defaultsFile="./$1-liquibase.properties" --classpath="$CLASSPATH" --diffTypes="data" generateChangeLog > "./$1-data-changelog.xml"
elif [ "$2" = "diffDatabases" ]; then
  java -jar "./lib/liquibase.jar" --logLevel="$LOG_LEVEL" --defaultsFile="./$1-liquibase.properties" --classpath="$CLASSPATH" diffChangeLog > "./$1-diff-changelog.xml"
elif [ "$2" = "validate" ]; then
  java -jar "./lib/liquibase.jar" --logLevel="$LOG_LEVEL" --defaultsFile="./$1-liquibase.properties" --classpath="$CLASSPATH" --changeLogFile="scripts/changelog/$1-changelog-master.xml" --contexts="$CONTEXTS" validate
else
  echo "Errors:"
  echo "   Invalid command"
  displayUsage
  exit 1
fi
