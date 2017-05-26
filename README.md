# Building Streaming Applications with Apache Kafka

This repository showcases how to build streaming applications using Apache Kafka and a variety of other tools and libraries that are a good fit for a Kafka-based application. 

This repository is structured into several smaller modules, each highlighting a different aspect of a Kafka-based application.

| Module                               | Purpose                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| ------------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `kafkasampler-elasticsearch-adapter` | Provides a small and simple adapter for Elasticsearch. The example application in `kafkasampler-tweet-processing` makes use of this adapter to push analyzed data records in to a dedicated index.                                                                                                                                                                                                                                                                       |
| `kafkasampler-eventsourced-gtd` | Provides a small showcase on how to build an event-sourced application with Kafka. |
| `kafkasampler-it`                      | Hosts integration tests.                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `kafkasampler-kafka-adapter`         | Provides a couple of useful abstractions that ease the integration of the client-side API of Apache Kafka.                                                                                                                                                                                                                                                                                                                                                               |
| `kafkasampler-kafka-clients`         | Provides client APIs for cluster management (e.g. topic management).                                                                                                                                                                                                                                                                                                                                                                                                     |
| `kafkasampler-streams-examples`      | Demonstrates the usage of Kafka Streams over the course of a couple of small applications.                                                                                                                                                                                                                                                                                                                                                                               |
| `kafkasampler-tweet-processing`      | A complete example application that conducts a sentiment analysis on Twitter tweets. Tweets are obtained in their raw from from Twitter and written to a dedicated Kafka topic. These raw data records are transformed and enriched across multiple filters before they are fed back into an Elasticsearch index for further visualization using Kibana. Apache Kafka is at the heart of this data pipeline, connecting filters as stream processors using Kafka Streams.|

## Prerequisites    

Running the showcase requires a working installation of Apache ZooKeeper and Apache Kafka. We provide `Dockerfile`s for both of them to get you started easily. Please make sure that [Docker](https://docs.docker.com/engine/installation/) as well as [Docker Compose](https://docs.docker.com/compose/install/) are installed on your system.

### Versions

| Application         | Version   | Docker Image            |
| ------------------- | --------- | ----------------------- |
| Apache Kafka        | 0.10.1.1  | kafka-sampler/kafka     |
| Apache ZooKeeper    | 3.4.8-1   | kafka-sampler/zookeeper |
| Elasticsearch       | 2.4.4     | elasticsearch:2         |
| Kibana              | 4.6.4     | kibana:4                |

### Building and Running the Containers

Before you execute the code samples, make sure that you have a working environment running. If you have not done it already, use the script ```docker/build-images``` to create Docker images for all required applications. After a couple of minutes, you should be ready to go.

Once the images have been successfully built, you can start the resp. containers using the provided ```docker-compose``` script. Simply issue

```bash
$ docker-compose up
```

for starting Apache Kafka, Apache Zookeeper and Yahoo Kafka Manager. Stopping the containers is best done using a separate terminal and issueing the following commands.

```bash
$ docker-compose stop
$ docker-compose rm
```

The final ```rm``` operation deletes the containers and thus clears all state so you can start over with a clean installation.

For simplicity, we restrict the Kafka cluster to a single Kafka broker. However, scaling to more Kafka brokers is easily done via `docker-compose`. You will have to provide a sensible value for `KAFKA_ADVERTISED_HOST_NAME` (other than `localhost`) for this to work, though. 

```bash
$ docker-compose scale kafka=3   # scales up to 3 Kafka brokers
$ docker-compose scale kafka=1   # scales down to 1 Kafka broker after the previous upscale
```

After changing the number of Kafka brokers, give the cluster some time so that all brokers can finish their cluster-join procedure. This should complete in a couple of seconds and you can inspect the output of the resp. Docker containers just to be sure that everything is fine. Kafka Manager should also reflect the change in the number of Kafka brokers after they successfully joined the cluster.

## Twitter Sentiment Analysis Example Application

The example application also requires a locally running Elasticsearch and a Kibana to visualize the data. Use the `docker-compose` script `with-search.yml` to fire up all systems required. Starting up Kafka, ZooKeeper, Elasticsearch and Kibana might take a couple of seconds. After that, run the `TweetProcessingApplication`.

Open up `localhost:5601` in your browser and set up the index `analyzed-tweets`. Use the `createdAt` index field as timestamp provider.

The example application provides a simple HTTP API to manage ingests.

### Creating Ingestion Feeds

Issue the following `CURL`-command from the command line.

```bash
$ curl -X POST http://localhost:8080/api/ingests?keywords=kafka -I
```

This yields

```
HTTP/1.1 201 
Location: http://localhost:8080/ingests/efa790e
Content-Length: 0
Date: Wed, 08 Mar 2017 18:59:57 GMT
```

which tells you that the new ingestion feed with ID `efa790e` has been created. Switching back to your application logs, you should see that Twitter4J is pulling data off of Twitter and that enriched tweets are fed into the local Elasticsearch.

### Showing Active Ingestion Feeds
 
Issue the following `CURL`-command from the command line.

```bash
$ curl http://localhost:8080/api/ingests -I
```

This yields

```
HTTP/1.1 200 
Content-Type: application/json
Content-Length: 57
Date: Wed, 08 Mar 2017 19:04:01 GMT

{"ingests":[{"ingestId":"efa790e","keywords":["kafka"]}]}
```

which tells you that there is currently a single ingestion feed active and it goes by the ID `efa790e`.

### Terminating Ingestion Feeds

Issue the following `CURL`-command from the command line.

```bash
curl -X DELETE http://localhost:8080/api/ingests/efa790e -I
```

This yields

```
HTTP/1.1 204 
Date: Wed, 08 Mar 2017 19:05:36 GMT
```

which tells you that the ingestion feed has been successfully terminated. Switching back to your application logs, you should see that Kafka Streams still processes the bulk of messages that has already been emitted to the resp. Kafka topic. Shortly after that, ingestion should stop as there is no new data available.

## Kafka Streams Demo

Module `kafkasampler-streams-examples` hosts a couple of small programs that demonstrate different uses of Kafka Streams high-level DSL. All programs require a working Kafka cluster consisting of at least one broker and one ZooKeeper instance. Use the default `docker-compose` script to fire up all required systems before executing any of the provided examples.

## Event-sourcing with Kafka

Module `kafkasampler-eventsourced-gtd` provides a minimalistic showcase on how to implement an event-sourced application using Apache Kafka. It integrates Apache Avro for event serialization. The showcase lacks a proper configuration of producers and consumers for the time being. This module will be updated as soon as Apache Kafka 0.11 is available, as it provides an idempotent and transactional producer that increases Kafka's suitability for event-sourced applications. De-duplication and idempotence are currently missing at the consumer-side as well. Please note that the code presented in this module should just get you started: Any production-grade event-sourced application *must* implement some kind of de-duplication (at producer- and consumer-level) or have idempotent consumers / idempotent state transitions to accommodate for the fact that duplicated events might be received.

## License
 
 This software is released under the terms of the GPL.
