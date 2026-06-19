package com.iduenduen.coreservice.domain.account.service;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.domain.account.dto.EtfTradeNotificationRequest;
import com.iduenduen.coreservice.domain.account.entity.Account;
import com.iduenduen.coreservice.domain.account.entity.AccountEtfHistory;
import com.iduenduen.coreservice.domain.account.enums.AccountType;
import com.iduenduen.coreservice.domain.account.enums.EtfEventType;
import com.iduenduen.coreservice.domain.account.repository.AccountEtfHistoryRepository;
import com.iduenduen.coreservice.domain.account.repository.AccountRepository;
import com.iduenduen.coreservice.domain.executionGoalLink.service.ExecutionGoalLinkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock private AccountRepository accountRepository;
    @Mock private AccountEtfHistoryRepository accountEtfHistoryRepository;
    @Mock private ExecutionGoalLinkService executionGoalLinkService;

    @InjectMocks
    private AccountService accountService;

    private static final Long PARENT_ID = 1L;
    private static final Long ACCOUNT_ID = 10L;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        ReflectionTestUtils.setField(account, "accountId", ACCOUNT_ID);
        ReflectionTestUtils.setField(account, "parentId", PARENT_ID);
        ReflectionTestUtils.setField(account, "accountType", AccountType.PARENT);
        ReflectionTestUtils.setField(account, "accountNumber", "1234567890");
        ReflectionTestUtils.setField(account, "availableAmt", 1_000_000L);
    }

    // ── getMyAccount ────────────────────────────────────────────

    @Test
    void getMyAccount_성공() {
        given(accountRepository.findByParentId(PARENT_ID)).willReturn(Optional.of(account));

        var response = accountService.getMyAccount(PARENT_ID);

        assertThat(response.accountId()).isEqualTo(ACCOUNT_ID);
        assertThat(response.availableAmt()).isEqualTo(1_000_000L);
    }

    @Test
    void getMyAccount_존재하지_않으면_예외() {
        given(accountRepository.findByParentId(PARENT_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getMyAccount(PARENT_ID))
            .isInstanceOf(GeneralException.class);
    }

    // ── getBalance ───────────────────────────────────────────────

    @Test
    void getBalance_성공() {
        given(accountRepository.findById(ACCOUNT_ID)).willReturn(Optional.of(account));

        var response = accountService.getBalance(ACCOUNT_ID);

        assertThat(response.getAvailableCash()).isEqualTo(1_000_000L);
    }

    @Test
    void getBalance_존재하지_않으면_예외() {
        given(accountRepository.findById(ACCOUNT_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getBalance(ACCOUNT_ID))
            .isInstanceOf(GeneralException.class);
    }

    // ── recordTrade (BUY) ────────────────────────────────────────

    @Test
    void recordTrade_매수_미연결_성공() {
        given(accountEtfHistoryRepository.save(any(AccountEtfHistory.class))).willAnswer(inv -> {
            AccountEtfHistory h = inv.getArgument(0);
            ReflectionTestUtils.setField(h, "id", 100L);
            return h;
        });
        given(executionGoalLinkService.createLink(eq(PARENT_ID), eq(100L), isNull(), isNull(), isNull())).willReturn(1L);

        EtfTradeNotificationRequest req = new EtfTradeNotificationRequest(
            ACCOUNT_ID, PARENT_ID, EtfEventType.BUY, "ETF001",
            3, 50_000L, null, null, null, null, null,
            LocalDateTime.now()
        );

        Long linkId = accountService.recordTrade(req);

        assertThat(linkId).isEqualTo(1L);
        verify(executionGoalLinkService).createLink(PARENT_ID, 100L, null, null, null);
    }

    @Test
    void recordTrade_매수시_qty_양수로_저장() {
        given(accountEtfHistoryRepository.save(any(AccountEtfHistory.class))).willAnswer(inv -> {
            AccountEtfHistory h = inv.getArgument(0);
            assertThat(h.getQtyDelta()).isPositive();
            ReflectionTestUtils.setField(h, "id", 100L);
            return h;
        });
        given(executionGoalLinkService.createLink(eq(PARENT_ID), eq(100L), isNull(), isNull(), isNull())).willReturn(1L);

        EtfTradeNotificationRequest req = new EtfTradeNotificationRequest(
            ACCOUNT_ID, PARENT_ID, EtfEventType.BUY, "ETF001",
            3, 50_000L, null, null, null, null, null,
            LocalDateTime.now()
        );

        accountService.recordTrade(req);
    }

    @Test
    void recordTrade_매도시_qty_음수로_저장() {
        given(accountEtfHistoryRepository.save(any(AccountEtfHistory.class))).willAnswer(inv -> {
            AccountEtfHistory h = inv.getArgument(0);
            assertThat(h.getQtyDelta()).isNegative();
            ReflectionTestUtils.setField(h, "id", 101L);
            return h;
        });
        given(executionGoalLinkService.createLink(eq(PARENT_ID), eq(101L), eq(1L), eq(1L), eq("목표 달성용 매도"))).willReturn(2L);

        EtfTradeNotificationRequest req = new EtfTradeNotificationRequest(
            ACCOUNT_ID, PARENT_ID, EtfEventType.SELL, "ETF001",
            1, 52_000L, null, null, 1L, 1L, "목표 달성용 매도",
            LocalDateTime.now()
        );

        accountService.recordTrade(req);
    }

    @Test
    void recordTrade_매도_즉시연결_성공() {
        given(accountEtfHistoryRepository.save(any(AccountEtfHistory.class))).willAnswer(inv -> {
            AccountEtfHistory h = inv.getArgument(0);
            ReflectionTestUtils.setField(h, "id", 101L);
            return h;
        });
        given(executionGoalLinkService.createLink(eq(PARENT_ID), eq(101L), eq(1L), eq(1L), eq("목표 달성용 매도"))).willReturn(2L);

        EtfTradeNotificationRequest req = new EtfTradeNotificationRequest(
            ACCOUNT_ID, PARENT_ID, EtfEventType.SELL, "ETF001",
            1, 52_000L, null, null, 1L, 1L, "목표 달성용 매도",
            LocalDateTime.now()
        );

        Long linkId = accountService.recordTrade(req);

        assertThat(linkId).isEqualTo(2L);
        verify(executionGoalLinkService).createLink(PARENT_ID, 101L, 1L, 1L, "목표 달성용 매도");
    }
}
