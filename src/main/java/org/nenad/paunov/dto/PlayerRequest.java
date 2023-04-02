package org.nenad.paunov.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerRequest {

	@NotNull(message = "Player name must not be null")
	@Schema(required = true, example = "Nenad")
	private String name;
	@Schema(example = "1")
	private Long playerId;
	@Schema(example = "1")
	private Long gameId;

	public PlayerRequest(Long playerId, Long gameId) {
		this.playerId = playerId;
		this.gameId = gameId;
	}

	public PlayerRequest(String name, Long gameId) {
		this.name = name;
		this.gameId = gameId;
	}
}
