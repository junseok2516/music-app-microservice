package songmicroservice.songmicroservice;

public interface SongDal {
	DbQueryStatus addSong(Song songToAdd);
	DbQueryStatus findSongById(String songId);
	DbQueryStatus getSongTitleById(String songId);
	DbQueryStatus deleteSongById(String songId);	
	DbQueryStatus updateSongFavouritesCount(String songId, boolean shouldDecrement);

	// add queries regarding two features
	DbQueryStatus getAllFromAlbum(String album); // get all info by album
	DbQueryStatus getTopLikedSongs(int n); // get top n song(s)
}
