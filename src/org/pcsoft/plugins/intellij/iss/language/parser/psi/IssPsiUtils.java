package org.pcsoft.plugins.intellij.iss.language.parser.psi;

import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.parser.IssGenTypes;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssSection;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssSectionTitle;
import org.pcsoft.plugins.intellij.iss.language.type.Section;

import javax.swing.*;

/**
 * Created by Christoph on 30.09.2016.
 */
public interface IssPsiUtils {

    static String getName(final IssSectionTitle sectionTitle) {
        return sectionTitle.getNode().findChildByType(IssGenTypes.NAME).getText();
    }

    static String getName(final IssSection section) {
        return section.getSectionTitle() == null ? null : section.getSectionTitle().getName();
    }

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

    static Section getSectionType(final IssSection section) {
        return Section.fromName(section.getName() == null ? "" : section.getName());
    }
}
