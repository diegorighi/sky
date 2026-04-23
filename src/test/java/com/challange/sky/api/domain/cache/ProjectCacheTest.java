package com.challange.sky.api.domain.cache;

import com.challange.sky.api.domain.entities.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectCacheTest {

    @BeforeEach
    void setUp() {
        ProjectCache.INSTANCE.clear();
    }

    @Test
    void shouldReturnSameInstance() {
        ProjectCache cache1 = ProjectCache.INSTANCE;
        ProjectCache cache2 = ProjectCache.INSTANCE;

        assertThat(cache1).isSameAs(cache2);
    }

    @Test
    void putAndGet() {
        var project = new Project("P-1", "Test", "desc");

        ProjectCache.INSTANCE.put(project);

        assertThat(ProjectCache.INSTANCE.get("P-1")).isPresent();
        assertThat(ProjectCache.INSTANCE.get("P-1").get().getName()).isEqualTo("Test");
    }

    @Test
    void getMissReturnsEmpty() {
        assertThat(ProjectCache.INSTANCE.get("nonexistent")).isEmpty();
    }

    @Test
    void evictRemovesEntry() {
        ProjectCache.INSTANCE.put(new Project("P-1", "Test", null));

        ProjectCache.INSTANCE.evict("P-1");

        assertThat(ProjectCache.INSTANCE.get("P-1")).isEmpty();
    }

    @Test
    void clearRemovesAll() {
        ProjectCache.INSTANCE.put(new Project("P-1", "A", null));
        ProjectCache.INSTANCE.put(new Project("P-2", "B", null));

        ProjectCache.INSTANCE.clear();

        assertThat(ProjectCache.INSTANCE.size()).isZero();
    }

    @Test
    void shouldBeThreadSafe() throws InterruptedException {
        int threadCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final String id = "P-" + i;
            executor.submit(() -> {
                try {
                    ProjectCache.INSTANCE.put(new Project(id, "Project " + id, null));
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertThat(ProjectCache.INSTANCE.size()).isEqualTo(threadCount);
    }
}
