package org.pcsoft.plugins.intellij.iss.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssFile;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssMultipleProperty;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssPreprocessorElement;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssSection;
import org.pcsoft.plugins.intellij.iss.language.type.PreprocessorType;
import org.pcsoft.plugins.intellij.iss.language.type.SectionType;
import org.pcsoft.plugins.intellij.iss.language.type.property.ComponentsPropertyType;
import org.pcsoft.plugins.intellij.iss.language.type.property.TasksPropertyType;
import org.pcsoft.plugins.intellij.iss.language.type.property.TypesPropertyType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class IssSearchForElementUtils {
    @NotNull
    public static List<IssPreprocessorElement> searchForDefineSymbols(@NotNull Project project, @NotNull IssFile file) {
        return searchForDefineSymbols(project, file, null, false);
    }

    @NotNull
    public static List<IssPreprocessorElement> searchForDefineSymbols(@NotNull Project project, @NotNull IssFile file, @Nullable String key, boolean strict) {
        final Collection<IssPreprocessorElement> preprocessorElements = PsiTreeUtil.findChildrenOfType(file, IssPreprocessorElement.class);
        return preprocessorElements.stream()
                .filter(preprocessorElement -> preprocessorElement.getType() == PreprocessorType.Define)
                .filter(preprocessorElement -> IssUtils.checkString(preprocessorElement.getPreprocessorDefine().getPreprocessorName().getText(), key, strict))
                .collect(Collectors.toList());
    }

    @NotNull
    public static List<IssMultipleProperty> searchForTypes(@NotNull Project project, @NotNull IssFile file) {
        return searchForTypes(project, file, null, false);
    }

    @NotNull
    public static List<IssMultipleProperty> searchForTypes(@NotNull Project project, @NotNull IssFile file, @Nullable String key, boolean strict) {
        if (file.getSections() == null)
            return new ArrayList<>();
        final IssSection section = Stream.of(file.getSections())
                .filter(issSection -> issSection.getSectionType() == SectionType.Types)
                .findFirst().orElse(null);
        if (section == null)
            return new ArrayList<>();

        return section.getMultipleSectionLineList().stream()
                .map(issMultipleSectionLine -> issMultipleSectionLine.getMultiplePropertyList().stream()
                        .filter(issMultipleProperty -> issMultipleProperty.getPropertyType() == TypesPropertyType.Name)
                        .findFirst().orElse(null))
                .filter(issMultipleProperty -> issMultipleProperty != null && issMultipleProperty.getMultipleValue() != null)
                .filter(issMultipleProperty -> {
                    if (!issMultipleProperty.getMultipleValue().getRefValueList().isEmpty())
                        //Single value case for key
                        return IssUtils.checkString(issMultipleProperty.getMultipleValue().getRefValueList().get(0).getName(), key, strict);
                    else if (!issMultipleProperty.getMultipleValue().getStringValue().getRefValueList().isEmpty())
                        //String case for key
                        return IssUtils.checkString(issMultipleProperty.getMultipleValue().getStringValue().getRefValueList().get(0).getName(), key, strict);

                    return false;
                })
                .collect(Collectors.toList());
    }

    @NotNull
    public static List<IssMultipleProperty> searchForTasks(@NotNull Project project, @NotNull IssFile file) {
        return searchForTasks(project, file, null, false);
    }

    @NotNull
    public static List<IssMultipleProperty> searchForTasks(@NotNull Project project, @NotNull IssFile file, @Nullable String key, boolean strict) {
        if (file.getSections() == null)
            return new ArrayList<>();
        final IssSection section = Stream.of(file.getSections())
                .filter(issSection -> issSection.getSectionType() == SectionType.Tasks)
                .findFirst().orElse(null);
        if (section == null)
            return new ArrayList<>();

        return section.getMultipleSectionLineList().stream()
                .map(issMultipleSectionLine -> issMultipleSectionLine.getMultiplePropertyList().stream()
                        .filter(issMultipleProperty -> issMultipleProperty.getPropertyType() == TasksPropertyType.Name)
                        .findFirst().orElse(null))
                .filter(issMultipleProperty -> issMultipleProperty != null && issMultipleProperty.getMultipleValue() != null)
                .filter(issMultipleProperty -> {
                    if (!issMultipleProperty.getMultipleValue().getRefValueList().isEmpty())
                        //Single value case for key
                        return IssUtils.checkString(issMultipleProperty.getMultipleValue().getRefValueList().get(0).getName(), key, strict);
                    else if (!issMultipleProperty.getMultipleValue().getStringValue().getRefValueList().isEmpty())
                        //String case for key
                        return IssUtils.checkString(issMultipleProperty.getMultipleValue().getStringValue().getRefValueList().get(0).getName(), key, strict);

                    return false;
                })
                .collect(Collectors.toList());
    }

    @NotNull
    public static List<IssMultipleProperty> searchForComponents(@NotNull Project project, @NotNull IssFile file) {
        return searchForComponents(project, file, null, false);
    }

    @NotNull
    public static List<IssMultipleProperty> searchForComponents(@NotNull Project project, @NotNull IssFile file, @Nullable String key, boolean strict) {
        if (file.getSections() == null)
            return new ArrayList<>();
        final IssSection section = Stream.of(file.getSections())
                .filter(issSection -> issSection.getSectionType() == SectionType.Components)
                .findFirst().orElse(null);
        if (section == null)
            return new ArrayList<>();

        return section.getMultipleSectionLineList().stream()
                .map(issMultipleSectionLine -> issMultipleSectionLine.getMultiplePropertyList().stream()
                        .filter(issMultipleProperty -> issMultipleProperty.getPropertyType() == ComponentsPropertyType.Name)
                        .findFirst().orElse(null))
                .filter(issMultipleProperty -> issMultipleProperty != null && issMultipleProperty.getMultipleValue() != null)
                .filter(issMultipleProperty -> {
                    if (!issMultipleProperty.getMultipleValue().getRefValueList().isEmpty())
                        //Single value case for key
                        return IssUtils.checkString(issMultipleProperty.getMultipleValue().getRefValueList().get(0).getName(), key, strict);
                    else if (!issMultipleProperty.getMultipleValue().getStringValue().getRefValueList().isEmpty())
                        //String case for key
                        return IssUtils.checkString(issMultipleProperty.getMultipleValue().getStringValue().getRefValueList().get(0).getName(), key, strict);

                    return false;
                })
                .collect(Collectors.toList());
    }

    private IssSearchForElementUtils() {
    }
}
