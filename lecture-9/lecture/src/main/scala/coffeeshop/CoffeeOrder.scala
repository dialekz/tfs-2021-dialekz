package coffeeshop

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class CoffeeOrder(coffeeType: String,
                       size: String,
                       name: String)

object CoffeeOrder {
  implicit val jsonDecoder: Decoder[CoffeeOrder] = deriveDecoder
  implicit val jsonEncoder: Encoder[CoffeeOrder] = deriveEncoder
}