package com.iduenduen.coreservice.domain.children.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iduenduen.coreservice.domain.children.entity.Children;
import com.iduenduen.coreservice.domain.children.enums.Gender;

public record ChildrenListResponse(List<ChildItem> children) {

    public record ChildItem(
        @JsonProperty("child_id")
        Long childId,

        String name,

        @JsonProperty("birth_date")
        LocalDate birthDate,

        int age,

        Gender gender,

        @JsonProperty("profile_image_url")
        String profileImageUrl,

        @JsonProperty("securities_account")
        String securitiesAccount,

        @JsonProperty("allowance_linked")
        boolean allowanceLinked
    ) {
        public static ChildItem from(Children children) {
            return new ChildItem(
                children.getId(),
                children.getName(),
                children.getBirthDate(),
                children.getAge(),
                children.getGender(),
                children.getProfileImageUrl(),
                children.getSecuritiesAccount(),
                children.isAllowanceLinked()
            );
        }
    }

    public static ChildrenListResponse from(List<Children> childrenList) {
        return new ChildrenListResponse(childrenList.stream().map(ChildItem::from).toList());
    }
}
