package com.iduenduen.coreservice.domain.gift.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iduenduen.coreservice.domain.gift.entity.GiftContract;
import com.iduenduen.coreservice.domain.gift.enums.ContractStatus;

public interface GiftContractRepository extends JpaRepository<GiftContract, Long> {

	List<GiftContract> findAllByChildIdAndCancelledAtIsNull(Long childId);

	List<GiftContract> findAllByChildIdAndStatusAndCancelledAtIsNull(Long childId, ContractStatus status);

	List<GiftContract> findAllByChildId(Long childId);
	Optional<GiftContract> findByIdAndChildIdAndCancelledAtIsNull(Long id, Long childId);
	Optional<GiftContract> findByIdAndParentId(Long id, Long parentId);
}