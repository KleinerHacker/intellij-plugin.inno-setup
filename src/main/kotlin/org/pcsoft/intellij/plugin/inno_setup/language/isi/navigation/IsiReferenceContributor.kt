package org.pcsoft.intellij.plugin.inno_setup.language.isi.navigation

import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar

// Both reference types (ISPP constants and section cross-refs) are now wired via
// getReferences() overrides in IsiConstantBodyMixinImpl and IsiParamValueMixinImpl.
// In IntelliJ 2025.3, ASTWrapperPsiElement.getReferences() no longer calls
// PsiReferenceContributor providers, so the mixin approach is the only reliable path.
class IsiReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {}
}
