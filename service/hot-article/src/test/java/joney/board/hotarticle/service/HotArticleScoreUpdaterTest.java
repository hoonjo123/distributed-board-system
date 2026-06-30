package joney.board.hotarticle.service;

import joney.board.common.event.Event;
import joney.board.hotarticle.repository.ArticleCreatedTimeRepository;
import joney.board.hotarticle.repository.HotArticleListRepository;
import joney.board.hotarticle.service.eventhandler.EventHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotArticleScoreUpdaterTest {
    @InjectMocks
    HotArticleScoreUpdater hotArticleScoreUpdater;

    @Mock
    HotArticleListRepository hotArticleListRepository;
    @Mock
    HotArticleScoreCalculator hotArticleScoreCalculator;
    @Mock
    ArticleCreatedTimeRepository articleCreatedTimeRepository;

    @Test
    void updateIfArticleIsNotCreatedTodayTest(){
        //given
        Long articleId = 1L;
        Event event = mock(Event.class); //클래스타입에 대해서 가짜 객체 생성
        EventHandler eventHandler = mock(EventHandler.class);

        given(eventHandler.findArticleId(event)).willReturn(articleId);

        LocalDateTime createTime = LocalDateTime.now().minusDays(1); // 게시글 생성시간 어제로 지정

        given(articleCreatedTimeRepository.read(articleId)).willReturn(createTime);

        //when
        hotArticleScoreUpdater.update(event, eventHandler);

        //then
        verify(eventHandler, never()).handle(event); // handle 메서드가 호출되지 않았는지 검증
        verify(hotArticleListRepository, never())
                .add(anyLong(), any(LocalDateTime.class), anyLong(), anyLong(), any(Duration.class));
    }

    @Test
    void updateTest(){
        //given
        Long articleId = 1L;
        Event event = mock(Event.class); //클래스타입에 대해서 가짜 객체 생성
        EventHandler eventHandler = mock(EventHandler.class);

        given(eventHandler.findArticleId(event)).willReturn(articleId);

        LocalDateTime createTime = LocalDateTime.now(); // 오늘 날짜

        given(articleCreatedTimeRepository.read(articleId)).willReturn(createTime);

        //when
        hotArticleScoreUpdater.update(event, eventHandler);

        //then
        verify(eventHandler).handle(event); // handle 메서드가 호출되지 않았는지 검증
        verify(hotArticleListRepository)
                .add(anyLong(), any(LocalDateTime.class), anyLong(), anyLong(), any(Duration.class));
    }
}