package com.iduenduen.coreservice.domain.parent.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;

import com.iduenduen.coreservice.domain.parent.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.iduenduen.coreservice.domain.account.repository.AccountCashHistoryRepository;
import com.iduenduen.coreservice.domain.account.repository.AccountEtfHistoryRepository;
import com.iduenduen.coreservice.domain.account.repository.AccountEtfHoldingRepository;
import com.iduenduen.coreservice.domain.account.repository.AccountRepository;
import com.iduenduen.coreservice.domain.children.repository.ChildrenRepository;
import com.iduenduen.coreservice.domain.executionGoalLink.repository.ExecutionGoalLinkRepository;
import com.iduenduen.coreservice.domain.gift.entity.GiftContract;
import com.iduenduen.coreservice.domain.gift.entity.GiftTransfer;
import com.iduenduen.coreservice.domain.gift.repository.GiftContractRepository;
import com.iduenduen.coreservice.domain.gift.repository.GiftTransferRepository;
import com.iduenduen.coreservice.domain.goals.repository.GoalRepository;
import com.iduenduen.coreservice.domain.onboarding.repository.UserAgreementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iduenduen.coreservice.common.exception.GeneralException;
import com.iduenduen.coreservice.common.status.ErrorStatus;
import com.iduenduen.coreservice.domain.goals.repository.GoalFrequencyProjection;
import com.iduenduen.coreservice.domain.parent.entity.Parent;
import com.iduenduen.coreservice.domain.parent.repository.ParentRepository;
import com.iduenduen.coreservice.domain.account.entity.Account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParentService {

    @Value("${assistant.url:http://localhost:8085}")
    private String assistantServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private final ParentRepository parentRepository;
    private final JdbcTemplate jdbcTemplate;
    private final AccountRepository accountRepository;
    private final AccountEtfHoldingRepository accountEtfHoldingRepository;
    private final AccountCashHistoryRepository accountCashHistoryRepository;
    private final AccountEtfHistoryRepository accountEtfHistoryRepository;
    private final ChildrenRepository childrenRepository;
    private final GoalRepository goalRepository;
    private final GiftContractRepository giftContractRepository;
    private final GiftTransferRepository giftTransferRepository;
    private final UserAgreementRepository userAgreementRepository;
    private final ExecutionGoalLinkRepository executionGoalLinkRepository;

    public ParentResponse getMe(Long parentId) {
        Parent parent = findActiveParent(parentId);
        Long accountId = accountRepository.findByParentId(parentId)
                .map(Account::getAccountId)
                .orElse(null);

        return ParentResponse.builder()
                .id(parent.getId())
                .accountId(accountId)
                .name(parent.getName())
                .accountNumber(parent.getAccountNumber())
                .birthDate(parent.getBirthDate())
                .relation(parent.getRelation())
                .region(parent.getRegion())
                .childCount(parent.getChildCount())
                .profileImageUrl(parent.getProfileImageUrl())
                .monthlyHouseholdIncome(parent.getMonthlyHouseholdIncome())
                .parentEconomicActivity(parent.getParentEconomicActivity())
                .educationLevel(parent.getEducationLevel())
                .clusterValue(parent.getClusterValue())
                .selectedChildId(parent.getSelectedChildId())
                .build();
    }

    @Transactional
    public ParentUpdateResponse updateMe(Long parentId, ParentUpdateRequest request) {
        Parent parent = findActiveParent(parentId);

        if (request.getName() == null || request.getBirthDate() == null || request.getRelation() == null
                || request.getChildCount() == null || request.getRegion() == null) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }

        parent.updateProfile(
                request.getName(),
                request.getBirthDate(),
                request.getRelation(),
                request.getChildCount(),
                request.getRegion(),
                request.getProfileImageUrl()
        );

        return ParentUpdateResponse.builder()
                .parentId(parent.getId())
                .build();
    }

    @Transactional
    public SelectedChildResponse updateSelectedChild(Long parentId, SelectedChildRequest request) {
        Parent parent = findActiveParent(parentId);

        parent.updateSelectedChild(request.getChildId());

        return SelectedChildResponse.builder()
                .selectedChildId(parent.getSelectedChildId())
                .build();
    }

    @Transactional
    public void updateWizardProfile(Long parentId, WizardProfileRequest request) {
        Parent parent = findActiveParent(parentId);

        parent.updateWizardProfile(
                request.getMonthlyHouseholdIncome(),
                request.getParentEconomicActivity(),
                request.getEducationLevel()
        );

        recluster(parentId);
    }

    @Transactional
    public void updateCertFileUrl(Long parentId, String certFileUrl) {
        Parent parent = findActiveParent(parentId);
        parent.updateCertFileUrl(certFileUrl);
    }

    @Transactional
    public void withdraw(Long parentId) {
        // 1. execution_goal_links → hard delete (FK: parent, child, goal, etf_history)
        executionGoalLinkRepository.deleteAll(
                executionGoalLinkRepository.findAllByParentId(parentId));

        // 2. account 관련 이력 + 잔고 → hard delete
        accountRepository.findByParentId(parentId).ifPresent(account -> {
            Long accountId = account.getAccountId();
            accountEtfHistoryRepository.deleteAll(
                    accountEtfHistoryRepository.findByAccountIdOrderByOccurredAtDesc(accountId));
            accountCashHistoryRepository.deleteAll(
                    accountCashHistoryRepository.findByAccountIdOrderByOccurredAtDesc(accountId));
            accountEtfHoldingRepository.deleteAll(
                    accountEtfHoldingRepository.findByIdAccountId(accountId));
            accountRepository.delete(account);
        });

        // 3. gift_transfers → hard delete (FK: gift_contracts)
        List<GiftContract> contracts = giftContractRepository.findAllByParentId(parentId);
        if (!contracts.isEmpty()) {
            List<Long> contractIds = contracts.stream().map(GiftContract::getId).toList();
            List<GiftTransfer> transfers = giftTransferRepository.findAllByGiftContractIdIn(contractIds);
            giftTransferRepository.deleteAll(transfers);
            giftContractRepository.deleteAll(contracts);
        }

        // 4. goals → hard delete
        goalRepository.deleteAll(goalRepository.findAllByParentId(parentId));

        // 5. children 계좌 및 관련 이력 → hard delete
        childrenRepository.findAllByParentId(parentId).forEach(child ->
            accountRepository.findByChildId(child.getId()).ifPresent(childAccount -> {
                Long childAccountId = childAccount.getAccountId();
                accountEtfHistoryRepository.deleteAll(
                        accountEtfHistoryRepository.findByAccountIdOrderByOccurredAtDesc(childAccountId));
                accountCashHistoryRepository.deleteAll(
                        accountCashHistoryRepository.findByAccountIdOrderByOccurredAtDesc(childAccountId));
                accountEtfHoldingRepository.deleteAll(
                        accountEtfHoldingRepository.findByIdAccountId(childAccountId));
                accountRepository.delete(childAccount);
            })
        );

        // 6. children → hard delete
        childrenRepository.deleteAll(childrenRepository.findAllByParentId(parentId));

        // 7. user_agreements → hard delete
        userAgreementRepository.deleteAll(userAgreementRepository.findAllByParent_Id(parentId));

        // 8. parent → hard delete
        Parent parent = findActiveParent(parentId);
        parentRepository.delete(parent);
    }

    @Transactional
    public Integer recluster(Long parentId) {
        log.info("[Parent] 클러스터 재분류 시작 - parentId={}", parentId);

        Parent parent = findActiveParent(parentId);

        Integer urbanFlag = jdbcTemplate.queryForList(
                "SELECT urban_flag FROM region_mapping WHERE region = ?",
                Integer.class, parent.getRegion())
                .stream().findFirst().orElse(0);

        ReclusterRequest reclusterRequest = ReclusterRequest.builder()
                .relation(parent.getRelation())
                .urbanFlag(urbanFlag)
                .monthlyHouseholdIncome(parent.getMonthlyHouseholdIncome())
                .educationLevel(parent.getEducationLevel())
                .parentEconomicActivity(parent.getParentEconomicActivity())
                .childCount(Math.min(parent.getChildCount(), 3))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        @SuppressWarnings("unchecked")
        java.util.Map<String, Integer> response = restTemplate.postForObject(
                assistantServiceUrl + "/parents/recluster",
                new HttpEntity<>(reclusterRequest, headers),
                java.util.Map.class);

        Integer returnedCluster = response != null ? response.get("cluster_value") : null;

        log.info("[Parent] 서비스 응답 수신 - parentId={}, clusterValue={}", parentId, returnedCluster);

        parent.updateClusterValue(returnedCluster);
        log.info("[Parent] cluster_value 업데이트 완료 - parentId={}, clusterValue={}", parentId, returnedCluster);

        return returnedCluster;
    }

    public StatsResponse getStatsKor(Long parentId, StatsKorRequest request) {
        log.info("[Parent] 국가통계 조회 - parentId={}, goalType1={}, goalType2={}, goalType3={}",
                parentId, request.getGoalType1(), request.getGoalType2(), request.getGoalType3());

        Parent parent = findActiveParent(parentId);
        log.info("[Parent] 지역 매핑 조회 - parentId={}, region={}", parentId, parent.getRegion());
        List<Integer> regionResult = jdbcTemplate.queryForList(
                "SELECT residence_region FROM region_mapping WHERE region = ?",
                Integer.class, parent.getRegion());
        if (regionResult.isEmpty()) {
            log.warn("[Parent] 지역 매핑 없음 - region={}", parent.getRegion());
            throw new GeneralException(ErrorStatus.NOT_FOUND);
        }
        Integer residenceRegion = regionResult.get(0);

        List<Integer> distribution = fetchDistribution(
                request.getGoalType1(), request.getGoalType2(), request.getGoalType3(),
                residenceRegion, parent.getParentEconomicActivity(), parent.getMonthlyHouseholdIncome());
        log.info("[Parent] 국가통계 distribution 건수 - count={}, region={}, residenceRegion={}, economicActivity={}, income={}, g1={}, g2={}, g3={}",
                distribution.size(), parent.getRegion(), residenceRegion,
                parent.getParentEconomicActivity(), parent.getMonthlyHouseholdIncome(),
                request.getGoalType1(), request.getGoalType2(), request.getGoalType3());

        StatsKorRequest requestWithDist = StatsKorRequest.builder()
                .goalType1(request.getGoalType1())
                .goalType2(request.getGoalType2())
                .goalType3(request.getGoalType3())
                .distribution(distribution)
                .build();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            StatsResponse response = restTemplate.postForObject(
                    assistantServiceUrl + "/stats/kor",
                    new HttpEntity<>(requestWithDist, headers),
                    StatsResponse.class);
            if (response != null) {
                response.setDistribution(distribution.stream().map(Double::valueOf).toList());
            }
            return response;
        } catch (Exception e) {
            log.warn("[Parent] 국가통계 Stats 서비스 호출 실패 - {}", e.getMessage());
            return null;
        }
    }

    private List<Integer> fetchDistribution(Integer g1, Integer g2, Integer g3,
                                             Integer residenceRegion, Integer economicActivity, Integer income) {
        if (g1 == 1 && (g2 == 2 || g2 == 3)) {
            return fetchEducationStat("elementary_middle_education_stat", "desired_high_school_type",
                    g2, residenceRegion, economicActivity, income, g3);
        } else if (g1 == 1 && g2 == 4) {
            return fetchEducationStat("high_school_education_stat", "desired_university_major",
                    g2, residenceRegion, economicActivity, income, g3);
        } else if (g1 == 2) {
            return fetchLivingStat(g2, economicActivity, income);
        }
        return List.of();
    }

    private List<Integer> fetchEducationStat(String table, String typeColumn,
                                              Integer schoolLevel, Integer region,
                                              Integer activity, Integer income, Integer type) {
        List<Integer> result;

        // 1단계: 전체 조건
        if (income != null && activity != null) {
            result = jdbcTemplate.queryForList(
                    "SELECT total_amount FROM " + table +
                    " WHERE school_level = ? AND residence_region = ? AND parent_economic_activity = ? AND monthly_household_income = ? AND " + typeColumn + " = ?",
                    Integer.class, schoolLevel, region, activity, income, type);
            if (!result.isEmpty()) return result;
        }

        // 2단계: income 제거
        if (activity != null) {
            result = jdbcTemplate.queryForList(
                    "SELECT total_amount FROM " + table +
                    " WHERE school_level = ? AND residence_region = ? AND parent_economic_activity = ? AND " + typeColumn + " = ?",
                    Integer.class, schoolLevel, region, activity, type);
            if (!result.isEmpty()) return result;
        }

        // 3단계: income, activity 제거
        result = jdbcTemplate.queryForList(
                "SELECT total_amount FROM " + table +
                " WHERE school_level = ? AND residence_region = ? AND " + typeColumn + " = ?",
                Integer.class, schoolLevel, region, type);
        if (!result.isEmpty()) return result;

        // 4단계: income, activity, region 모두 제거 (school_level + desired_type만)
        return jdbcTemplate.queryForList(
                "SELECT total_amount FROM " + table +
                " WHERE school_level = ? AND " + typeColumn + " = ?",
                Integer.class, schoolLevel, type);
    }

    private List<Integer> fetchLivingStat(Integer schoolLevel, Integer activity, Integer income) {
        List<Integer> result;
        int cappedIncome = (income != null && income < 6) ? income : 6;
        String schoolName = String.valueOf(schoolLevel);

        // 1단계: 전체 조건
        if (income != null && activity != null) {
            result = jdbcTemplate.queryForList(
                    "SELECT total_amount FROM living_stat WHERE school_name = ? AND parent_economic_activity = ? AND monthly_household_income = ?",
                    Integer.class, schoolName, String.valueOf(activity), String.valueOf(cappedIncome));
            if (!result.isEmpty()) return result;
        }

        // 2단계: income 제거
        if (activity != null) {
            result = jdbcTemplate.queryForList(
                    "SELECT total_amount FROM living_stat WHERE school_name = ? AND parent_economic_activity = ?",
                    Integer.class, schoolName, String.valueOf(activity));
            if (!result.isEmpty()) return result;
        }

        // 3단계: income, activity 모두 제거 (school_name만)
        return jdbcTemplate.queryForList(
                "SELECT total_amount FROM living_stat WHERE school_name = ?",
                Integer.class, schoolName);
    }

    public StatsResponse getStatsPersonal(Long parentId, StatsPersonalRequest request) {
        log.info("[Parent] 개인통계 조회 - parentId={}, goalType1={}, goalType2={}, goalType3={}",
                parentId, request.getGoalType1(), request.getGoalType2(), request.getGoalType3());

        Parent parent = findActiveParent(parentId);

        boolean hasDetail = request.getGoalType3() != null && request.getGoalType3() != 0;
        List<Integer> distribution = hasDetail
                ? jdbcTemplate.queryForList("""
                        SELECT go.target_amount
                        FROM goals go
                        JOIN parents p ON go.parent_id = p.id
                        WHERE p.cluster_value = ?
                          AND go.goal_type1   = ?
                          AND go.goal_type2   = ?
                          AND go.goal_type3   = ?
                          AND go.parent_id   != ?
                          AND go.status       = 'active'
                        """,
                        Integer.class,
                        parent.getClusterValue(), request.getGoalType1(), request.getGoalType2(),
                        request.getGoalType3(), parentId)
                : jdbcTemplate.queryForList("""
                        SELECT go.target_amount
                        FROM goals go
                        JOIN parents p ON go.parent_id = p.id
                        WHERE p.cluster_value = ?
                          AND go.goal_type1   = ?
                          AND go.goal_type2   = ?
                          AND go.parent_id   != ?
                          AND go.status       = 'active'
                        """,
                        Integer.class,
                        parent.getClusterValue(), request.getGoalType1(), request.getGoalType2(), parentId);

        log.info("[Parent] 개인통계 distribution 건수 - parentId={}, clusterValue={}, goalType1={}, goalType2={}, goalType3={}, count={}", parentId, parent.getClusterValue(), request.getGoalType1(), request.getGoalType2(), request.getGoalType3(), distribution.size());
        if (distribution.isEmpty()) {
            return null;
        }

        StatsPersonalRequest requestWithDist = StatsPersonalRequest.builder()
                .goalType1(request.getGoalType1())
                .goalType2(request.getGoalType2())
                .goalType3(request.getGoalType3())
                .clusterValue(parent.getClusterValue())
                .distribution(distribution)
                .build();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            StatsResponse response = restTemplate.postForObject(
                    assistantServiceUrl + "/stats/personal",
                    new HttpEntity<>(requestWithDist, headers),
                    StatsResponse.class);
            if (response != null) {
                response.setDistribution(distribution.stream().map(Double::valueOf).toList());
            }
            return response;
        } catch (Exception e) {
            log.warn("[Parent] 개인통계 Stats 서비스 호출 실패 - {}", e.getMessage());
            return null;
        }
    }

    @Transactional
    public void train() {
        log.info("[Parent] 클러스터 학습 시작");

        List<ParentFeatureRequest> features = jdbcTemplate.query("""
                SELECT p.id,
                       p.relation,
                       rm.urban_flag,
                       p.parent_economic_activity,
                       p.monthly_household_income,
                       p.education_level,
                       LEAST(p.child_count, 3) AS child_count
                FROM parents p
                JOIN region_mapping rm ON rm.region = p.region
                WHERE p.deleted_at IS NULL
                  AND p.monthly_household_income IS NOT NULL
                  AND p.parent_economic_activity IS NOT NULL
                  AND p.education_level IS NOT NULL
                """,
                (rs, rowNum) -> ParentFeatureRequest.builder()
                        .parentId(rs.getLong("id"))
                        .relation(rs.getString("relation"))
                        .urbanFlag(rs.getInt("urban_flag"))
                        .parentEconomicActivity(rs.getInt("parent_economic_activity"))
                        .monthlyHouseholdIncome(rs.getInt("monthly_household_income"))
                        .educationLevel(rs.getInt("education_level"))
                        .childCount(rs.getInt("child_count"))
                        .build());

        log.info("[Parent] 학습 대상 부모 수: {}", features.size());

        HttpHeaders trainHeaders = new HttpHeaders();
        trainHeaders.setContentType(MediaType.APPLICATION_JSON);
        ClusterTrainResponse[] results = restTemplate.postForObject(
                assistantServiceUrl + "/cluster/train",
                new HttpEntity<>(features, trainHeaders),
                ClusterTrainResponse[].class);

        if (results == null) return;

        for (ClusterTrainResponse result : results) {
            jdbcTemplate.update(
                    "UPDATE parents SET cluster_value = ? WHERE id = ?",
                    result.getClusterValue(), result.getParentId());
        }

        log.info("[Parent] 클러스터 학습 완료 - 업데이트 수: {}", results.length);
    }

    public List<ParentFrequencyResponse> getFrequency(ParentFrequencyRequest request) {
        try {
            List<GoalFrequencyProjection> projections =
                    goalRepository.countByClusterValue(request.getClusterValue());

            return projections.stream()
                    .map(p -> ParentFrequencyResponse.builder()
                            .goalType1(p.getGoalType1())
                            .goalType2(p.getGoalType2())
                            .goalType3(p.getGoalType3())
                            .count(p.getCount())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Parent findActiveParent(Long parentId) {
        return parentRepository.findByIdAndDeletedAtIsNull(parentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND));
    }
}
