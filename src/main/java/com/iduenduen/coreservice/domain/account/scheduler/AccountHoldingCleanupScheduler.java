package com.iduenduen.coreservice.domain.account.scheduler;

import com.iduenduen.coreservice.domain.account.repository.AccountEtfHoldingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountHoldingCleanupScheduler {

    private final AccountEtfHoldingRepository accountEtfHoldingRepository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteZeroQtyHoldings() {
        int deleted = accountEtfHoldingRepository.deleteByQtyLessThanEqualZero();
        if (deleted > 0) {
            log.warn("[HoldingCleanup] qty=0 보유 ETF {} 건 삭제", deleted);
        }
    }
}
