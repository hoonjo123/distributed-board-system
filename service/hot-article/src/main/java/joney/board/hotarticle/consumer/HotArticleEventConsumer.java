package joney.board.hotarticle.consumer;

import joney.board.common.event.Event;
import joney.board.common.event.EventPayload;
import joney.board.common.event.EventType;
import joney.board.hotarticle.service.HotArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class HotArticleEventConsumer {

    private final HotArticleService hotArticleService;

    @KafkaListener(topics = {
            EventType.Topic.JONEY_BOARD_ARTICLE,
            EventType.Topic.JONEY_BOARD_COMMENT,
            EventType.Topic.JONEY_BOARD_LIKE,
            EventType.Topic.JONEY_BOARD_VIEW
    })
    public void listen(String message, Acknowledgment ack) {
        log.info("[HotArticleEventConsumer.listen] received message={}", message);
        Event<EventPayload> event = Event.fromJson(message);
        if (event == null) {
            hotArticleService.handleEvent(event);
        }
        ack.acknowledge();
    }
}
