# Домашнее задание

## Future и Promise
* 1. Реализовать все методы в классе `ru.tinkoff.lecture6.future.assignment.assignment.Assignment`
* 2. Написать тесты в `ru.tinkoff.lecture6.future.assignment.AssignmentTest`
     * Для этого нужно удалить аннотацию @Ignore, которая заставляет scaltest игнорировать запуск наших тестов
     * Для реализации тестов, где нужно считать попытки, вам может пригодиться класс 
       `java.util.concurrent.atomic.AtomicInteger` в качестве безопасного счетчика для многопоточной программы.
     * `Await` и `Thread.sleep` не должны использоваться ни в тестах, ни в реализации
    