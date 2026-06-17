package com.iduenduen.coreservice.domain.gift.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iduenduen.coreservice.domain.gift.entity.GiftTransfer;

public interface GiftTransferRepository extends JpaRepository<GiftTransfer, Long> {

	List<GiftTransfer> findAllByGiftContractIdOrderBySequenceNoAsc(Long giftContractId);

	List<GiftTransfer> findAllByGiftContractIdIn(List<Long> giftContractIds);
}