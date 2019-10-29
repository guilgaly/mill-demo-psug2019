import mill._
import mill.scalalib._
import mill.scalalib.publish._
import mill.scalalib.scalafmt.ScalafmtModule
import mill.scalajslib._

import $file.settings
import $file.cowgen

trait CommonModule extends ScalaModule with ScalafmtModule {
  def platformSegment: String // jvm or js

  override def sources = T.sources (
    millSourcePath / "src",
    millSourcePath / s"src-$platformSegment"
  )
}

object cowsay extends Module {

  object jvm extends Cross[CowsayJvmModule](settings.scalaV2_12, settings.scalaV2_13)
  class CowsayJvmModule(val crossScalaVersion: String) extends CowsayModule {
    override def platformSegment = "jvm"

    object test extends CowsayTestModule
  }

  object js extends Cross[CowsayJsModule](settings.scalaV2_12, settings.scalaV2_13)
  class CowsayJsModule(val crossScalaVersion: String) extends CowsayModule with ScalaJSModule {
    override def platformSegment = "js"
    override def scalaJSVersion = "0.6.29"

    object test extends CowsayTestModule with TestScalaJSModule {
      override def scalaJSVersion = "0.6.29"
    }
  }

  trait CowsayModule extends CrossScalaModule with CommonModule with PublishModule { outer =>
    override def ivyDeps = Agg(ivy"com.beachape::enumeratum::1.5.13")
    override def millSourcePath = cowsay.millSourcePath

    override def pomSettings = PomSettings(
      description = "Cowsay library (mill demo)",
      organization = "fr.ggaly",
      url = "https://github.com/guilgaly/mill-demo-scalaio2019",
      licenses = Seq(License.MIT),
      versionControl = VersionControl.github("guilgaly", "mill-demo-scalaio2019"),
      developers = Seq(Developer("guilgaly", "Guillaume Galy", "https://github.com/guilgaly"))
    )
    override def publishVersion = "0.0.1-SNAPSHOT"
    override def artifactName = "cowsay-mill-demo"

    def cowfiles = T.sources(millSourcePath / "cowfiles")

    def allCowFiles = T {
      def isHiddenFile(path: os.Path) = path.last.startsWith(".")
      for {
        root <- cowfiles()
        path <- os.walk(root.path)
        if os.isFile(path) && path.last.endsWith(".cow") && !isHiddenFile(path)
      } yield PathRef(path)
    }

    override def generatedSources = T {
      val files = allCowFiles().map(_.path)
      val outputDir = T.ctx().dest
      cowgen.generateDefaultCows(outputDir, files)
      Seq(PathRef(outputDir))
    }

    trait CowsayTestModule extends Tests with CommonModule {
      override def platformSegment = outer.platformSegment
      override def testFrameworks = Seq("org.scalatest.tools.Framework")
      override def ivyDeps = Agg(ivy"org.scalatest::scalatest::3.0.8")
    }
  }
}

object cowsaycli extends ScalaModule with ScalafmtModule {
  override def scalaVersion = settings.defaultScalaVersion
  override def moduleDeps = Seq(cowsay.jvm(settings.defaultScalaVersion))
}
