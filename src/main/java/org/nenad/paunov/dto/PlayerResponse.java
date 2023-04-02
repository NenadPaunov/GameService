package org.nenad.paunov.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerResponse {
	private Long playerId;
	private String name;
	private List<Long> gameId;

	public PlayerResponse(Long playerId, String name) {
		this.playerId = playerId;
		this.name = name;
	}
}
