import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CsvSubtracterSpec extends AnyFlatSpec with Matchers {

  "The CsvSubtracter" should "semi-group maps correctly" in {
    val mapA = Map(PartAndColour("a", "red") -> 2, PartAndColour("b", "green") -> 4)
    val mapB = Map(PartAndColour("a", "red") -> 3, PartAndColour("c", "yellow") -> 5)


    val outcome: Map[PartAndColour, Int] = CsvSubtracter.mergeMaps(Seq(mapA, mapB))

    outcome shouldBe Map(PartAndColour("a", "red") -> 5, PartAndColour("b", "green") -> 4, PartAndColour("c", "yellow") -> 5)
  }
}
