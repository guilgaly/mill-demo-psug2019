package cowsay

import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable
import scala.io.Source

sealed abstract class Cow(val name: String) extends EnumEntry {

  override def entryName: String = name

  val value: String = {
    val path = s"cowfiles/$name.cow"
    val is = getClass.getClassLoader.getResourceAsStream(path)
    val src = Source.fromInputStream(is, "UTF-8")
    val res = src.iter.mkString
    src.close()
    res
  }
}

object Cow extends Enum[Cow] {
  object Default extends Cow("default")
  object HelloKitty extends Cow("hellokitty")
  object Satanic extends Cow("satanic")

  override def values: immutable.IndexedSeq[Cow] = findValues
}
