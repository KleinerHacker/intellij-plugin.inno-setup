package org.pcsoft.plugins.intellij.inno_setup.script.contributors.go_to;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssTypeUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssSectionTypeGoToContributor implements ChooseByNameContributor {

    @NotNull
    @Override
    public String[] getNames(Project project, boolean b) {
        final Collection<IssTypeDefinitionElement> typeDefinitions = IssTypeUtils.findTypeDefinitions(project);
        final List<String> list = typeDefinitions.stream().map(IssTypeDefinitionElement::getName).collect(Collectors.toList());
        return list.toArray(new String[list.size()]);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean b) {
        final Collection<IssTypeDefinitionElement> typeDefinitions = IssTypeUtils.findTypeDefinitions(project, name, true);
        return typeDefinitions.toArray(new NavigationItem[typeDefinitions.size()]);
    }
}
