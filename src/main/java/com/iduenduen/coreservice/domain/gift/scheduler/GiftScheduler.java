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

    private final GiftTransferRepository giftTransferRepository;
    private final GiftContractRepository giftContractRepository;
    private final AccountRepository accountRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void processInstallmentTransfers() {
        LocalDate today = LocalDate.now();
        List<GiftTransfer> transfers = giftTransferRepository.findAllByScheduledDateAndStatus(
                today, TransferStatus.SCHEDULED);

        log.info("[GiftScheduler] 오늘({}) 처리할 이체 건수: {}", today, transfers.size());

        for (GiftTransfer transfer : transfers) {
            processTransfer(transfer);
        }
    }

    private void processTransfer(GiftTransfer transfer) {
        GiftContract contract = giftContractRepository.findById(transfer.getGiftContractId())
                .orElse(null);
        if (contract == null) {
            log.warn("[GiftScheduler] 계약 없음 transferId={}", transfer.getId());
            return;
        }

        Account fromAccount = accountRepository.findByAccountId(contract.getFromAccountId())
                .orElse(null);
        if (fromAccount == null) {
            log.warn("[GiftScheduler] 계좌 없음 contractId={}", contract.getId());
            transfer.fail("계좌 없음");
            return;
        }

        if (fromAccount.getAvailableAmt() < contract.getCashAmount()) {
            log.warn("[GiftScheduler] 잔액 부족 contractId={}", contract.getId());
            transfer.fail("잔액 부족");
            return;
        }

        fromAccount.deductCash(contract.getCashAmount());
        transfer.complete(contract.getCashAmount(), 0);
        log.info("[GiftScheduler] 이체 완료 transferId={}", transfer.getId());

        boolean hasRemaining = giftTransferRepository.existsByGiftContractIdAndStatus(
                contract.getId(), TransferStatus.SCHEDULED);
        if (!hasRemaining) {
            contract.complete();
            log.info("[GiftScheduler] 계약 완료 contractId={}", contract.getId());
        }
    }
}