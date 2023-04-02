package org.nenad.paunov.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nenad.paunov.dao.GameDao;
import org.nenad.paunov.dto.GameRequest;
import org.nenad.paunov.dto.GameResponse;
import org.nenad.paunov.dto.PlayerRequest;
import org.nenad.paunov.dto.PlayerResponse;
import org.nenad.paunov.exception.EntityNotFoundException;
import org.nenad.paunov.exception.ExternalServiceException;
import org.nenad.paunov.model.GameRecord;
import org.nenad.paunov.repository.specifications.GameSpecification;
import org.nenad.paunov.vo.GameStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {

	private final GameDao gameDao;
	private final RestTemplate restTemplate;

	@Value("${player_service_host}")
	private String playerServiceHost;

	public GameResponse getGameInfo(Long gameId) throws Exception {
		return new GameResponse(gameDao.getGameInfoById(gameId));
	}

	@Transactional
	public GameResponse startGame(GameRequest gameRequest) {
		GameRecord gameRecord = gameDao.saveGame(new GameRecord(gameRequest.getName(), GameStatus.NEW));
		PlayerResponse playerResponse = registerPlayer(new PlayerRequest(gameRequest.getPlayerName(), gameRecord.getId()));
		if (playerResponse == null) {
			throw new ExternalServiceException("Unable to register player");
		}
		gameRecord.setPlayerId(playerResponse.getPlayerId());
		return new GameResponse(gameDao.saveGame(gameRecord));
	}

	public GameResponse updateGameStatus(GameRequest gameRequest) throws Exception {
		GameRecord gameRecord = gameDao.getGameInfoById(gameRequest.getGameId());
		if (GameStatus.FINISHED.equals(gameRequest.getStatus())) {
			LocalDateTime startDateTime = gameRecord.getCreatedAt();
			LocalDateTime endDateTime = LocalDateTime.now();
			gameRecord.setGameTime(Duration.between(startDateTime, endDateTime));
			gameRecord.setGameScore(gameRequest.getGameScore());
		}
		gameRecord.setStatus(gameRequest.getStatus());
		return new GameResponse(gameDao.saveGame(gameRecord));
	}

	@Transactional
	public void deleteGame(Long gameId) throws Exception {
		PlayerResponse playerResponse = registerPlayer(new PlayerRequest(gameDao.getGameInfoById(gameId).getPlayerId(), gameId));
		if (playerResponse == null) {
			throw new ExternalServiceException("Unable to delete player");
		}
		gameDao.deleteGame(gameId);
	}
	@Transactional
	public void deleteAllGamesByPlayerId(Long playerId) {
		gameDao.deleteAllGamesByPlayerId(playerId);
	}

	public List<GameResponse> searchGames(String name, String status, String playerName, LocalDateTime createdBefore, LocalDateTime createdAfter, Pageable pageable) throws EntityNotFoundException {
		GameStatus gameStatus = status != null ? GameStatus.valueOf(status.toUpperCase(Locale.ROOT)) : null;
		return gameDao.searchGames(setSpecification(name, gameStatus, playerName, createdBefore, createdAfter), pageable).getContent()
				.stream()
				.map(GameResponse::new)
				.collect(Collectors.toList());
	}

	public Specification<GameRecord> setSpecification(String name, GameStatus status, String playerName, LocalDateTime createdBefore, LocalDateTime createdAfter) {
		return Specification
				.where(GameSpecification.withName(name))
				.and(GameSpecification.withGameStatus(status))
				.and(GameSpecification.withId(getPlayerGameIds(playerName)))
				.and(GameSpecification.withDateCreated(createdAfter, createdBefore));
	}

	private URI getGameIdsEndpoint(String playerName) {
		return UriComponentsBuilder.fromUriString(playerServiceHost + "/playerservice/player/gameIds")
				.queryParam("name", playerName)
				.build()
				.toUri();
	}

	private URI getRegisterPlayerEndpoint() {
		return UriComponentsBuilder.fromUriString(playerServiceHost + "/playerservice/player/register")
				.build()
				.toUri();
	}

	public PlayerResponse registerPlayer(PlayerRequest playerRequest) {
		URI uri = getRegisterPlayerEndpoint();
		ResponseEntity<PlayerResponse> responseEntity = callPlayerServiceRegister(uri, playerRequest);

		if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
			log.warn("Player registration failed: {} ", playerRequest.getName());
			return null;
		}

		return responseEntity.getBody();
	}

	private ResponseEntity<PlayerResponse> callPlayerServiceRegister(URI uri, PlayerRequest playerRequest) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<PlayerRequest> requestEntity = new HttpEntity<>(playerRequest, headers);
		ResponseEntity<PlayerResponse> responseEntity = restTemplate.exchange(uri.toString(), HttpMethod.POST, requestEntity, PlayerResponse.class);

		if (!responseEntity.getStatusCode().is2xxSuccessful()) {
			log.error("Unable to get gameIds from Player Service. Status: {} Body: {}", responseEntity.getStatusCode(), responseEntity.getBody());
			throw new ExternalServiceException("Unable to get gameIds from Player Service.");
		}

		return responseEntity;
	}

	public List<Long> getPlayerGameIds(String playerName) {
		if (playerName == null) {
			return null;
		}

		URI uri = getGameIdsEndpoint(playerName);
		ResponseEntity<PlayerResponse> responseEntity = callPlayerServiceGameKeys(uri);
		if (responseEntity != null && responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
			log.warn("No gameIds found for player name: {} ", playerName);
			return null;
		}

		if (responseEntity == null || responseEntity.getBody() == null || responseEntity.getBody().getGameId() == null) {
			log.warn("No gameIds found for player name: {} ", playerName);
			return null;
		}

		return responseEntity.getBody().getGameId();
	}

	private ResponseEntity<PlayerResponse> callPlayerServiceGameKeys(URI uri) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> requestEntity = new HttpEntity<>(headers);
		ResponseEntity<PlayerResponse> responseEntity = restTemplate.exchange(uri.toString(), HttpMethod.GET, requestEntity, PlayerResponse.class);

		if (!responseEntity.getStatusCode().is2xxSuccessful()) {
			log.error("Unable to get gameIds from Player Service. Status: {} Body: {}", responseEntity.getStatusCode(), responseEntity.getBody());
			return null;
		}

		return responseEntity;
	}

}
