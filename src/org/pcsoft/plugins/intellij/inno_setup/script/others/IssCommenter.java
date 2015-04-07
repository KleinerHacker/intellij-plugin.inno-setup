package org.pcsoft.plugins.intellij.inno_setup.script.others;

import com.intellij.lang.Commenter;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Christoph on 23.12.2014.
 */
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
