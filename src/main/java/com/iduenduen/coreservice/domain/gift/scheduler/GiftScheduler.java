package com.iduenduen.coreservice.domain.gift.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.iduenduen.coreservice.domain.account.entity.Account;
import com.iduenduen.coreservice.domain.account.repository.AccountRepository;
import com.iduenduen.coreservice.domain.gift.entity.GiftContract;
import com.iduenduen.coreservice.domain.gift.entity.GiftTransfer;
import com.iduenduen.coreservice.domain.gift.enums.TransferStatus;
import com.iduenduen.coreservice.domain.gift.repository.GiftContractRepository;
import com.iduenduen.coreservice.domain.gift.repository.GiftTransferRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GiftScheduler {

    private static final int MAX_RETRY = 2;

    private final GiftTransferRepository giftTransferRepository;
    private final GiftContractRepository giftContractRepository;
    private final AccountRepository accountRepository;

    // 1차 시도: 자정
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void processFirstAttempt() {
        List<GiftTransfer> transfers = giftTransferRepository
                .findAllByScheduledDateAndStatus(LocalDate.now(), TransferStatus.SCHEDULED);
        log.info("[GiftScheduler] 1차 시도 건수: {}", transfers.size());
        transfers.forEach(this::attempt);
    }

    // 2차 재시도: 09:00
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void processSecondAttempt() {
        List<GiftTransfer> transfers = giftTransferRepository
                .findAllByScheduledDateAndStatusAndRetryCountGreaterThan(
                        LocalDate.now(), TransferStatus.SCHEDULED, 0);
        log.info("[GiftScheduler] 2차 재시도 건수: {}", transfers.size());
        transfers.forEach(this::attempt);
    }

    // 3차 재시도: 14:00 (최종)
    @Scheduled(cron = "0 0 14 * * *")
    @Transactional
    public void processFinalAttempt() {
        List<GiftTransfer> transfers = giftTransferRepository
                .findAllByScheduledDateAndStatusAndRetryCountGreaterThan(
                        LocalDate.now(), TransferStatus.SCHEDULED, 1);
        log.info("[GiftScheduler] 3차(최종) 재시도 건수: {}", transfers.size());
        transfers.forEach(t -> attempt(t, true));
    }

    private void attempt(GiftTransfer transfer) {
        attempt(transfer, false);
    }

    private void attempt(GiftTransfer transfer, boolean isFinal) {
        GiftContract contract = giftContractRepository.findById(transfer.getGiftContractId()).orElse(null);
        if (contract == null) {
            log.warn("[GiftScheduler] 계약 없음 transferId={}", transfer.getId());
            return;
        }

        Account fromAccount = accountRepository.findByAccountId(contract.getFromAccountId()).orElse(null);
        if (fromAccount == null) {
            log.warn("[GiftScheduler] 계좌 없음 contractId={}", contract.getId());
            failAndCheckComplete(transfer, contract, "계좌 없음");
            return;
        }

        transfer.recordAvailableAssets(fromAccount.getAvailableAmt(), 0);

        if (fromAccount.getAvailableAmt() < contract.getCashAmount()) {
            if (isFinal || transfer.getRetryCount() >= MAX_RETRY) {
                log.warn("[GiftScheduler] 최종 실패 transferId={}", transfer.getId());
                failAndCheckComplete(transfer, contract, "잔액 부족");
            } else {
                transfer.incrementRetry();
                log.warn("[GiftScheduler] 잔액 부족 재시도 예정 transferId={} retryCount={}",
                        transfer.getId(), transfer.getRetryCount());
            }
            return;
        }

        fromAccount.deductCash(contract.getCashAmount());
        transfer.complete(contract.getCashAmount(), 0);
        log.info("[GiftScheduler] 이체 완료 transferId={}", transfer.getId());

        checkContractComplete(contract);
    }

    private void failAndCheckComplete(GiftTransfer transfer, GiftContract contract, String reason) {
        transfer.fail(reason);
        checkContractComplete(contract);
    }

    private void checkContractComplete(GiftContract contract) {
        boolean hasRemaining = giftTransferRepository.existsByGiftContractIdAndStatus(
                contract.getId(), TransferStatus.SCHEDULED);
        if (!hasRemaining) {
            contract.complete();
            log.info("[GiftScheduler] 계약 완료 contractId={}", contract.getId());
        }
    }
}