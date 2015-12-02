package org.pcsoft.plugins.intellij.inno_setup.script.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.lexer.IssTokenFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssComponentProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssDirectoryProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssFileProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssINIProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssIconProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssInstallRunProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssLanguageOptionProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssLanguageProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssRegistryProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssSetupProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssTaskProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssTypeProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssUninstallRunProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.section.IssSectionType;

import java.util.function.Function;

/**
 * Created by Christoph on 14.12.2014.
 */
final class IssParserSectionUtility {

    public static void parseSections(PsiBuilder psiBuilder) {
        while (!psiBuilder.eof()) {
            if (psiBuilder.getTokenType() == IssTokenFactory.SHARP) {
                IssParserCompilerDirectiveUtility.parseCompilerDirective(psiBuilder);
            } else if (psiBuilder.getTokenType() == IssTokenFactory.BRACE_BRACKET_START) {
                final PsiBuilder.Marker sectionMark = psiBuilder.mark();
                final PsiBuilder.Marker headerMark = psiBuilder.mark();
                psiBuilder.advanceLexer();

                final IssSectionType sectionType = IssSectionType.fromId(psiBuilder.getTokenText());
                if (sectionType == null) {
                    while (psiBuilder.getTokenType() != IssTokenFactory.CRLF && !psiBuilder.eof()) {
                        psiBuilder.advanceLexer();
                    }
                    headerMark.error("Unknown Section Type");
                } else {
                    if (psiBuilder.getTokenType() != IssTokenFactory.WORD && psiBuilder.getTokenType() != IssTokenFactory.NAME) {
                        while (psiBuilder.getTokenType() != IssTokenFactory.CRLF && !psiBuilder.eof()) {
                            psiBuilder.advanceLexer();
                        }
                        headerMark.error("Section name expected!");
                    } else {
                        final PsiBuilder.Marker sectionTitleMark = psiBuilder.mark();
                        psiBuilder.advanceLexer();
                        sectionTitleMark.done(IssMarkerFactory.SECTION_NAME);
                        if (psiBuilder.getTokenType() != IssTokenFactory.BRACE_BRACKET_END) {
                            psiBuilder.error("']' expected");
                        }
                        psiBuilder.advanceLexer();
                        headerMark.done(IssMarkerFactory.SECTION_HEADER);
                        if (psiBuilder.getTokenType() != IssTokenFactory.CRLF) {
                            psiBuilder.error("line end expected!");
                        } else {
                            psiBuilder.advanceLexer();
                        }
                    }
                }

                parseSection(psiBuilder, sectionType);
                sectionMark.done(sectionType == null ? IssMarkerFactory.SECTION_UNKNOWN : sectionType.getPropertyMarkerElement());
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

        while (!psiBuilder.eof() && psiBuilder.getTokenType() != IssTokenFactory.BRACE_BRACKET_START) {
            if (psiBuilder.getTokenType() == IssTokenFactory.SHARP) {
                IssParserCompilerDirectiveUtility.parseCompilerDirective(psiBuilder);
            } else if (psiBuilder.getTokenType() == IssTokenFactory.NAME || psiBuilder.getTokenType() == IssTokenFactory.WORD) {
                if (sectionType == null) {
                    //Parse stupid
                    while (psiBuilder.getTokenType() != IssTokenFactory.CRLF && !psiBuilder.eof()) {
                        psiBuilder.advanceLexer();
                    }
                } else {
                    switch (sectionType) {
                        case Setup:
                            parseLineForStandardSection(psiBuilder, "Setup Section", IssSetupProperty::getPropertyValueMarkerElementFromId,
                                    IssSetupProperty::getPropertyMarkerElementFromId);
                            break;
                        case CustomMessage:
                            parseLineForStandardSection(psiBuilder, "CustomMessage Section", s -> IssMarkerFactory.CustomMessageSection.PROPERTY_VALUE_VALUE,
                                    s -> IssMarkerFactory.CustomMessageSection.PROPERTY_VALUE);
                            break;
                        case Message:
                            parseLineForStandardSection(psiBuilder, "Message Section", s -> IssMarkerFactory.MessageSection.PROPERTY_VALUE_VALUE,
                                    s -> IssMarkerFactory.MessageSection.PROPERTY_VALUE);
                            break;
                        case LanguageOption:
                            parseLineForStandardSection(psiBuilder, "LanguageOptions Section", IssLanguageOptionProperty::getPropertyValueMarkerElementFromId,
                                    IssLanguageOptionProperty::getPropertyMarkerElementFromId);
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
                        case Language:
                            parseLineForDefinableSection(psiBuilder, "Language Section", IssMarkerFactory.LanguageSection.SECTION_DEFINITION,
                                    IssLanguageProperty::getPropertyValueMarkerElementFromId, IssLanguageProperty::getPropertyMarkerElementFromId);
                            break;
                        default:
                            throw new RuntimeException();
                    }
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

                    final PsiBuilder.Marker propertyNameMark = psiBuilder.mark();
                    final PsiBuilder.Marker propertyNameInnerMark = psiBuilder.mark();
                    psiBuilder.advanceLexer();
                    if (psiBuilder.getTokenType() == IssTokenFactory.OPERATOR_POINT) {
                        propertyNameInnerMark.done(IssMarkerFactory.IDENTIFIER_REFERENCE);
                        psiBuilder.advanceLexer();

                        final PsiBuilder.Marker propertyNameInnerNameMark = psiBuilder.mark();
                        if (psiBuilder.getTokenType() != IssTokenFactory.NAME && psiBuilder.getTokenType() != IssTokenFactory.WORD) {
                            psiBuilder.advanceLexer();
                            propertyNameInnerNameMark.error("Property Name expected!");
                        } else {
                            psiBuilder.advanceLexer();
                            propertyNameInnerNameMark.done(IssMarkerFactory.IDENTIFIER_NAME);
                        }
                    } else {
                        propertyNameInnerMark.done(IssMarkerFactory.IDENTIFIER_NAME);
                    }
                    propertyNameMark.done(IssMarkerFactory.IDENTIFIER);

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
                                psiBuilder.getTokenType() != IssTokenFactory.NAME &&
                                psiBuilder.getTokenType() != IssTokenFactory.NUMBER &&
                                psiBuilder.getTokenType() != IssTokenFactory.BRACE_CURLY_START &&
                                psiBuilder.getTokenType() != IssTokenFactory.QUOTE) {
                            final PsiBuilder.Marker errorMark = psiBuilder.mark();
                            psiBuilder.advanceLexer();
                            errorMark.error("Unknown element");
                            continue;
                        }

                        final IElementType singlePropertyValueMarkerElement = singlePropertyValueResolver == null ?
                                null : singlePropertyValueResolver.apply(identifier);
                        if (singlePropertyValueMarkerElement != null) {
                            parseSingleValue(psiBuilder, singlePropertyValueMarkerElement);
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

            final PsiBuilder.Marker propertyNameMark = psiBuilder.mark();
            final PsiBuilder.Marker propertyNameInnerMark = psiBuilder.mark();
            psiBuilder.advanceLexer();
            if (psiBuilder.getTokenType() == IssTokenFactory.OPERATOR_POINT) {
                propertyNameInnerMark.done(IssMarkerFactory.IDENTIFIER_REFERENCE);
                psiBuilder.advanceLexer();

                final PsiBuilder.Marker propertyNameInnerNameMark = psiBuilder.mark();
                if (psiBuilder.getTokenType() != IssTokenFactory.NAME && psiBuilder.getTokenType() != IssTokenFactory.WORD) {
                    psiBuilder.advanceLexer();
                    propertyNameInnerNameMark.error("Property Name expected!");
                } else {
                    psiBuilder.advanceLexer();
                    propertyNameInnerNameMark.done(IssMarkerFactory.IDENTIFIER_NAME);
                }
            } else {
                propertyNameInnerMark.done(IssMarkerFactory.IDENTIFIER_NAME);
            }
            propertyNameMark.done(IssMarkerFactory.IDENTIFIER);

            if (psiBuilder.getTokenType() != IssTokenFactory.OPERATOR_EQUAL) {
                psiBuilder.advanceLexer();
                propertyMark.drop();
                psiBuilder.error("'=' expected");
                return;
            }
            psiBuilder.advanceLexer();

            final PsiBuilder.Marker propertyValueMark = psiBuilder.mark();
            while (!psiBuilder.eof() && psiBuilder.getTokenType() != IssTokenFactory.CRLF) {
                if (psiBuilder.getTokenType() != IssTokenFactory.WORD &&
                        psiBuilder.getTokenType() != IssTokenFactory.NAME &&
                        psiBuilder.getTokenType() != IssTokenFactory.NUMBER &&
                        psiBuilder.getTokenType() != IssTokenFactory.BRACE_CURLY_START &&
                        psiBuilder.getTokenType() != IssTokenFactory.QUOTE) {
                    final PsiBuilder.Marker errorMark = psiBuilder.mark();
                    psiBuilder.advanceLexer();
                    errorMark.error("Unknown element");
                    continue;
                }

                final IElementType singlePropertyValueMarkerElement = singlePropertyValueResolver == null ?
                        null : singlePropertyValueResolver.apply(identifier);
                if (singlePropertyValueMarkerElement != null) {
                    parseSingleValue(psiBuilder, singlePropertyValueMarkerElement);
                }
            }
            propertyValueMark.done(IssMarkerFactory.VALUE);

            if (!psiBuilder.eof() && psiBuilder.getTokenType() == IssTokenFactory.CRLF) {
                psiBuilder.advanceLexer();
            }

            propertyMark.done(markerElement);
        }
    }

    private static void parseSingleValue(PsiBuilder psiBuilder, IElementType singlePropertyValueMarkerElement) {
        final PsiBuilder.Marker singleValueMark = psiBuilder.mark();
        if (psiBuilder.getTokenType() == IssTokenFactory.QUOTE) {
            IssParserValueUtility.parseSingleStringValue(psiBuilder);
        } else if (psiBuilder.getTokenType() == IssTokenFactory.BRACE_CURLY_START) {
            IssParserValueUtility.parseSingleConstantValue(psiBuilder);
        } else {
            psiBuilder.advanceLexer();
        }
        singleValueMark.done(singlePropertyValueMarkerElement);
    }

    private static void parseCodeSection(PsiBuilder psiBuilder) {
        while (!psiBuilder.eof() && psiBuilder.getTokenType() != IssTokenFactory.BRACE_BRACKET_START) {
            psiBuilder.advanceLexer();
            //TODO
        }
    }

    private IssParserSectionUtility() {
    }
}
