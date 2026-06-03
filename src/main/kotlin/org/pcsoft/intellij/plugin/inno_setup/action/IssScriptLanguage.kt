package org.pcsoft.intellij.plugin.inno_setup.action

enum class IssScriptLanguage(val displayName: String, val issName: String, val messagesFile: String) {
    ENGLISH              ("English",              "english",              "compiler:Default.isl"),
    BRAZILIAN_PORTUGUESE ("Brazilian Portuguese", "brazilianportuguese",  "compiler:Languages\\BrazilianPortuguese.isl"),
    CATALAN              ("Catalan",              "catalan",              "compiler:Languages\\Catalan.isl"),
    CORSICAN             ("Corsican",             "corsican",             "compiler:Languages\\Corsican.isl"),
    CZECH                ("Czech",                "czech",                "compiler:Languages\\Czech.isl"),
    DANISH               ("Danish",               "danish",               "compiler:Languages\\Danish.isl"),
    DUTCH                ("Dutch",                "dutch",                "compiler:Languages\\Dutch.isl"),
    FINNISH              ("Finnish",              "finnish",              "compiler:Languages\\Finnish.isl"),
    FRENCH               ("French",               "french",               "compiler:Languages\\French.isl"),
    GERMAN               ("German",               "german",               "compiler:Languages\\German.isl"),
    GREEK                ("Greek",                "greek",                "compiler:Languages\\Greek.isl"),
    HEBREW               ("Hebrew",               "hebrew",               "compiler:Languages\\Hebrew.isl"),
    HUNGARIAN            ("Hungarian",            "hungarian",            "compiler:Languages\\Hungarian.isl"),
    ICELANDIC            ("Icelandic",            "icelandic",            "compiler:Languages\\Icelandic.isl"),
    ITALIAN              ("Italian",              "italian",              "compiler:Languages\\Italian.isl"),
    JAPANESE             ("Japanese",             "japanese",             "compiler:Languages\\Japanese.isl"),
    KOREAN               ("Korean",               "korean",               "compiler:Languages\\Korean.isl"),
    NORWEGIAN            ("Norwegian",            "norwegian",            "compiler:Languages\\Norwegian.isl"),
    POLISH               ("Polish",               "polish",               "compiler:Languages\\Polish.isl"),
    PORTUGUESE           ("Portuguese",           "portuguese",           "compiler:Languages\\Portuguese.isl"),
    RUSSIAN              ("Russian",              "russian",              "compiler:Languages\\Russian.isl"),
    SCOTTISH_GAELIC      ("Scottish Gaelic",      "scottishgaelic",       "compiler:Languages\\ScottishGaelic.isl"),
    SERBIAN              ("Serbian",              "serbian",              "compiler:Languages\\Serbian.isl"),
    SLOVENIAN            ("Slovenian",            "slovenian",            "compiler:Languages\\Slovenian.isl"),
    SPANISH              ("Spanish",              "spanish",              "compiler:Languages\\Spanish.isl"),
    TURKISH              ("Turkish",              "turkish",              "compiler:Languages\\Turkish.isl"),
    UKRAINIAN            ("Ukrainian",            "ukrainian",            "compiler:Languages\\Ukrainian.isl"),
    ;

    fun toIssEntry() = "Name: \"$issName\"; MessagesFile: \"$messagesFile\""
}
