package com.iduenduen.coreservice.domain.gift.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iduenduen.coreservice.domain.gift.entity.GiftTransfer;
import com.iduenduen.coreservice.domain.gift.enums.TransferStatus;

public interface GiftTransferRepository extends JpaRepository<GiftTransfer, Long> {

	List<GiftTransfer> findAllByGiftContractIdOrderBySequenceNoAsc(Long giftContractId);

	List<GiftTransfer> findAllByGiftContractIdIn(List<Long> giftContractIds);

	List<GiftTransfer> findAllByScheduledDateAndStatus(LocalDate scheduledDate, TransferStatus status);

	List<GiftTransfer> findAllByScheduledDateAndStatusAndRetryCountGreaterThan(
		LocalDate scheduledDate, TransferStatus status, int retryCount);

	boolean existsByGiftContractIdAndStatus(Long giftContractId, TransferStatus status);

	@Query("SELECT COALESCE(SUM(t.transferredCashAmt), 0) FROM GiftTransfer t " +
		"JOIN GiftContract c ON t.giftContractId = c.id " +
		"WHERE c.childId = :childId AND t.status = :status AND t.scheduledDate >= :since")
	Long sumTransferredCashAmtByChildIdAndStatusAndScheduledDateAfter(
		@Param("childId") Long childId,
		@Param("status") TransferStatus status,
		@Param("since") LocalDate since);
}