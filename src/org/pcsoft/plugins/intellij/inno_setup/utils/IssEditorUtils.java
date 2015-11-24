package org.pcsoft.plugins.intellij.inno_setup.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import org.apache.commons.lang.SystemUtils;
import org.pcsoft.plugins.intellij.inno_setup.run.IssRunErrorHandler;
import org.pcsoft.plugins.intellij.inno_setup.script.IssScriptFileType;

/**
 * Created by Christoph on 22.11.2015.
 */
public final class IssEditorUtils {

    public static boolean openFile(Project project, String file) {
        return openFileAndGoto(project, file, -1);
    }

    public static boolean openFileAndGoto(Project project, String file, int lineNumber) {
        return openFileAndGoto(project, file, lineNumber, 0);
    }

    public static boolean openFileAndGoto(Project project, String file, int lineNumber, int column) {
        final VirtualFile vFile = getVirtualFile(project, file);
        if (vFile == null)
            return false;

        openFileAndGoto(project, vFile, lineNumber, column);
        return true;
    }

    public static void openFile(Project project, VirtualFile virtualFile) {
        openFileAndGoto(project, virtualFile, -1);
    }

    public static void openFileAndGoto(Project project, VirtualFile virtualFile, int lineNumber) {
        openFileAndGoto(project, virtualFile, lineNumber, 0);
    }

    public static void openFileAndGoto(Project project, VirtualFile virtualFile, int lineNumber, int column) {
        ApplicationManager.getApplication().invokeLater(() -> {
            final FileEditor[] fileEditors = FileEditorManager.getInstance(project).openFile(virtualFile, true);
            if (fileEditors.length <= 0) {
                final Editor textEditor = FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, virtualFile, lineNumber, column), true);
                if (textEditor == null) {
                    Logger.getInstance(IssRunErrorHandler.class).error("Unable to find file: " + virtualFile.getPath());
                    return;
                }
            }
            if (lineNumber >= 0) {
                final Editor textEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                textEditor.getCaretModel().moveToLogicalPosition(new LogicalPosition(lineNumber, column));
            }
        });
    }

    private static VirtualFile getVirtualFile(Project project, String file) {
        return ApplicationManager.getApplication().runReadAction((Computable<VirtualFile>) () -> {
            final VirtualFile virtualFile = FileBasedIndex.getInstance().getContainingFiles(
                    FileTypeIndex.NAME, IssScriptFileType.INSTANCE, GlobalSearchScope.allScope(project)
            ).stream().filter(item -> item.getPath().replace("/", SystemUtils.FILE_SEPARATOR).equals(file.replace("/", SystemUtils.FILE_SEPARATOR))).findFirst().orElse(null);
            if (virtualFile == null) {
                Logger.getInstance(IssRunErrorHandler.class).error("Unable to find file: " + file);
                return null;
            }

            return virtualFile;
        });
    }

    private IssEditorUtils() {
    }
}
