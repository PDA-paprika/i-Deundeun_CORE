package com.iduenduen.coreservice.domain.executionGoalLink.service;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.status.ErrorStatus;
import com.iduenduen.coreservice.domain.account.entity.AccountEtfHistory;
import com.iduenduen.coreservice.domain.account.repository.AccountEtfHistoryRepository;
import com.iduenduen.coreservice.domain.children.repository.ChildrenRepository;
import com.iduenduen.coreservice.domain.executionGoalLink.dto.AllExecutionResponse;
import com.iduenduen.coreservice.domain.executionGoalLink.dto.GoalExecutionResponse;
import com.iduenduen.coreservice.domain.executionGoalLink.dto.GoalHoldingsResponse;
import com.iduenduen.coreservice.domain.executionGoalLink.dto.MoveRequest;
import com.iduenduen.coreservice.domain.executionGoalLink.dto.UnlinkedExecutionResponse;
import com.iduenduen.coreservice.domain.executionGoalLink.entity.ExecutionGoalLink;
import com.iduenduen.coreservice.domain.executionGoalLink.repository.ExecutionGoalLinkRepository;
import com.iduenduen.coreservice.domain.goals.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
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
                           Long childId, Long goalId, String memo) {
        AccountEtfHistory history = accountEtfHistoryRepository.findById(etfHistoryId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.EXECUTION_LINK_NOT_FOUND));

        ExecutionGoalLink link = ExecutionGoalLink.builder()
            .parentId(parentId)
            .etfHistoryId(etfHistoryId)
            .qty(history.getQtyDelta())
            .build();
        executionGoalLinkRepository.save(link);
        link.link(childId, goalId, memo);
        return link.getId();
    }

    @Transactional(readOnly = true)
    public GoalHoldingsResponse getGoalHoldings(Long goalId, Long childId) {
        List<Object[]> rows = executionGoalLinkRepository.sumQtyByGoalAndChild(goalId, childId);

        List<GoalHoldingsResponse.HoldingDto> holdings = rows.stream()
                .map(row -> GoalHoldingsResponse.HoldingDto.builder()
                        .etfId(((Number) row[0]).longValue())
                        .etfName((String) row[1])
                        .qty(((Number) row[2]).intValue())
                        .build())
                .toList();

        return GoalHoldingsResponse.builder()
                .goalId(goalId)
                .childId(childId)
                .holdings(holdings)
                .build();
    }

    @Transactional(readOnly = true)
    public Map<Long, Integer> getTaggedQtyMapByEtfId(Long parentId) {
        List<Object[]> rows = executionGoalLinkRepository.sumTaggedQtyByEtfIdForParent(parentId);
        Map<Long, Integer> result = new HashMap<>();
        for (Object[] row : rows) {
            Long etfId = ((Number) row[0]).longValue();
            int qty = ((Number) row[1]).intValue();
            result.put(etfId, qty);
        }
        return result;
    }

    @Transactional
    public void moveLink(Long parentId, Long linkId, MoveRequest req) {
        ExecutionGoalLink link = executionGoalLinkRepository.findById(linkId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.EXECUTION_LINK_NOT_FOUND));

        if (!link.getParentId().equals(parentId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        if ((req.childId() == null) != (req.goalId() == null)) {
            throw new GeneralException(ErrorStatus.EXECUTION_LINK_INVALID_CHILD_GOAL);
        }

        if (req.childId() != null && req.goalId() != null) {
            childrenRepository.findByIdAndParentIdAndDeletedAtIsNull(req.childId(), parentId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.CHILDREN_NOT_FOUND));
            goalRepository.findByIdAndChildIdAndParentId(req.goalId(), req.childId(), parentId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.GOAL_NOT_FOUND));
        }

        link.move(req.childId(), req.goalId());
    }

    @Transactional
    public void deductByFifo(Long childId, Long goalId, Long etfId, int sellQty) {
        List<ExecutionGoalLink> links = executionGoalLinkRepository.findForFifoDeduction(childId, goalId, etfId);

        int remaining = sellQty;
        for (ExecutionGoalLink link : links) {
            if (remaining <= 0) break;
            int deduct = Math.min(link.getQty(), remaining);
            link.deductQty(deduct);
            remaining -= deduct;
            if (link.getQty() == 0) {
                executionGoalLinkRepository.delete(link);
            }
        }

        if (remaining > 0) {
            throw new GeneralException(ErrorStatus.EXECUTION_LINK_QTY_EXCEEDED);
        }
    }

    @Transactional
    public void deductByLinkId(Long linkId, int sellQty) {
        ExecutionGoalLink link = executionGoalLinkRepository.findById(linkId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.EXECUTION_LINK_NOT_FOUND));

        if (link.getQty() < sellQty) {
            throw new GeneralException(ErrorStatus.EXECUTION_LINK_QTY_EXCEEDED);
        }

        link.deductQty(sellQty);
        if (link.getQty() == 0) {
            executionGoalLinkRepository.delete(link);
        }
    }

    @Transactional(readOnly = true)
    public List<AllExecutionResponse> getAll(Long parentId) {
        List<ExecutionGoalLink> links =
            executionGoalLinkRepository.findAllByParentId(parentId);

        List<Long> historyIds = links.stream().map(ExecutionGoalLink::getEtfHistoryId).toList();
        Map<Long, AccountEtfHistory> historyMap = accountEtfHistoryRepository.findAllById(historyIds)
            .stream().collect(Collectors.toMap(AccountEtfHistory::getId, h -> h));

        return links.stream()
            .map(link -> AllExecutionResponse.of(link, historyMap.get(link.getEtfHistoryId())))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<UnlinkedExecutionResponse> getUnlinked(Long parentId) {
        List<ExecutionGoalLink> links =
            executionGoalLinkRepository.findByParentIdAndChildIdIsNullAndGoalIdIsNullOrderByCreatedAtDesc(parentId);

        List<Long> historyIds = links.stream().map(ExecutionGoalLink::getEtfHistoryId).toList();
        Map<Long, AccountEtfHistory> historyMap = accountEtfHistoryRepository.findAllById(historyIds)
            .stream().collect(Collectors.toMap(AccountEtfHistory::getId, h -> h));

        return links.stream()
            .map(link -> UnlinkedExecutionResponse.of(link, historyMap.get(link.getEtfHistoryId())))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<GoalExecutionResponse> getByGoal(Long goalId, Long childId) {
        List<ExecutionGoalLink> links =
            executionGoalLinkRepository.findByGoalIdAndChildIdOrderByCreatedAtDesc(goalId, childId);

        List<Long> historyIds = links.stream().map(ExecutionGoalLink::getEtfHistoryId).toList();
        Map<Long, AccountEtfHistory> historyMap = accountEtfHistoryRepository.findAllById(historyIds)
            .stream().collect(Collectors.toMap(AccountEtfHistory::getId, h -> h));

        return links.stream()
            .map(link -> GoalExecutionResponse.of(link, historyMap.get(link.getEtfHistoryId())))
            .toList();
    }
}
