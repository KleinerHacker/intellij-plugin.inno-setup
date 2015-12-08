package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssBuiltinConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssCompilerDirectiveConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssDriveConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssEnvironmentConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssINIConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssMessageConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssParameterConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssRegistryConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.references.IssAbstractReference;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.constant.IssBuiltinConstant;

/**
 * Created by pfeifchr on 04.12.2015.
 */
public class IssConstantAnnotator implements Annotator {

    @Override
    public void annotate(PsiElement psiElement, AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssConstantElement) {
            final Annotation infoAnnotation = annotationHolder.createInfoAnnotation(psiElement, null);
            if (psiElement instanceof IssCompilerDirectiveConstantElement) {
                handleCompilerDirectiveConstant((IssCompilerDirectiveConstantElement) psiElement, annotationHolder, infoAnnotation);
            } else if (psiElement instanceof IssBuiltinConstantElement) {
                handleBuiltinConstant(annotationHolder, infoAnnotation, (IssBuiltinConstantElement) psiElement);
            } else if (psiElement instanceof IssMessageConstantElement) {
                handleMessageConstant(annotationHolder, infoAnnotation, (IssMessageConstantElement) psiElement);
            } else if (psiElement instanceof IssEnvironmentConstantElement) {
                handleEnvironmentConstant(annotationHolder, infoAnnotation, (IssEnvironmentConstantElement) psiElement);
            } else if (psiElement instanceof IssDriveConstantElement) {
                handleDriveConstant((IssDriveConstantElement) psiElement, annotationHolder, infoAnnotation);
            } else if (psiElement instanceof IssINIConstantElement) {
                handleINIConstant((IssINIConstantElement) psiElement, annotationHolder, infoAnnotation);
            } else if (psiElement instanceof IssRegistryConstantElement) {
                handleRegistryConstant((IssRegistryConstantElement) psiElement, annotationHolder, infoAnnotation);
            } else if (psiElement instanceof IssParameterConstantElement) {
                handleParameterConstant((IssParameterConstantElement) psiElement, annotationHolder, infoAnnotation);
            }
        }
    }

    private void handleParameterConstant(IssParameterConstantElement parameterConstantElement, AnnotationHolder annotationHolder, Annotation infoAnnotation) {
        infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_CONSTANT_PARAMETER);
        if (parameterConstantElement.getConstantArgumentList().size() > 0) {
            for (PsiElement element : parameterConstantElement.getConstantArgumentList()) {
                annotationHolder.createErrorAnnotation(element, "No argument allowed for parameter constant");
            }
        }
        if (parameterConstantElement.getConstantDefaultValue() == null) {
            annotationHolder.createErrorAnnotation(parameterConstantElement, "Missing default value");
        }
    }

    private void handleRegistryConstant(IssRegistryConstantElement registryConstantElement, AnnotationHolder annotationHolder, Annotation infoAnnotation) {
        infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_CONSTANT_REGISTRY);
        if (registryConstantElement.getConstantArgumentList().size() != 1) {
            annotationHolder.createErrorAnnotation(registryConstantElement, "Need exact one argument: value name");
        }
        if (registryConstantElement.getConstantDefaultValue() == null) {
            annotationHolder.createErrorAnnotation(registryConstantElement, "Missing default value");
        }
    }

    private void handleINIConstant(IssINIConstantElement iniConstantElement, AnnotationHolder annotationHolder, Annotation infoAnnotation) {
        infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_CONSTANT_INI);
        if (iniConstantElement.getConstantArgumentList().size() != 2) {
            annotationHolder.createErrorAnnotation(iniConstantElement, "Missing arguments. Must be exact two: section name and key name");
        }
        if (iniConstantElement.getConstantDefaultValue() == null) {
            annotationHolder.createErrorAnnotation(iniConstantElement, "Missing default value");
        }
    }

    private void handleDriveConstant(IssDriveConstantElement driveConstantElement, AnnotationHolder annotationHolder, Annotation infoAnnotation) {
        infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_CONSTANT_DRIVE);
        if (driveConstantElement.getConstantArgumentList().size() > 0) {
            for (final PsiElement element : driveConstantElement.getConstantArgumentList()) {
                annotationHolder.createErrorAnnotation(element, "Arguments not allowed for drive constants");
            }
        }
        if (driveConstantElement.getConstantDefaultValue() != null) {
            annotationHolder.createErrorAnnotation(driveConstantElement.getConstantDefaultValue(), "No default value allowed for drive constants");
        }
    }

    private void handleMessageConstant(@NotNull AnnotationHolder annotationHolder, Annotation infoAnnotation, IssMessageConstantElement messageConstantElement) {
        infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_CONSTANT_MESSAGE);
        if (messageConstantElement.getConstantName() != null && messageConstantElement.getConstantName().getReference() != null &&
                ((IssAbstractReference)messageConstantElement.getConstantName().getReference()).multiResolve(false).length <= 0) {
            final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(messageConstantElement.getConstantName(), "No message reference found!");
            errorAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
        }
        if (messageConstantElement.getConstantDefaultValue() != null) {
            annotationHolder.createErrorAnnotation(messageConstantElement.getConstantDefaultValue(), "Default value nor allowed for message constants");
        }
    }

    private void handleEnvironmentConstant(@NotNull AnnotationHolder annotationHolder, Annotation infoAnnotation, IssEnvironmentConstantElement environmentConstantElement) {
        infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_CONSTANT_ENVIRONMENT);
        if (environmentConstantElement.getConstantName() != null) {
            if (System.getenv(environmentConstantElement.getConstantName().getName()) == null) {
                final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(environmentConstantElement.getConstantName(),
                        "No environment variable with this name found currently!");
                errorAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
            }
        }
        if (environmentConstantElement.getConstantArgumentList().size() > 0) {
            for (final PsiElement element : environmentConstantElement.getConstantArgumentList()) {
                annotationHolder.createErrorAnnotation(element, "Arguments not allowed for environment constants");
            }
        }
    }

    private void handleBuiltinConstant(@NotNull AnnotationHolder annotationHolder, Annotation infoAnnotation, @NotNull IssBuiltinConstantElement builtinConstantElement) {
        final IssBuiltinConstant constant = builtinConstantElement.getBuiltinConstant();
        if (constant == null) {
            infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_CONSTANT_BUILTIN_SHELL);
        } else {
            infoAnnotation.setTextAttributes(constant.getType().getTextAttributesKey());
        }
        if (builtinConstantElement.getConstantArgumentList().size() > 0) {
            for (final PsiElement element : builtinConstantElement.getConstantArgumentList()) {
                annotationHolder.createErrorAnnotation(element, "Arguments not allowed for builtin constants");
            }
        }
        if (builtinConstantElement.getConstantDefaultValue() != null) {
            annotationHolder.createErrorAnnotation(builtinConstantElement.getConstantDefaultValue(), "No default value allowed for builtin constants");
        }
    }

    private void handleCompilerDirectiveConstant(@NotNull IssCompilerDirectiveConstantElement compilerDirectiveConstantElement, @NotNull AnnotationHolder annotationHolder, Annotation infoAnnotation) {
        infoAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_CONSTANT_COMPILER_DIRECTIVE);

        if (compilerDirectiveConstantElement.getConstantName() != null && compilerDirectiveConstantElement.getConstantName().getReference() != null &&
                compilerDirectiveConstantElement.getConstantName().getReference().resolve() == null) {
            final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(compilerDirectiveConstantElement.getConstantName(), "No symbol reference found!");
            errorAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
        }
        if (compilerDirectiveConstantElement.getConstantArgumentList().size() > 0) {
            for (final PsiElement element : compilerDirectiveConstantElement.getConstantArgumentList()) {
                annotationHolder.createErrorAnnotation(element, "Arguments not allowed for compiler directive constants");
            }
        }
        if (compilerDirectiveConstantElement.getConstantDefaultValue() != null) {
            annotationHolder.createErrorAnnotation(compilerDirectiveConstantElement.getConstantDefaultValue(), "No default value allowed for compiler directive constants");
        }
    }
}
