package io.github.jcodeforge.aipolicy.processor.ksp;

import com.google.devtools.ksp.processing.CodeGenerator;
import com.google.devtools.ksp.processing.Dependencies;
import com.google.devtools.ksp.processing.Resolver;
import com.google.devtools.ksp.processing.SymbolProcessor;
import com.google.devtools.ksp.symbol.KSAnnotated;
import com.google.devtools.ksp.symbol.KSAnnotation;
import com.google.devtools.ksp.symbol.KSDeclaration;
import com.google.devtools.ksp.symbol.KSFunctionDeclaration;
import com.google.devtools.ksp.symbol.KSValueArgument;
import org.jetbrains.annotations.NotNull;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import io.github.jcodeforge.aipolicy.CallerType;
import io.github.jcodeforge.aipolicy.capability.Capability;
import com.google.devtools.ksp.symbol.KSType;
import io.github.jcodeforge.aipolicy.capability.AppFunctionCapability;

public final class AiCapabilitySymbolProcessor implements SymbolProcessor {

    private static final String AI_CAPABILITY_ANNOTATION =
            "io.github.jcodeforge.aipolicy.capability.AiCapability";

    private static final String GENERATED_PACKAGE =
            "io.github.jcodeforge.aipolicy.generated";

    private static final String GENERATED_CLASS =
            "GeneratedCapabilityIndex";

    private static final String APP_FUNCTION_ANNOTATION =
            "androidx.appfunctions.AppFunction";

    private static final String GENERATED_PROVIDER_CLASS =
            "GeneratedCapabilityIndexProvider";

    private static final String CAPABILITY_INDEX_PROVIDER =
            "io.github.jcodeforge.aipolicy.capability.CapabilityIndexProvider";

    private static final String SERVICE_FILE =
            "META-INF/services/io.github.jcodeforge.aipolicy.capability.CapabilityIndexProvider";

    private static final String GENERATED_PROVIDER =
            GENERATED_PACKAGE + "." + GENERATED_PROVIDER_CLASS;

    private static final String APP_FUNCTION_SERVICE_ENTRY_POINT_ANNOTATION =
            "androidx.appfunctions.AppFunctionServiceEntryPoint";

    private static final String SERVICE_NAME_ARGUMENT = "serviceName";

    private final List<Capability> capabilities = new ArrayList<>();
    private final List<AppFunctionCapability> appFunctionCapabilities = new ArrayList<>();

    private final CodeGenerator codeGenerator;

    private boolean processed;

    public AiCapabilitySymbolProcessor(CodeGenerator codeGenerator) {
        this.codeGenerator = codeGenerator;
    }

    @NotNull
    @Override
    public List<KSAnnotated> process(@NotNull Resolver resolver) {
        if (processed) {
            return List.of();
        }

        processed = true;

        List<KSAnnotated> deferred = new ArrayList<>();

        Iterator<KSAnnotated> iterator = resolver.getSymbolsWithAnnotation(
                AI_CAPABILITY_ANNOTATION,
                false).iterator();

        while (iterator.hasNext()) {
            KSAnnotated symbol = iterator.next();

            if (!(symbol instanceof KSFunctionDeclaration)) {
                continue;
            }

            KSFunctionDeclaration function = (KSFunctionDeclaration) symbol;
            KSAnnotation annotation = findAiCapabilityAnnotation(function);

            if (annotation == null) {
                continue;
            }

            Capability capability = extractCapability(annotation);
            capabilities.add(capability);

            if (hasAppFunctionAnnotation(function)) {
                appFunctionCapabilities.add(
                        new AppFunctionCapability(createAppFunctionId(function), capability)
                );
            }
        }

        return deferred;
    }

    private String createAppFunctionId(KSFunctionDeclaration function) {
        KSDeclaration parent = function.getParentDeclaration();

        while (parent != null) {
            Iterator<KSAnnotation> annotations = parent.getAnnotations().iterator();

            while (annotations.hasNext()) {
                KSAnnotation annotation = annotations.next();

                String annotationName = annotation.getAnnotationType()
                        .resolve()
                        .getDeclaration()
                        .getQualifiedName()
                        .asString();

                if (APP_FUNCTION_SERVICE_ENTRY_POINT_ANNOTATION.equals(annotationName)) {
                    String serviceName = extractServiceName(annotation);

                    if (serviceName == null || serviceName.trim().isEmpty()) {
                        throw new IllegalArgumentException(
                                "@AppFunctionServiceEntryPoint must define serviceName: "
                                        + parent);
                    }

                    String packageName = parent.getPackageName().asString();

                    return packageName + "." + serviceName
                            + "#" + function.getSimpleName().asString();
                }
            }

            parent = parent.getParentDeclaration();
        }

        throw new IllegalArgumentException("Could not find enclosing @AppFunctionServiceEntryPoint for: "
                + function);
    }

