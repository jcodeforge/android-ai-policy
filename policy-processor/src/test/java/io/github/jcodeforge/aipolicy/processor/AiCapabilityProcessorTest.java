package io.github.jcodeforge.aipolicy.processor;

import com.google.testing.compile.Compilation;
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
    public void generatesIndexForMultipleCapabilities() {
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

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        assertNotNull(compiler);

        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null,
                null, null)) {

            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, List.of(outputDirectory.toFile()));

            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(
                    sourceFile.toFile());

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

        assertTrue(Files.exists(generatedIndex));

        String indexSource = Files.readString(generatedIndex);

        /*
         * The generated index must now contain Capability instances,
         * not capability classes.
         */
        assertTrue(indexSource.contains("new Capability("));
        assertTrue(indexSource.contains("\"customer.delete\""));
        assertTrue(indexSource.contains("\"Delete customer\""));
        assertTrue(
                indexSource.contains("true")
        );

        assertTrue(
                indexSource.contains("CallerType.SELF")
        );

        assertTrue(
                indexSource.contains(
                        "\"android.permission.INTERNET\""
                )
        );

        /*
         * The old reflection-based representation must no longer
         * be generated.
         */
        assertFalse(indexSource.contains(
                "example.CustomerService.class"
        ));
    }

    @Test
    public void generatesCapabilityIndexAndProvider()
            throws Exception {

        Path outputDirectory =
                Files.createTempDirectory(
                        "ai-policy-processor-test"
                );

        Path sourceDirectory =
                Files.createTempDirectory(
                        "ai-policy-processor-source"
                );

        Path sourceFile =
                sourceDirectory.resolve(
                        "CustomerService.java"
                );

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

        JavaCompiler compiler =
                ToolProvider.getSystemJavaCompiler();

        assertNotNull(compiler);

        try (StandardJavaFileManager fileManager =
                     compiler.getStandardFileManager(
                             null,
                             null,
                             null)) {

            fileManager.setLocation(
                    StandardLocation.CLASS_OUTPUT,
                    List.of(outputDirectory.toFile())
            );

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

            task.setProcessors(
                    List.of(new AiCapabilityProcessor())
            );

            assertTrue(task.call());
        }

        Path generatedIndex =
                outputDirectory.resolve(
                        GENERATED_PACKAGE.replace('.', '/')
                                + "/GeneratedCapabilityIndex.java"
                );

        Path generatedProvider =
                outputDirectory.resolve(
                        GENERATED_PACKAGE.replace('.', '/')
                                + "/GeneratedCapabilityIndexProvider.java"
                );

        assertTrue(Files.exists(generatedIndex));
        assertTrue(Files.exists(generatedProvider));

        String indexSource =
                Files.readString(generatedIndex);

        assertTrue(
                indexSource.contains(
                        "implements CapabilityIndex"
                )
        );

        assertTrue(
                indexSource.contains(
                        "List<Capability> getCapabilities()"
                )
        );

        assertTrue(
                indexSource.contains(
                        "new Capability("
                )
        );

        assertTrue(
                indexSource.contains(
                        "\"customer.read\""
                )
        );

        assertTrue(
                indexSource.contains(
                        "\"Read customer information\""
                )
        );

        String providerSource =
                Files.readString(generatedProvider);

        assertTrue(
                providerSource.contains(
                        "implements CapabilityIndexProvider"
                )
        );

        assertTrue(providerSource.contains("new GeneratedCapabilityIndex()"));
    }

    @Test
    public void generatesProviderServiceFile()
            throws Exception {

        Path outputDirectory =
                Files.createTempDirectory(
                        "ai-policy-processor-test"
                );

        Path sourceDirectory =
                Files.createTempDirectory(
                        "ai-policy-processor-source"
                );

        Path sourceFile =
                sourceDirectory.resolve(
                        "CustomerService.java"
                );

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

        JavaCompiler compiler =
                ToolProvider.getSystemJavaCompiler();

        assertNotNull(compiler);

        try (StandardJavaFileManager fileManager =
                     compiler.getStandardFileManager(
                             null,
                             null,
                             null)) {

            fileManager.setLocation(
                    StandardLocation.CLASS_OUTPUT,
                    List.of(outputDirectory.toFile())
            );

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

            task.setProcessors(
                    List.of(new AiCapabilityProcessor())
            );

            assertTrue(task.call());
        }

        Path serviceFile =
                outputDirectory.resolve(
                        "META-INF/services/"
                                + CapabilityIndexProvider.class.getName()
                );

        assertTrue(Files.exists(serviceFile));

        String serviceContent =
                Files.readString(serviceFile);

        assertTrue(serviceContent.contains(GENERATED_PACKAGE + ".GeneratedCapabilityIndexProvider"));
    }

    @Test
    public void generatesAppFunctionCapabilityIndex() throws Exception {
        JavaFileObject appFunctionAnnotation = forSourceLines(
                "androidx.appfunctions.AppFunction",
                "package androidx.appfunctions;",
                "",
                "public @interface AppFunction {",
                "}"
        );

        JavaFileObject appFunctionServiceEntryPointAnnotation = forSourceLines(
                "androidx.appfunctions.AppFunctionServiceEntryPoint",
                "package androidx.appfunctions;",
                "",
                "public @interface AppFunctionServiceEntryPoint {",
                "    String serviceName();",
                "    String appFunctionXmlFileName();",
                "}"
        );

        JavaFileObject source = forSourceLines(
                "example.CustomerService",
                "package example;",
                "",
                "import androidx.appfunctions.AppFunction;",
                "import androidx.appfunctions.AppFunctionServiceEntryPoint;",
                "import io.github.jcodeforge.aipolicy.capability.AiCapability;",
                "",
                "@AppFunctionServiceEntryPoint(",
                "        serviceName = \"CustomerAppFunctionService\",",
                "        appFunctionXmlFileName = \"customer_app_function_service\"",
                ")",
                "public final class CustomerService {",
                "",
                "    @AppFunction",
                "    @AiCapability(",
                "        name = \"customer.read\",",
                "        description = \"Read customer information\"",
                "    )",
                "    public String readCustomer() {",
                "        return \"Customer\";",
                "    }",
                "",
                "}"
        );

        Compilation compilation = javac()
                .withProcessors(new AiCapabilityProcessor())
                .compile(appFunctionAnnotation, appFunctionServiceEntryPointAnnotation, source);

        CompilationSubject.assertThat(compilation).succeeded();

        String generatedSource = compilation.generatedSourceFile(
                GENERATED_PACKAGE + ".GeneratedAppFunctionCapabilityIndex")
                .get()
                .getCharContent(false)
                .toString();

        assertTrue(generatedSource.contains("new AppFunctionCapability("));
        assertTrue(generatedSource.contains("example.CustomerAppFunctionService#readCustomer"));
        assertFalse(generatedSource.contains("example.CustomerService#readCustomer"));
        assertTrue(generatedSource.contains("\"customer.read\""));
        assertTrue(generatedSource.contains("\"Read customer information\""));
        assertTrue(generatedSource.contains("implements AppFunctionCapabilityIndex"));
    }
}