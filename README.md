# Liquibase to Querydsl Maven Plugin

[![Release](https://jitpack.io/v/humboldtdev/liquibase-querydsl3-postgres-maven-plugin.svg)](https://jitpack.io/#humboldtdev/liquibase-querydsl3-postgres-maven-plugin)

This Maven plugin generates [Querydsl SQL classes](http://www.querydsl.com/static/querydsl/3.7.4/reference/html_single/#sql_integration)
 from a [Liquibase](http://www.liquibase.org/) script. This version generates 
 classes for Querydsl 3.

The configuration is simpler than the standard Querydsl Maven plugin, but this plugin uses Docker to build the schema in a fresh Postgresql database on each run.

## Usage

Default configuration:

```xml
    <build>
        <plugins>
            <plugin>
                <groupId>com.github.humboldtdev</groupId>
                <artifactId>liquibase-querydsl3-postgres-maven-plugin</artifactId>
                <version>1.0.1</version>
                <configuration>
                    <outputDirectory>target/generated-sources/SQL</outputDirectory>
                    <targetPackage>uk.co.humboldt.MyApplication.SQL</targetPackage>
                    <liquibaseFile>src/main/resources/db_changelog.xml</liquibaseFile>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>generate-querydsl-sources</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        ...
        </plugins>
    </build>
```

The plugin is hosted by [JitPack](https://jitpack.io/):

```xml    
	<repositories>
		<repository>
			<id>jitpack.io</id>
			<url>https://jitpack.io</url>
		</repository>
		...
	</repositories>
```

# Options

Other parameters are:

###### liquibaseFile

The file path of the Liquibase script, relative to project base.

###### targetPackage

The package name for the generated classes.

###### outputDirectory

Output directory, relative to project base. The plugin will automatically add this to
the sources list.

###### postgresVersion

Version of the [Postgres Docker image](https://hub.docker.com/_/postgres/) to use. Defaults to `latest`.
