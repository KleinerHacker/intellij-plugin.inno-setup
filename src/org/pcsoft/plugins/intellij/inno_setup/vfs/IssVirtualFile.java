package org.pcsoft.plugins.intellij.inno_setup.vfs;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import org.apache.sanselan.util.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Arrays;

/**
 * Created by Christoph on 24.11.2015.
 */
public class IssVirtualFile extends VirtualFile {
    private final File path;
    private final IssVirtualFileSystem virtualFileSystem;
    private byte[] content;

    public IssVirtualFile(File path, IssVirtualFileSystem virtualFileSystem) {
        this.path = path;
        this.virtualFileSystem = virtualFileSystem;
        try {
            this.content = IOUtils.getFileBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
            content = new byte[0];
        }
    }

    @NotNull
    @Override
    public String getName() {
        return path.getAbsolutePath();
    }

    @NotNull
    @Override
    public VirtualFileSystem getFileSystem() {
        return virtualFileSystem;
    }

    @NotNull
    @Override
    public String getPath() {
        return path.getAbsolutePath();
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean isValid() {
        return content.length > 0;
    }

    @Override
    public VirtualFile getParent() {
        return null;
    }

    @Override
    public VirtualFile[] getChildren() {
        return null;
    }

    @NotNull
    @Override
    public OutputStream getOutputStream(Object o, long l, long l1) throws IOException {
        return new FileOutputStream(path, false);
    }

    @NotNull
    @Override
    public byte[] contentsToByteArray() throws IOException {
        return content;
    }

    @Override
    public long getTimeStamp() {
        return -1L;
    }

    @Override
    public long getModificationStamp() {
        return Arrays.hashCode(content);
    }

    @Override
    public long getLength() {
        return content.length;
    }

    @Override
    public void refresh(boolean b, boolean b1, @Nullable Runnable runnable) {

    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(path);
    }
}
