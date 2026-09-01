package io.github.jcodeforge.aipolicy.processor;

import com.google.testing.compile.CompilationSubject;
import org.junit.Test;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import static com.google.testing.compile.Compiler.javac;
import static com.google.testing.compile.JavaFileObjects.forSourceLines;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import io.github.jcodeforge.aipolicy.capability.CapabilityIndexProvider;

public class AiCapabilityProcessorTest {

    private static final String GENERATED_PACKAGE =
            "io.github.jcodeforge.aipolicy.capability.generated";

    @Test
    public void generatesCapabilityIndex() {

        JavaFileObject source = forSourceLines(
                "example.CustomerService",

                "package example;",

                "import io.github.jcodeforge.aipolicy.capability.AiCapability;",

                "public final class CustomerService {",

                "    @AiCapability(",
                "        name = \"customer.read\",",
                "        description = \"Read customer information\"",
                "    )",
                "    public void readCustomer() {",
                "    }",

                "}"
        );

        CompilationSubject.assertThat(javac()
                .withProcessors(new AiCapabilityProcessor())
                .compile(source))
                .succeeded();
    }

    @Test
    public void generatesCapabilityIndexProvider() {
        JavaFileObject source = forSourceLines(
                "example.CustomerService",

                "package example;",

                "import io.github.jcodeforge.aipolicy.capability.AiCapability;",

                "public final class CustomerService {",

                "    @AiCapability(",
                "        name = \"customer.read\",",
                "        description = \"Read customer information\"",
                "    )",
                "    public void readCustomer() {",
                "    }",

                "}"
        );

        CompilationSubject.assertThat(javac()
                        .withProcessors(new AiCapabilityProcessor())
                        .compile(source))
                .succeeded();
    }

    @Test
    public void generatesIndexForMultipleCapabilityClasses() {
        JavaFileObject customerService = forSourceLines(
                "example.CustomerService",

                "package example;",

                "import io.github.jcodeforge.aipolicy.capability.AiCapability;",

                "public final class CustomerService {",

                "    @AiCapability(",
                "        name = \"customer.read\",",
                "        description = \"Read customer information\"",
                "    )",
                "    public void readCustomer() {",
                "    }",

                "}"
        );

        JavaFileObject invoiceService = forSourceLines(
                "example.InvoiceService",

                "package example;",

                "import io.github.jcodeforge.aipolicy.capability.AiCapability;",

                "public final class InvoiceService {",

                "    @AiCapability(",
                "        name = \"invoice.create\",",
                "        description = \"Create an invoice\"",
                "    )",
                "    public void createInvoice() {",
                "    }",

                "}"
        );

        CompilationSubject.assertThat(javac()
                .withProcessors(new AiCapabilityProcessor())
                .compile(customerService, invoiceService))
                .succeeded();
    }

    @Test
    public void doesNotFailWhenNoCapabilitiesExist() {
        JavaFileObject source = forSourceLines(
                "example.CustomerService",

                "package example;",

                "public final class CustomerService {",

                "    public void readCustomer() {",
                "    }",

                "}"
        );

        CompilationSubject.assertThat(javac()
                .withProcessors(new AiCapabilityProcessor())
                .compile(source))
                .succeeded();
    }


    @Test
    public void generatesCapabilityIndexAndProvider() throws Exception {
        Path outputDirectory = Files.createTempDirectory("ai-policy-processor-test");
        Path sourceDirectory = Files.createTempDirectory("ai-policy-processor-source");
        Path sourceFile = sourceDirectory.resolve("CustomerService.java");

        Files.write(
                sourceFile,
                Arrays.asList(
                        "package example;",
                        "",
                        "import io.github.jcodeforge.aipolicy.capability.AiCapability;",
                        "",
                        "public final class CustomerService {",
                        "",
                        "    @AiCapability(",
                        "        name = \"customer.read\",",
                        "        description = \"Read customer information\"",
                        "    )",
                        "    public void readCustomer() {",
                        "    }",
                        "}"
                )
        );

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        assertNotNull(compiler);

        try (StandardJavaFileManager fileManager =
                     compiler.getStandardFileManager(null, null, null)) {

            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, List.of(outputDirectory.toFile()));

            Iterable<? extends JavaFileObject> compilationUnits =
                    fileManager.getJavaFileObjects(sourceFile.toFile());

            JavaCompiler.CompilationTask task =
                    compiler.getTask(
                            null,
                            fileManager,
                            null,
                            null,
                            null,
                            compilationUnits
                    );

            task.setProcessors(List.of(new AiCapabilityProcessor()));

            assertTrue(task.call());
        }

        Path generatedIndex =
                outputDirectory.resolve(
                        "io/github/jcodeforge/aipolicy/capability/generated/"
                                + "GeneratedCapabilityIndex.java"
                );

        Path generatedProvider =
                outputDirectory.resolve(
                        "io/github/jcodeforge/aipolicy/capability/generated/"
                                + "GeneratedCapabilityIndexProvider.java"
                );

        assertTrue(Files.exists(generatedIndex));
        assertTrue(Files.exists(generatedProvider));

        String indexSource = Files.readString(generatedIndex);

        assertTrue(indexSource.contains("example.CustomerService.class"));

        String providerSource = Files.readString(generatedProvider);

        assertTrue(providerSource.contains("implements CapabilityIndexProvider"));
        assertTrue(providerSource.contains("new GeneratedCapabilityIndex()"));
    }

    @Test
    public void generatesProviderServiceFile() throws Exception {
        Path outputDirectory = Files.createTempDirectory("ai-policy-processor-test");
        Path sourceDirectory = Files.createTempDirectory("ai-policy-processor-source");
        Path sourceFile = sourceDirectory.resolve("CustomerService.java");

        Files.write(
                sourceFile,
                Arrays.asList(
                        "package example;",
                        "",
                        "import io.github.jcodeforge.aipolicy.capability.AiCapability;",
                        "",
                        "public final class CustomerService {",
                        "",
                        "    @AiCapability(",
                        "        name = \"customer.read\",",
                        "        description = \"Read customer information\"",
                        "    )",
                        "    public void readCustomer() {",
                        "    }",
                        "}"
                )
        );

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        try (StandardJavaFileManager fileManager =
                     compiler.getStandardFileManager(null, null, null)) {

            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, List.of(outputDirectory.toFile()));

            Iterable<? extends JavaFileObject> compilationUnits =
                    fileManager.getJavaFileObjects(sourceFile.toFile());

            JavaCompiler.CompilationTask task =
                    compiler.getTask(
                            null,
                            fileManager,
                            null,
                            null,
                            null,
                            compilationUnits
                    );

            task.setProcessors(List.of(new AiCapabilityProcessor()));

            assertTrue(task.call());
        }

        Path serviceFile = outputDirectory.resolve("META-INF/services/"
                        + CapabilityIndexProvider.class.getName());

        assertTrue(Files.exists(serviceFile));

        String serviceContent = Files.readString(serviceFile);

        assertTrue(serviceContent.contains("io.github.jcodeforge.aipolicy.capability.generated."
                        + "GeneratedCapabilityIndexProvider"));
    }
}