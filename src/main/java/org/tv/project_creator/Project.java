package org.tv.project_creator;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.nio.file.Path;

@Data
@Log4j2
public class Project {
    private String id;
    private Path directory;
    private CustomerData customerData;
}
