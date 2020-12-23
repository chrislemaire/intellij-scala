package org.jetbrains.plugins.scala.lang.psi.impl.base

import com.intellij.openapi.util.TextRange
import org.jetbrains.plugins.scala.lang.psi.api.base.literals.ScStringLiteral
import org.jetbrains.plugins.scala.lang.psi.impl.base.literals.escapers.{ScalaStringParser, ScLiteralEscaperBase}

// todo: move to literals/escapers subpackage
class ScLiteralEscaper(val literal: ScStringLiteral) extends ScLiteralEscaperBase[ScStringLiteral](literal) {

  override def decode(rangeInsideHost: TextRange, outChars: java.lang.StringBuilder): Boolean = {
    TextRange.assertProperRange(rangeInsideHost)

    val chars = myHost.getText.substring(rangeInsideHost.getStartOffset, rangeInsideHost.getEndOffset)
    outSourceOffsets = new Array[Int](chars.length + 1)

    val parser = new ScalaStringParser(
      outSourceOffsets,
      isRaw = false
    )
    parser.parse(chars, outChars)
  }

  override def isOneLine: Boolean =
    myHost.getValue match {
      case str: String => str.indexOf('\n') < 0
      case _ => false
    }
}