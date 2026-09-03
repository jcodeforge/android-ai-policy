package io.github.jcodeforge.aipolicy.processor.ksp;

import com.google.devtools.ksp.processing.CodeGenerator;
import com.google.devtools.ksp.processing.Dependencies;
import com.google.devtools.ksp.processing.Resolver;
import com.google.devtools.ksp.symbol.KSAnnotated;
import com.google.devtools.ksp.symbol.KSAnnotation;
import com.google.devtools.ksp.symbol.KSDeclaration;
import com.google.devtools.ksp.symbol.KSFunctionDeclaration;
import com.google.devtools.ksp.symbol.KSName;
import com.google.devtools.ksp.symbol.KSType;
import com.google.devtools.ksp.symbol.KSTypeReference;
import com.google.devtools.ksp.symbol.KSValueArgument;
import com.google.devtools.ksp.symbol.Origin;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AiCapabilitySymbolProcessorTest {

    private static final String ANNOTATION_NAME =
            "io.github.jcodeforge.aipolicy.capability.AiCapability";

    private static final String GENERATED_PACKAGE =
            "io.github.jcodeforge.aipolicy.generated";

    @Test
    public void generatesCapabilityIndex() throws Exception {
        CodeGenerator codeGenerator = mock(CodeGenerator.class);
        GeneratedFiles generatedFiles = new GeneratedFiles();

        configureCodeGenerator(codeGenerator, generatedFiles);

        KSAnnotation annotation = createAnnotation(
                "customer.delete",
                "Delete customer",
                true,
                List.of("SELF", "EXTERNAL"),
                List.of(
                        "android.permission.READ_CONTACTS",
                        "android.permission.WRITE_CONTACTS"
                )
        );

        KSFunctionDeclaration function = mockFunction(annotation);

        Resolver resolver = mock(Resolver.class);

        Sequence<KSAnnotated> symbols = sequenceOf(function);

        when(resolver.getSymbolsWithAnnotation(
                eq(ANNOTATION_NAME),
                eq(false)
        )).thenReturn(symbols);

        AiCapabilitySymbolProcessor processor =
                new AiCapabilitySymbolProcessor(codeGenerator);

        processor.process(resolver);
        processor.finish();

        String generated = generatedFiles.get(
                "GeneratedKspCapabilityIndex"
        );

        assertTrue(generated.contains("customer.delete"));
        assertTrue(generated.contains("Delete customer"));
        assertTrue(generated.contains("true"));
        assertTrue(generated.contains("CallerType.SELF"));
        assertTrue(generated.contains("CallerType.EXTERNAL"));
        assertTrue(generated.contains(
                "\"android.permission.READ_CONTACTS\""
        ));
        assertTrue(generated.contains(
                "\"android.permission.WRITE_CONTACTS\""
        ));
        assertTrue(generated.contains(
                "implements CapabilityIndex"
        ));
    }

    @Test
    public void generatesCapabilityIndexProvider() throws Exception {
        CodeGenerator codeGenerator = mock(CodeGenerator.class);
        GeneratedFiles generatedFiles = new GeneratedFiles();

        configureCodeGenerator(codeGenerator, generatedFiles);

        KSAnnotation annotation = createAnnotation(
                "customer.read",
                "Read customer information",
                false,
                List.of(),
                List.of()
        );

        KSFunctionDeclaration function = mockFunction(annotation);

        Resolver resolver = mock(Resolver.class);

        Sequence<KSAnnotated> symbols = sequenceOf(function);

        when(resolver.getSymbolsWithAnnotation(
                eq(ANNOTATION_NAME),
                eq(false)
        )).thenReturn(symbols);

        AiCapabilitySymbolProcessor processor =
                new AiCapabilitySymbolProcessor(codeGenerator);

        processor.process(resolver);
        processor.finish();

        String generated = generatedFiles.get(
                "GeneratedKspCapabilityIndexProvider"
        );

        assertTrue(generated.contains(
                "implements CapabilityIndexProvider"
        ));

        assertTrue(generated.contains(
                "GeneratedKspCapabilityIndex"
        ));

        assertTrue(generated.contains(
                "getCapabilityIndex()"
        ));
    }

    @Test
    public void ignoresJavaOriginFunctions() throws Exception {
        CodeGenerator codeGenerator = mock(CodeGenerator.class);
        GeneratedFiles generatedFiles = new GeneratedFiles();

        configureCodeGenerator(codeGenerator, generatedFiles);

        KSAnnotation annotation = createAnnotation(
                "java.capability",
                "Java capability",
                false,
                List.of(),
                List.of()
        );

        KSFunctionDeclaration function = mockFunction(
                annotation,
                Origin.JAVA
        );

        Resolver resolver = mock(Resolver.class);

        Sequence<KSAnnotated> symbols = sequenceOf(function);

        when(resolver.getSymbolsWithAnnotation(
                eq(ANNOTATION_NAME),
                eq(false)
        )).thenReturn(symbols);

        AiCapabilitySymbolProcessor processor =
                new AiCapabilitySymbolProcessor(codeGenerator);

        processor.process(resolver);
        processor.finish();

        assertEquals(
                0,
                generatedFiles.size()
        );
    }

    @Test
    public void doesNotGenerateWithoutCapabilities() throws Exception {
        CodeGenerator codeGenerator = mock(CodeGenerator.class);
        GeneratedFiles generatedFiles = new GeneratedFiles();

        configureCodeGenerator(codeGenerator, generatedFiles);

        Resolver resolver = mock(Resolver.class);

        Sequence<KSAnnotated> symbols =
                SequencesKt.emptySequence();

        when(resolver.getSymbolsWithAnnotation(
                eq(ANNOTATION_NAME),
                eq(false)
        )).thenReturn(symbols);

        AiCapabilitySymbolProcessor processor =
                new AiCapabilitySymbolProcessor(codeGenerator);

        processor.process(resolver);
        processor.finish();

        assertEquals(
                0,
                generatedFiles.size()
        );
    }

    private KSFunctionDeclaration mockFunction(
            KSAnnotation annotation) {

        return mockFunction(
                annotation,
                Origin.KOTLIN
        );
    }

    private KSFunctionDeclaration mockFunction(
            KSAnnotation annotation,
            Origin origin) {

        KSFunctionDeclaration function =
                mock(KSFunctionDeclaration.class);

        Sequence<KSAnnotation> annotations =
                sequenceOf(annotation);

        when(function.getOrigin())
                .thenReturn(origin);

        when(function.getAnnotations())
                .thenReturn(annotations);

        return function;
    }

    private KSAnnotation createAnnotation(
            String name,
            String description,
            boolean userInitiatedRequired,
            List<String> callerTypes,
            List<String> permissions) {

        KSAnnotation annotation =
                mock(KSAnnotation.class);

        KSTypeReference annotationTypeReference =
                mock(KSTypeReference.class);

        KSType annotationType =
                mock(KSType.class);

        KSDeclaration annotationDeclaration =
                mock(KSDeclaration.class);

        KSName annotationQualifiedName =
                mock(KSName.class);

        when(annotationQualifiedName.asString())
                .thenReturn(ANNOTATION_NAME);

        when(annotationDeclaration.getQualifiedName())
                .thenReturn(annotationQualifiedName);

        when(annotationType.getDeclaration())
                .thenReturn(annotationDeclaration);

        when(annotationTypeReference.resolve())
                .thenReturn(annotationType);

        when(annotation.getAnnotationType())
                .thenReturn(annotationTypeReference);

        KSValueArgument nameArgument =
                valueArgument(
                        "name",
                        name
                );

        KSValueArgument descriptionArgument =
                valueArgument(
                        "description",
                        description
                );

        KSValueArgument userInitiatedArgument =
                valueArgument(
                        "userInitiatedRequired",
                        userInitiatedRequired
                );

        KSValueArgument callerTypesArgument =
                callerTypesArgument(callerTypes);

        KSValueArgument permissionsArgument =
                valueArgument(
                        "requiredPermissions",
                        permissions
                );

        List<KSValueArgument> arguments = List.of(
                nameArgument,
                descriptionArgument,
                userInitiatedArgument,
                callerTypesArgument,
                permissionsArgument
        );

        when(annotation.getArguments())
                .thenReturn(arguments);

        return annotation;
    }

    private KSValueArgument valueArgument(
            String name,
            Object value) {

        KSValueArgument argument =
                mock(KSValueArgument.class);

        KSName argumentName =
                mock(KSName.class);

        when(argumentName.asString())
                .thenReturn(name);

        when(argument.getName())
                .thenReturn(argumentName);

        when(argument.getValue())
                .thenReturn(value);

        return argument;
    }

    private KSValueArgument callerTypesArgument(
            List<String> callerTypes) {

        KSValueArgument argument =
                mock(KSValueArgument.class);

        KSName argumentName =
                mock(KSName.class);

        when(argumentName.asString())
                .thenReturn("allowedCallerTypes");

        when(argument.getName())
                .thenReturn(argumentName);

        List<KSType> types = callerTypes.stream()
                .map(this::mockCallerType)
                .toList();

        when(argument.getValue())
                .thenReturn(types);

        return argument;
    }

    private KSType mockCallerType(String name) {
        KSType type =
                mock(KSType.class);

        KSDeclaration declaration =
                mock(KSDeclaration.class);

        KSName simpleName =
                mock(KSName.class);

        when(simpleName.asString())
                .thenReturn(name);

        when(declaration.getSimpleName())
                .thenReturn(simpleName);

        when(type.getDeclaration())
                .thenReturn(declaration);

        return type;
    }

    private void configureCodeGenerator(
            CodeGenerator codeGenerator,
            GeneratedFiles generatedFiles) throws Exception {

        when(codeGenerator.createNewFile(
                any(Dependencies.class),
                eq(GENERATED_PACKAGE),
                any(String.class),
                eq("java")
        )).thenAnswer(invocation -> {

            String fileName =
                    invocation.getArgument(2);

            return generatedFiles.create(fileName);
        });
    }

    private static <T> Sequence<T> sequenceOf(
            T... values) {

        return SequencesKt.asSequence(
                Arrays.asList(values).iterator()
        );
    }

    private static final class GeneratedFiles {

        private final Map<String, ByteArrayOutputStream> files =
                new HashMap<>();

        OutputStream create(String fileName) {
            ByteArrayOutputStream output =
                    new ByteArrayOutputStream();

            files.put(
                    fileName,
                    output
            );

            return output;
        }

        String get(String fileName) {
            ByteArrayOutputStream output =
                    files.get(fileName);

            if (output == null) {
                throw new AssertionError(
                        "Generated file not found: " + fileName
                );
            }

            return output.toString(
                    StandardCharsets.UTF_8
            );
        }

        int size() {
            return files.size();
        }
    }
}