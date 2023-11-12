import java.io.File
import scopt.OParser

import scala.io.Source

case class Config(
                   in: File = new File("in.csv"),
                   subtract: Seq[File] = Seq.empty,
                   out: File = new File("out.csv"))

case class PartAndColour(part: String, colour: String)
object CsvSubtracter extends App {


  val builder = OParser.builder[Config]
  val parser1 = {
    import builder._
    OParser.sequence(
      programName("csv-subtracter"),
      head("scopt", "4.x"),
      opt[File]('s', "subtract")
        .unbounded()
        .required()
        .valueName("<file>")
        .action((x, c) => c.copy(subtract = c.subtract :+ x))
        .text("subtract filename"),
      opt[File]('o', "out")
        .optional()
        .valueName("<file>")
        .action((x, c) => c.copy(out = x))
        .text("output filename. Defaults to 'out.csv'"),
      help("help").text("prints this usage text"),
      arg[File]("<file>...")
        .required()
        .action((x, c) => c.copy(in = x))
        .text("input filename"),
      note("e.g. in.csv -s whatihave.csv -o whatineedtobuy.csv" + sys.props("line.separator")),
    )
  }

  // OParser.parse returns Option[Config]
  OParser.parse(parser1, args, Config()) match {
    case Some(config) =>
      val buildParts = parseCsv(config.in)
      val partsIHave =  mergeMaps(config.subtract.map(parseCsv))

      val whatINeed = buildParts.flatMap { case (partAndColour, quantity) =>
        val quantityIHave = partsIHave.getOrElse(partAndColour, 0)
        val newQuantity = quantity - quantityIHave
        if(newQuantity <= 0 ) {
          None
        } else {
          Some(partAndColour -> newQuantity)
        }
      }.toMap

      printToFile(config.out) { p =>
        whatINeed.foreach { case (partAndColour, quantity) => p.println(partAndColour.part + "," + partAndColour.colour + "," + quantity)}
      }

    case _ =>
    // arguments are bad, error message will have been displayed
  }

  def mergeMaps(maps: Seq[Map[PartAndColour, Int]]) = maps.flatMap(_.toList).groupBy(_._1).map {
    case (k, value) => k -> value.map(_._2).sum
  }

  private def parseCsv(csv: File) = {
    val s = Source.fromFile(csv)
    s.getLines().flatMap(_.toLowerCase.split(",") match {
      case Array("part", "color", "quantity", "is spare") => None
      case Array("part", "color", "quantity") => None
      case Array(part, colour, "0") => None
      case Array(part, colour, quantity, isSpare) => Some(PartAndColour(part, colour) -> quantity.toInt)
      case Array(part, colour, quantity) => Some(PartAndColour(part, colour) -> quantity.toInt)
      case unknown => throw new IllegalArgumentException("Unknown row in CSV:" + unknown)
    }).toMap
  }

  def printToFile(out: File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(out)
    try {
      p.println("Part,Color,Quantity")
      op(p)
    } finally {
      p.close()
    }
  }
}
