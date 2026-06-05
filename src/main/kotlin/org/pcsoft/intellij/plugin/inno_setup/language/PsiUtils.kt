package org.pcsoft.intellij.plugin.inno_setup.language

import com.intellij.psi.PsiElement

// ── PsiElement (Host) ──────────────────────────────────────────────────────────

fun PsiElement.issFile(): IssFile? = containingFile as? IssFile
