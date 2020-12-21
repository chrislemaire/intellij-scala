package org.jetbrains.plugins.scala.highlighter.lexer

import com.intellij.psi.tree.IElementType

// NOTE: I would better make it a mixing, but I can't due to this bug https://github.com/scala/bug/issues/3564
abstract class ScalaStringLiteralRawAwareLexer(
  quoteChar: Char,
  originalLiteralToken: IElementType
) extends ScalaStringLiteralLexer(quoteChar, originalLiteralToken) {

  /**
   * NOTE: since 2.13.0 octal escape sequences are dropped, and we don't handle them for < 2.13.0
   *
   * @todo we might try handling different versions
   */
  protected def getTokenTypeForRawString: IElementType = {
    if (myStart >= myEnd) return null

    val isEscapeStart = myBuffer.charAt(myStart) == '\\'
    if (!isEscapeStart)
      return myOriginalLiteralToken

    // handle unicode escape sequences: \u0025
    val nextChar = myBuffer.charAt(myStart + 1)
    if (nextChar == 'u')
      getUnicodeEscapeSequenceType
    else
      myOriginalLiteralToken
  }
}
