package org.pcsoft.plugins.intellij.inno_setup.script.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.lexer.IssTokenFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.types.*;

import java.util.function.Function;

/**
 * Created by Christoph on 14.12.2014.
 */
final class IssParserSectionUtility {

    public static void parseSections(PsiBuilder psiBuilder) {
        while (!psiBuilder.eof()) {
            if (psiBuilder.getTokenType() == IssTokenFactory.COMPILER_DIRECTIVE) {
                IssParserCompilerDirectiveUtility.parseCompilerDirective(psiBuilder);
            } else if (psiBuilder.getTokenType() == IssTokenFactory.SECTION_TITLE) {
                final PsiBuilder.Marker sectionMark = psiBuilder.mark();
                {
                    final IssSectionType sectionType = IssSectionType.fromId(psiBuilder.getTokenText());
                    if (sectionType == null) {
                        sectionMark.drop();

                        final PsiBuilder.Marker errorMark = psiBuilder.mark();
                        psiBuilder.advanceLexer();
                        errorMark.error("Unknown Section Type");

                        continue;
                    }

                    final PsiBuilder.Marker sectionTitleMark = psiBuilder.mark();
                    psiBuilder.advanceLexer();
                    if (psiBuilder.getTokenType() != IssTokenFactory.CRLF) {
                        psiBuilder.error("line end expected!");
                    } else {
                        psiBuilder.advanceLexer();
                    }
                    sectionTitleMark.done(IssMarkerFactory.SECTION_TITLE);

                    parseSection(psiBuilder, sectionType);
                    sectionMark.done(sectionType.getPropertyMarkerElement());
                }
            } else if (psiBuilder.getTokenType() == IssTokenFactory.CRLF) {
                psiBuilder.advanceLexer();//Empty line
            } else {
                final PsiBuilder.Marker errorMark = psiBuilder.mark();
                psiBuilder.advanceLexer();
                errorMark.error("Unknown element");
            }
        }
    }

    private static void parseSection(PsiBuilder psiBuilder, IssSectionType sectionType) {
        if (sectionType == IssSectionType.Code) {
            parseCodeSection(psiBuilder);
            return;
        }

        while (!psiBuilder.eof() && psiBuilder.getTokenType() != IssTokenFactory.SECTION_TITLE) {
            if (psiBuilder.getTokenType() == IssTokenFactory.COMPILER_DIRECTIVE) {
                IssParserCompilerDirectiveUtility.parseCompilerDirective(psiBuilder);
            } else if (psiBuilder.getTokenType() == IssTokenFactory.NAME) {
                switch (sectionType) {
                    case Setup:
                        parseLineForStandardSection(psiBuilder, "Setup Section", IssSetupProperty::getPropertyValueMarkerElementFromId,
                                IssSetupProperty::getPropertyMarkerElementFromId);
                        break;
                    case Task:
                        parseLineForDefinableSection(psiBuilder, "Tasks Section", IssMarkerFactory.TaskSection.SECTION_DEFINITION,
                                IssTaskProperty::getPropertyValueMarkerElementFromId, IssTaskProperty::getPropertyMarkerElementFromId);
                        break;
                    case File:
                        parseLineForDefinableSection(psiBuilder, "Files Section", IssMarkerFactory.FileSection.SECTION_DEFINITION,
                                IssFileProperty::getPropertyValueMarkerElementFromId, IssFileProperty::getPropertyMarkerElementFromId);
                        break;
                    case Directory:
                        parseLineForDefinableSection(psiBuilder, "Dirs Section", IssMarkerFactory.DirectorySection.SECTION_DEFINITION,
                                IssDirectoryProperty::getPropertyValueMarkerElementFromId, IssDirectoryProperty::getPropertyMarkerElementFromId);
                        break;
                    case Component:
                        parseLineForDefinableSection(psiBuilder, "Component Section", IssMarkerFactory.ComponentSection.SECTION_DEFINITION,
                                IssComponentProperty::getPropertyValueMarkerElementFromId, IssComponentProperty::getPropertyMarkerElementFromId);
                        break;
                    case Type:
                        parseLineForDefinableSection(psiBuilder, "Type Section", IssMarkerFactory.TypeSection.SECTION_DEFINITION,
                                IssTypeProperty::getPropertyValueMarkerElementFromId, IssTypeProperty::getPropertyMarkerElementFromId);
                        break;
                    case Icon:
                        parseLineForDefinableSection(psiBuilder, "Icon Section", IssMarkerFactory.IconSection.SECTION_DEFINITION,
                                IssIconProperty::getPropertyValueMarkerElementFromId, IssIconProperty::getPropertyMarkerElementFromId);
                        break;
                    case InstallRun:
                        parseLineForDefinableSection(psiBuilder, "Run Section", IssMarkerFactory.InstallRunSection.SECTION_DEFINITION,
                                IssInstallRunProperty::getPropertyValueMarkerElementFromId, IssInstallRunProperty::getPropertyMarkerElementFromId);
                        break;
                    case UninstallRun:
                        parseLineForDefinableSection(psiBuilder, "UninstallRun Section", IssMarkerFactory.UninstallRunSection.SECTION_DEFINITION,
                                IssUninstallRunProperty::getPropertyValueMarkerElementFromId, IssUninstallRunProperty::getPropertyMarkerElementFromId);
                        break;
                    case INI:
                        parseLineForDefinableSection(psiBuilder, "INI Section", IssMarkerFactory.INISection.SECTION_DEFINITION,
                                IssINIProperty::getPropertyValueMarkerElementFromId, IssINIProperty::getPropertyMarkerElementFromId);
                        break;
                    case Registry:
                        parseLineForDefinableSection(psiBuilder, "Registry Section", IssMarkerFactory.RegistrySection.SECTION_DEFINITION,
                                IssRegistryProperty::getPropertyValueMarkerElementFromId, IssRegistryProperty::getPropertyMarkerElementFromId);
                        break;
                    default:
                        throw new RuntimeException();
                }
            } else if (psiBuilder.getTokenType() == IssTokenFactory.CRLF) {
                psiBuilder.advanceLexer();//Empty line
            } else {
                final PsiBuilder.Marker errorMark = psiBuilder.mark();
                psiBuilder.advanceLexer();
                errorMark.error("Unknown element");
            }
        }
    }

