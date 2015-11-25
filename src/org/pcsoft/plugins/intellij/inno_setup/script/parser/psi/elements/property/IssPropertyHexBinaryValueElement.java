package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyValueElement;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssPropertyHexBinaryValueElement extends IssPropertyValueElement {

    public IssPropertyHexBinaryValueElement(ASTNode node) {
        super(node);
    }

    @Nullable
    public byte[] getBytes() {
        try {
            return new HexBinaryAdapter().unmarshal((getText().length() % 2 != 0 ? "0" : "") + getText());
        } catch (Exception e) {
            return null;
        }
    }
}
