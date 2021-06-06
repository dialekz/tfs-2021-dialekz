package coffeeshop.tapir

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import coffeeshop.{CoffeeOrder, CoffeeShopService}
import sttp.tapir.docs.openapi._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import sttp.tapir.swagger.akkahttp.SwaggerAkka

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class TapirEndpoint(coffeeShopService: CoffeeShopService)(implicit ec: ExecutionContext) {

  import sttp.tapir._

  private val coffeeEndpoint =
    endpoint
      .in("api" / "v1" / "coffee")

  val makeCoffee =
    coffeeEndpoint
      .in("please")
      .post
      .in(jsonBody[CoffeeOrder])
      .out(jsonBody[UUID])
      .serverLogic[Future] { order =>
        coffeeShopService.makeCoffee(order)
          .map(Right.apply)
      }

  val getCoffeeOrder =
    coffeeEndpoint
      .in(path[UUID]("id").description("Номер заказа"))
      .out(jsonBody[CoffeeOrder])
      .serverLogic[Future] { id =>
        coffeeShopService.getCoffeeOrder(id)
          .map(Right.apply)
      }

  val all = List(getCoffeeOrder, makeCoffee)

}

object TapirMain {
  import akka.http.scaladsl.server.RouteConcatenation._

  def main(args: Array[String]): Unit = {
    implicit val as = ActorSystem()
    import as.dispatcher

    val endpoints = new TapirEndpoint(new CoffeeShopService)
    val routes = AkkaHttpServerInterpreter.toRoute(endpoints.all)


    val openapi = OpenAPIDocsInterpreter.serverEndpointsToOpenAPI(endpoints.all, "coffee shop", "0.0.1")
    val swagger = new SwaggerAkka(openapi.toYaml).routes

    Http().newServerAt("localhost", 8080)
      .bind(routes ~ swagger)
      .foreach(b => println(s"server started at ${b.localAddress}"))
  }
}