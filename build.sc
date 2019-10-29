import mill._
import mill.scalalib._
import mill.scalalib.publish._
import mill.scalalib.scalafmt.ScalafmtModule
import mill.scalajslib._

import $file.settings

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


  trait CowsayModule extends CrossScalaModule with CommonModule { outer =>
    override def ivyDeps = Agg(ivy"com.beachape::enumeratum::1.5.13")
    override def millSourcePath = cowsay.millSourcePath

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
