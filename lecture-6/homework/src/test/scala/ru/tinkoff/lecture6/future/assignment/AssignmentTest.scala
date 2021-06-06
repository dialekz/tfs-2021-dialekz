package ru.tinkoff.lecture6.future.assignment

import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import ru.tinkoff.lecture6.future.assignment.assignment._
import ru.tinkoff.lecture6.future.assignment.bcrypt.AsyncBcryptImpl

import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Future, TimeoutException}

class AssignmentTest extends AsyncFlatSpec with Matchers {

  val config: Config = ConfigFactory.load()
  val credentialStore = new ConfigCredentialStore(config)
  val reliableBcrypt = new AsyncBcryptImpl
  val assignment = new Assignment(reliableBcrypt, credentialStore)

  import assignment._

  behavior of "verifyCredentials"

  it should "return true for valid user-password pair" in
    verifyCredentials("winnie", "pooh").map {
      _ shouldBe true
    }

  it should "return false if user does not exist in store" in
    verifyCredentials("pupa", "lupa").map {
      _ shouldBe false
    }

  it should "return false for invalid password" in
    verifyCredentials("winnie", "pooh123").map {
      _ shouldBe false
    }

  behavior of "withCredentials"

  it should "execute code block if credentials are valid" in
    withCredentials("winnie", "pooh")(999).map {
      _ shouldBe 999
    }

  it should "not execute code block if credentials are not valid" in
    withCredentials("winnie", "pooh123")(999).failed.map {
      _ shouldBe an[InvalidCredentialsException]
    }

  behavior of "hashPasswordList"

  it should "return matching password-hash pairs" in
    hashPasswordList(Seq("pooh", "wooh")).map {
      seq => seq.map(_._1) shouldBe Seq("pooh", "wooh")
    }

  behavior of "findMatchingPassword"

  it should "return matching password from the list" in
    findMatchingPassword(Seq("pooh123", "pooh"), "$2a$10$UDowKDR9MQ.xvl/T9kf6/uZhogxz2o9hS5HYDkLO.A6kDoc7KbC1q").map {
      _ shouldBe Some("pooh")
    }

  it should "return None if no matching password is found" in
    findMatchingPassword(Seq("pooh123", "wooh"), "$2a$10$UDowKDR9MQ.xvl/T9kf6/uZhogxz2o9hS5HYDkLO.A6kDoc7KbC1q").map {
      _ shouldBe None
    }

  behavior of "withLogging"

  it should "complete with logging" in
    withLogging(Future({
      Thread.sleep(500); 5
    })).map {
      _ shouldBe 5
    }

  behavior of "withRetry"

  it should "return result on passed future's success" in
    withRetry(Future(999), 2).map {
      _ shouldBe 999
    }

  // для счетчиков можно использовать java.util.concurrent.atomic.AtomicInteger
  it should "not execute more than specified number of retries" in {
    val counter: AtomicInteger = new AtomicInteger(0)

    def block: Int = {
      val result = counter.incrementAndGet()
      if (result >= 15) result else throw new Exception("Not this time")
    }

    withRetry(Future[Int](block), 10).failed.map {
      _ => counter.get() shouldBe 10
    }
  }

  it should "not execute unnecessary retries" in {
    val counter: AtomicInteger = new AtomicInteger(0)

    def block: Int = {
      val result = counter.incrementAndGet()
      if (result >= 5) result else throw new Exception("Not this time")
    }

    withRetry(Future[Int](block), 10).map {
      _ => counter.get() shouldBe 5
    }
  }

  it should "return the first error, if all attempts fail" in
    withRetry(Future[Int](throw new Exception("test")), 2).failed.map {
      _.getMessage shouldBe "First: test"
    }

  behavior of "withTimeout"

  it should "return result on passed future success" in
    withTimeout(Future(123), 1.second).map {
      _ shouldBe 123
    }
  it should "return result on passed future failure" in
    withTimeout(Future(throw new Exception), 1.second).failed.map {
      _ shouldBe an[Exception]
    }
  it should "complete on never-completing future" in
    withTimeout(Future((1 to 1000).product), 1.microsecond).failed.map {
      _ shouldBe an[TimeoutException]
    }

  behavior of "hashPasswordListReliably"
  val assignmentFlaky = new Assignment(new FlakyBcryptWrapper(reliableBcrypt), credentialStore)

  it should "return password-hash pairs for successful hashing operations" in
    assignmentFlaky.hashPasswordListReliably(Seq("pooh", "wooh", "pupa"), 2, 2.seconds).map {
      seq => seq.map(_._1) should contain atLeastOneElementOf Seq("pupa")
    }
}
