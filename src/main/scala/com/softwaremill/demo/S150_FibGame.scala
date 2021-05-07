package com.softwaremill.demo

import cats.effect.IO
import cats.syntax.all._
import com.typesafe.scalalogging.StrictLogging
import fs2.{Pipe, Stream}

import scala.util.Random

object S150_FibGame extends StrictLogging {
  // ws protocol: request
  case class Guess(number: Int)

  // ws protocol: response
  sealed trait Response
  case class Start(n1: Int, n2: Int) extends Response
  object Start {
    def apply(s: State): Start = Start(s.n1, s.n2)
  }
  case class Lost(expected: Int, got: Int) extends Response
  case class Won(difficulty: Int) extends Response

  // game state
  case class State(n1: Int, n2: Int, i: Int)

  def apply(difficulty: Int): IO[Pipe[IO, Guess, Response]] = for {
    initialState <- nextGame
  } yield { input: Stream[IO, Guess] =>
    Stream(Start(initialState)) ++
      input
        .evalMapAccumulate(initialState) { case (state, guess) =>
          val newState = State(state.n2, guess.number, state.i + 1)
          val response = if (guess.number == state.n1 + state.n2) {
            if (state.i == difficulty) Some(Won(difficulty)) else None
          } else Some(Lost(state.n1 + state.n2, guess.number))

          response match {
            case None    => (newState, List.empty[Response]).pure[IO]
            case Some(r) => nextGame.map(s => (s, List(r, Start(s))))
          }
        }
        .map(_._2)
        .flatMap(Stream.iterable)
  }

  private val nextGame: IO[State] = for {
    n2 <- IO(Random.nextInt(10) + 1)
    n1 <- IO(Random.nextInt(n2) + 1)
  } yield State(n1, n2, 1)
}
