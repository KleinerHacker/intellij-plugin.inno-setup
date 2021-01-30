package org.pcsoft.plugins.intellij.iss.core.language.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.core.language.parser.psi.element.IssProperty;
import org.pcsoft.plugins.intellij.iss.core.language.parser.psi.element.IssRefValue;
import org.pcsoft.plugins.intellij.iss.core.language.type.ValueType;
import org.pcsoft.plugins.intellij.iss.core.language.type.base.PropertyType;

import java.util.Collection;
import java.util.stream.Stream;

public class DoublePropertyValueAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof IssProperty) {
            final IssProperty property = (IssProperty) psiElement;

            final PropertyType propertyType = property.getPropertyType();
            if (propertyType == null)
                return;


            if (Stream.of(propertyType.getValueTypes()).anyMatch(propertyValueType -> propertyValueType == ValueType.MultiValue)) {
                final Collection<IssRefValue> refValues = PsiTreeUtil.findChildrenOfType(property.getValue(), IssRefValue.class);
                for (IssRefValue refValue : refValues) {
                    refValues.stream()
                            .filter(item -> item != refValue)
                            .filter(item -> item.getText().equalsIgnoreCase(refValue.getText()))
                            .forEach(item -> annotationHolder.createErrorAnnotation(item, "Double value found"));
                }
            }
        }
    }
}
