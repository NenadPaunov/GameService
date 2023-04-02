package org.nenad.paunov.repository;

import org.nenad.paunov.model.GameRecord;
import org.nenad.paunov.vo.GameStatus;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface GameRepository extends CrudRepository<GameRecord, Long>, JpaSpecificationExecutor<GameRecord> {

	@Modifying(clearAutomatically = true)
	@Query("update GameRecord g set g.status = :droppedStatus where g.status = :newStatus and g.createdAt < :date")
	int updateGameStatus(@Param("date") LocalDateTime date, @Param("newStatus") GameStatus newStatus, @Param("droppedStatus") GameStatus droppedStatus);

	void deleteAllByPlayerId(long playerId);
}
