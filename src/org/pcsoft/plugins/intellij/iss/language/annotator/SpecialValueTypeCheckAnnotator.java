package org.pcsoft.plugins.intellij.iss.language.annotator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.*;
import org.pcsoft.plugins.intellij.iss.language.type.ValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.PreprocessorType;
import org.pcsoft.plugins.intellij.iss.language.type.base.SpecialValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertyType;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

public class SpecialValueTypeCheckAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssProperty) {
            final IssProperty property = (IssProperty) psiElement;
            final PropertyType propertyType = property.getPropertyType();
            if (propertyType == null)
                return;
            final Class<? extends SpecialValueType> specialTypeClass = propertyType.getSpecialValueTypeClass();

            checkValue(propertyType.getValueTypes(), property.getValue(), specialTypeClass, annotationHolder);
        } else if (psiElement instanceof IssPreprocessorElement) {
            final IssPreprocessorElement preprocessorElement = (IssPreprocessorElement) psiElement;
            final PreprocessorType preprocessorType = preprocessorElement.getPreprocessorType();
            if (preprocessorType == null)
                return;
            final Class<? extends SpecialValueType> specialTypeClass = preprocessorType.getSpecialValueTypeClass();

            for (final IssPreprocessorValue preprocessorValue : preprocessorElement.getPreprocessorValueList()) {
                checkValue(preprocessorType.getValueTypes(), preprocessorValue, specialTypeClass, annotationHolder);
            }
        }
    }

    private void checkValue(@NotNull ValueType[] valueTypes, @Nullable PsiElement valueElement,
                            @Nullable Class<? extends SpecialValueType> specialTypeClass, @NotNull AnnotationHolder annotationHolder) {
        if (specialTypeClass == null)
            return;

        if (valueTypes.length > 1)
            return; //Ignore cause other values than enum values are allowed.

        for (ValueType valueType : valueTypes) {
            switch (valueType) {
                case MultiValue:
                    if (valueElement != null && !StringUtils.isEmpty(valueElement.getText())) {
                        final Collection<IssRefValue> children = PsiTreeUtil.findChildrenOfType(valueElement, IssRefValue.class);
                        for (IssRefValue refValue : children) {
                            if (Stream.of(specialTypeClass.getEnumConstants()).noneMatch(o -> o.getName().equalsIgnoreCase(refValue.getText()))) {
                                annotationHolder.createErrorAnnotation(refValue, "Wrong enum value, allowed are " +
                                        Arrays.toString(Stream.of(specialTypeClass.getEnumConstants()).map(SpecialValueType::getName).toArray()))
                                        .setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                            }
                        }
                    }
                    break;
                case BooleanEx:
                case SingleValue:
                    if (valueElement != null && !StringUtils.isEmpty(valueElement.getText())) {
                        if (Stream.of(specialTypeClass.getEnumConstants()).noneMatch(o -> o.getName().equalsIgnoreCase(valueElement.getText()))) {
                            annotationHolder.createErrorAnnotation(valueElement, "Wrong enum value, allowed are " +
                                    Arrays.toString(Stream.of(specialTypeClass.getEnumConstants()).map(SpecialValueType::getName).toArray()))
                                    .setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                        }
                    }
                    break;
                case Number:
                case Boolean:
                case ByteArray:
                case String:
                case Version:
                    break;
                default:
                    throw new RuntimeException();
            }
        }
    }
}
