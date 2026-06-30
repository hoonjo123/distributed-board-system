package joney.board.hotarticle.service;

import joney.board.common.event.Event;
import joney.board.common.event.EventPayload;
import joney.board.common.event.EventType;
import joney.board.hotarticle.client.ArticleClient;
import joney.board.hotarticle.repository.HotArticleListRepository;
import joney.board.hotarticle.service.eventhandler.EventHandler;
import joney.board.hotarticle.service.response.HotArticleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotArticleService {
    private final ArticleClient articleClient;
    private final List<EventHandler> eventHandlers;
    private final HotArticleScoreUpdater hotArticleScoreUpdater;
    private final HotArticleListRepository hotArticleListRepository;

    public void handleEvent(Event<EventPayload> event) {
        EventHandler<EventPayload> eventHandler = findEventHandler(event);
        if (eventHandler == null) {
            return;
        }

        if (isArticleCreatedOrDeleted(event)) {
            eventHandler.handle(event);
        } else {
            hotArticleScoreUpdater.update(event, eventHandler);
        }
    }

    private EventHandler<EventPayload> findEventHandler(Event<EventPayload> event) {
        return eventHandlers.stream()
                .filter(eventHandler -> eventHandler.supports(event))
                .findAny()
                .orElse(null);
    }

    private boolean isArticleCreatedOrDeleted(Event<EventPayload> event) {
        return EventType.ARTICLE_CREATED == event.getType() || EventType.ARTICLE_DELETED == event.getType();
    }

    // yyyyMMdd
    public List<HotArticleResponse> readAll(String dateStr) {
        return hotArticleListRepository.readAll(dateStr).stream()
                .map(articleClient::read) //원본 데이터 받아오기
                .filter(Objects::nonNull)
                .map(HotArticleResponse::from)
                .toList();
    }
}
