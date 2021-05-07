package com.softwaremill.demo

import sttp.client3.httpclient.zio.{HttpClientZioBackend, sendR}
import sttp.client3._
import sttp.ws.WebSocket
import zio.console.{Console, getStrLn, putStrLn}
import zio.{ExitCode, RIO, URIO}

object S160_FibClient extends zio.App {
  def play(ws: WebSocket[RIO[Console, *]]): RIO[Console, Unit] = {
    val receiveOne = ws.receiveText().flatMap(t => putStrLn(s"Received: $t"))

    val sendOne = for {
      _ <- putStrLn("Next guess:")
      g <- getStrLn
      _ <- ws.sendText(s"""{"number":$g}""")
    } yield ()

    receiveOne.forever.race(sendOne.forever)
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    val difficulty = 3
    val request = basicRequest
      .get(uri"ws://localhost:8080/fib?difficulty=$difficulty")
      .response(asWebSocket(play))

    sendR(request)
      .provideCustomLayer(HttpClientZioBackend.layer())
      .exitCode
  }
}
