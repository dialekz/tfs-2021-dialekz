package concurrency

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funspec.AnyFunSpec

import java.util.concurrent.Executors
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

class CoronaVisitorGuardTest extends AnyFunSpec with ScalaFutures {
  implicit val ec: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(Executors.newWorkStealingPool())


  describe("Visitors guard") {
    it("should limit visitors count") {
      val maxVisitors = 10
      val guard = new CoronaVisitorGuard(maxVisitors)


      def enterAndExit: Future[Unit] =
        for {
          enter <- Future(guard.tryEnter())
          _ <- if (enter) Future(guard.leave()) else Future.unit
        } yield ()

      Future.traverse((1 to 1000).toList)(_ => enterAndExit).futureValue

      assert(guard.getCurrentVisitorsCount() == 0)
    }
  }

  override implicit def patienceConfig: PatienceConfig = PatienceConfig(2.hours)
}
