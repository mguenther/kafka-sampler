# Building a Streaming Architecture with Apache Kafka

This repository showcases how to build a streaming architecture using Apache Kafka and a variety of other tools and libraries that fit well with Kafka. 

The example application focuses around sentiment analysis and analyzes a tweet stream obtained from Twitter. Raw tweets have to be processed and enriched over multiple filters until they can be properly analyzed. Apache Kafka is at the heart of this data pipeline, connecting filters using Kafka Streams.

## Prerequisites

Running the showcase requires a working installation of Apache ZooKeeper and Apache Kafka. We provide `Dockerfile`s for both of them to get you started easily. Please make sure that [Docker](https://docs.docker.com/engine/installation/) as well as [Docker Compose](https://docs.docker.com/compose/install/) are installed on your system.

### Versions

| Application         | Version   | Docker Image            |
| ------------------- | --------- | ----------------------- |
| Apache Kafka        | 0.10.1.1  | kafka-sampler/kafka     |
| Apache Zookeeper    | 3.4.8-1   | kafka-sampler/zookeeper |

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

In the default configuration a single Kafka broker instance is started in a separate container. It is easy to scale this to your needs. Use the following commands to scale up / down.

```bash
$ docker-compose scale kafka=3   # scales up to 3 Kafka brokers
$ docker-compose scale kafka=1   # scales down to 1 Kafka broker after the previous upscale
```

After changing the number of Kafka brokers, give the cluster some time so that all brokers can finish their cluster-join procedure. This should complete in a couple of seconds and you can inspect the output of the resp. Docker containers just to be sure that everything is fine. Kafka Manager should also reflect the change in the number of Kafka brokers after they successfully joined the cluster.

## License
 
 This software is released under the terms of the GPL.
