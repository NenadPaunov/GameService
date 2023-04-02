package org.nenad.paunov.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nenad.paunov.dao.GameDao;
import org.nenad.paunov.dto.GameRequest;
import org.nenad.paunov.dto.GameResponse;
import org.nenad.paunov.dto.PlayerResponse;
import org.nenad.paunov.exception.EntityNotFoundException;
import org.nenad.paunov.exception.ExternalServiceException;
import org.nenad.paunov.model.GameRecord;
import org.nenad.paunov.vo.GameStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

	@InjectMocks
	private static GameService instance;
	private static final GameDao gameDao = Mockito.mock(GameDao.class);
	private static final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);

	@BeforeAll
	public static void beforeAll() {
		instance = new GameService(gameDao, restTemplate);
	}

	@Test
	void getGameInfo() throws Exception {
		Long gameId = 1L;
		GameRecord gameRecord = new GameRecord("Test Game", GameStatus.NEW);
		given(gameDao.getGameInfoById(gameId)).willReturn(gameRecord);
		GameResponse expectedResponse = new GameResponse(gameRecord);

		GameResponse response = instance.getGameInfo(gameId);

		assertThat(response.getStatus()).isEqualTo(expectedResponse.getStatus());
	}

	@Test
	void startGame() throws Exception {
		String gameName = "Test Game";
		String playerName = "Test Player";
		Long playerId = 2L;
		Long gameId = 1L;
		GameRequest gameRequest = new GameRequest(gameName, playerName);
		GameRecord savedGameRecord = new GameRecord(gameName, GameStatus.NEW);
		savedGameRecord.setPlayerId(playerId);
		savedGameRecord.setId(gameId);
		PlayerResponse playerResponse = new PlayerResponse();
		playerResponse.setPlayerId(playerId);
		given(gameDao.saveGame(any()))
				.willReturn(savedGameRecord);
		given(gameDao.getGameInfoById(any())).willReturn(savedGameRecord);
		given(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(PlayerResponse.class)))
				.willReturn(new ResponseEntity<>(playerResponse, HttpStatus.OK));

		GameResponse response = instance.startGame(gameRequest);

		assertThat(response.getGameId()).isEqualTo(gameId);
	}

	@Test
	void startGame_playerRegistrationFails() {
		String gameName = "Test Game";
		String playerName = "Test Player";
		GameRequest gameRequest = new GameRequest(gameName, playerName);
		GameRecord savedGameRecord = new GameRecord(gameName, GameStatus.NEW);
		given(gameDao.saveGame(any()))
				.willReturn(savedGameRecord);
		given(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(PlayerResponse.class)))
				.willReturn(new ResponseEntity<>(null, HttpStatus.NO_CONTENT));

		assertThatThrownBy(() -> instance.startGame(gameRequest))
				.isInstanceOf(ExternalServiceException.class)
				.hasMessage("Unable to register player");
	}

	@Test
	void updateGameStatus_finished() throws Exception {
		Long gameId = 1L;
		GameRequest gameRequest = new GameRequest();
		gameRequest.setGameId(gameId);
		gameRequest.setStatus(GameStatus.FINISHED);
		GameRecord gameRecord = new GameRecord("Test Game", GameStatus.NEW);
		gameRecord.setCreatedAt(LocalDateTime.now());
		gameRecord.setUpdatedAt(LocalDateTime.now().plusMinutes(10));
		given(gameDao.getGameInfoById(gameId)).willReturn(gameRecord);
		given(gameDao.saveGame(any())).willReturn(gameRecord);

		GameResponse response = instance.updateGameStatus(gameRequest);
		assertThat(response.getStatus()).isEqualTo(GameStatus.FINISHED);
	}

	@Test
	void updateGameStatus_droped() throws Exception {
		Long gameId = 1L;
		Integer gameScore = 100;
		GameRequest gameRequest = new GameRequest();
		gameRequest.setGameId(gameId);
		gameRequest.setStatus(GameStatus.DROPED);
		GameRecord gameRecord = new GameRecord("Test Game", GameStatus.NEW);
		gameRecord.setCreatedAt(LocalDateTime.now());
		gameRecord.setUpdatedAt(LocalDateTime.now().plusMinutes(10));
		given(gameDao.getGameInfoById(gameId)).willReturn(gameRecord);
		given(gameDao.saveGame(any())).willReturn(gameRecord);

		GameResponse response = instance.updateGameStatus(gameRequest);
		assertThat(response.getStatus()).isEqualTo(GameStatus.DROPED);
	}

	@Test
	void deleteGame() throws Exception {
		Long gameId = 1L;
		Long playerId = 2L;
		GameRecord gameRecord = new GameRecord("Test Game", GameStatus.NEW);
		gameRecord.setId(gameId);
		gameRecord.setPlayerId(playerId);
		given(gameDao.getGameInfoById(gameId)).willReturn(gameRecord);

		given(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(PlayerResponse.class)))
				.willReturn(new ResponseEntity<>(new PlayerResponse(), HttpStatus.OK));

		assertDoesNotThrow(() -> instance.deleteGame(gameId));
	}

	@Test
	void deleteGame_playerRegistrationFails() throws Exception {
		Long gameId = 1L;
		Long playerId = 2L;
		GameRecord gameRecord = new GameRecord("Test Game", GameStatus.NEW);
		gameRecord.setId(gameId);
		gameRecord.setPlayerId(playerId);
		given(gameDao.getGameInfoById(gameId)).willReturn(gameRecord);

		given(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(PlayerResponse.class)))
				.willReturn(new ResponseEntity<>(null, HttpStatus.NO_CONTENT));

		assertThatThrownBy(() -> instance.deleteGame(gameId))
				.isInstanceOf(ExternalServiceException.class)
				.hasMessage("Unable to delete player");
	}

	@Test
	void deleteAllGamesByPlayerId() {
		Long playerId = 2L;

		instance.deleteAllGamesByPlayerId(playerId);

		verify(gameDao, times(1)).deleteAllGamesByPlayerId(playerId);
	}

	@Test
	void searchGames_withNullValues_shouldReturnAllGames() throws EntityNotFoundException {
		String name = null;
		String status = null;
		String playerName = null;
		LocalDateTime createdBefore = null;
		LocalDateTime createdAfter = null;
		GameRecord gameRecord = new GameRecord("Test Game", GameStatus.NEW);
		gameRecord.setCreatedAt(LocalDateTime.now());
		gameRecord.setUpdatedAt(LocalDateTime.now().plusMinutes(10));
		Pageable pageable = PageRequest.of(0, 10);
		Page<GameRecord> games = new PageImpl<>(Collections.singletonList(gameRecord));
		given(gameDao.searchGames(any(), any())).willReturn(games);

		List<GameResponse> result = instance.searchGames(name, status, playerName, createdBefore, createdAfter, pageable);

		assertEquals(games.getTotalElements(), result.size());
	}


}