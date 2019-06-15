package eu.stamp_project.automaticbuilder.maven;

import eu.stamp_project.automaticbuilder.AutomaticBuilder;
import eu.stamp_project.utils.program.InputConfiguration;
import eu.stamp_project.utils.DSpotUtils;
import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.reflect.declaration.CtType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.nio.charset.Charset.forName;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 09/07/17.
 */
public class MavenAutomaticBuilder implements AutomaticBuilder {

    public static final String CMD_PIT_MUTATION_COVERAGE = "org.pitest:pitest-maven:mutationCoverage";

    public static final String OPT_TARGET_TESTS = "-DtargetTests=";

    private static final Logger LOGGER = LoggerFactory.getLogger(MavenAutomaticBuilder.class);

    private String classpath = null;

    private boolean hasGeneratePom = false;

    public MavenAutomaticBuilder() {
        delete(false);
    }

    @Override
    public String compileAndBuildClasspath() {
        System.out.println("in maven automatic builder");
        try {
            FileUtils.writeStringToFile(new File("/home/andrew/Skrivbord/stamp/dspot/dspot/src/test/resources/test-projects2/target/dspot/dependencies/eu/stamp_project/compare/test.txt"), "before", forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (this.classpath == null) {
            this.runGoals(false,

                    "clean",
                    "test",
                    "-DskipTests",
                    "dependency:build-classpath",
                    "-Dmdep.outputFile=" + "target/dspot/classpath"
            );
            System.out.println("tttttttt after run goals");
            try {
                FileUtils.writeStringToFile(new File("/home/andrew/Skrivbord/stamp/dspot/dspot/src/test/resources/test-projects2/target/dspot/dependencies/eu/stamp_project/compare/after.txt"), "after", forName("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            final File classpathFile = new File(InputConfiguration.get().getAbsolutePathToProjectRoot() + "/target/dspot/classpath");
            try (BufferedReader buffer = new BufferedReader(new FileReader(classpathFile))) {
                this.classpath = buffer.lines().collect(Collectors.joining());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return this.classpath;
    }

    @Override
    public void compile() {
        this.runGoals(false,
                "clean",
                "test",
                "-DskipTests"
        );
    }

    @Override
    public String buildClasspath() {
        if (this.classpath == null) {
            try {
                final File classpathFile = new File(InputConfiguration.get().getAbsolutePathToProjectRoot() + "/target/dspot/classpath");
                if (!classpathFile.exists()) {
                    this.runGoals(false,
                            "dependency:build-classpath",
                            "-Dmdep.outputFile=" + "target/dspot/classpath"
                    );
                }
                try (BufferedReader buffer = new BufferedReader(new FileReader(classpathFile))) {
                    this.classpath = buffer.lines().collect(Collectors.joining());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return this.classpath;
    }

    private boolean shouldDeleteGeneratedPom() {
        return hasGeneratePom ||
                new File(InputConfiguration.get().getAbsolutePathToProjectRoot() + "/" + DSpotPOMCreator.getPOMName()).exists();
    }

    private void delete(boolean displayError) {
        if (this.shouldDeleteGeneratedPom()) {
            this.hasGeneratePom = false;
            try {
                FileUtils.forceDelete(new File(InputConfiguration.get().getAbsolutePathToProjectRoot() + "/" + DSpotPOMCreator.getPOMName()));
            } catch (IOException e) {
                if (displayError) {
                    LOGGER.warn("Something bad happened when trying to delete {}.", DSpotPOMCreator.getPOMName());
                    e.printStackTrace();
                    LOGGER.warn("Ignoring, moving forward...");
                }
            }
        }
    }

    @Override
    public void reset() {
        delete(true);
    }

    @Override
    public void runPit(CtType<?>... testClasses) {
        try {
            FileUtils.deleteDirectory(new File(InputConfiguration.get().getAbsolutePathToProjectRoot() + "/target/pit-reports"));
        } catch (Exception ignored) {

        }
        try {
            String[] goals = new String[]{
                    CMD_PIT_MUTATION_COVERAGE, //
                    testClasses.length > 0 ?
                            OPT_TARGET_TESTS + Arrays.stream(testClasses)
                                    .map(DSpotUtils::ctTypeToFullQualifiedName)
                                    .collect(Collectors.joining(",")) :
                            "" //
            };
            if (this.runGoals(true, goals) != 0) {
                throw new RuntimeException("Maven build failed! Enable verbose mode for more information (--verbose)");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void runPit() {
        this.runPit(new CtType<?>[0]);
    }

    private int runGoals(boolean specificPom, String... goals) {
        if (specificPom && !new File(InputConfiguration.get().getAbsolutePathToProjectRoot() + DSpotPOMCreator.getPOMName()).exists()) {
            DSpotPOMCreator.createNewPom();
            this.hasGeneratePom = true;
        }
        InvocationRequest request = new DefaultInvocationRequest();
        request.setGoals(Arrays.asList(goals));
        final String pomPathname = InputConfiguration.get().getAbsolutePathToProjectRoot() + (specificPom ? DSpotPOMCreator.getPOMName() : DSpotPOMCreator.POM_FILE);
        LOGGER.info("Using {} to run maven.", pomPathname);
        request.setPomFile(new File(pomPathname));
        request.setJavaHome(new File(System.getProperty("java.home")));
        if (specificPom) {
            request.setProfiles(Collections.singletonList(DSpotPOMCreator.PROFILE_ID));
        }

        Properties properties = new Properties();
        properties.setProperty("enforcer.skip", "true");
        properties.setProperty("checkstyle.skip", "true");
        properties.setProperty("cobertura.skip", "true");
        properties.setProperty("skipITs", "true");
        properties.setProperty("rat.skip", "true");
        properties.setProperty("license.skip", "true");
        properties.setProperty("findbugs.skip", "true");
        properties.setProperty("gpg.skip", "true");
        request.setProperties(properties);

        Invoker invoker = new DefaultInvoker();
        final String mavenHome = this.buildMavenHome();
        LOGGER.info("Using {} for maven home", mavenHome);
        invoker.setMavenHome(new File(mavenHome));
        LOGGER.info(String.format("run maven: %s/bin/mvn %s", mavenHome, String.join(" ", goals)));
        if (InputConfiguration.get().isVerbose()) {
            invoker.setOutputHandler(System.out::println);
            invoker.setErrorHandler(System.err::println);
        } else {
            invoker.setOutputHandler(null);
            invoker.setErrorHandler(null);
        }
        System.out.println("ttttttttttt before invoker.execute");

        try {
            return invoker.execute(request).getExitCode();
        } catch (MavenInvocationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getOutputDirectoryPit() {
        return DSpotPOMCreator.REPORT_DIRECTORY_VALUE;
    }

    private String buildMavenHome() {
        InputConfiguration configuration = InputConfiguration.get();
        String mavenHome = null;
        if (configuration != null) {
            if (!configuration.getMavenHome().isEmpty()) {
                mavenHome = configuration.getMavenHome();
            } else {
                mavenHome = getMavenHome(envVariable -> System.getenv().get(envVariable) != null,
                        envVariable -> System.getenv().get(envVariable),
                        "MAVEN_HOME", "M2_HOME");
                if (mavenHome == null) {
                    mavenHome = getMavenHome(path -> new File(path).exists(),
                            Function.identity(),
                            "/usr/share/maven/", "/usr/local/maven-3.3.9/", "/usr/share/maven3/");
                    if (mavenHome == null) {
                        throw new RuntimeException("Maven home not found, please set properly MAVEN_HOME or M2_HOME.");
                    }
                }
                // update the value inside the input configuration
                configuration.setMavenHome(mavenHome);
            }
        }
        return mavenHome;
    }

    private String getMavenHome(Predicate<String> conditional,
                                Function<String, String> getFunction,
                                String... possibleValues) {
        String mavenHome = null;
        final Optional<String> potentialMavenHome = Arrays.stream(possibleValues).filter(conditional).findFirst();
        if (potentialMavenHome.isPresent()) {
            mavenHome = getFunction.apply(potentialMavenHome.get());
        }
        return mavenHome;
    }
}
