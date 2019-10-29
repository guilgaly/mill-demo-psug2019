import java.nio.charset.StandardCharsets

import $ivy.`org.apache.commons:commons-text:1.6`
import org.apache.commons.text.{CaseUtils, StringEscapeUtils}

def generateDefaultCows(outputDir: os.Path, cowfiles: Seq[os.Path]): Unit = {

  def loadCowFromFile(cowName: String, cowfile: os.Path) = {
    val rawCowString =
      new String(ammonite.ops.read.bytes(cowfile), StandardCharsets.UTF_8)
    rawCowString.replace("\r\n", "\n").replace('\r', '\n')
  }

  def generateCowContents() = cowfiles.map { cowfile =>
    val cowName = cowfile.last.dropRight(4)
    val scalaName = CaseUtils.toCamelCase(cowName, true, '.', '-', '_')
    val rawCowValue = loadCowFromFile(cowName, cowfile)
    val cowValue = StringEscapeUtils.escapeJava(rawCowValue)

    val scalaObjectSource =
      s"""package cowsay.cows
         |
         |private[cowsay] object $scalaName {
         |
         |  def cowName: String = "$cowName"
         |
         |  def cowValue: String = "$cowValue"
         |}""".stripMargin

    val scalaFile = outputDir / 'cows / s"$scalaName.scala"
    ammonite.ops.write(scalaFile, scalaObjectSource, createFolders = true)

    scalaName
  }

  def orderNames(names: Seq[String]): Seq[String] = {
    val sortedNames = names.filterNot(_ == "Default").sorted
    "Default" +: sortedNames
  }

  def generateCowEnum(scalaNames: Seq[String]): Unit = {
    val cowObjects =
      scalaNames
        .map { scalaName =>
          s"  case object $scalaName extends Cow(cows.$scalaName.cowName, cows.$scalaName.cowValue)"
        }
        .mkString("\n")

    val scalaEnumSource =
      s"""package cowsay
         |
         |import enumeratum.{Enum, EnumEntry}
         |
         |import scala.collection.immutable
         |
         |sealed abstract class Cow(val name: String, val value: String) extends EnumEntry {
         |  override def entryName: String = name
         |}
         |
         |object Cow extends Enum[Cow] {
         |
         |$cowObjects
         |
         |  override def values: immutable.IndexedSeq[Cow] = findValues
         |}""".stripMargin

    val scalaFile = outputDir / "Cow.scala"
    ammonite.ops.write(scalaFile, scalaEnumSource, createFolders = true)
  }

  val scalaNames = generateCowContents()
  val orderedScalaNames = orderNames(scalaNames)
  generateCowEnum(orderedScalaNames)
}
