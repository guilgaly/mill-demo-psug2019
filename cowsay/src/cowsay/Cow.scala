package cowsay

import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable

sealed abstract class Cow(val name: String) extends EnumEntry {

  override def entryName: String = name

  val value: String
}

object Cow extends Enum[Cow] {
  object Default extends Cow("default") {
    override val value: String =
      """        $thoughts   ^__^
        |         $thoughts  (oo)\_______
        |            (__)\       )\/\
        |                ||----w |
        |                ||     ||
        |""".stripMargin
  }

  object HelloKitty extends Cow("hellokitty") {
    override val value: String =
      """  $thoughts
        |   $thoughts
        |      /\_)o<
        |     |      \
        |     | O . O|
        |      \_____/
        |""".stripMargin
  }

  object Satanic extends Cow("satanic") {
    override val value: String =
      """     $thoughts
        |      $thoughts  (__)
        |         (\/)
        |  /-------\/
        | / | 666 ||
        |*  ||----||
        |   ~~    ~~
        |""".stripMargin
  }

  override def values: immutable.IndexedSeq[Cow] = findValues
}
