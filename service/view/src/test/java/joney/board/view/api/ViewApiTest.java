package joney.board.view.api;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewApiTest {
    RestClient restClient = RestClient.create("http://localhost:9003");

    @Test
    void viewTest() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(10000);

        for(int i=0; i<10000; i++) {
            executorService.submit(() -> {
                restClient.post()
                        .uri("/v1/article-views/articles/{articleId}/users/{userId}", 4L, 1L)
                        .retrieve();
                latch.countDown();
            });
        }

        latch.await();

        Long count = restClient.get()
                .uri("/v1/article-views/articles/{articleId}/count", 4L)
                .retrieve()
                .body(Long.class);

        System.out.println("count = " + count);
        // 인메모리 db 사용으로 만건의 결과 대략 똥컴스펙으로 3초, mysql에 백업도 잘 됨 ( 100개 단위로 )
        // 어뷰징 방지 로직 적용 후 -> 1개만 집계되는걸 확인
    }
}
