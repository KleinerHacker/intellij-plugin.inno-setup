package org.pcsoft.plugins.intellij.iss.language.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssMultipleProperty;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssProperty;
import org.pcsoft.plugins.intellij.iss.language.type.PropertyValueType;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertyType;

import java.util.Arrays;
import java.util.function.Predicate;

public class PropertyValueTypeCheckAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssProperty) {
            final IssProperty property = (IssProperty) psiElement;
            if (property.getValue() == null) {
                annotationHolder.createErrorAnnotation(property.getKey(), "Missing value");
                return;
            }
            final PropertyType propertyType = property.getPropertyType();
            if (propertyType == null)
                return;
            final String valueText = property.getValue().getText();

            if (!checkValue(propertyType, valueText, s -> !(property instanceof IssMultipleProperty) || s.matches("\"[^\"]*\""))) {
                annotationHolder.createErrorAnnotation(property.getValue(), "Wrong value, allowed are " +
                        Arrays.toString(propertyType.getPropertyValueTypes()));
            }
        }
    }

    private boolean checkValue(@NotNull PropertyType propertyType, @Nullable String valueText, @NotNull Predicate<String> testFunc) {
        for (PropertyValueType valueType : propertyType.getPropertyValueTypes()) {
            switch (valueType) {
                case String:
                    if (testFunc.test(valueText))
                        return true;
                    break;
                case MultiValue:
                    if (!StringUtils.isEmpty(valueText)) {
                        boolean check = true;
                        for (String part : valueText.split(" ")) {
                            if (!part.matches("[a-zA-Z_][a-zA-Z0-9\\-_\\\\/]*[a-zA-Z0-9_]?")) {
                                check = false;
                                break;
                            }
                        }
                        if (check)
                            return true;
                    }
                    break;
                case BooleanEx:
                case SingleValue:
                    if (!StringUtils.isEmpty(valueText) && valueText.matches("[a-zA-Z_][a-zA-Z0-9\\-_\\\\/]*[a-zA-Z0-9_]?"))
                        return true;
                    break;
                case Number:
                    if (!StringUtils.isEmpty(valueText) && valueText.matches("[0-9]+|\\$([0-9a-fA-F]{2})+"))
                        return true;
                    break;
                case Boolean:
                    if (!StringUtils.isEmpty(valueText) && (valueText.equalsIgnoreCase("yes") || valueText.equalsIgnoreCase("no")))
                        return true;
                    break;
                case ByteArray:
                    if (!StringUtils.isEmpty(valueText) && valueText.matches("([0-9a-fA-F]{2} )*[0-9a-fA-F]{2}"))
                        return true;
                    break;
                case Version:
                    if (!StringUtils.isEmpty(valueText) && valueText.matches("([0-9]+\\.)*[0-9]+"))
                        return true;
                    break;
                default:
                    throw new RuntimeException();
            }
        }

        return false;
    }
}
