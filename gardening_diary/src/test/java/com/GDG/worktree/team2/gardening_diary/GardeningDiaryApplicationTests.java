package com.GDG.worktree.team2.gardening_diary;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
    "firebase.service-account-file=classpath:diarygarden-7bb2d-firebase-adminsdk-fbsvc-eb51c377a7.json"
})
class GardeningDiaryApplicationTests {

	@Test
	void contextLoads() {
	}

}
