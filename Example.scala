
import scala.math.BigDecimal.RoundingMode

object BigDecimalSerializer {
  // Custom implicit writes to serialize BigDecimal as string with scale and rounding
  implicit val bigDecimalWrites: Writes[BigDecimal] = Writes { bd =>
    JsString(bd.setScale(2, RoundingMode.HALF_UP).toString)
  }

  // Custom implicit reads to deserialize BigDecimal from string or number
  implicit val bigDecimalReads: Reads[BigDecimal] = Reads {
    case JsString(s) =>
      try {
        JsSuccess(BigDecimal(s))
      } catch {
        case _: NumberFormatException => JsError("Invalid BigDecimal string")
      }
    case JsNumber(num) => JsSuccess(num)
    case _ => JsError("Expected BigDecimal as string or number")
  }

  // Combine both into a Format
  implicit val bigDecimalFormat: Format[BigDecimal] = Format(bigDecimalReads, bigDecimalWrites)
}

// Sample case class using BigDecimal
case class Product(name: String, price: BigDecimal)

object Product {
  import BigDecimalSerializer._
  implicit val productFormat: Format[Product] = Json.format[Product]
}

// Test app
object BigDecimalSerializerApp extends App {
  import BigDecimalSerializer._

  val product = Product("Laptop", BigDecimal(