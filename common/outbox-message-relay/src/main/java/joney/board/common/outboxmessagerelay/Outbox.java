package joney.board.common.outboxmessagerelay;


import jakarta.persistence.*;
import joney.board.common.event.EventType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Table(name = "outbox")
@Getter
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Outbox {
    @Id
    private Long outboxId;
    @Enumerated(EnumType.STRING)
    private EventType eventType;
    private String payload; //event가 어떤 데이터를 가지고 있는가
    private Long shardKey; //동일한 샤드에서 처리되어야 단일 트랜젝션으로 처리가 됨
    private LocalDateTime createdAt;

    public static Outbox create(Long outboxId, EventType eventType, String payload, Long shardKey) {
        Outbox outbox = new Outbox();
        outbox.outboxId = outboxId;
        outbox.eventType = eventType;
        outbox.payload = payload;
        outbox.shardKey = shardKey;
        outbox.createdAt = LocalDateTime.now();
        return outbox;
    }
}
