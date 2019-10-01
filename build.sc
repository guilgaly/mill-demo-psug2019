import mill._
import mill.scalalib._
import mill.scalalib.publish._
import mill.scalalib.scalafmt.ScalafmtModule
import mill.scalajslib._

import $file.settings

trait CowsayModule extends ScalaModule with ScalafmtModule {
  override def scalaVersion = settings.defaultScalaVersion
}

object cowsay extends CowsayModule {
  override def ivyDeps = Agg(ivy"com.beachape::enumeratum:1.5.13")

  object test extends Tests with ScalafmtModule {
    override def testFrameworks = Seq("org.scalatest.tools.Framework")
    override def ivyDeps = Agg(ivy"org.scalatest::scalatest:3.0.8")
  }
}

object cowsaycli extends CowsayModule {
  override def moduleDeps = Seq(cowsay)
}
