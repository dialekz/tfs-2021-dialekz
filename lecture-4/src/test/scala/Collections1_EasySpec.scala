import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

/**
 * Задания легкого уровня - для ознакомления с API коллекций.
 * API документация: https://www.scala-lang.org/api/current/scala/collection/index.html
 * Описание коллекций https://docs.scala-lang.org/overviews/collections-2.13/introduction.html
 *
 * Все задания необходимо решать используя иммутабельные коллекции,
 * т.е. scala.collection._ и scala.collection.immutable._
 *
 * Многие задания можно выполнить более чем одним способом, экспериментируйте!
 *
 * Для запуска тестов только в этом файле: `sbt testOnly *.Collections1_Easy`
 */
class Collections1_EasySpec extends AnyFunSuite with Matchers {

  test("Создание коллекций") {
    val emptySeq: Seq[String] = Seq.empty

    emptySeq shouldBe a[Seq[_]]
    emptySeq shouldBe empty

    val nonEmptySeq: Seq[Int] = Seq(1, 2, 3, 42)

    nonEmptySeq shouldBe a[Seq[_]]
    nonEmptySeq should contain allOf(1, 2, 3, 42)

    val emptySet: Set[String] = Set.empty

    emptySet shouldBe a[Set[_]]
    emptySet shouldBe empty

    val nonEmptySet: Set[Int] = Set(1, 2, 3, 42)

    nonEmptySet shouldBe a[Set[_]]
    nonEmptySet should contain allOf(1, 2, 3, 42)

    val emptyMap: Map[String, String] = Map.empty

    emptyMap shouldBe a[Map[_, _]]
    emptyMap shouldBe empty

    val nonEmptyMap: Map[Int, Int] = Map(1 -> 2, 3 -> 42)

    nonEmptyMap shouldBe a[Map[_, _]]
    nonEmptyMap should contain allOf(1 -> 2, 3 -> 42)
  }

  test("Добавление и удаление элементов последовательности") {
    val seq = Seq(3, 4, 5)

    val seq1: Seq[Int] = 1 +: 2 +: seq

    seq1 shouldBe Seq(1, 2, 3, 4, 5)

    val seq2: Seq[Int] = seq1.filter(x => x != 1 && x != 2) :+ 6 :+ 7

    seq2 shouldBe Seq(3, 4, 5, 6, 7)
  }

  test("Объединение последовательностей") {
    val seq1 = Seq(1, 2, 3)
    val seq2 = Seq(4, 5, 6)

    val `seq1 concat seq2`: Seq[Int] = seq1 ++ seq2

    `seq1 concat seq2` shouldBe Seq(1, 2, 3, 4, 5, 6)
  }

  test("Объединение списков") {
    val list1 = List(1, 2, 3)
    val list2 = List(4, 5, 6)

    val `list1 concat list2`: List[Int] = list1 ::: list2

    `list1 concat list2` shouldBe List(1, 2, 3, 4, 5, 6)
  }

