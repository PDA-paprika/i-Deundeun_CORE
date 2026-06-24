package com.iduenduen.coreservice.domain.parent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StatsPersonalRequest {
    @JsonProperty("goal_type1")
    private Integer goalType1;

    @JsonProperty("goal_type2")
    private Integer goalType2;

    @JsonProperty("goal_type3")
    private Integer goalType3;

    @JsonProperty("cluster_value")
    private Integer clusterValue;

    /*
    g1, g2, g3 = info['goal_type1'], info['goal_type2'], info['goal_type3']
        cluster_val = info['cluster_value']

        # goal_type1=2이면 goal_type3=0으로 저장됨. <=> NULL-safe equality 사용
        cursor.execute("""
            SELECT go.target_amount
            FROM goals go
            JOIN parents p ON go.parent_id = p.id
            WHERE p.cluster_value = %s
              AND go.goal_type1   = %s
              AND go.goal_type2   = %s
              AND go.goal_type3   <=> %s
              AND go.parent_id   != %s
              AND go.deleted_at   IS NULL
        """, (cluster_val, g1, g2, g3, parent_id))
     */
}
