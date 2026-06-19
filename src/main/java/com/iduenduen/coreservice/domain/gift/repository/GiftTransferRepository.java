package com.iduenduen.coreservice.domain.gift.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iduenduen.coreservice.domain.gift.entity.GiftTransfer;
import com.iduenduen.coreservice.domain.gift.enums.TransferStatus;

public interface GiftTransferRepository extends JpaRepository<GiftTransfer, Long> {

	List<GiftTransfer> findAllByGiftContractIdOrderBySequenceNoAsc(Long giftContractId);

	List<GiftTransfer> findAllByGiftContractIdIn(List<Long> giftContractIds);

	List<GiftTransfer> findAllByScheduledDateAndStatus(LocalDate scheduledDate, TransferStatus status);

	boolean existsByGiftContractIdAndStatus(Long giftContractId, TransferStatus status);
}