# Домашнее задание
## Kafka Connect

## Цель:
Научиться разворачивать Kafka Connect и настраивать интеграцию с postgresSQL.

## Описание/Пошаговая инструкция выполнения домашнего задания:
Развернуть Kafka Connect, настроить интеграцию с postgreSQL используя Debezium PostgreSQL CDC Source Connector:

* Запустить Kafka
* Запустить PostgreSQL
* Создать в PostgreSQL тестовую таблицу
* Настроить Debezium PostgreSQL CDC Source Connector
* Запустить Kafka Connect
* Добавить записи в таблицу
* Проверить, что записи появились в Kafka

## Решение

### Запустить Kafka
### Запустить PostgreSQL
```bash
docker-compose up -d
```
```log
[+] Running 7/7
 ✔ Network hw06_default      Created                                                                                                                                                                                                                                                                     0.0s 
 ✔ Volume hw06_pgdata        Created                                                                                                                                                                                                                                                                     0.0s 
 ✔ Container postgres        Started                                                                                                                                                                                                                                                                     0.3s 
 ✔ Container zookeeper       Started                                                                                                                                                                                                                                                                     0.3s 
 ✔ Container kafka           Started                                                                                                                                                                                                                                                                     0.4s 
 ✔ Container hw06-kafdrop-1  Started                                                                                                                                                                                                                                                                     0.4s 
 ✔ Container connect         Started    
```

```bash
curl http://localhost:8083 | jq
curl http://localhost:8083/connector-plugins | jq
```
( часть строк пропущена )
```log
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100    91  100    91    0     0   1952      0 --:--:-- --:--:-- --:--:--  1978
{
  "version": "3.7.0",
  "commit": "2ae524ed625438c5",
  "kafka_cluster_id": "pm1L8CN0T-y62d2vwpY9KQ"
}
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100  1488  100  1488    0     0   138k      0 --:--:-- --:--:-- --:--:--  145k
[
....
  {
    "class": "io.debezium.connector.postgresql.PostgresConnector",
    "type": "source",
    "version": "2.7.1.Final"
  },
  {
    "class": "io.debezium.connector.spanner.SpannerConnector",
    "type": "source",
    "version": "2.7.1.Final"
  },
.....
]
```
### Создать в PostgreSQL тестовую таблицу
```bash
docker exec -i postgres psql -U postgres testdb <<!
CREATE TABLE persons (id INT PRIMARY KEY, name TEXT, age INT);
INSERT INTO persons (id, name, age) VALUES (1, 'John', 30);
\q
!
```
```log
CREATE TABLE
INSERT 0 1
```

### Запустить Kafka Connect
```
curl -X POST --data-binary "@postgres-cdc.json" -H "Content-Type: application/json" http://localhost:8083/connectors | jq
```
```log
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100  1047  100   503  100   544   3048   3297 --:--:-- --:--:-- --:--:--  6345
{
  "name": "postgres-cdc",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "database.hostname": "postgres",
    "database.port": "5432",
    "database.user": "postgres",
    "database.password": "admin",
    "database.dbname": "testdb",
    "topic.prefix": "pg",
    "plugin.name": "pgoutput",
    "slot.name": "debezium_slot",
    "publication.name": "dbz_publication",
    "slot.drop.on.stop": "false",
    "publication.autocreate.mode": "all_tables",
    "include.schema.changes": "true",
    "name": "postgres-cdc"
  },
  "tasks": [],
  "type": "source"
}
```

### Добавить записи в таблицу
```bash
docker exec -i postgres psql -U postgres testdb << ! 
INSERT INTO persons (id, name, age) VALUES (2, 'Paul', 25);
UPDATE persons set name='Tom' where id = 1;
select * from persons;
\q
!
```

```log
INSERT 0 1
UPDATE 1
 id | name | age 
----+------+-----
  2 | Paul |  25
  1 | Tom  |  30
(2 rows)
```


