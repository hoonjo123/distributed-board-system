```
docker run -d --name joney-board-kafka -p 9092:9092 apache/kafka:3.8.0

docker exec --workdir /opt/kafka/bin/ -it joney-board-kafka sh

/opt/kafka/bin $ ./kafka-topics.sh --bootstrap-server localhost:9092 --create --topic joney-board-article --replication-factor 1 --partitions 3
Created topic joney-board-article.

/opt/kafka/bin $ ./kafka-topics.sh --bootstrap-server localhost:9092 --create --topic joney-board-comment --replication-factor 1 --partitions 3
Created topic joney-board-comment.

/opt/kafka/bin $ ./kafka-topics.sh --bootstrap-server localhost:9092 --create --topic joney-board-like --replication-factor 1 --partitions 3
Created topic joney-board-like.

/opt/kafka/bin $ ./kafka-topics.sh --bootstrap-server localhost:9092 --create --topic joney-board-view --replication-factor 1 --partitions 3
Created topic joney-board-view.

```

