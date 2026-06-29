package com.iduenduen.coreservice.scheduler;

import com.iduenduen.coreservice.domain.gift.service.GiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EtfGiftValuationScheduler {

    private final GiftService giftService;

    @Scheduled(cron = "0 0 1 * * *")
    public void confirmPendingEtfValuations() {
        giftService.resolvePendingEtfValuations();
    }
}
