# Модуль 1
## Начало работы, JVM, IDEA, SBT


## Домашнее задание:
### Наш первый SBT проект и дерево зависимостей
1. Создать пустой SBT проект
1. Добавить в качестве зависимостей две библиотеки [scala-course-tom](https://gitlab.com/tinkoff-fintech-scala-online-2021/registry/-/packages/1127777) и [scala-course-john](https://gitlab.com/tinkoff-fintech-scala-online-2021/registry/-/packages/1138566) <br/>
   (для того чтобы sbt увидел эти библиотеки, необходимо добавить в [resolvers](https://www.scala-sbt.org/1.x/docs/Resolvers.html) https://gitlab.com/api/v4/projects/24262845/packages/maven)
1. "Записаться на курс"
    * Создать точку входа в нашем проекте. Эта точка входа должна вызывать любой из методов *Tom.signUp()* или *John.signUp()* <br/>
      (*Tom* и *John* используют для реализации метода *signUp* библиотеку [scala-course-clerk](https://gitlab.com/tinkoff-fintech-scala-online-2021/registry/-/packages/1127737) различных версий)
    * Запомнить, что предложил персонал курса(информация выводимая в консоль во время запуска программы)
1. Изучить дерево зависимостей
    * Запустить *task* dependencyTree [Dependency graph](https://github.com/sbt/sbt-dependency-graph)
      и выяснить какой версией scala-course-clerk пользуются tom и john.
      Сам плагин добавлять в проект нет необходимости, потому что начиная с версии 1.4 он включен в состав SBT.
      Результат в виде ASCII дерева сложить в файл dependency-resolution.txt1. 
1. \* Заменяем версию библиотеки
    * Заставить SBT использовать предыдущую версию библиотеки scala-course-clerk
    * Что на этот раз предложил персонал? 
1. Готовимся к лекции 2
   * Самостоятельно изучить на [Stepik](https://stepik.org/lesson/105955/step/1?unit=80485) 
      * Переменные и их области видимости
      * Пространства имен
      * Типы


