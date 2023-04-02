package org.nenad.paunov.scheduler;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import net.javacrumbs.shedlock.core.LockProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.util.TimeZone;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
@EnableSchedulerLock(defaultLockAtMostFor = "PT5M")
//default amount of time the lock should be kept in case the executing node dies
public class ShedLockSchedulerConfig implements InitializingBean {

	private final DataSource dataSource;

	@Bean
	public LockProvider lockProvider() {
		return new JdbcTemplateLockProvider(
				JdbcTemplateLockProvider.Configuration.builder()
//                        .withTransactionManager(jtaTransactionManager)
						.withJdbcTemplate(new JdbcTemplate(dataSource))
						.withTableName("gameservice.shedlock")
						.withTimeZone(TimeZone.getTimeZone("UTC"))
						.build()
		);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("Schedulers enabled");
	}
}
