package profilemicroservice;

import okhttp3.*;
import org.neo4j.driver.v1.*;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public class PlaylistDriverImpl implements PlaylistDriver {

	Driver driver = ProfileMicroserviceApplication.driver;
	OkHttpClient client = new OkHttpClient();

	public static void InitPlaylistDb() {
		String queryStr;

		try (Session session = ProfileMicroserviceApplication.driver.session()) {
			try (Transaction trans = session.beginTransaction()) {
				queryStr = "CREATE CONSTRAINT ON (nPlaylist:playlist) ASSERT exists(nPlaylist.plName)";
				trans.run(queryStr);
				trans.success();
			} catch (Exception e) {
				if (e.getMessage().contains("An equivalent constraint already exists")) {
					System.out.println("INFO: Playlist constraint already exist (DB likely already initialized), should be OK to continue");
				} else {
					// something else, yuck, bye
					throw e;
				}
			}
			session.close();
		}
	}


	@Override
	public DbQueryStatus likeSong(String userName, String songId) {
		DbQueryStatus dbS = new DbQueryStatus("", DbQueryExecResult.QUERY_OK);

		String addToPlaylistQuery = String.format(
						"MATCH (pl:playlist {plName: \"%s-favourites\"})" +
						"MERGE (s:song {songId: \"%s\"})\r\n" +
						"MERGE (pl)-[:includes]->(s)", userName, songId);
		// Check if the user has already liked the song
		DbQueryStatus userLikedStatus = checkUserLikedSong(userName, songId);
		if (userLikedStatus.getdbQueryExecResult() == DbQueryExecResult.QUERY_ERROR_GENERIC) {
			// Handle the error from the checkUserLikedSong method
			dbS.setMessage("Error checking user's like status");
			dbS.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_GENERIC);
			return dbS;
		} else if (userLikedStatus.getData() != null && (boolean) userLikedStatus.getData()) {
			// If the user has already liked the song, return without incrementing the count
			dbS.setMessage("User has already liked the song");
			return dbS;
		}
		try (Session session = driver.session()) {

			StatementResult addResult = session.run(addToPlaylistQuery);
			String favCountUrl = "http://localhost:3001/updateSongFavouritesCount";
			MediaType JSON = MediaType.parse("application/json; charset=utf-8");

			// Create the JSON request body
			String json = "{\"songId\": \"" + songId + "\", \"shouldDecrement\": false}";
			okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(JSON, json);

			// Create the OkHttp request
			Request updateRequest = new Request.Builder()
					.url(favCountUrl)
					.put(requestBody)
					.build();

			// Use OkHttpClient to execute the request
			Response response = client.newCall(updateRequest).execute();

			dbS.setMessage("OK");

			session.close();
		} catch (Exception e) {
			e.printStackTrace();
			dbS.setMessage("Error liking the song: " + e.getMessage());
			dbS.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_GENERIC);
		}

		return dbS;
	}

	@Override
	public DbQueryStatus unlikeSong(String userName, String songId) {
		DbQueryStatus dbS = new DbQueryStatus("", DbQueryExecResult.QUERY_OK);
		String queryStr = String.format(
				"OPTIONAL MATCH (pl:playlist {plName: \"%s-favourites\"})\r\n" +
						"OPTIONAL MATCH (s:song {songId: \"%s\"})\r\n" +
						"OPTIONAL MATCH (pl)-[r:includes]->(s) " + // Ensure there's a LIKE relationship to delete
						"DELETE r", userName, songId);

		try (Session session = driver.session()) {
			StatementResult result = session.run(queryStr);

			if (result.consume().counters().relationshipsDeleted() > 0) {
				String favCountUrl = "http://localhost:3001/updateSongFavouritesCount";
				MediaType JSON = MediaType.parse("application/json; charset=utf-8");

				// Create the JSON request body
				String json = "{\"songId\": \"" + songId + "\", \"shouldDecrement\": true}";
				okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(JSON, json);

				// Create the OkHttp request
				Request updateRequest = new Request.Builder()
						.url(favCountUrl)
						.put(requestBody)
						.build();

				// Use OkHttpClient to execute the request
				OkHttpClient client = new OkHttpClient();
				Response response = client.newCall(updateRequest).execute();

				if (response.isSuccessful()) {
					dbS.setMessage("OK");
				} else {
					dbS.setMessage("Error decrementing the favorite count");
					dbS.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_GENERIC);
				}
			} else {
				dbS.setMessage("Song was not liked");
				dbS.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_GENERIC);
			}
			session.close();

		} catch (Exception e) {
			dbS.setMessage("Error unliking the song: " + e.getMessage());
			dbS.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_GENERIC);
			e.printStackTrace();
		}
		return dbS;
	}



	private DbQueryStatus checkUserLikedSong(String userName, String songId) {
		try (Session session = driver.session()) {
			// Assuming your database query language is Cypher (Neo4j)
			String checkQuery = String.format("MATCH (pl:playlist {plName: \"%s-favourites\"})\r\n" +
					" MATCH (s:song {songId: \"%s\"})\r\n" +
					" MATCH (pl)-[r:includes]->(s) RETURN count(s) > 0 AS liked", userName, songId);


			StatementResult result = session.run(checkQuery);

			if (result.hasNext()) {
				org.neo4j.driver.v1.Record record = result.next();
				boolean alreadyLiked = record.get("liked").asBoolean();
				DbQueryStatus likeStatus = new DbQueryStatus("User like status checked", DbQueryExecResult.QUERY_OK);
				likeStatus.setData(alreadyLiked);
				return likeStatus;
			} else {
				// User hasn't liked the song
				DbQueryStatus likeStatus = new DbQueryStatus("User like status checked", DbQueryExecResult.QUERY_OK);
				likeStatus.setData(false);
				return likeStatus;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Error occurred while checking user's like status
			DbQueryStatus likeStatus = new DbQueryStatus("Error checking user's like status: " + e.getMessage(), DbQueryExecResult.QUERY_ERROR_GENERIC);
			return likeStatus;
		}
	}
}

