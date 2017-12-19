package org.pcsoft.plugins.intellij.iss.language.parser.psi;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.parser.IssGenTypes;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.*;
import org.pcsoft.plugins.intellij.iss.language.type.Section;

import javax.swing.*;

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
    static ItemPresentation getPresentation(final IssSection section) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return section.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return section.getContainingFile() == null ? null : section.getContainingFile().getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                final Section sectionType = section.getSectionType();
                return sectionType == null ? null : sectionType.getIcon();
            }
        };
    }

    @Nullable
    static Section getSectionType(final IssSection section) {
        return Section.fromName(section.getName() == null ? "" : section.getName());
    }

    //</editor-fold>

    //<editor-fold desc="Element Key">

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
                return sectionLine.getText();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return sectionLine.getSection() == null ? null : sectionLine.getSection().getName();
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
    static String getName(final  IssConstValue constValue) {
        return constValue.getFirstChild().getNextSibling().getText();
    }
}
