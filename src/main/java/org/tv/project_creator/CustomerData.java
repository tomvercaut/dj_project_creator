package org.tv.project_creator;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.nio.file.Path;

@Data
@Log4j2
public class CustomerData {
    private Path directory;
    private String description;
    private String customer;
    private String contact;

    /**
     * Check if the directory is not null and if the properties are not empty.
     *
     * @return True if the directory is not null and if all properties are not empty.
     */
    public boolean isValid() {
        return directory != null && directory != null && customer != null && contact != null
                && !description.isBlank() && !customer.isBlank() && !customer.isBlank();
    }
}
