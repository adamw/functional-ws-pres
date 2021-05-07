package com.softwaremill.demo

import cats.effect._
import cats.syntax.all._
import com.softwaremill.demo.S150_FibGame.{Guess, Response}
import com.typesafe.scalalogging.StrictLogging
import fs2.Pipe
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.syntax.kleisli._
import sttp.capabilities.WebSockets
import sttp.capabilities.fs2.Fs2Streams
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._

import scala.concurrent.ExecutionContext

object S140_FibGameServer extends StrictLogging {
  val difficultyInput: EndpointInput.Query[Int] = query[Int]("difficulty")
    .description("How many numbers to provide to win")
    .example(2)
    .validate(Validator.min(1, exclusive = true))

  val fibGameEndpoint: Endpoint[Int, String, Pipe[IO, Guess, Response], WebSockets with Fs2Streams[IO]] =
    endpoint
      .in("fib")
      .in(difficultyInput)
      .errorOut(stringBody)
      .out(webSocketBody[Guess, CodecFormat.Json, Response, CodecFormat.Json](Fs2Streams[IO]))

  val fibGameServerEndpoint = fibGameEndpoint.serverLogic(difficulty => S150_FibGame(difficulty).map(_.asRight[String]))

  //

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ec)
  implicit val timer: Timer[IO] = IO.timer(ec)

  val fibGameServerRoutes: HttpRoutes[IO] = Http4sServerInterpreter.toRoutes(fibGameServerEndpoint)

  def main(args: Array[String]): Unit = {
    BlazeServerBuilder[IO](ec)
      .bindHttp(8080, "localhost")
      .withHttpApp(Router("/" -> fibGameServerRoutes).orNotFound)
      .resource
      .use { _ => IO(logger.info("Server started")) >> IO.never }
      .unsafeRunSync()
  }
}
