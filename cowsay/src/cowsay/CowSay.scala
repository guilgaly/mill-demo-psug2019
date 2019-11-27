package cowsay

object CowSay {
  def talk(cow: Cow, message: String): String = {
    val preparedCow = cow.value.replace("$thoughts", """\""")
    val baloon = Baloon.format(message, 40)
    s"$baloon\n$preparedCow"
  }
}
