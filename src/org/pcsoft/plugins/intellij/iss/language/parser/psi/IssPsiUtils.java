package org.pcsoft.plugins.intellij.iss.language.parser.psi;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.parser.IssGenTypes;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.*;
import org.pcsoft.plugins.intellij.iss.language.reference.IssComponentsReference;
import org.pcsoft.plugins.intellij.iss.language.reference.IssDefineSymbolReference;
import org.pcsoft.plugins.intellij.iss.language.reference.IssTasksReference;
import org.pcsoft.plugins.intellij.iss.language.reference.IssTypesReference;
import org.pcsoft.plugins.intellij.iss.language.type.PreprocessorType;
import org.pcsoft.plugins.intellij.iss.language.type.SectionType;
import org.pcsoft.plugins.intellij.iss.language.type.SectionTypeVariant;
import org.pcsoft.plugins.intellij.iss.language.type.base.ConstantType;
import org.pcsoft.plugins.intellij.iss.language.type.base.PropertyType;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Christoph on 30.09.2016.
 */
public interface IssPsiUtils {

    //<editor-fold desc="Section Title">
    @Nullable
    static String getName(final IssSectionTitle sectionTitle) {
        final ASTNode childByType = sectionTitle.getNode().findChildByType(IssGenTypes.NAME);
        if (childByType == null)
            return null;

        return childByType.getText();
    }

    @Nullable
    static SectionType getSectionType(final IssSectionTitle sectionTitle) {
        return SectionType.fromName(sectionTitle.getName());
    }
    //</editor-fold>

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
                if (issSection.getMultipleSectionLineList().isEmpty() && issSection.getDefaultSectionLineList().isEmpty())
                    return null;
                final Class<? extends PropertyType> sectionValueClass = sectionType.getSectionPropertyClass();

                //Find key and its content value
                final PropertyType propertyTypeKey = Stream.of(sectionValueClass.getEnumConstants())
                        .filter(PropertyType::isKey)
                        .findFirst().orElse(null);
                if (propertyTypeKey == null)
                    return null;
                final String sectionValueKeyContent = getSectionValueContent(propertyTypeKey);
                if (StringUtils.isEmpty(sectionValueKeyContent))
                    return null;

