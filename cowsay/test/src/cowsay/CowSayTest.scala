package cowsay

import org.scalatest.{Matchers, WordSpec}

class CowSayTest extends WordSpec with Matchers {

  "CowSay" when {
    "given a message" should {
      "say it!" in {
        val expected =
          """ _____________________
            |< Hello ScalaIO 2019! >
            | ---------------------
            |        \   ^__^
            |         \  (oo)\_______
            |            (__)\       )\/\
            |                ||----w |
            |                ||     ||
            |""".stripMargin
        val actual = CowSay.talk(Cow.Default, "Hello ScalaIO 2019!")
        actual shouldBe expected
      }
    }
  }
}
