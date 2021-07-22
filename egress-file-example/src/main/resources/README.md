###How should the application recover given these scenarios?
What happens when the consumer attempts to commit an offset after exceeding the `max.poll.interval.ms` time?

What happens when the application throws an exception?

What happens when the application has written to file already but throws an exception?

What happens when the application has published a message downstream after exceeding the `max.poll.interval.ms` time?

