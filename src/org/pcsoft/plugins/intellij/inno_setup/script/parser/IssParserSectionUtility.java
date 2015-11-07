package org.pcsoft.plugins.intellij.inno_setup.script.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.lexer.IssTokenFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssComponentProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssSectionType;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssSetupProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTaskProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTypeProperty;

/**
 * Created by Christoph on 14.12.2014.
 */
final class IssParserSectionUtility {

    private static interface MarkerElementResolver {
        IElementType resolveMarkerElement(final String identifier);
    }

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
                    sectionMark.done(sectionType.getItemMarkerElement());
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
                        parseLineForSetupSection(psiBuilder);
                        break;
                    case Task:
                        parseLineForDefaultSection(psiBuilder, "Tasks Section", IssMarkerFactory.TaskSection.SECTION_DEFINITION,
                                IssTaskProperty::getValueMarkerElementFromId, IssTaskProperty::getItemMarkerElementFromId);
                        break;
                    case File:
                        parseLineForDefaultSection(psiBuilder, "Files Section", IssMarkerFactory.FileSection.SECTION_DEFINITION,
                                IssFileProperty::getValueMarkerElementFromId, IssFileProperty::getItemMarkerElementFromId);
                        break;
                    case Component:
                        parseLineForDefaultSection(psiBuilder, "Component Section", IssMarkerFactory.ComponentSection.SECTION_DEFINITION,
                                IssComponentProperty::getValueMarkerElementFromId, IssComponentProperty::getItemMarkerElementFromId);
                        break;
                    case Type:
                        parseLineForDefaultSection(psiBuilder, "Type Section", IssMarkerFactory.TypeSection.SECTION_DEFINITION,
                                IssTypeProperty::getValueMarkerElementFromId, IssTypeProperty::getItemMarkerElementFromId);
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

    private static void parseLineForDefaultSection(PsiBuilder psiBuilder, String sectionName, IElementType definitionMarkerElement,
                                                   MarkerElementResolver resolver) {
        parseLineForDefaultSection(psiBuilder, sectionName, definitionMarkerElement, null, resolver);
    }

    private static void parseLineForDefaultSection(PsiBuilder psiBuilder, String sectionName, IElementType definitionMarkerElement,
                                                   MarkerElementResolver singleValueResolver, MarkerElementResolver definitionResolver) {
        final PsiBuilder.Marker definitionMark = psiBuilder.mark();
        {
            while (!psiBuilder.eof() && psiBuilder.getTokenType() != IssTokenFactory.CRLF) {
                final PsiBuilder.Marker itemMark = psiBuilder.mark();
                {
                    final String identifier = psiBuilder.getTokenText();
                    final IElementType markerElement = definitionResolver.resolveMarkerElement(identifier);
                    if (markerElement == null) {
                        itemMark.drop();

                        final PsiBuilder.Marker errorMark = psiBuilder.mark();
                        psiBuilder.advanceLexer();
                        errorMark.error("Unknown " + sectionName + " Item");

                        continue;
                    }

                    if (psiBuilder.getTokenType() != IssTokenFactory.NAME) {
                        itemMark.drop();
                        psiBuilder.advanceLexer();
                        psiBuilder.error("Name expected");
                        continue;
                    }

                    final PsiBuilder.Marker itemNameMark = psiBuilder.mark();
                    psiBuilder.advanceLexer();
                    itemNameMark.done(IssMarkerFactory.IDENTIFIER);

                    if (psiBuilder.getTokenType() != IssTokenFactory.OPERATOR_COLON) {
                        itemMark.drop();
                        psiBuilder.advanceLexer();
                        psiBuilder.error("':' expected");
                        continue;
                    }
                    psiBuilder.advanceLexer();

                    final PsiBuilder.Marker itemValueMark = psiBuilder.mark();
                    while (!psiBuilder.eof() && psiBuilder.getTokenType() != IssTokenFactory.CRLF && psiBuilder.getTokenType() != IssTokenFactory.OPERATOR_SEMICOLON) {
                        if (psiBuilder.getTokenType() != IssTokenFactory.WORD &&
                                psiBuilder.getTokenType() != IssTokenFactory.STRING &&
                                psiBuilder.getTokenType() != IssTokenFactory.NAME) {
                            final PsiBuilder.Marker errorMark = psiBuilder.mark();
                            psiBuilder.advanceLexer();
                            errorMark.error("Unknown element");
                            continue;
                        }

                        final IElementType singleValueMarkerElement = singleValueResolver == null ?
                                null : singleValueResolver.resolveMarkerElement(identifier);
                        if (singleValueMarkerElement != null) {
                            final PsiBuilder.Marker singleValueMark = psiBuilder.mark();
                            psiBuilder.advanceLexer();
                            singleValueMark.done(singleValueMarkerElement);
                        } else {
                            psiBuilder.advanceLexer();
                        }
                    }
                    itemValueMark.done(IssMarkerFactory.VALUE);

                    if ((!psiBuilder.eof() && (psiBuilder.getTokenType() == IssTokenFactory.CRLF || psiBuilder.getTokenType() == IssTokenFactory.OPERATOR_SEMICOLON)) || psiBuilder.eof()) {
                        if (psiBuilder.getTokenType() == IssTokenFactory.OPERATOR_SEMICOLON) {
                            psiBuilder.advanceLexer();
                        }
                        itemMark.done(markerElement);
                    } else {
                        itemValueMark.drop();
                        itemMark.drop();

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

    private static void parseLineForSetupSection(PsiBuilder psiBuilder) {
        final PsiBuilder.Marker itemMark = psiBuilder.mark();
        {
            final String identifier = psiBuilder.getTokenText();
            final IssSetupProperty setupItem = IssSetupProperty.fromId(identifier);
            if (setupItem == null) {
                itemMark.drop();

                final PsiBuilder.Marker errorMark = psiBuilder.mark();
                psiBuilder.advanceLexer();
                errorMark.error("Unknown Setup Section Item");

                return;
            }

            final PsiBuilder.Marker itemNameMark = psiBuilder.mark();
            psiBuilder.advanceLexer();
            itemNameMark.done(IssMarkerFactory.IDENTIFIER);

            if (psiBuilder.getTokenType() != IssTokenFactory.OPERATOR_EQUAL) {
                psiBuilder.advanceLexer();
                itemMark.drop();
                psiBuilder.error("'=' expected");
                return;
            }
            psiBuilder.advanceLexer();

            final PsiBuilder.Marker itemValueMark = psiBuilder.mark();
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
            itemValueMark.done(IssMarkerFactory.VALUE);

            if (!psiBuilder.eof() && psiBuilder.getTokenType() == IssTokenFactory.CRLF) {
                psiBuilder.advanceLexer();
            }

            itemMark.done(setupItem.getItemMarkerElement());
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
