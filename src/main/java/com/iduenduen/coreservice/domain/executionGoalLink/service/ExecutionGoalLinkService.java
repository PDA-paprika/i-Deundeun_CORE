package com.iduenduen.coreservice.domain.executionGoalLink.service;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.status.ErrorStatus;
import com.iduenduen.coreservice.domain.account.entity.AccountEtfHistory;
import com.iduenduen.coreservice.domain.account.repository.AccountEtfHistoryRepository;
import com.iduenduen.coreservice.domain.children.repository.ChildrenRepository;
import com.iduenduen.coreservice.domain.executionGoalLink.dto.GoalExecutionResponse;
import com.iduenduen.coreservice.domain.executionGoalLink.dto.LinkRequest;
import com.iduenduen.coreservice.domain.executionGoalLink.dto.UnlinkedExecutionResponse;
import com.iduenduen.coreservice.domain.executionGoalLink.entity.ExecutionGoalLink;
import com.iduenduen.coreservice.domain.executionGoalLink.repository.ExecutionGoalLinkRepository;
import com.iduenduen.coreservice.domain.goals.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExecutionGoalLinkService {

    private final ExecutionGoalLinkRepository executionGoalLinkRepository;
    private final AccountEtfHistoryRepository accountEtfHistoryRepository;
    private final ChildrenRepository childrenRepository;
    private final GoalRepository goalRepository;

    @Transactional
    public Long createLink(Long parentId, Long etfHistoryId,
                           Long childId, Long goalId, int qty, String memo) {
        AccountEtfHistory history = accountEtfHistoryRepository.findById(etfHistoryId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.EXECUTION_LINK_NOT_FOUND));

        int usedQty = executionGoalLinkRepository.sumQtyByEtfHistoryId(etfHistoryId);
        if (usedQty + qty > history.getQtyDelta()) {
            throw new GeneralException(ErrorStatus.EXECUTION_LINK_QTY_EXCEEDED);
        }

        ExecutionGoalLink link = ExecutionGoalLink.builder()
            .parentId(parentId)
            .etfHistoryId(etfHistoryId)
            .qty(qty)
            .build();
        executionGoalLinkRepository.save(link);
        link.link(childId, goalId, memo);
        return link.getId();
    }

    @Transactional
    public void deductByFifo(Long childId, Long goalId, int sellQty) {
        List<ExecutionGoalLink> links = executionGoalLinkRepository.findForFifoDeduction(childId, goalId);

        int remaining = sellQty;
        for (ExecutionGoalLink link : links) {
            if (remaining <= 0) break;
            int deduct = Math.min(link.getQty(), remaining);
            link.deductQty(deduct);
            remaining -= deduct;
        }

        if (remaining > 0) {
            throw new GeneralException(ErrorStatus.EXECUTION_LINK_QTY_EXCEEDED);
        }
    }

    @Transactional(readOnly = true)
    public List<UnlinkedExecutionResponse> getUnlinked(Long parentId) {
        List<ExecutionGoalLink> links =
            executionGoalLinkRepository.findByParentIdAndLinkedAtIsNullOrderByCreatedAtDesc(parentId);

        List<Long> historyIds = links.stream().map(ExecutionGoalLink::getEtfHistoryId).toList();
        Map<Long, AccountEtfHistory> historyMap = accountEtfHistoryRepository.findAllById(historyIds)
            .stream().collect(Collectors.toMap(AccountEtfHistory::getId, h -> h));

        return links.stream()
            .map(link -> UnlinkedExecutionResponse.of(link, historyMap.get(link.getEtfHistoryId())))
            .toList();
    }

    @Transactional
    public void link(Long parentId, Long linkId, LinkRequest req) {
        ExecutionGoalLink link = executionGoalLinkRepository.findById(linkId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.EXECUTION_LINK_NOT_FOUND));

        if (!link.getParentId().equals(parentId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        if (link.getLinkedAt() != null) {
            throw new GeneralException(ErrorStatus.EXECUTION_LINK_ALREADY_LINKED);
        }

        childrenRepository.findByIdAndParentIdAndDeletedAtIsNull(req.childId(), parentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHILDREN_NOT_FOUND));

        goalRepository.findByIdAndChildIdAndParentIdAndDeletedAtIsNull(req.goalId(), req.childId(), parentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.GOAL_NOT_FOUND));

        link.link(req.childId(), req.goalId(), req.memo());
    }

    @Transactional(readOnly = true)
    public List<GoalExecutionResponse> getByGoal(Long goalId) {
        List<ExecutionGoalLink> links =
            executionGoalLinkRepository.findByParentIdAndLinkedAtIsNullOrderByCreatedAtAsc(goalId);

        List<Long> historyIds = links.stream().map(ExecutionGoalLink::getEtfHistoryId).toList();
        Map<Long, AccountEtfHistory> historyMap = accountEtfHistoryRepository.findAllById(historyIds)
            .stream().collect(Collectors.toMap(AccountEtfHistory::getId, h -> h));

        return links.stream()
            .map(link -> GoalExecutionResponse.of(link, historyMap.get(link.getEtfHistoryId())))
            .toList();
    }
}
