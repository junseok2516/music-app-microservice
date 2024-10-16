package songmicroservice.songmicroservice;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import songmicroservice.songmicroservice.DbQueryStatus;
import songmicroservice.songmicroservice.Song;
import songmicroservice.songmicroservice.SongDal;
import songmicroservice.songmicroservice.SongDalImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SongMicroserviceApplicationTests {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Before
	public void cleanUpDB() {
		mongoTemplate.getDb().drop();
	}

//
//	public SongMicroserviceApplicationTests(MongoTemplate mongoTemplate) {
//		this.mongoTemplate = mongoTemplate;
//	}
//	@Test
//	public void addSong() {
//		Song s = new Song("Black and Yellow", "Black Eyed Peas", "Colour");
////		ObjectId id = new ObjectId("5d61728193528481fe5a3127");
////		s.setId(id);
//		s.setSongAmountFavourites(3);
//		SongDal sd = new SongDalImpl(mongoTemplate);
//		DbQueryStatus res = sd.addSong(s);
//		assertEquals(res.getMessage(), "added song");
//	}

//	@Test
//	public void findSong() {
////		this cannot be a global variable, needs to be reinstantiated for each test case
//		SongDal sd = new SongDalImpl(mongoTemplate);
//		String id = "65629392c876b8a46690afdd";
//		DbQueryStatus res = sd.findSongById(id);
//		Song song = (Song)res.getData();
//		assertEquals(res.getMessage(), "found song");
////		assertEquals(song.getSongName(), "Kill Bill");
//	}

//	@Test
//	public void findSong2() {
//		 SongDal sd = new SongDalImpl(mongoTemplate);
//		String id = "5d620f54d78b833e34e65b48";
//		DbQueryStatus res = sd.findSongById(id);
//		Song song = (Song)res.getData();
//		assertEquals(res.getMessage(), "found song");
////		assertEquals(song.getSongName(), "Kill Bill");
//		assertEquals(song.getSongName(), "Mine (Taylor's Version)");
//		assertEquals(song.getSongAmountFavourites(), 99);
//	}


//	@Test
//	public void songNotInDB() {
//		SongDal sd = new SongDalImpl(mongoTemplate);
//		String id = "ZZZZZZZZZZZZ";
//		DbQueryStatus res = sd.findSongById(id);
//		Song song = (Song)res.getData();
//		assertEquals(song, null);
//	}
//	@Test
//	public void delete() {
//		SongDal sd = new SongDalImpl(mongoTemplate);
//		String id = "65629392c876b8a46690afdd";
//		DbQueryStatus res = sd.deleteSongById(id);
//		assertEquals(res.getMessage(), "song was deleted");
//	}

//	@Test
//	public void notDeleteNotFound() {
//		SongDal sd = new SongDalImpl(mongoTemplate);
//		String id = "ZZZZZZZZ";
//		DbQueryStatus res = sd.deleteSongById(id);
//		assertEquals(res.getMessage(), "Could not find song");
//	}

//	@Test
//	public void getSongByTitle() {
//		String id = "5d61728193528481fe5a3127";
//		SongDal sd = new SongDalImpl(mongoTemplate);
//		DbQueryStatus res = sd.getSongTitleById(id);
//		assertEquals(res.getMessage(), "castle in the sky");
//	}

//	@Test
//	public void getSongByTitleNotFound() {
//		String id = "5d61728193528481fe5a3127dddd";
//		SongDal sd = new SongDalImpl(mongoTemplate);
//		DbQueryStatus res = sd.getSongTitleById(id);
//		assertEquals(res.getMessage(), "Not found");
//	}

//	@Test
//	public void updateSongIncrement() {
//		String id = "5d620f54d78b833e34e65b48";
//		SongDal sd = new SongDalImpl(mongoTemplate);
//		DbQueryStatus res = sd.updateSongFavouritesCount (id, false);
//		Song s = (Song) res.getData();
//		assertEquals(s.getSongAmountFavourites(), 99);
//	}

//	@Test
//	public void updateSongIncrementNotFounf() {
//		String id = "5d620f54d78b833e34e65b48ddddd";
//		SongDal sd = new SongDalImpl(mongoTemplate);
//		DbQueryStatus res = sd.updateSongFavouritesCount (id, false);
////		Song s = (Song) res.getData();
//		assertEquals(res.getMessage(), "Could not find song to update");
//
//	}


//	@Test
//	public void getSongByTitle() {
//		String id = "5d61728193528481fe5a3127";
//		SongDal sd = new SongDalImpl(mongoTemplate);
//		DbQueryStatus res = sd.getSongTitleById(id);
//		assertEquals(res.getMessage(), "castle in the sky");
//	}
//
//	@Test
//	public void getSongByTitleNotFound() {
//		String id = "5d61728193528481fe5a3127dddd";
//		SongDal sd = new SongDalImpl(mongoTemplate);
//		DbQueryStatus res = sd.getSongTitleById(id);
//		assertEquals(res.getMessage(), "Not found");
//	}

//	@Test
//	public void updateSongIncrement() {
//		String id = "65660e857c3a58302b407269";
//		SongDal sd = new SongDalImpl(mongoTemplate);
//		DbQueryStatus res = sd.updateSongFavouritesCount (id, false);
//		Song s = (Song) res.getData();
//		assertEquals(s.getSongAmountFavourites(), 1);
//	}
//	@Test
//	public void updateSongIncrement2() {
//		String id = "6563de4fd0a3f86e4da2a390";
//		SongDal sd = new SongDalImpl(mongoTemplate);
//		DbQueryStatus res = sd.updateSongFavouritesCount (id, true);
//		Song s = (Song) res.getData();
//		assertEquals(s.getSongAmountFavourites(), 8);
//	}
//
//	@Test
//	public void updateSongIncrementNotFound() {
//		String id = "5d620f54d78b833e34e65b48ddddd";
//		SongDal sd = new SongDalImpl(mongoTemplate);
//		DbQueryStatus res = sd.updateSongFavouritesCount (id, false);
////		Song s = (Song) res.getData();
//		assertEquals(res.getMessage(), "Could not find song to update");
//
//	}

//	@Test
//	public void testGetEntityById() {
//		// Assuming you have an existing entity with a known ID in the test database
////		String entityId = "5d61728193528481fe5a3122";
//////		YourEntity entity = SongController.findSongById(entityId);
////		Object res = SongDalImpl.findSongById(entityId);
////		YourEntity entity = yourEntityService.getEntityById(entityId);
//
//		//assertNotNull(entity, "Entity should not be null");
//		// Add more assertions based on your expectations
//
//
//
//
//	}

	@Test
	public void testGetAllFromAlbumFound() {
		Song s = new Song("Get You (feat. Kali Uchis)", "Daniel Caesar", "Freudian");
		Song s1 = new Song("Best Part (feat. H.E.R.)", "Daniel Caesar", "Freudian");
		Song s2 = new Song("We Find Love", "Daniel Caesar", "Freudian");
		Song s3 = new Song("Honesty", "Pink Sweat$", "Volume 1 - EP");
		SongDal sd = new SongDalImpl(mongoTemplate);
		sd.addSong(s);sd.addSong(s1);sd.addSong(s2);sd.addSong(s3);
		DbQueryStatus res = sd.getAllFromAlbum("Freudian");
		assertEquals(res.getMessage(), "Song(s) by Album: "+ "Freudian");
	}

	@Test
	public void testGetAllFromAlbumNotFound() {
		Song s = new Song("Get You (feat. Kali Uchis)", "Daniel Caesar", "Freudian");
		Song s1 = new Song("Best Part (feat. H.E.R.)", "Daniel Caesar", "Freudian");
		Song s2 = new Song("We Find Love", "Daniel Caesar", "Freudian");
		Song s3 = new Song("Honesty", "Pink Sweat$", "Volume 1 - EP");
		SongDal sd = new SongDalImpl(mongoTemplate);
		sd.addSong(s);sd.addSong(s1);sd.addSong(s2);sd.addSong(s3);
		DbQueryStatus res = sd.getAllFromAlbum("Zero");
		assertEquals(res.getMessage(), "Not found");
	}

	@Test
	public void testGetTopLikedSongs_Found() {
		Song s = new Song("Get You (feat. Kali Uchis)", "Daniel Caesar", "Freudian");
		Song s1 = new Song("Best Part (feat. H.E.R.)", "Daniel Caesar", "Freudian");
		Song s2 = new Song("We Find Love", "Daniel Caesar", "Freudian");
		s.setSongAmountFavourites(30); s1.setSongAmountFavourites(20); s2.setSongAmountFavourites(10);
		SongDal sd = new SongDalImpl(mongoTemplate);
		sd.addSong(s);sd.addSong(s1);sd.addSong(s2);
		DbQueryStatus res = sd.getTopLikedSongs(3);
		assertEquals(res.getMessage(), "Top 3 song(s)");
	}

	@Test
	public void testGetTopLikedSongs_N_Zero() {
		Song s = new Song("Get You (feat. Kali Uchis)", "Daniel Caesar", "Freudian");
		Song s1 = new Song("Best Part (feat. H.E.R.)", "Daniel Caesar", "Freudian");
		Song s2 = new Song("We Find Love", "Daniel Caesar", "Freudian");
		s.setSongAmountFavourites(30); s1.setSongAmountFavourites(20); s2.setSongAmountFavourites(10);
		SongDal sd = new SongDalImpl(mongoTemplate);
		sd.addSong(s);sd.addSong(s1);sd.addSong(s2);
		DbQueryStatus res = sd.getTopLikedSongs(0);
		assertEquals(res.getMessage(), "The input should be greater than 0");
	}

	@Test
	public void testGetTopLikedSongs_N_Greater() {
		Song s = new Song("Get You (feat. Kali Uchis)", "Daniel Caesar", "Freudian");
		Song s1 = new Song("Best Part (feat. H.E.R.)", "Daniel Caesar", "Freudian");
		Song s2 = new Song("We Find Love", "Daniel Caesar", "Freudian");
		s.setSongAmountFavourites(30); s1.setSongAmountFavourites(20); s2.setSongAmountFavourites(10);
		SongDal sd = new SongDalImpl(mongoTemplate);
		sd.addSong(s);sd.addSong(s1);sd.addSong(s2);
		DbQueryStatus res = sd.getTopLikedSongs(10);
		assertEquals(res.getMessage(), "The input should be less than the total songs(3)");
	}

}
