package org.nenad.paunov.repository.specifications;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.nenad.paunov.model.GameRecord;
import org.nenad.paunov.vo.GameStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class GameSpecification {

	public static Specification<GameRecord> withId(List<Long> ids) {
		log.debug("Searching by ids: {}", ids);
		return (root, query, criteriaBuilder) -> ids == null ?
				criteriaBuilder.isTrue(criteriaBuilder.literal(true)) :
				root.get("id").in(ids);
	}

	public static Specification<GameRecord> withName(String name) {
		log.debug("Searching by name: {}", name);
		return (root, query, criteriaBuilder) -> name == null ?
				criteriaBuilder.isTrue(criteriaBuilder.literal(true)) :
				criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
	}

	public static Specification<GameRecord> withGameStatus(GameStatus status) {
		log.debug("Searching by status: {}", status);
		return (root, query, criteriaBuilder) -> status == null ?
				criteriaBuilder.isTrue(criteriaBuilder.literal(true)) :
				criteriaBuilder.equal(root.get("status"), status);
	}

	public static Specification<GameRecord> withDateCreated(LocalDateTime dateCreatedBefore, LocalDateTime dateCreatedAfter) {
		log.debug("Searching by dateCreatedBefore: {} and dateCreatedAfter: {}", dateCreatedBefore, dateCreatedAfter);
		return (root, query, criteriaBuilder) -> {
			Predicate dateCreatedBeforePredicate = dateCreatedBefore == null ?
					criteriaBuilder.isTrue(criteriaBuilder.literal(true)) :
					criteriaBuilder.greaterThan(root.get("createdAt"), dateCreatedBefore);

			Predicate dateCreatedAfterPredicate = dateCreatedAfter == null ?
					criteriaBuilder.isTrue(criteriaBuilder.literal(true)) :
					criteriaBuilder.lessThan(root.get("createdAt"), dateCreatedAfter);

			return criteriaBuilder.and(dateCreatedBeforePredicate, dateCreatedAfterPredicate);
		};
	}
}
