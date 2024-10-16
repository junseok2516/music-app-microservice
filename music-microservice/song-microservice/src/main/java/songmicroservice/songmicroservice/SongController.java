package songmicroservice.songmicroservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping
public class SongController {

	@Autowired
	private final SongDal songDal;

	private OkHttpClient client = new OkHttpClient();

	//THis is a practice push
	public SongController(SongDal songDal) {
		this.songDal = songDal;
	}

	/**
	 * This method is partially implemented for you to follow as an example of
	 * how to complete the implementations of methods in the controller classes.
	 * @param songId
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getSongById/{songId}", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getSongById(@PathVariable("songId") String songId,
			HttpServletRequest request) {

		Map<String, Object> response = new HashMap<String, Object>();
		response.put("path", String.format("GET %s", Utils.getUrl(request)));

		DbQueryStatus dbQueryStatus = songDal.findSongById(songId);

		// TODO: uncomment these two lines when you have completed the implementation of findSongById in SongDal
		 response.put("message", dbQueryStatus.getMessage());
		 return Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), dbQueryStatus.getData());
	}

	
	@RequestMapping(value = "/getSongTitleById/{songId}", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getSongTitleById(@PathVariable("songId") String songId,
			HttpServletRequest request) {

		Map<String, Object> response = new HashMap<String, Object>();

		// TODO: add any other values to the map following the example in getSongById
		DbQueryStatus dbQueryStatus = songDal.getSongTitleById(songId);
		response.put("message", dbQueryStatus.getMessage());
//		return ResponseEntity.status(HttpStatus.OK).body(response); // TODO: replace with return statement similar to in getSongById
		return Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), dbQueryStatus.getData());
	}

	
	@RequestMapping(value = "/deleteSongById/{songId}", method = RequestMethod.DELETE)
	public ResponseEntity<Map<String, Object>> deleteSongById(@PathVariable("songId") String songId,
			HttpServletRequest request) {

		Map<String, Object> response = new HashMap<String, Object>();
		// TODO: add any other values to the map following the example in getSongById
		DbQueryStatus dbQueryStatus = songDal.deleteSongById(songId);
		response.put("message", dbQueryStatus.getMessage());
//		return ResponseEntity.status(HttpStatus.OK).body(response); // TODO: replace with return statement similar to in getSongById
		return Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), dbQueryStatus.getData());
	}

	
	@RequestMapping(value = "/addSong", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> addSong(@RequestBody Map<String, String> params,
			HttpServletRequest request) {

		Map<String, Object> response = new HashMap<String, Object>();
		// TODO: add any other values to the map following the example in getSongById
		String songName = params.get("songName");
		String songArtistFullName = params.get("songArtistFullName");
		String songAlbum = params.get("songAlbum");

		Song newSong = new Song(songName, songArtistFullName, songAlbum);

		DbQueryStatus dbQueryStatus = songDal.addSong(newSong);
		response.put("message", dbQueryStatus.getMessage());
		return ResponseEntity.status(HttpStatus.OK).body(response); // TODO: replace with return statement similar to in getSongById
	}

	
	@RequestMapping(value = "/updateSongFavouritesCount", method = RequestMethod.PUT)
	public ResponseEntity<Map<String, Object>> updateFavouritesCount(@RequestBody Map<String, String> params, HttpServletRequest request) {

		Map<String, Object> response = new HashMap<String, Object>();
		// TODO: add any other values to the map following the example in getSongById
		String songId = params.get("songId");
		Boolean shouldDecrement = Boolean.valueOf(params.get("shouldDecrement"));

		DbQueryStatus dbQueryStatus = songDal.updateSongFavouritesCount(songId, shouldDecrement);
		response.put("message", dbQueryStatus.getMessage());

		return ResponseEntity.status(HttpStatus.OK).body(response); // TODO: replace with return statement similar to in getSongById
	}

	@RequestMapping(value = "/getAllFromAlbum/{album}", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getAllFromAlbum(@PathVariable("album") String album, HttpServletRequest request) {

		Map<String, Object> response = new HashMap<String, Object>();

		DbQueryStatus dbQueryStatus = songDal.getAllFromAlbum(album);

		response.put("message", dbQueryStatus.getMessage());
		return Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), dbQueryStatus.getData());
	}

	@RequestMapping(value = "/getTopLikedSongs/{n}", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getTopLikedSongs(@PathVariable("n") Integer n, HttpServletRequest request) {

		Map<String, Object> response = new HashMap<String, Object>();

		DbQueryStatus dbQueryStatus = songDal.getTopLikedSongs(n);

		response.put("message", dbQueryStatus.getMessage());
		return Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), dbQueryStatus.getData());
	}
}