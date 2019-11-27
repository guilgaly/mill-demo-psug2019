package cowsay

private[cowsay] object Baloon {

  final case class DelimiterCouple(start: Char, end: Char)

  private object Delimiters {
    val first: DelimiterCouple = DelimiterCouple('/', '\\')
    val middle: DelimiterCouple = DelimiterCouple('|', '|')
    val last: DelimiterCouple = DelimiterCouple('\\', '/')
    val only: DelimiterCouple = DelimiterCouple('<', '>')
  }

  def format(text: String, lineWidth: Int): String = {
    val lines = TextUtil.softWrap(text, lineWidth)
    val maxLength = lines.map(TextUtil.displayLength).max

    val top = topLine(maxLength)
    val middle = lines match {
      case Nil =>
        Nil
      case one +: Nil =>
        List(line(one, maxLength, Delimiters.only))
      case head +: mid :+ last =>
        line(head, maxLength, Delimiters.first) +:
          mid.map(s => line(s, maxLength, Delimiters.middle)) :+
          line(last, maxLength, Delimiters.last)
    }
    val bottom = bottomLine(maxLength)

    (top +: middle :+ bottom).mkString("\n")
  }

  private def line(textLine: String, textLength: Int, delims: DelimiterCouple) =
    s"${delims.start} ${TextUtil.padToDisplayLength(textLine, textLength)} ${delims.end}"

  private def topLine(length: Int) = s" ${"_" * (length + 2)}"

  private def bottomLine(length: Int) = s" ${"-" * (length + 2)}"
}
