package concurrency

import java.time.Instant

sealed trait Status
object Status {
  case class On(enabledAt: Instant) extends Status
  case class Off(enabledAt: Instant) extends Status
}

class Teapot {
  private var turnedOn: Boolean = true
  private var turnedOnAt: Option[Instant] = Some(Instant.now())
  private var turnedOffAt: Option[Instant] = None

  def turnOff(): Unit = this.synchronized {
    turnedOn = false
    turnedOnAt = None
    turnedOffAt = Some(Instant.now())
  }

  def turnOn(): Unit = this.synchronized {
    turnedOn = true
    turnedOnAt = Some(Instant.now())
    turnedOffAt = None
  }

  def getStatus: Status = this.synchronized {
    if (turnedOn) Status.On(turnedOnAt.getOrElse(throw new IllegalStateException()))
    else Status.Off(turnedOffAt.getOrElse(throw new IllegalStateException()))
  }
}
