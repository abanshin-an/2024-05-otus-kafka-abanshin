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
gradle build
java -cp ./libs/hw04-1.0.jar ru.otus.kafka.hw04.Application &

./script/produce.sh
sleep 5
./script/produce.sh
sleep 5
./script/produce.sh
sleep 301
./script/produce.sh

```

## Остановить кафка
```shell
make down 
```