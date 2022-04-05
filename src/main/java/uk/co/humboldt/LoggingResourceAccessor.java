package uk.co.humboldt;

import liquibase.resource.FileSystemResourceAccessor;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class LoggingResourceAccessor extends FileSystemResourceAccessor {

    private final Log log;

    public LoggingResourceAccessor(Log log, String base) {
        super(base);
        this.log = log;
    }

    @Override
    public Set<InputStream> getResourcesAsStream(String path) throws IOException {
        log.info("getResource " + path);

        // Get arounnd mis-handling of relative paths.
        if (path.startsWith("file:/"))
            path = path.substring(6);
        return super.getResourcesAsStream(path);
    }

}
