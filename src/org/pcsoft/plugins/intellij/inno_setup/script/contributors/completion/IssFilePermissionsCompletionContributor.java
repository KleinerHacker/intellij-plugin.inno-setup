package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFilePropertyFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFilePropertyPermissionsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFilePermissions;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFlag;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssFilePermissionsCompletionContributor extends IssFlagCompletionContributor<IssFilePropertyPermissionsElement> {
    public IssFilePermissionsCompletionContributor() {
        super(IssFilePropertyPermissionsElement.class);
    }

    @Override
    protected IssFlag[] getFlagList() {
        return IssFilePermissions.values();
    }
}
