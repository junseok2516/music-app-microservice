package profilemicroservice;

import okhttp3.*;
import okhttp3.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.*;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping
public class ProfileController {
	public static final String KEY_USER_NAME = "userName";
	public static final String KEY_USER_FULLNAME = "fullName";
	public static final String KEY_USER_PASSWORD = "password";
	public static final String KEY_FRIEND_USER_NAME = "friendUserName";
	public static final String KEY_SONG_ID = "songId";



	@Autowired
	private final ProfileDriverImpl profileDriver;

	@Autowired
	private final PlaylistDriverImpl playlistDriver;

	static OkHttpClient client = new OkHttpClient();

	public ProfileController(ProfileDriverImpl profileDriver, PlaylistDriverImpl playlistDriver) {
		this.profileDriver = profileDriver;
		this.playlistDriver = playlistDriver;
	}

	@RequestMapping(value = "/profile", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> addProfile(@RequestBody Map<String, String> params, HttpServletRequest request) {

		Map<String, Object> response = new HashMap<String, Object>();

		String userName = "", fullName = "", password = "";

		if (!params.keySet().contains("userName") || !params.keySet().contains("fullName") || !params.keySet().contains("password")) {
			// insert error here
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		} else {
			userName = params.get("userName");
			fullName = params.get("fullName");
			password = params.get("password");
		}

		DbQueryStatus dbQueryStatus = profileDriver.createUserProfile(userName, fullName, password);

		// TODO: uncomment these two lines when you have completed the implementation of findSongById in SongDal
		response.put("message", dbQueryStatus.getMessage());
		return Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), dbQueryStatus.getData());

//		return ResponseEntity.status(HttpStatus.OK).body(response); // TODO: replace with return statement similar to in getSongById
	}

	@RequestMapping(value = "/followFriend", method = RequestMethod.PUT)
	public ResponseEntity<Map<String, Object>> followFriend(@RequestBody Map<String, String> params, HttpServletRequest request) {

		Map<String, Object> response = new HashMap<String, Object>();
		// TODO: add any other values to the map following the example in SongController.getSongById

		String userName = "", friendUserName = "";

		if (!params.keySet().contains("userName") || !params.keySet().contains("friendUserName")) {
			// insert error here
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		} else {
			userName = params.get("userName");
			friendUserName = params.get("friendUserName");
		}

		DbQueryStatus dbQueryStatus = profileDriver.followFriend(userName, friendUserName);
		response.put("message", dbQueryStatus.getMessage());

		return Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), dbQueryStatus.getData());
	}

	@RequestMapping(value = "/getAllFriendFavouriteSongTitles/{userName}", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getAllFriendFavouriteSongTitles(@PathVariable("userName") String userName,
																			   HttpServletRequest request) {

		Map<String, Object> response = new HashMap<String, Object>();
		// TODO: add any other values to the map following the example in SongController.getSongById

		DbQueryStatus dbQueryStatus = profileDriver.getAllSongFriendsLike(userName);
		response.put("message", dbQueryStatus.getMessage());

		return Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), dbQueryStatus.getData());

//		return ResponseEntity.status(HttpStatus.OK).body(response); // TODO: replace with return statement similar to in getSongById
	}


	@RequestMapping(value = "/unfollowFriend", method = RequestMethod.PUT)
	public ResponseEntity<Map<String, Object>> unfollowFriend(@RequestBody Map<String, String> params, HttpServletRequest request) {

		Map<String, Object> response = new HashMap<String, Object>();
		// TODO: add any other values to the map following the example in SongController.getSongById

		String userName = "", friendUserName = "";

		if (!params.keySet().contains("userName") || !params.keySet().contains("friendUserName")) {
			// insert error here
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		} else {
			userName = params.get("userName");
			friendUserName = params.get("friendUserName");
		}

		DbQueryStatus dbQueryStatus = profileDriver.unfollowFriend(userName, friendUserName);
		response.put("message", dbQueryStatus.getMessage());

		return Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), dbQueryStatus.getData());
	}

	@RequestMapping(value = "/likeSong", method = RequestMethod.PUT)
	public ResponseEntity<Map<String, Object>> likeSong(@RequestBody Map<String, String> params, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();

		String userName = params.getOrDefault("userName", "");
		String songId = params.getOrDefault("songId", "");

		if (songId.isEmpty()) {
			response.put("status", "No song ID provided");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		// Check if the songId is valid using getSongName method
		String songName = getSongName(songId);
		if (songName == null) {
			response.put("status", "Invalid song ID");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		// Call the PlaylistDriverImpl's likeSong method to handle song liking logic
		DbQueryStatus likeStatus = playlistDriver.likeSong(userName, songId);

		if (likeStatus.getdbQueryExecResult() == DbQueryExecResult.QUERY_OK) {
			response.put("status", likeStatus.getMessage());
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} else {
			response.put("status", "Error liking the song");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@RequestMapping(value = "/unlikeSong", method = RequestMethod.PUT)
	public ResponseEntity<Map<String, Object>> unlikeSong(@RequestBody Map<String, String> params, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();

		String userName = params.getOrDefault("userName", "");
		String songId = params.getOrDefault("songId", "");

		// Validate inputs
		if (userName.isEmpty() || songId.isEmpty()) {
			response.put("status", "Missing userName or songId");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		// Call method from PlaylistDriverImpl to unlike the song for the user
		DbQueryStatus dbQueryStatus = playlistDriver.unlikeSong(userName, songId);
		response.put("status", dbQueryStatus.getMessage());

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	public static String getSongName(String id) {
		String url = "http://localhost:3001/getSongTitleById/" + id;
		System.out.println(url);
		Request request = new Request.Builder().url(url).build();
		Call call = client.newCall(request);
		try {
			Response response = call.execute();
			if(response.code() == 404)
			{
				return null;
			} else {
				JSONObject jo = new JSONObject(response.body().string());
				return jo.getString("data");
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}
}
