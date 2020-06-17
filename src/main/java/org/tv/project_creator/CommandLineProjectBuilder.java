package org.tv.project_creator;

import lombok.extern.log4j.Log4j2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Collectors;

@Log4j2
public class CommandLineProjectBuilder {
    private Path outputDirectory;
    private String projectPrefix;

    /**
     * Construct the project builder based on some input parameters.
     *
     * @param outputDirectory output directory in which a sub directory with the project will be created
     * @param prefix          prefix name of the project
     */
    public CommandLineProjectBuilder(Path outputDirectory, String prefix) {
        this.outputDirectory = outputDirectory;
        this.projectPrefix = prefix;
        validate();
    }

    /**
     * Validate the input parameters.
     */
    private void validate() {
        if (outputDirectory == null) {
            log.error("Argument \"outputDirectory\" cannot be null.");
            System.exit(1);
        }
        if (!Files.isDirectory(outputDirectory)) {
            log.error("Output directory does not exist.");
            System.exit(1);
        }
        if (projectPrefix == null) projectPrefix = "";
    }

    /**
     * Build a project based on the user input from the commandline
     *
     * @return Project data
     */
    public Project build() {
        Project project = new Project();
        try {
            var ld = Files.list(outputDirectory).filter(path -> {
                var name = path.getFileName().toString();
                return name.startsWith(projectPrefix + "-");
            }).map(path -> {
                var s = path.getFileName().toString().substring(projectPrefix.length() + 1);
                return Integer.parseInt(s);
            }).collect(Collectors.toList());
            var n = -1;
            for (var x : ld) {
                if (x > n) n = x;
            }
            n += 1;
            project.setId(String.valueOf(n));
            // TODO ask if the proposed project number is ok, or if another one is requested
            // If the latter exists, do nothing
            project.setDirectory(Paths.get(outputDirectory.toAbsolutePath().toString(), String.format("pr-%06d", n)));
            Files.createDirectories(project.getDirectory());

            boolean again = true;
            var nl = System.lineSeparator();
            while (again) {
                CustomerData customerData = new CustomerData();
                customerData.setDirectory(Paths.get(project.getDirectory().toAbsolutePath().toString(), "00_data_customer"));
                Scanner scanner = new Scanner(System.in);
                System.out.println("Customer: ");
                customerData.setCustomer(scanner.next());
                System.out.println("Contact: ");
                customerData.setContact(scanner.next());
                System.out.println("Project description: ");
                customerData.setDescription(scanner.next());

                StringBuilder sb = new StringBuilder();
                sb.append("Customer: ").append(customerData.getCustomer()).append(nl)
                        .append("Contact: ").append(customerData.getContact()).append(nl)
                        .append("Description: ").append(customerData.getDescription()).append(nl)
                        .append("Path: ").append(customerData.getDirectory().toAbsolutePath().toString()).append(nl);
                System.out.println(sb.toString());
                var ans = questionYesNo(scanner, "Is all data correct");
                if (ans && customerData.isValid()) {
                    again = false;
                    Files.createDirectories(customerData.getDirectory());
                    File customerFile = Paths.get(customerData.getDirectory().toAbsolutePath().toString(), "customer").toFile();
                    File descriptionFile = Paths.get(customerData.getDirectory().toAbsolutePath().toString(), "description").toFile();
                    File contactFile = Paths.get(customerData.getDirectory().toAbsolutePath().toString(), "contact").toFile();
                    boolean createdCustomer = customerFile.createNewFile();
                    boolean createdDescription = descriptionFile.createNewFile();
                    boolean createdContact = contactFile.createNewFile();

                    if (!createdCustomer) {
                        log.warn(String.format("File \"%s\" already existed", customerFile.getAbsolutePath().toString()));
                    } else {
                        var writer = new BufferedWriter(new FileWriter(customerFile));
                        writer.write(customerData.getCustomer());
                        writer.close();
                    }
                    if (!createdDescription) {
                        log.warn(String.format("File \"%s\" already existed", descriptionFile.getAbsolutePath().toString()));
                    } else {
                        var writer = new BufferedWriter(new FileWriter(descriptionFile));
                        writer.write(customerData.getDescription());
                        writer.close();
                    }
                    if (!createdContact) {
                        log.warn(String.format("File \"%s\" already existed", contactFile.getAbsolutePath().toString()));
                    } else {
                        var writer = new BufferedWriter(new FileWriter(contactFile));
                        writer.write(customerData.getContact());
                        writer.close();
                    }
                }
            }

        } catch (IOException | NumberFormatException e) {
            log.error(e);
            System.exit(1);
        }
        return project;
    }

    private boolean questionYesNo(Scanner scanner, String msg) {
        System.out.println(msg);
        var ans = scanner.next().toLowerCase();
        if (ans.equals("y") || ans.equals("yes")) {
            return true;
        }
        return false;
    }

}
