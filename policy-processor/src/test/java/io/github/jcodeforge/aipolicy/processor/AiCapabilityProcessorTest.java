package io.github.jcodeforge.aipolicy.processor;

import com.google.testing.compile.CompilationSubject;
import org.junit.Test;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import io.github.jcodeforge.aipolicy.capability.CapabilityIndexProvider;
import static com.google.testing.compile.Compiler.javac;
import static com.google.testing.compile.JavaFileObjects.forSourceLines;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AiCapabilityProcessorTest {

    private static final String GENERATED_PACKAGE =
            "io.github.jcodeforge.aipolicy.generated";

    private static final String GENERATED_JAVA_INDEX =
            "GeneratedJavaCapabilityIndex";

    private static final String GENERATED_JAVA_PROVIDER =
            "GeneratedCapabilityIndexProvider";

    private static final String GENERATED_KSP_PROVIDER =
            "GeneratedKspCapabilityIndexProvider";

    @Test
    public void generatesCapabilityIndex() {
        JavaFileObject source = forSourceLines(
                "example.CustomerService",

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
                "",
                "}"
        );

        CompilationSubject.assertThat(
                javac()
                        .withProcessors(new AiCapabilityProcessor())
                        .compile(source)
        ).succeeded();
    }

    @Test
    public void generatesCapabilityIndexProvider() {
        JavaFileObject source = forSourceLines(
                "example.CustomerService",

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
                "",
                "}"
        );

        CompilationSubject.assertThat(javac()
                .withProcessors(new AiCapabilityProcessor())
                .compile(source)).succeeded();
    }

    @Test
    public void generatesIndexForMultipleCapabilities() {
        JavaFileObject customerService = forSourceLines(
                "example.CustomerService",

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
                "",
                "}"
        );

        JavaFileObject invoiceService = forSourceLines(
                "example.InvoiceService",

                "package example;",
                "",
                "import io.github.jcodeforge.aipolicy.capability.AiCapability;",
                "",
                "public final class InvoiceService {",
                "",
                "    @AiCapability(",
                "        name = \"invoice.create\",",
                "        description = \"Create an invoice\"",
                "    )",
                "    public void createInvoice() {",
                "    }",
                "",
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
                "",
                "public final class CustomerService {",
                "",
                "    public void readCustomer() {",
                "    }",
                "",
                "}"
        );

        CompilationSubject.assertThat(javac()
                .withProcessors(new AiCapabilityProcessor())
                .compile(source))
                .succeeded();
    }

    @Test
    public void generatesCapabilityMetadata() throws Exception {
        Path outputDirectory = Files.createTempDirectory("ai-policy-processor-test");
        Path sourceDirectory = Files.createTempDirectory("ai-policy-processor-source");
        Path sourceFile = sourceDirectory.resolve("CustomerService.java");

        Files.write(
                sourceFile,
                Arrays.asList(
                        "package example;",
                        "",
                        "import io.github.jcodeforge.aipolicy.CallerType;",
                        "import io.github.jcodeforge.aipolicy.capability.AiCapability;",
                        "",
                        "public final class CustomerService {",
                        "",
                        "    @AiCapability(",
                        "        name = \"customer.delete\",",
                        "        description = \"Delete customer\",",
                        "        userInitiatedRequired = true,",
                        "        allowedCallerTypes = {CallerType.SELF},",
                        "        requiredPermissions = {\"android.permission.INTERNET\"}",
                        "    )",
                        "    public void deleteCustomer() {",
                        "    }",
                        "",
                        "}"
                )
        );

        compile(outputDirectory, sourceFile);

        Path generatedIndex = outputDirectory.resolve(GENERATED_PACKAGE.replace('.', '/')
                        + "/" + GENERATED_JAVA_INDEX + ".java");

        assertTrue(Files.exists(generatedIndex));

        String indexSource = Files.readString(generatedIndex);

        assertTrue(indexSource.contains("new Capability("));
        assertTrue(indexSource.contains("\"customer.delete\""));
        assertTrue(indexSource.contains("\"Delete customer\""));
        assertTrue(indexSource.contains("true"));
        assertTrue(indexSource.contains("CallerType.SELF"));
        assertTrue(indexSource.contains("\"android.permission.INTERNET\""));
        assertFalse(indexSource.contains("example.CustomerService.class"));
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
                        "",
                        "}"
                )
        );

        compile(outputDirectory, sourceFile);

        Path generatedIndex =
                outputDirectory.resolve(
                        GENERATED_PACKAGE.replace('.', '/')
                                + "/"
                                + GENERATED_JAVA_INDEX
                                + ".java"
                );

        Path generatedProvider =
                outputDirectory.resolve(
                        GENERATED_PACKAGE.replace('.', '/')
                                + "/"
                                + GENERATED_JAVA_PROVIDER
                                + ".java"
                );

        assertTrue(Files.exists(generatedIndex));
        assertTrue(Files.exists(generatedProvider));

        String indexSource = Files.readString(generatedIndex);

        assertTrue(indexSource.contains("implements CapabilityIndex"));
        assertTrue(indexSource.contains("List<Capability> getCapabilities()"));
        assertTrue(indexSource.contains("new Capability("));
        assertTrue(indexSource.contains("\"customer.read\""));
        assertTrue(indexSource.contains("\"Read customer information\""));

        String providerSource = Files.readString(generatedProvider);

        assertTrue(providerSource.contains("implements CapabilityIndexProvider"));

        assertTrue(providerSource.contains("new " + GENERATED_JAVA_INDEX + "()"));
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
                        "",
                        "}"
                )
        );

        compile(outputDirectory, sourceFile);

        Path serviceFile = outputDirectory.resolve("META-INF/services/"
                        + CapabilityIndexProvider.class.getName());

        assertTrue(Files.exists(serviceFile));

        String serviceContent = Files.readString(serviceFile);

        assertTrue(serviceContent.contains(GENERATED_PACKAGE + "." + GENERATED_JAVA_PROVIDER));
        assertTrue(serviceContent.contains(GENERATED_PACKAGE + "." + GENERATED_KSP_PROVIDER));
    }

    private void compile(Path outputDirectory, Path sourceFile) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        assertNotNull(compiler);

        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null,
                null, null)) {

            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, List.of(outputDirectory.toFile()));

            Iterable<? extends JavaFileObject> compilationUnits =
                    fileManager.getJavaFileObjects(
                            sourceFile.toFile()
                    );

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
    }
}