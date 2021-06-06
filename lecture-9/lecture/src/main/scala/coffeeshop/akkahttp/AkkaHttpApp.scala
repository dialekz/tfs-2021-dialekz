package coffeeshop.akkahttp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import coffeeshop.CoffeeShopService

object AkkaHttpApp {
  def main(args: Array[String]): Unit = {
    implicit val as: ActorSystem = ActorSystem()
    import as.dispatcher
    val endpoints = new CoffeeShopEndpoints(new CoffeeShopService)

    Http().newServerAt("localhost", 8080)
      .bind(endpoints.route)
      .foreach(binding => println(s"server at: $binding"))
  }
}
