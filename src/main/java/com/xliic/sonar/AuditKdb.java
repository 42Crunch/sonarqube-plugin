package com.xliic.sonar;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xliic.sonar.model.Issues;

public class AuditKdb {

    private static final String PATH_TO_AUDIT_JSON = "/audit/audit-with-yaml.json";

    public static Issues loadIssues() throws JsonParseException, JsonMappingException, IOException {
        InputStream auditJson = AuditKdb.class.getResourceAsStream(PATH_TO_AUDIT_JSON);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(auditJson, Issues.class);
    }

}
