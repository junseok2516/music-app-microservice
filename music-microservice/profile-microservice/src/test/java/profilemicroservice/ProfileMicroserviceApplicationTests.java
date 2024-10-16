package profilemicroservice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import profilemicroservice.DbQueryStatus;
import profilemicroservice.PlaylistDriver;
import profilemicroservice.PlaylistDriverImpl;


import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProfileMicroserviceApplicationTests {

	@Test
	public void testSongLike() {
		String id = "65660e857c3a58302b407269";
		PlaylistDriver pd = new PlaylistDriverImpl();

		DbQueryStatus db =  pd.likeSong("Jeff", id);

		assertEquals(db.getMessage(), "Not null");
	}


}