  // Путешествуя по двору за домом, Анатолий, тренер покемонов,
  // сразился с тренером покемонов Алексеем, и отжал всех покемонов последнего.
  // Нужно вычислить:
  // - сколько видов покемонов было у обоих тренеров, а сколько - только у одного из них?
  // - какие теперь уникальные покемоны у Анатолия?
  // - сколько у него лишних покемонов, если считать что повторяющиеся покемоны ему не нужны?
  // - какие из покемонов, которые водятся во дворе, теперь есть у Анатолия, а каких еще нужно поймать?
  test("Операции над множествами") {
    val `local pokemon types` = Set("caterpie", "rattata", "pidgey", "pikachu", "diglett")
    val anatolyPokemons = Seq("bulbasaur", "charmander", "caterpie", "pidgey", "weedle")
    val alexeyPokemons = Seq("squirtle", "rattata", "pidgey", "rattata", "weedle")

    val sharedCount: Int = (anatolyPokemons.toSet & alexeyPokemons.toSet).size

    val nonSharedCount: Int = (anatolyPokemons.toSet &~ alexeyPokemons.toSet) // TODO think about solution again
      .++(alexeyPokemons.toSet &~ anatolyPokemons.toSet)
      .size

    val totalUnique: Set[String] = anatolyPokemons.toSet ++ alexeyPokemons

    val excessCount: Int = (anatolyPokemons ++ alexeyPokemons).sorted
      .sliding(2)
      .count { case Seq(a, b) => a == b }

    val localObtained: Set[String] = anatolyPokemons.toSet
      .++(alexeyPokemons)
      .intersect(`local pokemon types`)

    val localMissing: Set[String] = `local pokemon types`.diff(anatolyPokemons.toSet ++ alexeyPokemons)

    sharedCount shouldBe 2
    nonSharedCount shouldBe 5
    totalUnique should contain theSameElementsAs Set("bulbasaur", "charmander", "caterpie", "pidgey", "squirtle", "rattata", "weedle")
    excessCount shouldBe 3
    localObtained should contain theSameElementsAs Set("caterpie", "pidgey", "rattata")
    localMissing should contain theSameElementsAs Set("pikachu", "diglett")
  }

  // Для переданной последовательности строк, вернуть общую последовательность букв в них, пропустив остальные символы
  test("Разбиение строк на символы") {
    def letterSequence(strings: Seq[String]): Seq[Char] = strings.flatMap(_.toList).filter(_.isLetter)

    letterSequence(Seq.empty) shouldBe empty
    letterSequence(Seq("foo", "bar")) shouldBe Seq('f', 'o', 'o', 'b', 'a', 'r')
    letterSequence(Seq("a->b", "42", "some word")) shouldBe Seq('a', 'b', 's', 'o', 'm', 'e', 'w', 'o', 'r', 'd')
  }

  test("Вывод имени") {
    def printFullName(lastName: Option[String], firstName: Option[String], middleName: Option[String]): String =
      (lastName ++ firstName ++ middleName).mkString(" ")

    printFullName(Some("Петров"), Some("Иван"), Some("Иванович")) shouldBe "Петров Иван Иванович"
    printFullName(Some("Бонд"), Some("Джеймс"), None) shouldBe "Бонд Джеймс"
    printFullName(None, Some("Тарзан"), None) shouldBe "Тарзан"
    printFullName(None, None, Some("Петрович")) shouldBe "Петрович"
  }

  // Реализовать удаление повторов самостоятельно, не используя стандартный метод distinct
  // Порядок элементов дожен быть сохранен; из повторяющихся элементов - оставлять первый
  test("Удаление повторов (distinct)") {
    def distinct(seq: Seq[Int]): Seq[Int] =
      seq.foldLeft((Set.empty[Int], Seq.empty[Int])) {
        case ((set, res), value) => if (set.contains(value)) (set, res) else (set + value, value +: res)
      }._2.reverse

    distinct(Seq(1, 2, 2, 2, 2, 5, 4, 5, 6, 1)) shouldBe Seq(1, 2, 5, 4, 6)
  }

  test("Число повторений") {
    val seq = Seq(1, 2, 2, 3, 4, 5, 6, 5, 6, 5, 9)

    val frequency: Map[Int, Int] = seq.groupBy(identity).map { case (k, v) => (k, v.size) }

    frequency should contain theSameElementsAs (Map(
      1 -> 1,
      2 -> 2,
      3 -> 1,
      4 -> 1,
      5 -> 3,
      6 -> 2,
      9 -> 1
    ))

  }

  test("Сортировка строк по возрастанию длины") {
    val strings = Seq("bear", "tortilla", "scala", "pie")

    val sorted: Seq[String] = strings.sortBy(_.length)

    sorted shouldBe Seq("pie", "bear", "scala", "tortilla")
  }
}
