package org.pcsoft.plugins.intellij.iss.language.type.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.type.constant.CommonConstantType;
import org.pcsoft.plugins.intellij.iss.language.type.constant.DirectoryConstantType;
import org.pcsoft.plugins.intellij.iss.language.type.constant.ShellFolderConstantType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface ConstantType extends TypeBase {
    @Nullable
    static ConstantType fromName(@Nullable final String name) {
        ConstantType constantType = DirectoryConstantType.fromName(name);
        if (constantType == null) {
            constantType = ShellFolderConstantType.fromName(name);
            if (constantType == null) {
                constantType = CommonConstantType.fromName(name);
            }
        }

        return constantType;
    }

    @NotNull
    static ConstantType[] getAllConstantTypes() {
        final List<ConstantType> list = new ArrayList<>(Arrays.asList(DirectoryConstantType.values()));
        list.addAll(Arrays.asList(ShellFolderConstantType.values()));
        list.addAll(Arrays.asList(CommonConstantType.values()));

        return list.toArray(new ConstantType[list.size()]);
    }

    int getArgumentCount();
    boolean needDefault();
}
