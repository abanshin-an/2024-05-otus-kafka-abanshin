# Домашнее задание

## Разработка приложения Kafka Streams

## Цель:
Разработать приложение Kafka Streams.

## Описание/Пошаговая инструкция выполнения домашнего задания:
Разработка приложения Kafka Streams:

* Запустить Kafka
* Создать топик events
* Разработать приложение, которое подсчитывает количество событий с одинаковыми key в рамках сессии 5 минут
* Для проверки отправлять сообщения, используя console producer

# Решение

## Запустить Kafka
```shell 
make up
```

* Создать топик events
```shell
docker exec hw04-kafka1-1 kafka-topics --create --topic events --bootstrap-server localhost:9092
```
## Компиляция и запуск приложения
```shell
gradle buildd
java -cp ./libs/hw03-1.0.jar ru.otus.kafka.hw03.Shell producer
```

## Остановить кафка
```shell
make down 
```