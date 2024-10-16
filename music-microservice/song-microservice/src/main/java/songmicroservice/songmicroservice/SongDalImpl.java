package songmicroservice.songmicroservice;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.zip.ZipEntry;

@Repository
public class SongDalImpl implements SongDal {

	private final MongoTemplate db;
	private Object data;

	@Autowired
	public SongDalImpl(MongoTemplate mongoTemplate) {
		this.db = mongoTemplate;
	}

	@Override
	public DbQueryStatus addSong(Song songToAdd) {
		DbQueryStatus dbS = new DbQueryStatus("added song", DbQueryExecResult.QUERY_OK);
		try {
			Song addedSong = db.save(songToAdd);
			dbS.setData(addedSong);
		} catch (Exception e) {
			dbS.setMessage("could not add song");
			dbS.setdbQueryExecResult( DbQueryExecResult.QUERY_ERROR_GENERIC);
		}
		return dbS;
	}

	@Override
	public DbQueryStatus findSongById(String songId) {
		DbQueryStatus dbS = new DbQueryStatus("found song", DbQueryExecResult.QUERY_OK);
		try {
			Song res = db.findById(songId, Song.class);
			if (res != null) {
				dbS.setData(res);
			} else {
				dbS.setMessage("could not find the song");
				dbS.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
			}
		} catch (Exception e) {
			dbS.setMessage("error finding song");
			System.out.println("e: " + e);
			dbS.setdbQueryExecResult( DbQueryExecResult.QUERY_ERROR_GENERIC);
		}
		return dbS;
	}

