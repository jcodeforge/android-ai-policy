package io.github.jcodeforge.aipolicy.processor.ksp;

import com.google.devtools.ksp.processing.SymbolProcessor;
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment;
import com.google.devtools.ksp.processing.SymbolProcessorProvider;

import org.jetbrains.annotations.NotNull;

public final class AiCapabilitySymbolProcessorProvider implements SymbolProcessorProvider {

    @NotNull
    @Override
    public SymbolProcessor create(@NotNull SymbolProcessorEnvironment environment) {
        return new AiCapabilitySymbolProcessor(environment.getCodeGenerator());
    }
}