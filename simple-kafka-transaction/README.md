Scenario: Consumer1 throws exception while processing
====================================
```
Producer -> Msg1 -> Consumer1
```

> Given that Producer is a TX producer
And Consumer1 is configured with read_committed
And that commitRecovered is set to true with a AfterRollBackProcessor(ARP) with FixedBackOff(0L, 0L)
When Consumer1 throws an exception upon processing Msg1
Then the message is not re-consumed at all. Even if the application restarts, the message will not be re-consumed.
Because the FixedBackOff is set to have 0 attempts, no retries are made
And the flag commitRecovered allows the ARP to commit the offset and transaction of the failed record to the commit and transaction log.
Hence no re-consumption is made even if the application restarts

> Given that the Producer is a TX producer
And Consumer1 is non transactional with a SeekToCurrentErrorHandler configured with FixedBackOff(0L, 0L)
When Consumer1 throws an exception upon processing Msg1
Then the message is not re-consumed at all. Even if the application restarts, the message will not be re-consumed.
Because the FixedBackOff is set to have 0 attempts, no retries are made
And Consumer1 can read all messages regardless if it's committed or not.

> Given that the Producer is a TX producer
And Consumer1 is non transactional with a SeekToCurrentErrorHandler configured with FixedBackOff(0L, 0L)
When Consumer1 throws an exception after persisting a db record while processing Msg1
Then the message is not re-consumed at all. Even if the application restarts, the message will not be re-consumed.
Because the FixedBackOff is set to have 0 attempts, no retries are made
And no rollback of the db record occurred because the method running the db operation is not annotated with `@transactional`.

> Given that the Producer is a TX producer
And Consumer1 is non transactional with a SeekToCurrentErrorHandler configured with FixedBackOff(0L, 0L)
And the method doing the db operation is annotated with `@Transactional` i.e.  
`
@Transactional
public void consume(Dto dto) {
    // DB Operation
    throw new RuntimeException("HA!");
}
`
When Consumer1 throws an exception after persisting a db record while processing Msg1
Then the message is not re-consumed at all. Even if the application restarts, the message will not be re-consumed.
Because the FixedBackOff is set to have 0 attempts, no retries are made
And no rollback of the db record occurred because the method running the db operation is not annotated with transaction.



Why Transactions?
We designed transactions in Kafka primarily for applications that exhibit a “read-process-write” pattern where the reads and writes are from and to asynchronous data streams such as Kafka topics. Such applications are more popularly known as stream processing applications.

The first generation of stream processing applications could tolerate inaccurate processing. For instance, applications that consumed a stream of web page impressions and produced aggregate counts of views per web page could tolerate some error in the counts.

However, the demand for stream processing applications with stronger semantics has grown along with the popularity of these applications. For instance, some financial institutions use stream processing applications to process debits and credits on user accounts. In these situations, there is no tolerance for errors in processing: we need every message to be processed exactly once, without exception.

More formally, if a stream processing application consumes message A and produces message B such that B = F(A), then exactly-once processing means that A is considered consumed if and only if B is successfully produced, and vice versa.

Using vanilla Kafka producers and consumers configured for at-least-once delivery semantics, a stream processing application could lose exactly-once processing semantics in the following ways:

The producer.send() could result in duplicate writes of message B due to internal retries. This is addressed by the idempotent producer and is not the focus of the rest of this post.
We may reprocess the input message A, resulting in duplicate B messages being written to the output, violating the exactly-once processing semantics. Reprocessing may happen if the stream processing application crashes after writing B but before marking A as consumed. Thus when it resumes, it will consume A again and write B again, causing a duplicate.
Finally, in distributed environments, applications will crash or—worse!—temporarily lose connectivity to the rest of the system. Typically, new instances are automatically started to replace the ones which were deemed lost. Through this process, we may have multiple instances processing the same input topics and writing to the same output topics, causing duplicate outputs and violating the exactly-once processing semantics. We call this the problem of “zombie instances.”
We designed transaction APIs in Kafka to solve the second and third problems. Transactions enable exactly-once processing in read-process-write cycles by making these cycles atomic and by facilitating zombie fencing.

Transactional Semantics
Atomic multi-partition writes
Transactions enable atomic writes to multiple Kafka topics and partitions. All of the messages included in the transaction will be successfully written or none of them will be. For example, an error during processing can cause a transaction to be aborted, in which case none of the messages from the transaction will be readable by consumers. We will now look at how this enables atomic read-process-write cycles.

First, let’s consider what an atomic read-process-write cycle means. In a nutshell, it means that if an application consumes a message A at offset X of some topic-partition tp0, and writes message B to topic-partition tp1 after doing some processing on message A such that B = F(A), then the read-process-write cycle is atomic only if messages A and B are considered successfully consumed and published together, or not at all.

Now, the message A will be considered consumed from topic-partition tp0 only when its offset X is marked as consumed. Marking an offset as consumed is called committing an offset. In Kafka, we record offset commits by writing to an internal Kafka topic called the offsets topic. A message is considered consumed only when its offset is committed to the offsets topic.

Thus since an offset commit is just another write to a Kafka topic, and since a message is considered consumed only when its offset is committed, atomic writes across multiple topics and partitions also enable atomic read-process-write cycles: the commit of the offset X to the offsets topic and the write of message B to tp1 will be part of a single transaction, and hence atomic.

Zombie fencing
We solve the problem of zombie instances by requiring that each transactional producer be assigned a unique identifier called the transactional.id. This is used to identify the same producer instance across process restarts.

The API requires that the first operation of a transactional producer should be to explicitly register its transactional.id with the Kafka cluster. When it does so, the Kafka broker checks for open transactions with the given transactional.id and completes them. It also increments an epoch associated with the transactional.id. The epoch is an internal piece of metadata stored for every transactional.id.

Once the epoch is bumped, any producers with same transactional.id and an older epoch are considered zombies and are fenced off, ie. future transactional writes from those producers are rejected.

Reading Transactional Messages
Now, let’s turn our attention to the guarantees provided when reading messages written as part of a transaction.

The Kafka consumer will only deliver transactional messages to the application if the transaction was actually committed. Put another way, the consumer will not deliver transactional messages which are part of an open transaction, and nor will it deliver messages which are part of an aborted transaction.

It is worth noting that the guarantees above fall short of atomic reads. In particular, when using a Kafka consumer to consume messages from a topic, an application will not know whether these messages were written as part of a transaction, and so they do not know when transactions start or end. Further, a given consumer is not guaranteed to be subscribed to all partitions which are part of a transaction, and it has no way to discover this, making it tough to guarantee that all the messages which were part of a single transaction will eventually be consumed by a single consumer.

In short: Kafka guarantees that a consumer will eventually deliver only non-transactional messages or committed transactional messages. It will withhold messages from open transactions and filter out messages from aborted transactions.