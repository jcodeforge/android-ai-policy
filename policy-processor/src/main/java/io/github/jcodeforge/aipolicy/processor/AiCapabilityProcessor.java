package io.github.jcodeforge.aipolicy.processor;

import io.github.jcodeforge.aipolicy.capability.AiCapability;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;

@SupportedAnnotationTypes(
        "io.github.jcodeforge.aipolicy.capability.AiCapability"
)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public final class AiCapabilityProcessor extends AbstractProcessor {

    private static final String GENERATED_PACKAGE =
            "io.github.jcodeforge.aipolicy.capability.generated";

    private static final String GENERATED_CLASS = "GeneratedCapabilityIndex";

    private final Set<String> capabilityClasses = new LinkedHashSet<>();

    private boolean generated;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
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
            Element enclosingElement = element.getEnclosingElement();

            if (enclosingElement instanceof TypeElement) {
                TypeElement type = (TypeElement) enclosingElement;

                capabilityClasses.add(type.getQualifiedName().toString());
            }
        }

        return false;
    }

    private void generateCapabilityIndex() {
        try {
            Filer filer = processingEnv.getFiler();

            JavaFileObject file =
                    filer.createSourceFile(GENERATED_PACKAGE + "." + GENERATED_CLASS);

            try (Writer writer = file.openWriter()) {
                writer.write("package " + GENERATED_PACKAGE + ";\n\n");
                writer.write("import io.github.jcodeforge.aipolicy.capability.CapabilityIndex;\n");
                writer.write("import java.util.Arrays;\n");
                writer.write("import java.util.List;\n\n");
                writer.write("public final class " + GENERATED_CLASS + " implements CapabilityIndex {\n\n");
                writer.write("    @Override\n");
                writer.write("    public List<Class<?>> getCapabilityClasses() {\n");

                if (capabilityClasses.isEmpty()) {
                    writer.write("        return Arrays.asList();\n");
                } else {
                    writer.write("        return Arrays.asList(\n");

                    int index = 0;

                    for (String className : capabilityClasses) {
                        writer.write("                " + className + ".class");

                        if (index < capabilityClasses.size() - 1) {
                            writer.write(",");
                        }

                        writer.write("\n");

                        index++;
                    }

                    writer.write("        );\n");
                }

                writer.write("    }\n");
                writer.write("}\n");
            }

        } catch (IOException exception) {
            processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR,
                    "Failed to generate " + GENERATED_CLASS + ": " + exception.getMessage()
            );
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
}