package profilemicroservice;

public interface PlaylistDriver {
	DbQueryStatus likeSong(String userName, String songId);
	DbQueryStatus unlikeSong(String userName, String songId);
}