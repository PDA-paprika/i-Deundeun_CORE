package com.iduenduen.coreservice.common.scheduler;

import com.iduenduen.coreservice.domain.parent.service.ParentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClusterTrainScheduler {
    private final ParentService parentService;

    @Scheduled(cron = "0 0 3 * * *")
    public void scheduleTrain() {
        log.info("[Scheduler] 클러스터 학습 스케줄 실행");
        parentService.train();
    }
}
