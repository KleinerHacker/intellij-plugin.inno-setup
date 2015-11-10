package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.setup;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinableSectionIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssValueType;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssSetupSectionAppNameElement extends IssDefinitionPropertyElement {

    public IssSetupSectionAppNameElement(ASTNode node) {
        super(node, null, null);
    }

    @NotNull
    @Override
    public IssDefinableSectionIdentifier getPropertyType() {
        return new IssDefinableSectionIdentifier() {
            @Nullable
            @Override
            public IElementType getValueMarkerElement() {
                return null;
            }

            @Override
            public boolean isRequired() {
                return false;
            }

            @NotNull
            @Override
            public IssValueType getValueType() {
                return null;
            }

            @NotNull
            @Override
            public String getId() {
                return null;
            }

            @NotNull
            @Override
            public String getDescriptionKey() {
                return null;
            }

            @NotNull
            @Override
            public IElementType getItemMarkerElement() {
                return null;
            }

            @Override
            public boolean isDeprecated() {
                return false;
            }
        };
    }
}
