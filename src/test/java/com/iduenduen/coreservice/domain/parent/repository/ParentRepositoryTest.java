package com.iduenduen.coreservice.domain.parent.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

import com.iduenduen.coreservice.domain.parent.entity.Parent;

@DataJpaTest
class ParentRepositoryTest {

    @Autowired
    private ParentRepository parentRepository;

    private Parent createParent(String email, String accountNumber) {
        return Parent.builder()
                .email(email)
                .accountNumber(accountNumber)
                .passwordHash("hashed")
                .name("홍길동")
                .birthDate(LocalDate.of(1990, 1, 1))
                .relation("MOTHER")
                .region("서울")
                .childCount(1)
                .certFileUrl("https://example.com/cert.pdf")
                .build();
    }

    @Test
    void 저장하면_id가_생성된다() {
        Parent parent = createParent("save@example.com", "1111111111");

        Parent saved = parentRepository.save(parent);

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void findByIdAndDeletedAtIsNull_삭제되지_않은_경우_조회된다() {
        Parent saved = parentRepository.save(createParent("active@example.com", "2222222222"));

        var found = parentRepository.findByIdAndDeletedAtIsNull(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("active@example.com");
    }

    @Test
    void findByIdAndDeletedAtIsNull_삭제된_경우_조회되지_않는다() {
        Parent parent = createParent("deleted@example.com", "3333333333");
        ReflectionTestUtils.setField(parent, "deletedAt", java.time.LocalDateTime.now());
        Parent saved = parentRepository.save(parent);

        var found = parentRepository.findByIdAndDeletedAtIsNull(saved.getId());

        assertThat(found).isEmpty();
    }
}
