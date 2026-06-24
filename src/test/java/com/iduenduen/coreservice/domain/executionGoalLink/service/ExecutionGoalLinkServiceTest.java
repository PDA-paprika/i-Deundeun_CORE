package com.iduenduen.coreservice.domain.executionGoalLink.service;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.domain.account.entity.AccountEtfHistory;
import com.iduenduen.coreservice.domain.account.enums.EtfEventType;
import com.iduenduen.coreservice.domain.account.repository.AccountEtfHistoryRepository;
import com.iduenduen.coreservice.domain.executionGoalLink.dto.LinkRequest;
import com.iduenduen.coreservice.domain.executionGoalLink.entity.ExecutionGoalLink;
import com.iduenduen.coreservice.domain.executionGoalLink.repository.ExecutionGoalLinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExecutionGoalLinkServiceTest {

    @Mock private ExecutionGoalLinkRepository executionGoalLinkRepository;
    @Mock private AccountEtfHistoryRepository accountEtfHistoryRepository;

    @InjectMocks
    private ExecutionGoalLinkService executionGoalLinkService;

    private static final Long PARENT_ID = 1L;
    private static final Long CHILD_ID = 2L;
    private static final Long GOAL_ID = 3L;
    private static final Long HISTORY_ID = 10L;
    private static final Long LINK_ID = 100L;

    private ExecutionGoalLink unlinkedLink;
    private AccountEtfHistory history;

    @BeforeEach
    void setUp() {
        unlinkedLink = ExecutionGoalLink.builder()
            .parentId(PARENT_ID)
            .etfHistoryId(HISTORY_ID)
            .build();
        ReflectionTestUtils.setField(unlinkedLink, "id", LINK_ID);

        history = AccountEtfHistory.builder()
            .accountId(1L)
            .eventType(EtfEventType.BUY)
            .etfId(1L)
            .qtyDelta(3)
            .price(50_000L)
            .occurredAt(LocalDateTime.now())
            .build();
        ReflectionTestUtils.setField(history, "id", HISTORY_ID);
    }

    // ── createLink (매수 미연결) ──────────────────────────────────

    @Test
    void createLink_매수_미연결_성공() {
        given(executionGoalLinkRepository.save(any(ExecutionGoalLink.class))).willAnswer(inv -> {
            ExecutionGoalLink l = inv.getArgument(0);
            ReflectionTestUtils.setField(l, "id", LINK_ID);
            return l;
        });

        Long linkId = executionGoalLinkService.createLink(PARENT_ID, HISTORY_ID, null, null, null);

        ArgumentCaptor<ExecutionGoalLink> captor = ArgumentCaptor.forClass(ExecutionGoalLink.class);
        verify(executionGoalLinkRepository).save(captor.capture());
        assertThat(linkId).isEqualTo(LINK_ID);
        assertThat(captor.getValue().getLinkedAt()).isNull();
    }

    @Test
    void createLink_매도_즉시연결_성공() {
        given(executionGoalLinkRepository.save(any(ExecutionGoalLink.class))).willAnswer(inv -> {
            ExecutionGoalLink l = inv.getArgument(0);
            ReflectionTestUtils.setField(l, "id", LINK_ID);
            return l;
        });

        Long linkId = executionGoalLinkService.createLink(PARENT_ID, HISTORY_ID, CHILD_ID, GOAL_ID, "메모");

        ArgumentCaptor<ExecutionGoalLink> captor = ArgumentCaptor.forClass(ExecutionGoalLink.class);
        verify(executionGoalLinkRepository).save(captor.capture());
        ExecutionGoalLink saved = captor.getValue();
        assertThat(linkId).isEqualTo(LINK_ID);
        assertThat(saved.getChildId()).isEqualTo(CHILD_ID);
        assertThat(saved.getGoalId()).isEqualTo(GOAL_ID);
        assertThat(saved.getMemo()).isEqualTo("메모");
        assertThat(saved.getLinkedAt()).isNotNull();
    }

    // ── getUnlinked ──────────────────────────────────────────────

    @Test
    void getUnlinked_미연결_목록_반환() {
        given(executionGoalLinkRepository
            .findByParentIdAndLinkedAtIsNullOrderByCreatedAtDesc(PARENT_ID))
            .willReturn(List.of(unlinkedLink));
        given(accountEtfHistoryRepository.findAllById(List.of(HISTORY_ID)))
            .willReturn(List.of(history));

        var result = executionGoalLinkService.getUnlinked(PARENT_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(LINK_ID);
        assertThat(result.get(0).etfId()).isEqualTo(1L);
    }

    @Test
    void getUnlinked_없으면_빈_리스트() {
        given(executionGoalLinkRepository
            .findByParentIdAndLinkedAtIsNullOrderByCreatedAtDesc(PARENT_ID))
            .willReturn(List.of());
        given(accountEtfHistoryRepository.findAllById(List.of()))
            .willReturn(List.of());

        var result = executionGoalLinkService.getUnlinked(PARENT_ID);

        assertThat(result).isEmpty();
    }

    // ── link (자녀·목표 연결) ────────────────────────────────────

    @Test
    void link_성공() {
        given(executionGoalLinkRepository.findById(LINK_ID)).willReturn(Optional.of(unlinkedLink));

        executionGoalLinkService.link(PARENT_ID, LINK_ID, new LinkRequest(CHILD_ID, GOAL_ID, "대학 등록금"));

        assertThat(unlinkedLink.getChildId()).isEqualTo(CHILD_ID);
        assertThat(unlinkedLink.getGoalId()).isEqualTo(GOAL_ID);
        assertThat(unlinkedLink.getLinkedAt()).isNotNull();
    }

    @Test
    void link_존재하지_않으면_예외() {
        given(executionGoalLinkRepository.findById(LINK_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() ->
            executionGoalLinkService.link(PARENT_ID, LINK_ID, new LinkRequest(CHILD_ID, GOAL_ID, "메모")))
            .isInstanceOf(GeneralException.class);
    }

    @Test
    void link_이미_연결됐으면_예외() {
        unlinkedLink.link(CHILD_ID, GOAL_ID, "기존 메모");
        given(executionGoalLinkRepository.findById(LINK_ID)).willReturn(Optional.of(unlinkedLink));

        assertThatThrownBy(() ->
            executionGoalLinkService.link(PARENT_ID, LINK_ID, new LinkRequest(CHILD_ID, GOAL_ID, "새 메모")))
            .isInstanceOf(GeneralException.class);
    }

    // ── getByGoal ────────────────────────────────────────────────

    @Test
    void getByGoal_목표별_체결내역_반환() {
        unlinkedLink.link(CHILD_ID, GOAL_ID, "메모");
        given(executionGoalLinkRepository.findByGoalIdOrderByCreatedAtDesc(GOAL_ID))
            .willReturn(List.of(unlinkedLink));
        given(accountEtfHistoryRepository.findAllById(List.of(HISTORY_ID)))
            .willReturn(List.of(history));

        var result = executionGoalLinkService.getByGoal(GOAL_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).childId()).isEqualTo(CHILD_ID);
        assertThat(result.get(0).etfId()).isEqualTo(1L);
    }

    @Test
    void getByGoal_없으면_빈_리스트() {
        given(executionGoalLinkRepository.findByGoalIdOrderByCreatedAtDesc(GOAL_ID))
            .willReturn(List.of());
        given(accountEtfHistoryRepository.findAllById(List.of()))
            .willReturn(List.of());

        var result = executionGoalLinkService.getByGoal(GOAL_ID);

        assertThat(result).isEmpty();
    }
}
