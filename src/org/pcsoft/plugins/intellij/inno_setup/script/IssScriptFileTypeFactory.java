package org.pcsoft.plugins.intellij.inno_setup.script;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Christoph on 12.12.2014.
 */
public class IssScriptFileTypeFactory extends FileTypeFactory {

    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(IssScriptFileType.INSTANCE);
    }
}