### Проверить, что записи появились в Kafka
```bash
docker exec kafka kafka-console-consumer --topic pg.public.persons --bootstrap-server kafka:9092 --property print.offset=true --property print.key=true --from-beginning
```
```log
Offset:0        {"schema":{"type":"struct","fields":[{"type":"int32","optional":false,"field":"id"}],"optional":false,"name":"pg.public.persons.Key"},"payload":{"id":1}}       {"schema":{"type":"struct","fields":[{"type":"struct","fields":[{"type":"int32","optional":false,"field":"id"},{"type":"string","optional":true,"field":"name"},{"type":"int32","optional":true,"field":"age"}],"optional":true,"name":"pg.public.persons.Value","field":"before"},{"type":"struct","fields":[{"type":"int32","optional":false,"field":"id"},{"type":"string","optional":true,"field":"name"},{"type":"int32","optional":true,"field":"age"}],"optional":true,"name":"pg.public.persons.Value","field":"after"},{"type":"struct","fields":[{"type":"string","optional":false,"field":"version"},{"type":"string","optional":false,"field":"connector"},{"type":"string","optional":false,"field":"name"},{"type":"int64","optional":false,"field":"ts_ms"},{"type":"string","optional":true,"name":"io.debezium.data.Enum","version":1,"parameters":{"allowed":"true,last,false,incremental"},"default":"false","field":"snapshot"},{"type":"string","optional":false,"field":"db"},{"type":"string","optional":true,"field":"sequence"},{"type":"int64","optional":true,"field":"ts_us"},{"type":"int64","optional":true,"field":"ts_ns"},{"type":"string","optional":false,"field":"schema"},{"type":"string","optional":false,"field":"table"},{"type":"int64","optional":true,"field":"txId"},{"type":"int64","optional":true,"field":"lsn"},{"type":"int64","optional":true,"field":"xmin"}],"optional":false,"name":"io.debezium.connector.postgresql.Source","field":"source"},{"type":"struct","fields":[{"type":"string","optional":false,"field":"id"},{"type":"int64","optional":false,"field":"total_order"},{"type":"int64","optional":false,"field":"data_collection_order"}],"optional":true,"name":"event.block","version":1,"field":"transaction"},{"type":"string","optional":false,"field":"op"},{"type":"int64","optional":true,"field":"ts_ms"},{"type":"int64","optional":true,"field":"ts_us"},{"type":"int64","optional":true,"field":"ts_ns"}],"optional":false,"name":"pg.public.persons.Envelope","version":2},"payload":{"before":null,"after":{"id":1,"name":"John","age":30},"source":{"version":"2.7.1.Final","connector":"postgresql","name":"pg","ts_ms":1764099880079,"snapshot":"last","db":"testdb","sequence":"[null,\"23043592\"]","ts_us":1764099880079860,"ts_ns":1764099880079860000,"schema":"public","table":"persons","txId":490,"lsn":23043592,"xmin":null},"transaction":null,"op":"r","ts_ms":1764099880139,"ts_us":1764099880139409,"ts_ns":1764099880139409296}}
Offset:1        {"schema":{"type":"struct","fields":[{"type":"int32","optional":false,"field":"id"}],"optional":false,"name":"pg.public.persons.Key"},"payload":{"id":2}}       {"schema":{"type":"struct","fields":[{"type":"struct","fields":[{"type":"int32","optional":false,"field":"id"},{"type":"string","optional":true,"field":"name"},{"type":"int32","optional":true,"field":"age"}],"optional":true,"name":"pg.public.persons.Value","field":"before"},{"type":"struct","fields":[{"type":"int32","optional":false,"field":"id"},{"type":"string","optional":true,"field":"name"},{"type":"int32","optional":true,"field":"age"}],"optional":true,"name":"pg.public.persons.Value","field":"after"},{"type":"struct","fields":[{"type":"string","optional":false,"field":"version"},{"type":"string","optional":false,"field":"connector"},{"type":"string","optional":false,"field":"name"},{"type":"int64","optional":false,"field":"ts_ms"},{"type":"string","optional":true,"name":"io.debezium.data.Enum","version":1,"parameters":{"allowed":"true,last,false,incremental"},"default":"false","field":"snapshot"},{"type":"string","optional":false,"field":"db"},{"type":"string","optional":true,"field":"sequence"},{"type":"int64","optional":true,"field":"ts_us"},{"type":"int64","optional":true,"field":"ts_ns"},{"type":"string","optional":false,"field":"schema"},{"type":"string","optional":false,"field":"table"},{"type":"int64","optional":true,"field":"txId"},{"type":"int64","optional":true,"field":"lsn"},{"type":"int64","optional":true,"field":"xmin"}],"optional":false,"name":"io.debezium.connector.postgresql.Source","field":"source"},{"type":"struct","fields":[{"type":"string","optional":false,"field":"id"},{"type":"int64","optional":false,"field":"total_order"},{"type":"int64","optional":false,"field":"data_collection_order"}],"optional":true,"name":"event.block","version":1,"field":"transaction"},{"type":"string","optional":false,"field":"op"},{"type":"int64","optional":true,"field":"ts_ms"},{"type":"int64","optional":true,"field":"ts_us"},{"type":"int64","optional":true,"field":"ts_ns"}],"optional":false,"name":"pg.public.persons.Envelope","version":2},"payload":{"before":null,"after":{"id":2,"name":"Paul","age":25},"source":{"version":"2.7.1.Final","connector":"postgresql","name":"pg","ts_ms":1764100127886,"snapshot":"false","db":"testdb","sequence":"[null,\"23043920\"]","ts_us":1764100127886692,"ts_ns":1764100127886692000,"schema":"public","table":"persons","txId":491,"lsn":23043920,"xmin":null},"transaction":null,"op":"c","ts_ms":1764100128174,"ts_us":1764100128174770,"ts_ns":1764100128174770216}}
Offset:2        {"schema":{"type":"struct","fields":[{"type":"int32","optional":false,"field":"id"}],"optional":false,"name":"pg.public.persons.Key"},"payload":{"id":1}}       {"schema":{"type":"struct","fields":[{"type":"struct","fields":[{"type":"int32","optional":false,"field":"id"},{"type":"string","optional":true,"field":"name"},{"type":"int32","optional":true,"field":"age"}],"optional":true,"name":"pg.public.persons.Value","field":"before"},{"type":"struct","fields":[{"type":"int32","optional":false,"field":"id"},{"type":"string","optional":true,"field":"name"},{"type":"int32","optional":true,"field":"age"}],"optional":true,"name":"pg.public.persons.Value","field":"after"},{"type":"struct","fields":[{"type":"string","optional":false,"field":"version"},{"type":"string","optional":false,"field":"connector"},{"type":"string","optional":false,"field":"name"},{"type":"int64","optional":false,"field":"ts_ms"},{"type":"string","optional":true,"name":"io.debezium.data.Enum","version":1,"parameters":{"allowed":"true,last,false,incremental"},"default":"false","field":"snapshot"},{"type":"string","optional":false,"field":"db"},{"type":"string","optional":true,"field":"sequence"},{"type":"int64","optional":true,"field":"ts_us"},{"type":"int64","optional":true,"field":"ts_ns"},{"type":"string","optional":false,"field":"schema"},{"type":"string","optional":false,"field":"table"},{"type":"int64","optional":true,"field":"txId"},{"type":"int64","optional":true,"field":"lsn"},{"type":"int64","optional":true,"field":"xmin"}],"optional":false,"name":"io.debezium.connector.postgresql.Source","field":"source"},{"type":"struct","fields":[{"type":"string","optional":false,"field":"id"},{"type":"int64","optional":false,"field":"total_order"},{"type":"int64","optional":false,"field":"data_collection_order"}],"optional":true,"name":"event.block","version":1,"field":"transaction"},{"type":"string","optional":false,"field":"op"},{"type":"int64","optional":true,"field":"ts_ms"},{"type":"int64","optional":true,"field":"ts_us"},{"type":"int64","optional":true,"field":"ts_ns"}],"optional":false,"name":"pg.public.persons.Envelope","version":2},"payload":{"before":null,"after":{"id":1,"name":"Tom","age":30},"source":{"version":"2.7.1.Final","connector":"postgresql","name":"pg","ts_ms":1764100127887,"snapshot":"false","db":"testdb","sequence":"[\"23044320\",\"23044320\"]","ts_us":1764100127887783,"ts_ns":1764100127887783000,"schema":"public","table":"persons","txId":492,"lsn":23044320,"xmin":null},"transaction":null,"op":"u","ts_ms":1764100128175,"ts_us":1764100128175288,"ts_ns":1764100128175288883}}
```
