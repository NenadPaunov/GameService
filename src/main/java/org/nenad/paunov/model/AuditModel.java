package org.nenad.paunov.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class AuditModel {

	@Column(name="created_at", nullable=false, updatable=false)
	@CreatedDate
	private LocalDateTime createdAt;

	@Column(name="updated_at", nullable=false)
	@LastModifiedDate
	private LocalDateTime updatedAt;

}
