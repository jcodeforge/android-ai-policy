package io.github.jcodeforge.aipolicy.processor;

import io.github.jcodeforge.aipolicy.CallerType;
import io.github.jcodeforge.aipolicy.capability.AiCapability;
import io.github.jcodeforge.aipolicy.capability.Capability;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes(
        "io.github.jcodeforge.aipolicy.capability.AiCapability"
)
public final class AiCapabilityProcessor extends AbstractProcessor {

    private static final String GENERATED_PACKAGE =
            "io.github.jcodeforge.aipolicy.capability.generated";

    private static final String GENERATED_CLASS = "GeneratedCapabilityIndex";

    private final List<Capability> capabilities = new ArrayList<>();

    private boolean generated;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            if (!generated) {
                generateCapabilityIndex();
                generateCapabilityIndexProvider();
                generateCapabilityIndexProviderServiceFile();
                generated = true;
            }

            return false;
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(AiCapability.class)) {
            AiCapability annotation = element.getAnnotation(AiCapability.class);

            if (annotation == null) {
                continue;
            }

            capabilities.add(new Capability(annotation.name(), annotation.description(),
                    annotation.userInitiatedRequired(),
                    annotation.allowedCallerTypes(), annotation.requiredPermissions()));
        }

        return false;
    }

    private void generateCapabilityIndex() {
        try {
            Filer filer = processingEnv.getFiler();

            JavaFileObject file = filer.createSourceFile(GENERATED_PACKAGE + "."
                    + GENERATED_CLASS);

            try (Writer writer = file.openWriter()) {
                writer.write("package " + GENERATED_PACKAGE + ";\n\n");
                writer.write("import io.github.jcodeforge.aipolicy.CallerType;\n");
                writer.write("import io.github.jcodeforge.aipolicy.capability.Capability;\n");
                writer.write("import io.github.jcodeforge.aipolicy.capability.CapabilityIndex;\n");
                writer.write("import java.util.Arrays;\n");
                writer.write("import java.util.List;\n\n");
                writer.write("public final class " + GENERATED_CLASS + " implements CapabilityIndex {\n\n");
                writer.write("    @Override\n");
                writer.write("    public List<Capability> getCapabilities() {\n");

                if (capabilities.isEmpty()) {
                    writer.write("        return Arrays.asList();\n");

                } else {
                    writer.write("        return Arrays.asList(\n");

                    for (int i = 0; i < capabilities.size(); i++) {
                        Capability capability = capabilities.get(i);

                        writer.write("                new Capability(\n");
                        writer.write("                        " + quote(capability.getName()) + ",\n");
                        writer.write("                        " + quote(capability.getDescription()) + ",\n");
                        writer.write("                        " + capability.isUserInitiatedRequired() + ",\n");
                        writer.write("                        "
                                + generateCallerTypes(capability.getAllowedCallerTypes()) + ",\n");
                        writer.write("                        "
                                + generateStringList(capability.getRequiredPermissions()));

                        writer.write("\n                )");

                        if (i < capabilities.size() - 1) {
                            writer.write(",");
                        }

                        writer.write("\n");
                    }

                    writer.write("        );\n");
                }

                writer.write("    }\n");
                writer.write("}\n");
            }

        } catch (IOException exception) {
            processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR,
                    "Failed to generate " + GENERATED_CLASS + ": " + exception.getMessage());
        }
    }

    private void generateCapabilityIndexProvider() {
        try {
            JavaFileObject file = processingEnv.getFiler().createSourceFile(
                    GENERATED_PACKAGE + ".GeneratedCapabilityIndexProvider");

            try (Writer writer = file.openWriter()) {
                writer.write("package " + GENERATED_PACKAGE + ";\n\n");
                writer.write("import io.github.jcodeforge.aipolicy.capability.CapabilityIndex;\n");
                writer.write("import io.github.jcodeforge.aipolicy.capability.CapabilityIndexProvider;\n\n");
                writer.write("public final class GeneratedCapabilityIndexProvider "
                        + "implements CapabilityIndexProvider {\n\n");
                writer.write("    @Override\n");
                writer.write("    public CapabilityIndex getCapabilityIndex() {\n");
                writer.write("        return new GeneratedCapabilityIndex();\n");
                writer.write("    }\n");
                writer.write("}\n");
            }

        } catch (IOException exception) {
            processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR,
                    "Failed to generate GeneratedCapabilityIndexProvider: "
                            + exception.getMessage());
        }
    }

    private void generateCapabilityIndexProviderServiceFile() {
        try {
            FileObject file = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT,
                    "", "META-INF/services/"
                            + "io.github.jcodeforge.aipolicy.capability.CapabilityIndexProvider");

            try (Writer writer = file.openWriter()) {
                writer.write(GENERATED_PACKAGE + ".GeneratedCapabilityIndexProvider\n");
            }

        } catch (IOException exception) {
            processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR,
                    "Failed to generate CapabilityIndexProvider service file: "
                            + exception.getMessage());
        }
    }

    private String quote(String value) {
        return "\"" + escape(value) + "\"";
    }

    private String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String generateCallerTypes(List<CallerType> callerTypes) {
        if (callerTypes.isEmpty()) {
            return "java.util.Collections.emptyList()";
        }

        StringBuilder builder = new StringBuilder();

        builder.append("java.util.Arrays.asList(");

        for (int i = 0; i < callerTypes.size(); i++) {

            if (i > 0) {
                builder.append(", ");
            }

            builder.append("CallerType.").append(callerTypes.get(i).name());
        }

        builder.append(")");

        return builder.toString();
    }

    private String generateStringList(List<String> values) {
        if (values.isEmpty()) {
            return "java.util.Collections.emptyList()";
        }

        StringBuilder builder = new StringBuilder();

        builder.append("java.util.Arrays.asList(");

        for (int i = 0; i < values.size(); i++) {

            if (i > 0) {
                builder.append(", ");
            }

            builder.append(quote(values.get(i)));
        }

        builder.append(")");

        return builder.toString();
    }
}