                //Find info and its content value
                final PropertyType propertyTypeInfo = Stream.of(sectionValueClass.getEnumConstants())
                        .filter(PropertyType::isInfo)
                        .findFirst().orElse(null);
                final String sectionValueInfoContent = getSectionValueContent(propertyTypeInfo);

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
             * @param propertyTypeInfo
             * @return
             */
            @Nullable
            private String getSectionValueContent(PropertyType propertyTypeInfo) {
                return propertyTypeInfo == null ? null : issSection.getDefaultSectionLineList().stream()
                        .map(IssDefaultSectionLine::getDefaultProperty)
                        .filter(singleElement -> singleElement.getName().equalsIgnoreCase(propertyTypeInfo.getName()))
                        .map(singleElement -> singleElement.getDefaultValue().getText())
                        .findFirst().orElse(null);
            }
        };
    }

    @Nullable
    static SectionType getSectionType(final IssSection section) {
        return section.getSectionTitle().getSectionType();
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

    @Nullable
    static PropertyType getPropertyType(final IssKey key) {
        final IssSection section = key.getSection();
        if (section == null)
            return null;
        final SectionType sectionType = section.getSectionType();
        if (sectionType == null)
            return null;
        final Class<? extends PropertyType> sectionPropertyClass = sectionType.getSectionPropertyClass();

        for (PropertyType propertyType : sectionPropertyClass.getEnumConstants()) {
            if (propertyType.getName().equalsIgnoreCase(key.getName()))
                return propertyType;
        }

        return null;
    }

    //</editor-fold>

    //<editor-fold desc="Value">
    @Nullable
    static IssSection getSection(final IssValue value) {
        return PsiTreeUtil.getParentOfType(value, IssSection.class);
    }

    @Nullable
    static IssProperty getProperty(final IssValue value) {
        return PsiTreeUtil.getParentOfType(value, IssProperty.class);
    }
    //</editor-fold>

    //<editor-fold desc="Section Line">

    @NotNull
    static ItemPresentation getPresentation(final IssSectionLine sectionLine) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                if (!(sectionLine instanceof IssMultipleSectionLine))
                    return null;
                if (sectionLine.getSection() == null)
                    return null;
                final SectionType sectionType = sectionLine.getSection().getSectionType();
                if (sectionType == null)
                    return null;
                final PropertyType[] propertyTypeList = sectionType.getSectionPropertyClass().getEnumConstants();
                if (propertyTypeList.length <= 0)
                    return null;
                final PropertyType propertyType = Stream.of(propertyTypeList)
                        .filter(PropertyType::isKey).findFirst().orElse(propertyTypeList[0]);

                return ((IssMultipleSectionLine) sectionLine).getMultiplePropertyList().stream()
                        .filter(multiElement -> multiElement.getName().equalsIgnoreCase(propertyType.getName()))
                        .map(multiElement -> multiElement.getMultipleValue().getText())
                        .findFirst().orElse(null);
            }

            @Nullable
            @Override
            public String getLocationString() {
                if (!(sectionLine instanceof IssMultipleSectionLine))
                    return null;
                if (sectionLine.getSection() == null)
                    return null;
                final SectionType sectionType = sectionLine.getSection().getSectionType();
                if (sectionType == null)
                    return null;
                final PropertyType[] propertyTypeList = sectionType.getSectionPropertyClass().getEnumConstants();
                if (propertyTypeList.length <= 0)
                    return null;
                final PropertyType propertyType = Stream.of(propertyTypeList)
                        .filter(PropertyType::isInfo).findFirst().orElse(null);
                if (propertyType == null)
                    return null;

                return ((IssMultipleSectionLine) sectionLine).getMultiplePropertyList().stream()
                        .filter(multiElement -> multiElement.getName().equalsIgnoreCase(propertyType.getName()))
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

    //<editor-fold desc="Constant">
    @Nullable
    static String getName(final IssConstValue constValue) {
        if (constValue.getConstName() == null)
            return null;

        return constValue.getConstName().getText();
    }

    @Nullable
    static ConstantType getConstantType(final IssConstValue constValue) {
        if (constValue.getName() == null)
            return null;

        return ConstantType.fromName(constValue.getName());
    }
    //</editor-fold>

    //<editor-fold desc="Property">

    @NotNull
    static String getName(final IssProperty property) {
        if (property instanceof IssMultipleProperty)
            return ((IssMultipleProperty) property).getMultipleKey().getName();
        else if (property instanceof IssDefaultProperty)
            return ((IssDefaultProperty) property).getDefaultKey().getName();

        throw new RuntimeException();
    }

    @NotNull
    static IssKey getKey(final IssProperty property) {
        if (property instanceof IssMultipleProperty)
            return ((IssMultipleProperty) property).getMultipleKey();
        else if (property instanceof IssDefaultProperty)
            return ((IssDefaultProperty) property).getDefaultKey();

        throw new RuntimeException();
    }

    @Nullable
    static PropertyType getPropertyType(final IssProperty property) {
        return property.getKey().getPropertyType();
    }

    @Nullable
    static IssValue getValue(final IssProperty property) {
        if (property instanceof IssDefaultProperty)
            return ((IssDefaultProperty) property).getDefaultValue();
        else if (property instanceof IssMultipleProperty)
            return ((IssMultipleProperty) property).getMultipleValue();

        throw new RuntimeException();
    }

    @Nullable
    static IssSection getSection(final IssProperty property) {
        return PsiTreeUtil.getParentOfType(property, IssSection.class);
    }

    //</editor-fold>

    //<editor-fold desc="Preprocessor Name">
    @Nullable
    static String getName(final IssPreprocessorName preprocessorName) {
        ASTNode nameNode = preprocessorName.getNode().findChildByType(IssGenTypes.NAME);
        if (nameNode == null)
            return null;

        return nameNode.getText();
    }

    @Nullable
    static org.pcsoft.plugins.intellij.iss.language.type.PreprocessorType getPreprocessorType(final IssPreprocessorName preprocessorName) {
        if (preprocessorName.getName() == null)
            return null;

        return org.pcsoft.plugins.intellij.iss.language.type.PreprocessorType.fromName(preprocessorName.getName());
    }
    //</editor-fold>

    //<editor-fold desc="Preprocessor Element">
    @Nullable
    static String getName(final IssPreprocessorElement preprocessorElement) {
        if (preprocessorElement.getPreprocessorName() == null)
            return null;

        return preprocessorElement.getPreprocessorName().getName();
    }

    @Nullable
    static org.pcsoft.plugins.intellij.iss.language.type.PreprocessorType getPreprocessorType(final IssPreprocessorElement preprocessorElement) {
        return preprocessorElement.getPreprocessorName().getPreprocessorType();
    }
    //</editor-fold>

    //<editor-fold desc="Identifier Value">
    @NotNull
    static String getName(final IssIdentifierValue identifierValue) {
        return identifierValue.getText();
    }

    @Nullable
    static PsiElement setName(final IssIdentifierValue identifierValue, final String name) {
        return null;
    }

    @Nullable
    static PsiElement getNameIdentifier(final IssIdentifierValue identifierValue) {
        final IssPreprocessorElement preprocessorElement = PsiTreeUtil.getParentOfType(identifierValue, IssPreprocessorElement.class);
        if (preprocessorElement != null) {
            final PreprocessorType preprocessorType = preprocessorElement.getPreprocessorType();
            if (preprocessorType == null)
                return null;

            switch (preprocessorType) {
                case Define:
                    if (preprocessorElement.getPreprocessorValueList().isEmpty())
                        return null;
                    return preprocessorElement.getPreprocessorValueList().get(0);
                case Include:
                case PreProc:
                    return null;
                default:
                    throw new RuntimeException();
            }
        }

        return null;
    }
    //</editor-fold>

    //<editor-fold desc="Reference Value">
    @NotNull
    static String getName(final IssRefValue refValue) {
        return refValue.getText();
    }

    @Nullable
    static PsiElement setName(final IssRefValue refValue, final String name) {
        return null;
    }

    @Nullable
    static PsiElement getNameIdentifier(final IssRefValue refValue) {
        return refValue;
    }

    @NotNull
    static PsiReference[] getReferences(final IssRefValue refValue) {
        final List<PsiReference> list = new ArrayList<>();
        if (PsiTreeUtil.getParentOfType(refValue, IssConstValue.class) != null) {
            list.add(new IssDefineSymbolReference(refValue, true));
        } else if (PsiTreeUtil.getParentOfType(refValue, IssProperty.class) != null) {
            final IssProperty property = PsiTreeUtil.getParentOfType(refValue, IssProperty.class);
            final PropertyType propertyType = property.getPropertyType();
            if (propertyType != null) {
                if (propertyType.getReferenceTargetSectionType() == SectionType.Types) {
                    list.add(new IssTypesReference(refValue, true));
                } else if (propertyType.getReferenceTargetSectionType() == SectionType.Tasks) {
                    list.add(new IssTasksReference(refValue, true));
                } else if (propertyType.getReferenceTargetSectionType() == SectionType.Components) {
                    list.add(new IssComponentsReference(refValue, true));
                }
            }
        }

        return list.toArray(new PsiReference[list.size()]);
    }
    //</editor-fold>
}
