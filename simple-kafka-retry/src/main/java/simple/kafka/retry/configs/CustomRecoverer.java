package simple.kafka.retry.configs;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;

import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;

public class CustomRecoverer implements ConsumerRecordRecoverer {
    @Override
    public void accept(ConsumerRecord<?, ?> record, Exception e) {
        System.out.println("Record being accepted by the recoverer is "+convert((byte[])record.value()));
    }

    @Override
    public BiConsumer<ConsumerRecord<?, ?>, Exception> andThen(BiConsumer<? super ConsumerRecord<?, ?>, ? super Exception> after) {
        return ConsumerRecordRecoverer.super.andThen(after);
    }

    private String convert(byte[] payload) {
        return new String(payload, StandardCharsets.UTF_8);
    }
}