    private static void parseLineForDefinableSection(PsiBuilder psiBuilder, String sectionName, IElementType definitionMarkerElement,
                                                     Function<String, IElementType> definitionResolver) {
        parseLineForDefinableSection(psiBuilder, sectionName, definitionMarkerElement, null, definitionResolver);
    }

    private static void parseLineForDefinableSection(PsiBuilder psiBuilder, String sectionName, IElementType definitionMarkerElement,
                                                     Function<String, IElementType> singlePropertyValueResolver, Function<String, IElementType> propertyResolver) {
        final PsiBuilder.Marker definitionMark = psiBuilder.mark();
        {
            while (!psiBuilder.eof() && psiBuilder.getTokenType() != IssTokenFactory.CRLF) {
                final PsiBuilder.Marker propertyMark = psiBuilder.mark();
                {
                    final String identifier = psiBuilder.getTokenText();
                    final IElementType markerElement = propertyResolver.apply(identifier);
                    if (markerElement == null) {
                        propertyMark.drop();

                        final PsiBuilder.Marker errorMark = psiBuilder.mark();
                        psiBuilder.advanceLexer();
                        errorMark.error("Unknown " + sectionName + " Property");

                        continue;
                    }

                    if (psiBuilder.getTokenType() != IssTokenFactory.NAME) {
                        propertyMark.drop();
                        psiBuilder.advanceLexer();
                        psiBuilder.error("Name expected");
                        continue;
                    }

                    final PsiBuilder.Marker itemNameMark = psiBuilder.mark();
                    psiBuilder.advanceLexer();
                    itemNameMark.done(IssMarkerFactory.IDENTIFIER);

                    if (psiBuilder.getTokenType() != IssTokenFactory.OPERATOR_COLON) {
                        propertyMark.drop();
                        psiBuilder.advanceLexer();
                        psiBuilder.error("':' expected");
                        continue;
                    }
                    psiBuilder.advanceLexer();

                    final PsiBuilder.Marker propertyValueMark = psiBuilder.mark();
                    while (!psiBuilder.eof() && psiBuilder.getTokenType() != IssTokenFactory.CRLF && psiBuilder.getTokenType() != IssTokenFactory.OPERATOR_SEMICOLON) {
                        if (psiBuilder.getTokenType() != IssTokenFactory.WORD &&
                                psiBuilder.getTokenType() != IssTokenFactory.STRING &&
                                psiBuilder.getTokenType() != IssTokenFactory.NAME) {
                            final PsiBuilder.Marker errorMark = psiBuilder.mark();
                            psiBuilder.advanceLexer();
                            errorMark.error("Unknown element");
                            continue;
                        }

                        final IElementType singlePropertyValueMarkerElement = singlePropertyValueResolver == null ?
                                null : singlePropertyValueResolver.apply(identifier);
                        if (singlePropertyValueMarkerElement != null) {
                            final PsiBuilder.Marker singleValueMark = psiBuilder.mark();
                            psiBuilder.advanceLexer();
                            singleValueMark.done(singlePropertyValueMarkerElement);
                        } else {
                            psiBuilder.advanceLexer();
                        }
                    }
                    propertyValueMark.done(IssMarkerFactory.VALUE);

                    if ((!psiBuilder.eof() && (psiBuilder.getTokenType() == IssTokenFactory.CRLF || psiBuilder.getTokenType() == IssTokenFactory.OPERATOR_SEMICOLON)) || psiBuilder.eof()) {
                        if (psiBuilder.getTokenType() == IssTokenFactory.OPERATOR_SEMICOLON) {
                            psiBuilder.advanceLexer();
                        }
                        propertyMark.done(markerElement);
                    } else {
                        propertyValueMark.drop();
                        propertyMark.drop();

                        psiBuilder.advanceLexer();
                        psiBuilder.error("';' or line end expected");
                    }
                }
            }

            if (!psiBuilder.eof() && psiBuilder.getTokenType() == IssTokenFactory.CRLF) {
                psiBuilder.advanceLexer();
            }
        }
        definitionMark.done(definitionMarkerElement);
    }

