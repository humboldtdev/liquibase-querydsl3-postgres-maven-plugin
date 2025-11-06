package uk.co.humboldt;

import liquibase.resource.DirectoryResourceAccessor;
import liquibase.resource.Resource;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LoggingResourceAccessor extends DirectoryResourceAccessor {

    private final Log log;

    public LoggingResourceAccessor(Log log, String base) throws FileNotFoundException {
        super(new File(base));
        this.log = log;
    }

    @Override
    public Resource get(String path) throws IOException {
        log.info("getResource " + path);
        return super.get(path);
    }

}
