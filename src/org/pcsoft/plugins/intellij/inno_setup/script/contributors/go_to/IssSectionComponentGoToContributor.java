package org.pcsoft.plugins.intellij.inno_setup.script.contributors.go_to;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssComponentDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssSectionComponentGoToContributor implements ChooseByNameContributor {

    @NotNull
    @Override
    public String[] getNames(Project project, boolean b) {
        final Collection<IssComponentDefinitionElement> componentDefinitionElements = IssFindUtils.findComponentDefinitionElements(project);
        final List<String> list = componentDefinitionElements.stream().map(IssComponentDefinitionElement::getName).collect(Collectors.toList());
        return list.toArray(new String[list.size()]);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean b) {
        final Collection<IssComponentDefinitionElement> componentDefinitionElements = IssFindUtils.findComponentDefinitionElements(project, name, true);
        return componentDefinitionElements.toArray(new NavigationItem[componentDefinitionElements.size()]);
    }
}
