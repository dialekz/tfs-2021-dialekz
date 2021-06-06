# Hangman

1. Игровой сервер на akka-http 
Ранее в модуле 7 мы реализовывали игру "Виселица".
Сейчас предлагается реализовать ее online версию. Для это нужно реализовать:
* Игровую логику
  1. ru.tinkoff.homework9.hangman.storage.impl.ImMemoryGameStorage
  1. ru.tinkoff.homework9.hangman.logic.impl.PlayerGameServiceImpl
  1. ru.tinkoff.homework9.hangman.logic.impl.GameServiceImpl
* Json Api для игроков (`ru.tinkoff.homework9.hangman.api.GameApi`)
    
    В качестве спецификации api нужно опираться на `ru.tinkoff.homework9.hangman.api.base.GameApiSpecBase` 
    и интеграционный тест `ru.tinkoff.homework9.hangman.base.HangmanISpecBase`
  
* Json Api для администраторов (`ru.tinkoff.homework9.hangman.api.AdminApi`)

    В качестве спецификации api нужно опираться на `ru.tinkoff.homework9.hangman.api.base.AdminApiSpecBase` и
    интеграционный тест `ru.tinkoff.homework9.hangman.base.HangmanISpecBase`
  
Для реализации http-слоя **НЕОБХОДИМО** использовать akka-http.
Вместо `Future` в сервисном уровне можно использовать `monix Task`, `cats IO` или `ZIO` 

2. Игровой сервер на tapir*

*Это дополнительное задание, не обязательное для выполнения*

1. Нужно реализовать http-слой для игры hangman с использованием библиотеки tapir
2. Выставить на сервере эндпоинт со swagger-ui
3. Реализовать тесты и убрать с них аннотация `@Ignore`
  * `ru.tinkoff.homework9.hangman.api.TapirAdminApiSpec`
  * `ru.tinkoff.homework9.hangman.api.TapirGameApiSpec`
