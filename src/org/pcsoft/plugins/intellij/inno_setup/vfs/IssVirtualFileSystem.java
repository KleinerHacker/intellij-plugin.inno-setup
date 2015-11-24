package org.pcsoft.plugins.intellij.inno_setup.vfs;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.ex.dummy.DummyFileSystem;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Created by Christoph on 24.11.2015.
 */
public class IssVirtualFileSystem extends DummyFileSystem {
    private static final String KEY = "iss-file-system";

    public IssVirtualFileSystem() {
    }

    @NotNull
    @Override
    public String getProtocol() {
        return KEY;
    }

    @Override
    public VirtualFile findFileByPath(@NotNull String path) {
        return new IssVirtualFile(new File(path), this);
    }

    public static IssVirtualFileSystem getInstance() {
        return (IssVirtualFileSystem) VirtualFileManager.getInstance().getFileSystem(KEY);
    }
}
