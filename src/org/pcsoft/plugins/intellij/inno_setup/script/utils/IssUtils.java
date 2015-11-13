package org.pcsoft.plugins.intellij.inno_setup.script.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.IssFileType;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.IssFile;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinitionElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Christoph on 18.12.2014.
 */
final class IssUtils {

    @NotNull
    public static <T extends IssDefinitionElement> Collection<T> findDefinitions(
            final Project project, final String name, final boolean variant, final Class<T> classToSearchFor,
            final Function<T, Boolean> notNullOrEmptyCheck, final Function<T, String> getName
            ) {
        final List<T> list = new ArrayList<>();
        IssUtils.findFiles(project, file -> {
            final Collection<T> definitionElements = PsiTreeUtil.findChildrenOfType(file, classToSearchFor);
            if (name == null) {
                list.addAll(
                        definitionElements.stream()
                                .filter(notNullOrEmptyCheck::apply)
                                .collect(Collectors.toList())
                );
                return;
            }

            for (final T definitionElement : definitionElements) {
                if (!notNullOrEmptyCheck.apply(definitionElement))
                    continue;

                if (variant) {
                    if (StringUtils.startsWithIgnoreCase(getName.apply(definitionElement), name)) {
                        list.add(definitionElement);
                    }
                } else {
                    if (getName.apply(definitionElement).equalsIgnoreCase(name)) {
                        list.add(definitionElement);
                    }
                }
            }
        });
        return list;
    }

    private static void findFiles(@NotNull Project project, @NotNull Consumer<IssFile> visitor) {
        final Collection<VirtualFile> virtualFiles = FileBasedIndex.getInstance().getContainingFiles(
                FileTypeIndex.NAME, IssFileType.INSTANCE, GlobalSearchScope.allScope(project)
        );
        for (final VirtualFile virtualFile : virtualFiles) {
            final IssFile issFile = (IssFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (issFile != null) {
                visitor.accept(issFile);
            }
        }
    }

    @NotNull
    public static IssFile createFile(@NotNull Project project, @NotNull String text) {
        return (IssFile) PsiFileFactory.getInstance(project).createFileFromText("tmp.asn", IssLanguage.INSTANCE, text);
    }

    private IssUtils() {
    }
}
