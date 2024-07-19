package com.twenty_nine

import akka.actor.testkit.typed.scaladsl.{ActorTestKit, TestProbe}
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.model.{HttpMethod, HttpMethods, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.Timeout
import com.twenty_nine.actor.{GameManagerActor, GameSessionActor}
import com.twenty_nine.requests.CreateGameRequest
import com.twenty_nine.routes.GameRoutes
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.{MatchResult, Matcher}
import org.scalatest.wordspec.AnyWordSpec
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.concurrent.duration._

class GameServerSpec extends AnyWordSpec with Matchers with ScalatestRouteTest with SprayJsonSupport with DefaultJsonProtocol {
  lazy val testKit: ActorTestKit = ActorTestKit()
  implicit val typedSystem: ActorSystem[Nothing] = testKit.system
  implicit val timeout: Timeout = 3.seconds

  implicit val createGameRequestFormat: RootJsonFormat[CreateGameRequest] = jsonFormat3(CreateGameRequest)

  val gameManagerProbe: TestProbe[GameManagerActor.ManagerCommand] = testKit.createTestProbe[GameManagerActor.ManagerCommand]()
  val gameSessionProbe: TestProbe[GameSessionActor.Command] = testKit.createTestProbe[GameSessionActor.Command]()

  val routes: Route = new GameRoutes(gameManagerProbe.ref).route

  val expectedMethods: Seq[HttpMethod] = Seq(GET, POST, PUT, DELETE, HEAD, OPTIONS).sortBy(_.value)
  // tests:

  // preflight
  Options() ~> Origin(HttpOrigin("http://localhost:8080")) ~> `Access-Control-Request-Method`(HttpMethods.GET) ~> routes ~> check {
    status shouldBe StatusCodes.OK
    response.headers should contain atLeastOneElementOf Seq(
      `Access-Control-Allow-Origin`(HttpOrigin("http://localhost:8080")),
//      `Access-Control-Allow-Methods`(expectedMethods),
      `Access-Control-Max-Age`(1800),
      `Access-Control-Allow-Credentials`(allow = true)
    )
//    response.headers should contain(containSameMethodsAs(Seq(GET, POST, PUT, DELETE, HEAD, OPTIONS)))
  }

  // regular request
//  Get("/game/game-info/default") ~> Origin(HttpOrigin("http://localhost:8080")) ~> routes ~> check {
//    status shouldEqual OK
//    response.headers should contain theSameElementsAs Seq(
//      `Access-Control-Allow-Origin`(HttpOrigin("http://localhost:8080")),
//      `Access-Control-Allow-Credentials`(allow = true)
//    )
//  }

  override def afterAll(): Unit = {
    testKit.shutdownTestKit()
  }

  def containSameMethodsAs(expected: Seq[HttpMethod]): Matcher[`Access-Control-Allow-Methods`] =
    (left: `Access-Control-Allow-Methods`) => {
      val leftMethods = left.methods.toSet
      val rightMethods = expected.toSet
      MatchResult(
        leftMethods == rightMethods,
        s"$left did not contain the same methods as ${expected.mkString(", ")}",
        s"$left contained the same methods as ${expected.mkString(", ")}"
      )
    }
}