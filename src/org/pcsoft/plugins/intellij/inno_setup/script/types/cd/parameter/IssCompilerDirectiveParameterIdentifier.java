package org.pcsoft.plugins.intellij.inno_setup.script.types.cd.parameter;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by pfeifchr on 02.12.2015.
 */
public interface IssCompilerDirectiveParameterIdentifier {
    public static IssCompilerDirectiveParameterIdentifier[] getValues(Class<? extends IssCompilerDirectiveParameterIdentifier> clazz) {
        final List<IssCompilerDirectiveParameterIdentifier> list = Arrays.asList(clazz.getEnumConstants());
        Collections.sort(list, (o1, o2) -> Integer.compare(o1.getOrderNumber(), o2.getOrderNumber()));

        return list.toArray(new IssCompilerDirectiveParameterIdentifier[list.size()]);
    }

    @NotNull
    String getName();

    @NotNull
    IElementType getElementType();

    int getOrderNumber();

    boolean isOptional();

    @NotNull
    Predicate<String> isParameter();
}
