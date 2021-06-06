package coffeeshop.akkahttp

import akka.http.scaladsl.server.Route
import coffeeshop.{CoffeeOrder, CoffeeShopService}


class CoffeeShopEndpoints(coffeeShopService: CoffeeShopService) {

  import akka.http.scaladsl.server.Directives._
  import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._

  val getCoffeeRoute: Route = (get & path(JavaUUID)) { id =>
    complete(coffeeShopService.getCoffeeOrder(id))
  }

  val makeCoffeeRoute = (path("please") & post & entity(as[CoffeeOrder])) { order =>
    complete(coffeeShopService.makeCoffee(order))
  }

  val route: Route = pathPrefix("api" / "v1" / "coffee") {
    makeCoffeeRoute ~ getCoffeeRoute
  }
}


