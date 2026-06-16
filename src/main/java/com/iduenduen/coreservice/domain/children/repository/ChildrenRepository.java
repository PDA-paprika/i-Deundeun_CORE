package com.iduenduen.coreservice.domain.children.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iduenduen.coreservice.domain.children.entity.Children;

public interface ChildrenRepository extends JpaRepository<Children, Long> {

	boolean existsByParentIdAndNameAndDeletedAtIsNull(Long parentId, String name);

	List<Children> findAllByParentIdAndDeletedAtIsNull(Long parentId);

	Optional<Children> findByIdAndParentIdAndDeletedAtIsNull(Long id, Long parentId);

	boolean existsByParentIdAndNameAndDeletedAtIsNullAndIdNot(Long parentId, String name, Long id);
}