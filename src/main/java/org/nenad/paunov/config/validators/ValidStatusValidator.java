package org.nenad.paunov.config.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.nenad.paunov.config.annotations.ValidStatus;
import org.nenad.paunov.vo.GameStatus;

import java.util.Set;

public class ValidStatusValidator implements ConstraintValidator<ValidStatus, GameStatus> {
	private static final Set<GameStatus> ALLOWED_STATUSES = Set.of(GameStatus.FINISHED, GameStatus.DROPED);

	public boolean isValid(GameStatus status, ConstraintValidatorContext context) {
		return status == null || ALLOWED_STATUSES.contains(status);
	}
}
