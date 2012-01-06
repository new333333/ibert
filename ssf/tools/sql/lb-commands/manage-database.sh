#!/bin/sh

displayUsage () {
echo "Usage: manage-database [db type] [command]"
echo ""
echo "db types:"
echo "   mysql                               MySQL"
echo "   oracle                              Oracle"
echo "   sqlserver                           MS SQL Server"
echo ""
echo "primary commands:"
echo "   updateDatabase                      Update database to current version by applying un-run change sets to the database."
echo "   generateSqlToUpdateDatabase         Apply un-run change sets to generate SQL in a file which can later be executed to update database to current version."
echo "   markDatabaseAsUpdated               Mark all change sets as ran against the database. Used only for transition from earlier legacy schema."
echo "   generateSqlToMarkDatabaseAsUpdated  Generate SQL in a file which can later be executed to mark all change sets as ran against the database."
echo ""
echo "secondary commands:"
echo "   exportSchema                        Export schema from existing database into a change log file. Useful for verification, testing or troubleshooting."
echo "   exportData                          Export data from existing database into a change log file. Useful for verification, testing or troubleshooting."
echo "   diffDatabases                       Output schema differences between two databases into a change log file. Useful for verification, testing or troubleshooting."
echo ""
echo "Note: Additional parameters are read in from corresponding [db type]-liquibase.properties file.
}

WD='cd'
cd $WD

CP=./lib/mysql-connector.jar:./lib/ojdbc6.jar:./lib/jtds.jar

# Set default log level. Valid values are debug, info, warning, severe, off.
LL=info

JAVA_OPTS=

if [ "$1" != "mysql" ]; then
  if [ "$1" != "oracle" ]; then
    if [ "$1" != "sqlserver" ]; then
      echo "Errors:"
      echo "   Invalid database type"
      displayUsage
      exit 1
    fi
  fi
fi

if [ "$2 = "updateDatabase" ]; then
  java -jar "./lib/liquibase.jar" --logLevel="$LL" --defaultsFile="./$1-liquibase.properties" --classpath="$CP" --changeLogFile="./scripts/changelog/$1-changelog-master.xml" update
elif [ "$2 = "generateSqlToUpdateDatabase" ]; then
  java -jar "./lib/liquibase.jar" --logLevel="$LL" --defaultsFile="./$1-liquibase.properties" --classpath="$CP" --changeLogFile="./scripts/changelog/$1-changelog-master.xml" updateSQL > "./$1-update.sql"
elif [ "$2 = "markDatabaseAsUpdated" ]; then
  java -jar "./lib/liquibase.jar" --logLevel="$LL" --defaultsFile="./$1-liquibase.properties" --classpath="$CP" --changeLogFile="./scripts/changelog/$1-changelog-master.xml" changeLogSync
elif [ "$2 = "generateSqlToMarkDatabaseAsUpdated" ]; then
  java -jar "./lib/liquibase.jar" --logLevel="$LL" --defaultsFile="./$1-liquibase.properties" --classpath="$CP" --changeLogFile="./scripts/changelog/$1-changelog-master.xml" changeLogSyncSQL > "./$1-markasupdated.sql"
elif [ "$2 = "exportSchema" ]; then
  java -jar "./lib/liquibase.jar" --logLevel="$LL" --defaultsFile="./$1-liquibase.properties" --classpath="$CP" generateChangeLog > "./$1-schema-changelog.xml"
elif [ "$2 = "exportData" ]; then
  java -jar "./lib/liquibase.jar" --logLevel="$LL" --defaultsFile="./$1-liquibase.properties" --classpath="$CP" --diffTypes="data" generateChangeLog > "./$1-data-changelog.xml"
elif [ "$2 = "diffDatabases" ]; then
  java -jar "./lib/liquibase.jar" --logLevel="$LL" --defaultsFile="./$1-liquibase.properties" --classpath="$CP" diffChangeLog > "./$1-diff-changelog.xml"
else
  echo "Errors:"
  echo "   Invalid command"
  displayUsage
  exit 1
fi
