package com.twenty_nine

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.twenty_nine.routes.GameRoutes
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameServerSpec extends AnyWordSpec with Matchers with ScalatestRouteTest {
  lazy val testKit = ActorTestKit()
  implicit val typedSystem = testKit.system

  val routes = new GameRoutes().route

  "GameServer" should {
    "allow a player to join" in {
      Get("/join?playerId=player1") ~> routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] should include("player1 joined")
      }
    }

    "allow a player to make a move" in {
      Get("/move?playerId=player1&move=up") ~> routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] should include("Move 'up' accepted for player player1")
      }
    }
  }

  override def afterAll(): Unit = {
    testKit.shutdownTestKit()
  }
}