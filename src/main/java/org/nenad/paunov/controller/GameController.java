package org.nenad.paunov.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nenad.paunov.config.annotations.ApiPageable;
import org.nenad.paunov.dto.GameRequest;
import org.nenad.paunov.dto.GameResponse;
import org.nenad.paunov.exception.EntityNotFoundException;
import org.nenad.paunov.service.GameService;
import org.nenad.paunov.vo.GameStatus;
import org.nenad.paunov.vo.SwaggerConstants;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class GameController {

	private final GameService gameService;

	@Operation(summary = SwaggerConstants.GAME_INFO)
	@GetMapping("/game/{id}")
	public ResponseEntity<?> getGameInfo(@PathVariable("id") Long id) throws Exception {
		return ResponseEntity.ok().body(gameService.getGameInfo(id));
	}

	@Operation(summary = SwaggerConstants.PLAY_GAME)
	@PostMapping("/play")
	public ResponseEntity<?> startGame(@RequestBody @Validated GameRequest gameRequest) {
		return ResponseEntity.ok().body(gameService.startGame(gameRequest));
	}

	@Operation(summary = SwaggerConstants.UPDATE_GAME)
	@PutMapping("/play")
	public ResponseEntity<?> updateGameStatus(@RequestBody @Validated GameRequest gameRequest) throws Exception {
		if (gameRequest.getStatus() != null &&
				!(gameRequest.getStatus().equals(GameStatus.FINISHED) || gameRequest.getStatus().equals(GameStatus.DROPED))) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok().body(gameService.updateGameStatus(gameRequest));
	}

	@Operation(summary = SwaggerConstants.DELETE_GAME)
	@DeleteMapping("/game/{id}")
	public ResponseEntity<?> deleteGame(@PathVariable("id") Long id) throws Exception {
		gameService.deleteGame(id);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = SwaggerConstants.PLAYER_GAME)
	@DeleteMapping("/player/{id}")
	public ResponseEntity<?> deleteAllGamesByPlayerId(@PathVariable("id") Long id) {
		gameService.deleteAllGamesByPlayerId(id);
		return ResponseEntity.ok().build();
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = SwaggerConstants.SEARCH_GAME)
	@ApiPageable
	public ResponseEntity<?> getGameRecords(
			@Parameter(example = "chess") @RequestParam(required = false) String name,
			@Parameter(example = "FINISHED") @RequestParam(required = false) String status,
			@Parameter(example = "Nenad") @RequestParam(required = false) String playerName,
			@Parameter(example = "2023-04-01T07:45:18.950204") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdBefore,
			@Parameter(example = "2023-04-18T07:45:18.950204") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAfter,
			@PageableDefault(size = 20) @Parameter(hidden = true) Pageable pageable) throws EntityNotFoundException {
		List<GameResponse> gameList = gameService.searchGames(name, status, playerName, createdBefore, createdAfter, pageable);
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Total-Count", String.valueOf(gameList.size()));
		return ResponseEntity.ok()
				.headers(headers)
				.body(gameList);
	}
}
