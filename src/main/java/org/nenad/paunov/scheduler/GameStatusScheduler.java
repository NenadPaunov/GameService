package org.nenad.paunov.scheduler;

import jakarta.persistence.LockModeType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nenad.paunov.dao.GameDao;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Component
@Lazy(value = false)
@Getter
@RequiredArgsConstructor
public class GameStatusScheduler {

	private final GameDao gameDao;

	// TODO For testing purposes
	//	@Scheduled(cron = "0 */1 * * * *")
		@Scheduled(cron = "0 0 */3 * * *")
		@Lock(LockModeType.READ)
		@Transactional(propagation = Propagation.NOT_SUPPORTED)
		public void updateGameStatusForDroppedGames() {
			try {
				LocalDateTime threeHoursAgo = LocalDateTime.now().minus(Duration.ofHours(3));
				// TODO For testing purposes
				// LocalDateTime threeHoursAgo = LocalDateTime.now().minus(Duration.ofMinutes(1));
				int numberOfUpdatedGames = gameDao.updateGameStatus(threeHoursAgo);
				log.info("Invoking updateGameStatusForDroppedGames - Game status to dropped updated to {} games and is running every: 3 hours", numberOfUpdatedGames);
			} catch (Exception e) {
				log.error("updateGameStatusForDroppedGames - Unexpected exception while updating game status for dropped games", e);
				throw e;
			}
		}
}