	@Override
	public DbQueryStatus getSongTitleById(String songId) {
		DbQueryStatus dbS = new DbQueryStatus("", DbQueryExecResult.QUERY_OK);
		// TODO Auto-generated method stub
		try {
			Song res = db.findById(songId, Song.class);
			if (res != null) {
				dbS.setMessage(res.getSongName());
				dbS.setData(res.getSongName()); // should this be the entire song or just the title?
			} else {
				dbS.setMessage("Not found");
				dbS.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
			}
		} catch (Exception e) {
			dbS.setMessage("error finding song");
			dbS.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_GENERIC);
		}
		return dbS;
	}

	@Override
	public DbQueryStatus deleteSongById(String songId) {
		DbQueryStatus dbS = new DbQueryStatus("song was deleted", DbQueryExecResult.QUERY_OK);

//		first find since we can only delete by object itself
		DbQueryStatus find = findSongById(songId);

		if(find.getdbQueryExecResult() == DbQueryExecResult.QUERY_ERROR_NOT_FOUND) {
			dbS.setMessage("Could not find song");
			dbS.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
		}
		else if(find.getdbQueryExecResult() == DbQueryExecResult.QUERY_ERROR_GENERIC) {
			dbS.setMessage("Could not delete song due to error in finding song in database");
			dbS.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_GENERIC);
		}else{
			try{
				DeleteResult res = db.remove(find.getData());
				if(!res.wasAcknowledged()) {
					dbS.setMessage("delete did not occur");
					dbS.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_GENERIC);
				}
			}catch (Exception e){
				dbS.setMessage("delete did not occur");
				dbS.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_GENERIC);
			}
		}
		return dbS;
	}

	@Override
	public DbQueryStatus updateSongFavouritesCount(String songId, boolean shouldDecrement) {
		DbQueryStatus dbS = new DbQueryStatus("song favourites count was incremented", DbQueryExecResult.QUERY_OK);

//		first find object then try to update it
		DbQueryStatus find = findSongById(songId);

		if(find.getdbQueryExecResult() == DbQueryExecResult.QUERY_ERROR_NOT_FOUND) {
			dbS.setMessage("Could not find song to update");
			dbS.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
		}
		else if(find.getdbQueryExecResult() == DbQueryExecResult.QUERY_ERROR_GENERIC) {
			dbS.setMessage("Error, could not update favourites count");
			dbS.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_GENERIC);
		}else{

			try {
				Query query = new Query(Criteria.where("_id").is(songId));
				Song s = (Song) find.getData();
				Update update;
				if (shouldDecrement) {
					if(s.getSongAmountFavourites() > 0) {
						dbS.setMessage("song favourites count was decremented");
						s.setSongAmountFavourites(s.getSongAmountFavourites() - 1);
					}
					else if(s.getSongAmountFavourites() == 0)dbS.setMessage("song favourites count is already 0");
				} else {
					s.setSongAmountFavourites(s.getSongAmountFavourites() + 1);
				}
				update = new Update().set("songAmountFavourites", s.getSongAmountFavourites());
				UpdateResult res = db.updateFirst(query, update, Song.class);

				if (!res.wasAcknowledged()) {
					dbS.setMessage("error, could not update favourites count");
					dbS.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_GENERIC);
				} else {
//					filling the get data so we have object to check update on
					find = findSongById(songId);
					dbS.setData(find.getData());
				}

			}catch (Exception E) {
				dbS.setMessage("error, could not update favourites count");
				dbS.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_GENERIC);
			}
		}
		return dbS;
	}

	@Override
	public DbQueryStatus getAllFromAlbum(String album) {
		String tmpStr = "Song(s) by Album: " + album;
		DbQueryStatus dbS = new DbQueryStatus(tmpStr, DbQueryExecResult.QUERY_OK);

		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("songAlbum").is(album));
			List<Song> songs = db.find(query, Song.class);
			if (!songs.isEmpty()) {
//				StringBuilder messageBuilder = new StringBuilder();
//				for (Song song : songs) {
////					messageBuilder.append(song.getId()).append(" ");
//					messageBuilder.append("SongName: ").append(song.getSongName()).append(" ");
//					messageBuilder.append("Artist: ").append(song.getSongArtistFullName()).append(" ");
//					messageBuilder.append("Album: ").append(song.getSongAlbum()).append(" ");
//					messageBuilder.append("Liked: ").append(song.getSongAmountFavourites()).append("\n");
//				}
//				dbS.setMessage(messageBuilder.toString());
				dbS.setData(songs);
			} else {
				dbS.setMessage("Not found");
				dbS.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
			}
		} catch (Exception e) {
			dbS.setMessage("error finding album");
			dbS.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_GENERIC);
		}
		return dbS;
	}

	@Override
	public DbQueryStatus getTopLikedSongs(int n) {
		String tmpStr = "Top " + n + " song(s)";
		DbQueryStatus dbS = new DbQueryStatus(tmpStr, DbQueryExecResult.QUERY_OK);


		Query query = new Query();
		query.with(Sort.by(Sort.Order.desc("songAmountFavourites"))).limit(n);
		List<Song> topOrderSongs = db.find(query, Song.class);

		if (n == 0) {
			dbS.setMessage("The input should be greater than 0");
			dbS.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_GENERIC);
		} else if (n > topOrderSongs.size()) {
			dbS.setMessage("The input should be less than the total songs(" + topOrderSongs.size() + ")");
			dbS.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_GENERIC);
		} else {
			try {
				if (!topOrderSongs.isEmpty()) {
					//				StringBuilder messageBuilder = new StringBuilder();
					//
					//				for (int i = 0; i < topOrderSongs.size(); i++) {
					//					messageBuilder.append("Top " + i + ": ").append(topOrderSongs.get(i).getSongName()).append(" ");
					//					messageBuilder.append("Liked: ").append(topOrderSongs.get(i).getSongAmountFavourites()).append("\n");
					//				}
					//				dbS.setMessage(messageBuilder.toString());
					dbS.setData(topOrderSongs);
				} else {
					dbS.setMessage("No Songs found");
					dbS.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
				}
			} catch (Exception e) {
				dbS.setMessage("Error finding song");
				dbS.setdbQueryExecResult(DbQueryExecResult.QUERY_ERROR_GENERIC);
			}
		}
		return dbS;
	}
}