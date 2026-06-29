package com.iduenduen.coreservice.domain.gift.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iduenduen.coreservice.domain.gift.entity.GiftContract;
import com.iduenduen.coreservice.domain.gift.enums.ContractStatus;
import com.iduenduen.coreservice.domain.gift.enums.GiftType;

public interface GiftContractRepository extends JpaRepository<GiftContract, Long> {

	List<GiftContract> findAllByChildIdAndCancelledAtIsNull(Long childId);

	List<GiftContract> findAllByChildIdAndStatusAndCancelledAtIsNull(Long childId, ContractStatus status);

	List<GiftContract> findAllByChildId(Long childId);
	List<GiftContract> findAllByParentId(Long parentId);
	Optional<GiftContract> findByIdAndChildIdAndCancelledAtIsNull(Long id, Long childId);
	Optional<GiftContract> findByIdAndParentId(Long id, Long parentId);

	@Query("SELECT c FROM GiftContract c WHERE c.giftType = :giftType " +
		"AND c.finalGiftAmount IS NULL AND c.valuationBaseDate IS NOT NULL " +
		"AND c.valuationBaseDate <= :threshold")
	List<GiftContract> findPendingEtfValuations(
		@Param("giftType") GiftType giftType,
		@Param("threshold") LocalDate threshold);
}