    private static void parseLineForStandardSection(PsiBuilder psiBuilder, String sectionName, Function<String, IElementType> singlePropertyValueResolver,
                                                    Function<String, IElementType> propertyResolver) {
        final PsiBuilder.Marker propertyMark = psiBuilder.mark();
        {
            final String identifier = psiBuilder.getTokenText();
            final IElementType markerElement = propertyResolver.apply(identifier);
            if (markerElement == null) {
                propertyMark.drop();

                final PsiBuilder.Marker errorMark = psiBuilder.mark();
                psiBuilder.advanceLexer();
                errorMark.error("Unknown " + sectionName + " Property");

                return;
            }

            final PsiBuilder.Marker itemNameMark = psiBuilder.mark();
            psiBuilder.advanceLexer();
            itemNameMark.done(IssMarkerFactory.IDENTIFIER);

            if (psiBuilder.getTokenType() != IssTokenFactory.OPERATOR_EQUAL) {
                psiBuilder.advanceLexer();
                propertyMark.drop();
                psiBuilder.error("'=' expected");
                return;
            }
            psiBuilder.advanceLexer();

            final PsiBuilder.Marker propertyValueMark = psiBuilder.mark();
            final PsiBuilder.Marker propertyValueInnerMark = psiBuilder.mark();
            while (!psiBuilder.eof() && psiBuilder.getTokenType() != IssTokenFactory.CRLF) {
                if (psiBuilder.getTokenType() == IssTokenFactory.WORD ||
                        psiBuilder.getTokenType() == IssTokenFactory.STRING ||
                        psiBuilder.getTokenType() == IssTokenFactory.NAME) {
                    psiBuilder.advanceLexer();
                } else {
                    final PsiBuilder.Marker errorMark = psiBuilder.mark();
                    psiBuilder.advanceLexer();
                    errorMark.error("Unknown element");
                }
            }
            final IElementType singlePropertyValueMarkerElement = singlePropertyValueResolver == null ?
                    null : singlePropertyValueResolver.apply(identifier);
            if (singlePropertyValueMarkerElement != null) {
                propertyValueInnerMark.done(singlePropertyValueMarkerElement);
            } else {
                propertyValueInnerMark.drop();
            }
            propertyValueMark.done(IssMarkerFactory.VALUE);

            if (!psiBuilder.eof() && psiBuilder.getTokenType() == IssTokenFactory.CRLF) {
                psiBuilder.advanceLexer();
            }

            propertyMark.done(markerElement);
        }
    }

    private static void parseCodeSection(PsiBuilder psiBuilder) {
        while (!psiBuilder.eof() && psiBuilder.getTokenType() != IssTokenFactory.SECTION_TITLE) {
            psiBuilder.advanceLexer();
            //TODO
        }
    }

    private IssParserSectionUtility() {
    }
}
