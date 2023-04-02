package org.nenad.paunov.model;

import jakarta.persistence.*;
import lombok.*;
import org.nenad.paunov.vo.GameStatus;

import java.math.BigDecimal;
import java.time.Duration;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameRecord extends AuditModel {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private GameStatus status;
	private Long playerId;
	@Column(name = "game_time")
	private Duration gameTime;
	@Column(name = "game_score")

	private BigDecimal gameScore;

	public GameRecord(String name, GameStatus status) {
		this.name = name;
		this.status = status;
	}
}
