package com.softwaremill.demo

import com.softwaremill.demo.S140_FibGameServer.fibGameEndpoint
import sttp.tapir.asyncapi.circe.yaml._
import sttp.tapir.docs.asyncapi.AsyncAPIInterpreter

object S170_FibDocs extends App {
  val apiDocs =
    AsyncAPIInterpreter
      .toAsyncAPI(
        fibGameEndpoint,
        "Fibonacci game",
        "1.0",
        List("dev" -> sttp.tapir.asyncapi.Server("localhost:8080", "ws"))
      )
      .toYaml
  println(s"Paste into https://playground.asyncapi.io/ to see the docs:\n\n$apiDocs")
}
