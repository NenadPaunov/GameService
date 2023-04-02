package org.nenad.paunov.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.nenad.paunov.config.annotations.ValidStatus;
import org.nenad.paunov.vo.GameStatus;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameRequest {

	@Schema(example = "1")
	private Long gameId;
	@NotNull(message = "Game name must not be null")
	@Schema(required = true, example = "chess")
	private String name;
	@Schema(example = "FINISHED")
	@ValidStatus
	private GameStatus status;
	@NotNull(message = "Player name must not be null")
	@Schema(required = true, example = "Nenad")
	private String playerName;
	@Schema(example = "255.55")
	private BigDecimal gameScore;

	public GameRequest(String name, String playerName) {
		this.name = name;
		this.playerName = playerName;
	}


}
