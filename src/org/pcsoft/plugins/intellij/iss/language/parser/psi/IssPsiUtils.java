package org.pcsoft.plugins.intellij.iss.language.parser.psi;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.parser.IssGenTypes;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.*;
import org.pcsoft.plugins.intellij.iss.language.type.SectionType;
import org.pcsoft.plugins.intellij.iss.language.type.SectionTypeVariant;
import org.pcsoft.plugins.intellij.iss.language.type.base.SectionProperty;

import javax.swing.*;
import java.util.stream.Stream;

/**
 * Created by Christoph on 30.09.2016.
 */
public interface IssPsiUtils {

    @Nullable
    static String getName(final IssSectionTitle sectionTitle) {
        final ASTNode childByType = sectionTitle.getNode().findChildByType(IssGenTypes.NAME);
        if (childByType == null)
            return null;

        return childByType.getText();
    }

    //<editor-fold desc="Section">

    @NotNull
    static String getName(final IssSection section) {
        return section.getSectionTitle().getName();
    }

    @NotNull
    static ItemPresentation getPresentation(final IssSection issSection) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return issSection.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                //Initial checks (single values only)
                final SectionType sectionType = issSection.getSectionType();
                if (sectionType == null)
                    return null;
                if (sectionType.getVariant() != SectionTypeVariant.Default)
                    return null;
                if (issSection.getSectionContent().getSectionLineList().isEmpty())
                    return null;
                final Class<? extends SectionProperty> sectionValueClass = sectionType.getSectionValueClass();

                //Find key and its content value
                final SectionProperty sectionPropertyKey = Stream.of(sectionValueClass.getEnumConstants())
                        .filter(SectionProperty::isKey)
                        .findFirst().orElse(null);
                if (sectionPropertyKey == null)
                    return null;
                final String sectionValueKeyContent =  getSectionValueContent(sectionPropertyKey);
                if (StringUtils.isEmpty(sectionValueKeyContent))
                    return null;

                //Find info and its content value
                final SectionProperty sectionPropertyInfo = Stream.of(sectionValueClass.getEnumConstants())
                        .filter(SectionProperty::isInfo)
                        .findFirst().orElse(null);
                final String sectionValueInfoContent = getSectionValueContent(sectionPropertyInfo);

                //Contract strings
                return sectionValueKeyContent + (sectionValueInfoContent == null ? "" : " " + sectionValueInfoContent);
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                final SectionType sectionType = issSection.getSectionType();
                return sectionType == null ? null : sectionType.getIcon();
            }

            /**
             * Find content value for given section value
             * @param sectionPropertyInfo
             * @return
             */
            @Nullable
            private String getSectionValueContent(SectionProperty sectionPropertyInfo) {
                return sectionPropertyInfo == null ? null : issSection.getSectionContent().getSectionLineList().stream()
                        .filter(issSectionLine -> issSectionLine.getDefaultSectionLine() != null)
                        .map(issSectionLine1 -> issSectionLine1.getDefaultSectionLine().getDefaultProperty())
                        .filter(singleElement -> singleElement.getName().equalsIgnoreCase(sectionPropertyInfo.getName()))
                        .map(singleElement -> singleElement.getDefaultValue().getText())
                        .findFirst().orElse(null);
            }
        };
    }

    @Nullable
    static SectionType getSectionType(final IssSection section) {
        return SectionType.fromName(section.getName() == null ? "" : section.getName());
    }

    //</editor-fold>

    //<editor-fold desc="Key">

    @Nullable
    static IssSection getSection(final IssKey key) {
        return PsiTreeUtil.getParentOfType(key, IssSection.class);
    }

    @NotNull
    static String getName(final IssKey key) {
        return key.getText();
    }

    //</editor-fold>

    //<editor-fold desc="Section Line">

    @NotNull
    static ItemPresentation getPresentation(final IssSectionLine sectionLine) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                if (sectionLine.getMultipleSectionLine() == null)
                    return null;
                if (sectionLine.getSection() == null)
                    return null;
                final SectionType sectionType = sectionLine.getSection().getSectionType();
                if (sectionType == null)
                    return null;
                final SectionProperty[] sectionPropertyList = sectionType.getSectionValueClass().getEnumConstants();
                if (sectionPropertyList.length <= 0)
                    return null;
                final SectionProperty sectionProperty = Stream.of(sectionPropertyList)
                        .filter(SectionProperty::isKey).findFirst().orElse(sectionPropertyList[0]);

                return sectionLine.getMultipleSectionLine().getMultiplePropertyList().stream()
                        .filter(multiElement -> multiElement.getName().equalsIgnoreCase(sectionProperty.getName()))
                        .map(multiElement -> multiElement.getMultipleValue().getText())
                        .findFirst().orElse(null);
            }

            @Nullable
            @Override
            public String getLocationString() {
                if (sectionLine.getMultipleSectionLine() == null)
                    return null;
                if (sectionLine.getSection() == null)
                    return null;
                final SectionType sectionType = sectionLine.getSection().getSectionType();
                if (sectionType == null)
                    return null;
                final SectionProperty[] sectionPropertyList = sectionType.getSectionValueClass().getEnumConstants();
                if (sectionPropertyList.length <= 0)
                    return null;
                final SectionProperty sectionProperty = Stream.of(sectionPropertyList)
                        .filter(SectionProperty::isInfo).findFirst().orElse(null);
                if (sectionProperty == null)
                    return null;

                return sectionLine.getMultipleSectionLine().getMultiplePropertyList().stream()
                        .filter(multiElement -> multiElement.getName().equalsIgnoreCase(sectionProperty.getName()))
                        .map(multiElement -> multiElement.getMultipleValue().getText())
                        .findFirst().orElse(null);
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return null;
            }
        };
    }

    @Nullable
    static IssSection getSection(final IssSectionLine sectionLine) {
        return PsiTreeUtil.getParentOfType(sectionLine, IssSection.class);
    }

    //</editor-fold>

    @NotNull
    static String getValue(final IssStringValue stringValue) {
        return stringValue.getText().replace("\"", "");
    }

    @NotNull
    static String getName(final IssConstValue constValue) {
        return constValue.getFirstChild().getNextSibling().getText();
    }

    @NotNull
    static String getName(final IssDefaultProperty defaultProperty) {
        return defaultProperty.getDefaultKey().getName();
    }

    @NotNull
    static String getName(final IssMultipleProperty issMultipleProperty) {
        return issMultipleProperty.getMultipleKey().getName();
    }
}