    @Override
    public void finish() {
        if (capabilities.isEmpty()) {
            return;
        }

        try {
            generateFile(GENERATED_CLASS, generateCapabilityIndex());
            generateFile(GENERATED_PROVIDER_CLASS, generateCapabilityIndexProvider());
            generateFile("GeneratedAppFunctionCapabilityIndex",
                    generateAppFunctionCapabilityIndex());
            generateServiceFile();

        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate " + GENERATED_CLASS, e);
        }
    }

    private boolean hasAppFunctionAnnotation(KSFunctionDeclaration function) {
        Iterator<KSAnnotation> iterator = function.getAnnotations().iterator();

        while (iterator.hasNext()) {
            KSAnnotation annotation = iterator.next();

            String annotationName = annotation.getAnnotationType()
                    .resolve()
                    .getDeclaration()
                    .getQualifiedName()
                    .asString();

            if (APP_FUNCTION_ANNOTATION.equals(annotationName)) {
                return true;
            }
        }

        return false;
    }

    private void generateFile(String className, String source) throws Exception {
        OutputStream output = codeGenerator.createNewFile(new Dependencies(false),
                GENERATED_PACKAGE, className, "java");

        try {
            output.write(source.getBytes(StandardCharsets.UTF_8));
        } finally {
            output.close();
        }
    }

    private void generateServiceFile() throws Exception {
        try (OutputStream output = codeGenerator.createNewFileByPath(new Dependencies(false),
                SERVICE_FILE,
                "")) {
            output.write((GENERATED_PROVIDER + "\n").getBytes(StandardCharsets.UTF_8));
        }
    }

    private Capability extractCapability(KSAnnotation annotation) {
        String name = null;
        String description = null;
        boolean userInitiatedRequired = false;

        List<CallerType> allowedCallerTypes = new ArrayList<>();
        List<String> requiredPermissions = new ArrayList<>();

        for (KSValueArgument argument : annotation.getArguments()) {
            String argumentName = argument.getName() == null ? null
                    : argument.getName().asString();

            if (argumentName == null) {
                continue;
            }

            Object value = argument.getValue();

            switch (argumentName) {
                case "name":
                    name = (String) value;
                    break;

                case "description":
                    description = (String) value;
                    break;

                case "userInitiatedRequired":
                    userInitiatedRequired = (Boolean) value;
                    break;

                case "allowedCallerTypes":
                    extractCallerTypes(
                            value,
                            allowedCallerTypes);
                    break;

                case "requiredPermissions":
                    extractStringValues(
                            value,
                            requiredPermissions);
                    break;

                default:
                    break;
            }
        }

        if (name == null) {
            throw new IllegalArgumentException("@AiCapability name must not be null");
        }

        if (description == null) {
            throw new IllegalArgumentException("@AiCapability description must not be null");
        }

        return new Capability(name, description, userInitiatedRequired, allowedCallerTypes,
                requiredPermissions);
    }

    private String generateCapabilityIndexProvider() {
        StringBuilder source = new StringBuilder();

        source.append("package ").append(GENERATED_PACKAGE).append(";\n\n");

        source.append("import ")
                .append(CAPABILITY_INDEX_PROVIDER)
                .append(";\n");

        source.append("import io.github.jcodeforge.aipolicy.capability.CapabilityIndex;\n");
        source.append("import io.github.jcodeforge.aipolicy.capability.AppFunctionCapabilityIndex;\n\n");

        source.append("public final class ")
                .append(GENERATED_PROVIDER_CLASS)
                .append(" implements CapabilityIndexProvider {\n\n");

        source.append("    @Override\n");
        source.append("    public CapabilityIndex getCapabilityIndex() {\n");
        source.append("        return new ")
                .append(GENERATED_CLASS)
                .append("();\n");
        source.append("    }\n\n");

        source.append("    @Override\n");
        source.append("    public AppFunctionCapabilityIndex getAppFunctionCapabilityIndex() {\n");
        source.append("        return new GeneratedAppFunctionCapabilityIndex();\n");
        source.append("    }\n");

        source.append("}\n");

        return source.toString();
    }

    private String generateCapabilityIndex() {
        StringBuilder source = new StringBuilder();

        source.append("package ")
                .append(GENERATED_PACKAGE)
                .append(";\n\n");

        source.append("import io.github.jcodeforge.aipolicy.CallerType;\n");
        source.append("import io.github.jcodeforge.aipolicy.capability.Capability;\n");
        source.append("import io.github.jcodeforge.aipolicy.capability.CapabilityIndex;\n");
        source.append("import java.util.List;\n\n");

        source.append("public final class ")
                .append(GENERATED_CLASS)
                .append(" implements CapabilityIndex {\n\n");

        source.append("    @Override\n");
        source.append("    public List<Capability> getCapabilities() {\n");
        source.append("        return List.of(\n");

        for (int i = 0; i < capabilities.size(); i++) {
            source.append(generateCapability(capabilities.get(i)));

            if (i < capabilities.size() - 1) {
                source.append(",");
            }

            source.append("\n");
        }

        source.append("        );\n");
        source.append("    }\n");
        source.append("}\n");

        return source.toString();
    }

