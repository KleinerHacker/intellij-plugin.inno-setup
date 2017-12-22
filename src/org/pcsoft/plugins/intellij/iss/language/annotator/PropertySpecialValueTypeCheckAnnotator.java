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
import org.pcsoft.plugins.intellij.iss.language.type.PropertyValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertySpecialValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertyType;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

public class PropertySpecialValueTypeCheckAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssProperty) {
            final IssProperty property = (IssProperty) psiElement;
            final PropertyType propertyType = property.getPropertyType();
            if (propertyType == null)
                return;
            final Class<? extends PropertySpecialValueType> specialTypeClass = propertyType.getPropertySpecialValueTypeClass();

            checkValue(propertyType, property.getValue(), specialTypeClass, annotationHolder);
        }
    }

    private void checkValue(@NotNull PropertyType propertyType, @Nullable IssValue value,
                            @Nullable Class<? extends PropertySpecialValueType> specialTypeClass, @NotNull AnnotationHolder annotationHolder) {
        if (specialTypeClass == null)
            return;

        if (propertyType.getPropertyValueTypes().length > 1)
            return; //Ignore cause other values than enum values are allowed.

        for (PropertyValueType valueType : propertyType.getPropertyValueTypes()) {
            switch (valueType) {
                case MultiValue:
                    if (value != null && !StringUtils.isEmpty(value.getText())) {
                        final Collection<IssRefValue> children = PsiTreeUtil.findChildrenOfType(value, IssRefValue.class);
                        for (IssRefValue refValue : children) {
                            if (Stream.of(specialTypeClass.getEnumConstants()).noneMatch(o -> o.getName().equalsIgnoreCase(refValue.getText()))) {
                                annotationHolder.createErrorAnnotation(refValue, "Wrong enum value, allowed are " +
                                        Arrays.toString(Stream.of(specialTypeClass.getEnumConstants()).map(PropertySpecialValueType::getName).toArray()))
                                        .setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                            }
                        }
                    }
                    break;
                case BooleanEx:
                case SingleValue:
                    if (value != null && !StringUtils.isEmpty(value.getText())) {
                        if (Stream.of(specialTypeClass.getEnumConstants()).noneMatch(o -> o.getName().equalsIgnoreCase(value.getText()))) {
                            annotationHolder.createErrorAnnotation(value, "Wrong enum value, allowed are " +
                                    Arrays.toString(Stream.of(specialTypeClass.getEnumConstants()).map(PropertySpecialValueType::getName).toArray()))
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
