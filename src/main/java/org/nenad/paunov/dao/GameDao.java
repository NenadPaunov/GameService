package org.nenad.paunov.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nenad.paunov.exception.EntityNotFoundException;
import org.nenad.paunov.model.GameRecord;
import org.nenad.paunov.repository.GameRepository;
import org.nenad.paunov.vo.GameStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameDao {

	private final GameRepository dbRepository;

	public GameRecord getGameInfoById(Long gameId) throws Exception {
		try {
			Optional<GameRecord> gameRecord = dbRepository.findById(gameId);
			if (gameRecord.isEmpty()) {
				log.error("There is no game associated with requested id - {}", gameId);
				throw new EntityNotFoundException();
			}
			return gameRecord.get();
		} catch (Exception e) {
			log.error("Unexpected exception while getting game with requested id - {}", gameId);
			throw e;
		}
	}

	public GameRecord saveGame(GameRecord gameRecord) {
		try {
			return (dbRepository.save(gameRecord));
		} catch (Exception e) {
			log.error("GameRecord {} - Unexpected exception while saving game",
					gameRecord.getName(), e);
			throw e;
		}
	}

	public Page<GameRecord> searchGames(Specification<GameRecord> specification, Pageable pageable) throws EntityNotFoundException {
		Page<GameRecord> games = dbRepository.findAll(specification, pageable);
		if (games.isEmpty()) {
			log.error("No games matched search criteria");
			throw new EntityNotFoundException();
		}

		return games;
	}

	public void deleteGame(Long gameId) {
		try {
			dbRepository.deleteById(gameId);
		} catch (Exception e) {
			log.error("GameRecord {} - Unexpected exception while deleting game",
					gameId, e);
			throw e;
		}
	}

	public void deleteAllGamesByPlayerId(Long playerId) {
		try {
			dbRepository.deleteAllByPlayerId(playerId);
		} catch (Exception e) {
			log.error("GameRecord {} - Unexpected exception while deleting games by playerId",
					playerId, e);
			throw e;
		}
	}

	@Transactional
	public int updateGameStatus(LocalDateTime dateTime) {
		try {
			 return dbRepository.updateGameStatus(dateTime, GameStatus.NEW, GameStatus.DROPED);
		} catch (Exception e) {
			log.error("Unexpected exception while updating game status for dropped games", e);
			throw e;
		}
	}
}