    private String generateCapability(Capability capability) {
        StringBuilder source = new StringBuilder();

        source.append("            new Capability(")
                .append(quote(capability.getName()))
                .append(", ")
                .append(quote(capability.getDescription()))
                .append(", ")
                .append(capability.isUserInitiatedRequired())
                .append(", ")
                .append(generateCallerTypes(
                        capability.getAllowedCallerTypes()))
                .append(", ")
                .append(generateStringList(
                        capability.getRequiredPermissions()))
                .append(")");

        return source.toString();
    }

    private KSAnnotation findAiCapabilityAnnotation(KSFunctionDeclaration function) {
        Iterator<KSAnnotation> iterator = function.getAnnotations().iterator();

        while (iterator.hasNext()) {
            KSAnnotation annotation = iterator.next();

            if (AI_CAPABILITY_ANNOTATION.equals(
                    annotation.getAnnotationType()
                            .resolve()
                            .getDeclaration()
                            .getQualifiedName()
                            .asString())) {

                return annotation;
            }
        }

        return null;
    }

    private void extractStringValues(Object value, List<String> target) {
        if (!(value instanceof List<?>)) {
            return;
        }

        for (Object item : (List<?>) value) {
            if (item instanceof String) {
                target.add((String) item);
            }
        }
    }

    private void extractCallerTypes(Object value, List<CallerType> target) {
        if (!(value instanceof List<?>)) {
            return;
        }

        for (Object item : (List<?>) value) {
            if (!(item instanceof KSType)) {
                continue;
            }

            KSType type = (KSType) item;
            String enumName = type.getDeclaration().getSimpleName().asString();

            target.add(CallerType.valueOf(enumName));
        }
    }

    private String generateCallerTypes(List<CallerType> callerTypes) {
        StringBuilder source = new StringBuilder("List.of(");

        for (int i = 0; i < callerTypes.size(); i++) {
            if (i > 0) {
                source.append(", ");
            }

            source.append("CallerType.").append(callerTypes.get(i).name());
        }

        source.append(")");

        return source.toString();
    }

    private String generateAppFunctionCapabilityIndex() {
        StringBuilder source = new StringBuilder();

        source.append("package ")
                .append(GENERATED_PACKAGE)
                .append(";\n\n");

        source.append("import io.github.jcodeforge.aipolicy.capability.Capability;\n");
        source.append("import io.github.jcodeforge.aipolicy.capability.AppFunctionCapability;\n");
        source.append("import io.github.jcodeforge.aipolicy.capability.AppFunctionCapabilityIndex;\n");
        source.append("import java.util.List;\n\n");
        source.append("public final class GeneratedAppFunctionCapabilityIndex ")
                .append("implements AppFunctionCapabilityIndex {\n\n");
        source.append("    @Override\n");
        source.append("    public List<AppFunctionCapability> getAppFunctionCapabilities() {\n");
        source.append("        return List.of(\n");

        for (int i = 0; i < appFunctionCapabilities.size(); i++) {
            AppFunctionCapability appFunctionCapability = appFunctionCapabilities.get(i);

            source.append("            new AppFunctionCapability(\n");
            source.append("                ")
                    .append(quote(appFunctionCapability.getFunctionId()))
                    .append(",\n");

            Capability capability = appFunctionCapability.getCapability();

            source.append("                new Capability(")
                    .append(quote(capability.getName()))
                    .append(", ")
                    .append(quote(capability.getDescription()))
                    .append(", ")
                    .append(capability.isUserInitiatedRequired())
                    .append(", ")
                    .append(generateCallerTypes(
                            capability.getAllowedCallerTypes()))
                    .append(", ")
                    .append(generateStringList(
                            capability.getRequiredPermissions()))
                    .append(")\n");

            source.append("            )");

            if (i < appFunctionCapabilities.size() - 1) {
                source.append(",");
            }

            source.append("\n");
        }

        source.append("        );\n");
        source.append("    }\n");
        source.append("}\n");

        return source.toString();
    }

    private String generateStringList(List<String> values) {
        StringBuilder source = new StringBuilder("List.of(");

        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                source.append(", ");
            }

            source.append(quote(values.get(i)));
        }

        source.append(")");

        return source.toString();
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

    private String extractServiceName(KSAnnotation annotation) {
        Iterator<KSValueArgument> arguments = annotation.getArguments().iterator();

        while (arguments.hasNext()) {
            KSValueArgument argument = arguments.next();

            if (argument.getName() == null) {
                continue;
            }

            if (!SERVICE_NAME_ARGUMENT.equals(
                    argument.getName().asString())) {
                continue;
            }

            Object value = argument.getValue();

            if (value instanceof String) {
                return (String) value;
            }
        }

        return null;
    }
}