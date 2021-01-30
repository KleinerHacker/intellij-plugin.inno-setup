package org.pcsoft.plugins.intellij.iss.core.language.common;

import com.intellij.lang.Commenter;
import org.jetbrains.annotations.Nullable;

public class IssCommenter implements Commenter {

    @Nullable
    @Override
    public String getLineCommentPrefix() {
        return ";";
    }

    @Nullable
    @Override
    public String getBlockCommentPrefix() {
        return null;
    }

    @Nullable
    @Override
    public String getBlockCommentSuffix() {
        return null;
    }

    @Nullable
    @Override
    public String getCommentedBlockCommentPrefix() {
        return null;
    }

    @Nullable
    @Override
    public String getCommentedBlockCommentSuffix() {
        return null;
    }
}
