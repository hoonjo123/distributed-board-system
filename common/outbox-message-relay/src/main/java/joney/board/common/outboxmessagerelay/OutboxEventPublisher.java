package joney.board.common.outboxmessagerelay;

import joney.board.common.event.Event;
import joney.board.common.event.EventPayload;
import joney.board.common.event.EventType;
import joney.board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final Snowflake outboxIdSnowflake = new Snowflake();
    private final Snowflake eventIdSnowflake = new Snowflake();
    private final ApplicationEventPublisher applicationEventPublisher;

    public void publish(EventType type, EventPayload payload, Long shardKey) {
        // 예를들어, article_id = 10, sharedkey == article_id
        // 10 % 4 == 2 로 물리적 샤드의 위치를 파악할 수 있다
        Outbox outbox = Outbox.create(
                outboxIdSnowflake.nextId(),
                type,
                Event.of(
                        eventIdSnowflake.nextId(), type, payload
                ).toJson(),
                shardKey % MessageRelayConstants.SHARD_COUNT
        );
        applicationEventPublisher.publishEvent(OutboxEvent.of(outbox));
    }
}
