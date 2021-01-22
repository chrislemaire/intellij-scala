package org.jetbrains.plugins.scala
package format

import com.intellij.psi.PsiElement

trait StringParser extends StringParserLike[PsiElement]

trait StringParserLike[E <: PsiElement] {
  def parse(element: E): Option[Seq[StringPart]]
}