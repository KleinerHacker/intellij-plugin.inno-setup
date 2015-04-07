package org.pcsoft.plugins.intellij.inno_setup.script.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import org.pcsoft.plugins.intellij.inno_setup.script.IssFileType;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.IssFile;

import java.util.Collection;

/**
 * Created by Christoph on 18.12.2014.
 */
final class IssUtils {

    public static interface FileVisitor {
        void visitFile(IssFile file);
    }

    public static void findFiles(Project project, FileVisitor visitor) {
        final Collection<VirtualFile> virtualFiles = FileBasedIndex.getInstance().getContainingFiles(
                FileTypeIndex.NAME, IssFileType.INSTANCE, GlobalSearchScope.allScope(project)
        );
        for (final VirtualFile virtualFile : virtualFiles) {
            final IssFile issFile = (IssFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (issFile != null) {
                visitor.visitFile(issFile);
            }
        }
    }

    public static IssFile createFile(Project project, String text) {
        return (IssFile) PsiFileFactory.getInstance(project).createFileFromText("tmp.asn", IssLanguage.INSTANCE, text);
    }

    private IssUtils() {
    }
}
