package ru.tinkoff.homework9.hangman.logic

import ru.tinkoff.homework9.hangman.storage.Dictionary.Dictionary

import scala.util.Random

class DictionaryService {
  def chooseWord(): String = Dictionary(Random.nextInt(Dictionary.length))

  def listAll(): Seq[String] = Dictionary
}
