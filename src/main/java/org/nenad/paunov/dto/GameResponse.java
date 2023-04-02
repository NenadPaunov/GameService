package org.nenad.paunov.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.nenad.paunov.model.GameRecord;
import org.nenad.paunov.vo.GameStatus;

import java.time.Duration;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameResponse {
	private Long gameId;
	private String name;
	private GameStatus status;
	private String gameTime;

	public GameResponse(GameRecord gameRecord) {
		setGameId(gameRecord.getId());
		setName(gameRecord.getName());
		setStatus(gameRecord.getStatus());
		setGameTime(formatDuration(gameRecord.getGameTime()));
	}

	private String formatDuration(Duration duration) {
		if (duration != null) {
			long minutes = duration.toMinutes();
			long seconds = duration.minusMinutes(minutes).getSeconds();
			log.debug("Formatted duration to is: {} min, {} sec", minutes, seconds);
			return String.format("%d min, %d sec", minutes, seconds);
		}
		log.debug("Duration is null");
		return null;
	}
}
