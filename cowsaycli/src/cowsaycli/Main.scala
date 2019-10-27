package cowsaycli

import cowsay.{Cow, CowSay}

object Main {
  def main(args: Array[String]): Unit =
    println(CowSay.talk(Cow.withNameInsensitive(args(0)), args(1)))
}
