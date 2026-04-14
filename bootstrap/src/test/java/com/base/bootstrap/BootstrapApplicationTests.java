package com.base.bootstrap;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
		properties = {
				"spring.flyway.enabled=false",
				"spring.datasource.url=jdbc:h2:mem:residence_test;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
				"spring.datasource.driver-class-name=org.h2.Driver",
				"spring.datasource.username=sa",
				"spring.datasource.password=",
				"spring.jpa.hibernate.ddl-auto=none",
				"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
		}
)
@ActiveProfiles("test")
class BootstrapApplicationTests {

	@Test
	void contextLoads() {
	}

}
