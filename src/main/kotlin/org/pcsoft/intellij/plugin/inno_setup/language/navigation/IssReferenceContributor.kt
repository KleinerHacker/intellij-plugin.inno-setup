package org.pcsoft.intellij.plugin.inno_setup.language.navigation

import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar

// Both reference types (ISPP constants and section cross-refs) are now wired via
// getReferences() overrides in IssConstantBodyMixinImpl and IssParamValueMixinImpl.
// In IntelliJ 2025.3, ASTWrapperPsiElement.getReferences() no longer calls
// PsiReferenceContributor providers, so the mixin approach is the only reliable path.
class IssReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {}
}
