package profilemicroservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

import org.springframework.stereotype.Repository;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.exceptions.ServiceUnavailableException;

@Repository
public class ProfileDriverImpl implements ProfileDriver {

	Driver driver = ProfileMicroserviceApplication.driver;

	public static void InitProfileDb() {
		String queryStr;

		try (Session session = ProfileMicroserviceApplication.driver.session()) {
			try (Transaction trans = session.beginTransaction()) {
				queryStr = "CREATE CONSTRAINT ON (nProfile:profile) ASSERT exists(nProfile.userName)";
				trans.run(queryStr);

				queryStr = "CREATE CONSTRAINT ON (nProfile:profile) ASSERT exists(nProfile.password)";
				trans.run(queryStr);

				queryStr = "CREATE CONSTRAINT ON (nProfile:profile) ASSERT nProfile.userName IS UNIQUE";
				trans.run(queryStr);

				trans.success();
			} catch (Exception e) {
				if (e.getMessage().contains("An equivalent constraint already exists")) {
					System.out.println("INFO: Profile constraints already exist (DB likely already initialized), should be OK to continue");
				} else {
					// something else, yuck, bye
					throw e;
				}
			}
			session.close();
		}
	}
	
	//Need to remove the function if the profile already exists
	
	@Override
	public DbQueryStatus createUserProfile(String userName, String fullName, String password) {
		String queryStr = String.format(
				  "MERGE (p:profile {userName: \"%s\", fullName: \"%s\", password: \"%s\"})\r\n"
				+ "MERGE (pl:playlist {plName: \"%s-favourites\"})\r\n"
				+ "MERGE (p)-[:created]-(pl)",
				userName, fullName, password, userName);
		
		DbQueryStatus status = null;
		
		try (Session session = driver.session()) {
			StatementResult result = session.run(queryStr);
			
			status = new DbQueryStatus("Created new profile", DbQueryExecResult.QUERY_OK);
			
			Map<String, String> data = new HashMap<>();
			
			data.put("userName", userName);
			data.put("fullName", fullName);
			data.put("password", password);
			status.setData(data);
			
			session.close();
		}	catch (ServiceUnavailableException e) {
			status = new DbQueryStatus(e.toString(), DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
			System.out.println(e);
		} catch(Exception e) {
			status = new DbQueryStatus(e.toString(), DbQueryExecResult.QUERY_ERROR_GENERIC);
			System.out.println(e);
		}
		
		return status;
	}

	//p1 does not exist
	//p2 does not exist
	// if it already has a reslationship, don't make another
	//ServiceUnavailable
	//Later implement RETURN p1, p2
	
	@Override
	public DbQueryStatus followFriend(String userName, String frndUserName) {
		
		DbQueryStatus status = null;
		
		//Check if userName and frndUserName exist in DB
		if(!doesUserExist(userName)) {
			return new DbQueryStatus(userName + " does not exist in database", DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
		}
		if(!doesUserExist(frndUserName)) {
			return new DbQueryStatus(userName + " does not exist in database", DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
		}
		
		//Check if relationship already exists
		if(doesFollowRelExist(userName, frndUserName)) {
			return new DbQueryStatus(userName + " already follows " + frndUserName, DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
		}
		
		String queryStr = String.format(
				  "MATCH (p1:profile), (p2:profile)\n"
				+ "WHERE p1.userName = \"%s\" AND p2.userName = \"%s\"\n"
				+ "CREATE (p1)-[:follows]->(p2)\n"
				+ "RETURN p1,p2",
				userName, frndUserName);
		
		try (Session session = driver.session()) {
			StatementResult result = session.run(queryStr);
			
//			System.out.println("-");
			while(result.hasNext()) {
				System.out.println(result.next().toString());
			}
//			System.out.println("-");
			
			status = new DbQueryStatus(userName + " now follows " + frndUserName, DbQueryExecResult.QUERY_OK);
			//I'm not sure what data to send so I put the summary...
//			status.setData(result.summary().toString());
			session.close();
			
		}	catch (ServiceUnavailableException e) {
			status = new DbQueryStatus(e.toString(), DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
			System.out.println(e);
		} catch(Exception e) {
			status = new DbQueryStatus(e.toString(), DbQueryExecResult.QUERY_ERROR_GENERIC);
			System.out.println(e);
		}
		
		return status;
	}
	
	private boolean doesUserExist(String userName) {
		String queryStr = String.format(
				"OPTIONAL MATCH (p1:profile {userName: \"%s\"})\n"
				+ "RETURN p1", userName);
	
		try (Session session = driver.session()) {
			StatementResult result = session.run(queryStr);
			
			if(result.hasNext()) {
				String temp = result.next().get("p1").toString();
				
				if(!temp.equalsIgnoreCase("NULL")) {
					return true;
				}
			}
			session.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return false;
	}
	
	//Checks if follow relationship exists
	private boolean doesFollowRelExist(String userName, String frndUserName) {
		String queryStr = String.format(
				"RETURN EXISTS ((:profile {userName: \"%s\"})-[:follows]->(:profile {userName: \"%s\"}))",
				userName, frndUserName);
		
		try (Session session = driver.session()) {
			StatementResult result = session.run(queryStr);
			
			if(result.hasNext()) {
				String temp = result.next().get(0).toString();
				
				if(!temp.equalsIgnoreCase("FALSE")) {
					return true;
				}
			}
			session.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return false;
	}
	
	// Check if they follow each other
	
	@Override
	public DbQueryStatus unfollowFriend(String userName, String frndUserName) {
		
		DbQueryStatus status = null;
		
		String queryStr = "";
		
		if(!doesFollowRelExist(userName, frndUserName)) {
			status =  new DbQueryStatus(userName + " does not follow " + frndUserName, DbQueryExecResult.QUERY_OK);
		} else {
			try(Session session = driver.session()) {
				
				queryStr = String.format(
						"MATCH (p1:profile {userName: \"%s\"})-[f:follows]->(p2:profile {userName: \"%s\"})\n"
						+ "DELETE f",
						userName, frndUserName);
				System.out.println(queryStr);
				
				StatementResult result = session.run(queryStr);
				
				status = new DbQueryStatus(userName + " no longer follows " + frndUserName, DbQueryExecResult.QUERY_OK);
				session.close();
			}	catch (ServiceUnavailableException e) {
				status = new DbQueryStatus(e.toString(), DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
				System.out.println(e);
			} catch(Exception e) {
				status = new DbQueryStatus(e.toString(), DbQueryExecResult.QUERY_ERROR_GENERIC);
				System.out.println(e);
			}
		}
		
		return status;
	}
	
	// check if userName exists
	
	@Override
	public DbQueryStatus getAllSongFriendsLike(String userName) {
		
		DbQueryStatus status = null;
		Map<String, List<String>> data = new HashMap<>();
		
		String queryStr = "";
		
		// List all the friends
		List<String> friends = getFriends(userName);
		
		try(Session session = driver.session()) {
			try (Transaction trans = session.beginTransaction()) {
				for(String f : friends) {
					List<String> temp = new ArrayList<>();
					queryStr = String.format(
							  	"MATCH (pl:playlist {plName: \"%s-favourites\"})-[l:includes]-(song)\n"
							  + "RETURN song.songId",
							f);
					
					StatementResult result = trans.run(queryStr);
					
					while(result.hasNext()) {
//						temp.add(result.next().get("songs.name").toString().replace("\"", ""));
						String songID = result.next().get("song.songId").toString().replace("\"", "");
						String songName = ProfileController.getSongName(songID); 
						if(songName != null) {
							temp.add(songName);
						}
					}
					
					data.put(f, temp);
				}
				trans.success();
			} catch (Exception e) {
				System.out.println(e);
			}
			
			
			status = new DbQueryStatus("All friend's favourite songs", DbQueryExecResult.QUERY_OK);
			status.setData(data);
			
			session.close();
		} catch (ServiceUnavailableException e) {
			status = new DbQueryStatus(e.toString(), DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
			System.out.println(e);
		} catch(Exception e) {
			status = new DbQueryStatus(e.toString(), DbQueryExecResult.QUERY_ERROR_GENERIC);
			System.out.println(e);
		}
		
		return status;
	}
	
	private List<String> getFriends(String userName) {
		
		List<String> friends = new ArrayList<>();
		
		String queryStr = "";
		
		try(Session session = driver.session()) {
			
			// List all the songs of a person
			queryStr = String.format(
					  "MATCH (p:profile {userName: \"%s\"})-[:follows]-(friends)\n"
					  + "RETURN friends.userName",
					userName);
			
			StatementResult result = session.run(queryStr);
			
			while(result.hasNext()) {
				friends.add(result.next().get("friends.userName").toString().replace("\"", ""));
			}
		
			session.close();
			
		} catch (ServiceUnavailableException e) {
			System.out.println("Error: " + e);
		} catch(Exception e) {
			System.out.println("Error: " + e);
		}
		return friends;
	}
}
