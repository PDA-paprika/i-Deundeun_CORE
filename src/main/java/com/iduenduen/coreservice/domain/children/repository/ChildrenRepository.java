package com.iduenduen.coreservice.domain.children.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iduenduen.coreservice.domain.children.entity.Children;

public interface ChildrenRepository extends JpaRepository<Children, Long> {

	boolean existsByParentIdAndNameAndDeletedAtIsNull(Long parentId, String name);
}