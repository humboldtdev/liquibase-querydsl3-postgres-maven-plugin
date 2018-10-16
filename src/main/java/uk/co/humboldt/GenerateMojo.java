package uk.co.humboldt;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.mysema.query.sql.Configuration;
import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.sql.codegen.MetaDataExporter;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;

@Mojo(name = "generate-querydsl-sources",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateMojo
    extends AbstractMojo
{
    @Parameter(property = "liquibaseFile", required = true)
    private File liquibaseFile;

    @Parameter(property = "targetPackage", required = true)
    private String targetPackage;

    @Parameter(property = "outputDirectory",
        defaultValue = "${project.build.directory}/generated-sources/SQL")
    private File outputDirectory;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "latest")
    private String postgresVersion;

    public void execute()
        throws MojoExecutionException
    {
        File f = outputDirectory;

        if ( !f.exists() )
        {
            if (! f.mkdirs())
                throw new MojoExecutionException(
                        "Unable to create output directory " + outputDirectory);
        }

        getLog().info("Starting Postgres " + postgresVersion);
        try(PostgreSQLContainer container = new PostgreSQLContainer("postgres:" + postgresVersion)) {
            container.start();
            generateQClasses(container);
            project.addCompileSourceRoot(outputDirectory.getPath());
        } catch (Exception ex) {
            throw new MojoExecutionException("Unable to generate QueryDSL classes", ex);
        }
    }

    private void generateQClasses(PostgreSQLContainer container) throws Exception {

        try (Connection connection = container.createConnection("")) {

            getLog().info("Creating database schema");

            // Play back our standard liquibase script
            Liquibase liquibase = createLiquibase(connection);
            liquibase.dropAll();
            liquibase.update("");

            getLog().info("Generating Q Classes from metadata");
            MetaDataExporter exporter = new MetaDataExporter();
            exporter.setPackageName(targetPackage);
            exporter.setTargetFolder(outputDirectory);
            Configuration conf = new Configuration(SQLTemplates.DEFAULT);
            conf.registerNumeric(1, 30, 0, 3, BigDecimal.class);
            exporter.setConfiguration(conf);
            exporter.export(connection.getMetaData());
        }
    }

    private Liquibase createLiquibase(Connection c)
            throws LiquibaseException {
        return new Liquibase(liquibaseFile.getName(),
                new FileSystemResourceAccessor(liquibaseFile.getParent()),
                createDatabase(c));
    }

    private Database createDatabase(Connection c)
            throws DatabaseException {
        return DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
                new JdbcConnection(c));
    }
}
