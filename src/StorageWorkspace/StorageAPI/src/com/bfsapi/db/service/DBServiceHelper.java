package com.bfsapi.db.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bfsapi.db.connpool.ConnectionPool;
import com.bfsapi.db.model.ScssGroup;
import com.bfsapi.db.model.ScssObject;
import com.bfsapi.db.model.ScssUser;

public class DBServiceHelper {
	private static ConnectionPool connPool;
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	static {
		if (connPool == null) {
			connPool = new ConnectionPool("/db.properties");
		}
	}
	private static DBServiceHelper DBHelper;

	public static void putObject(String key, Long BFS_File, Long Owner_ID,
			Long Bucket_ID, String Meta, Long Size, String Media_Type,
			boolean Version_enabled, String Version, boolean Deleted,
			Date Expiration_time, Date Create_time, Date Modify_time) {
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = connPool.getConnection();
			//TODO :
			String sql = "insert into scss_object(`Key`," + "`BFS_File`,"
					+ "`Owner_ID`," + "`Bucket_ID`," + "`Meta`," + "`Size`,"
					+ "`Media_Type`," + "`Version_enabled`,"
					+ "`Version`,`Deleted`," + "`Expiration_time`,"
					+ "`Create_time`," + "`Modify_time`) " + "values ('"
					+ key
					+ "',"
					+ BFS_File
					+ ","
					+ Owner_ID
					+ ","
					+ Bucket_ID
					+ ",'"
					+ Meta
					+ "',"
					+ Size
					+ ",'"
					+ Media_Type
					+ "',"
					+ Version_enabled
					+ ",'"
					+ Version
					+ "',"
					+ Deleted
					+ ",'"
					+ dateFormat.format(Expiration_time)
					+ "','"
					+ dateFormat.format(Create_time)
					+ "','"
					+ dateFormat.format(Modify_time) + "')";
			stmt = connection.prepareStatement(sql);
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void putObject(String key, Long BFS_File, Long Owner_ID,
			Long Bucket_ID, String Meta, Long Size, String Media_Type) {
		putObject(key, BFS_File, Owner_ID, Bucket_ID, "Meta", 1024l,
				Media_Type, false, "v1.0", true, new Date(System
						.currentTimeMillis()
						+ 1000 * 60 * 60 * 24 * 7), new Date(), new Date());
	}

	public static ScssObject getObject(Long BFS_File) {
		Connection connection = null;
		Statement stmt = null;
		ScssObject so = new ScssObject();
		try {
			connection = connPool.getConnection();
			String sql = "select `ID`, `Key`, `BFS_File`, `Owner_ID`, `Bucket_ID`, "
					+ "`Meta`, `Size`, `Media_Type`, "
					+ "`Version_enabled`, `Version`, "
					+ "`Deleted`, `Expiration_time`, `Create_time`, `Modify_time` "
					+ "from `bfsapi`.`scss_object` as object"
					+ " where object.BFS_File=" + BFS_File + "";
			stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				so.setBfsFile(BFS_File);
				so.setId(rs.getLong("ID"));
				so.setKey(rs.getString("Key"));
				so.setOwnerId(rs.getLong("Owner_ID"));
				so.setBucketId(rs.getLong("Bucket_ID"));
				so.setMeta(rs.getString("Meta"));
				so.setSize(rs.getLong("Size"));
				so.setMediaType(rs.getString("Media_Type"));
				so.setVersionEnabled(rs.getBoolean("Version_enabled"));
				so.setVersion(rs.getString("Version"));
				so.setDeleted(rs.getBoolean("Deleted"));
				so.setExpirationTime(rs.getDate("Expiration_time"));
				so.setCreateTime(rs.getDate("Create_time"));
				so.setModifyTime(rs.getDate("Modify_time"));
			}

			rs.close();
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return so;
	}

	public static ScssObject getObjectByKey(String key, Long Owner_ID) {
		Connection connection = null;
		Statement stmt = null;
		ScssObject so = new ScssObject();
		try {
			connection = connPool.getConnection();
			String sql = "select `ID`, `Key`, `BFS_File`, `Owner_ID`, `Bucket_ID`, "
					+ "`Meta`, `Size`, `Media_Type`, "
					+ "`Version_enabled`, `Version`, "
					+ "`Deleted`, `Expiration_time`, `Create_time`, `Modify_time` "
					+ "from `bfsapi`.`scss_object` as object "
					+ "where object.key='"
					+ key
					+ "' and object.Owner_ID="
					+ Owner_ID;
			stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				so.setBfsFile(rs.getLong("BFS_File"));
				so.setId(rs.getLong("ID"));
				so.setKey(rs.getString("Key"));
				so.setOwnerId(rs.getLong("Owner_ID"));
				so.setBucketId(rs.getLong("Bucket_ID"));
				so.setMeta(rs.getString("Meta"));
				so.setSize(rs.getLong("Size"));
				so.setMediaType(rs.getString("Media_Type"));
				so.setVersionEnabled(rs.getBoolean("Version_enabled"));
				so.setVersion(rs.getString("Version"));
				so.setDeleted(rs.getBoolean("Deleted"));
				so.setExpirationTime(rs.getDate("Expiration_time"));
				so.setCreateTime(rs.getDate("Create_time"));
				so.setModifyTime(rs.getDate("Modify_time"));
			}

			rs.close();
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return so;
	}

	public static void deleteObject(String key, Long Owner_ID, Long Bucket_ID) {
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "delete from scss_object " + "where `key`='" + key
					+ "' and owner_id=" + Owner_ID + " and Bucket_ID="
					+ Bucket_ID + "";
			stmt = connection.prepareStatement(sql);
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void deleteGroup(ScssGroup sg) {
		deleteGroup(sg.getId());
	}

	public static void deleteGroup(String name) {
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "delete from scss_group " + "where `name`='" + name
					+ "'";
			stmt = connection.prepareStatement(sql);
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void deleteGroup(Long gid) {
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "delete from scss_group " + "where `id`=" + gid;
			stmt = connection.prepareStatement(sql);
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void deleteBucket(String Name, Long Owner_ID) {
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "delete from scss_bucket " + "where `name`='" + Name
					+ "' and owner_id=" + Owner_ID + "";
			stmt = connection.prepareStatement(sql);
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void deleteBucketById(Long bucket_ID) {
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "delete from scss_bucket " + "where `id`=" + bucket_ID
					+ "";
			stmt = connection.prepareStatement(sql);
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void putBucket(String name, Long ownerId,
			Boolean exprirationEnabled, Boolean loggingEnabled, String meta,
			Boolean deleted, Date createTime, Date modifyTime) {
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "insert into scss_bucket(`name`," + "`Owner_id`,"
					+ "`expriration_enabled`," + "`Logging_enabled`,"
					+ "`Meta`," + "`deleted`," + "`create_time`,"
					+ "`Modify_time` ) " + "values ('" + name + "'," + ownerId
					+ "," + exprirationEnabled + "," + loggingEnabled + ",'"
					+ meta + "'," + deleted + ",'"
					+ dateFormat.format(createTime) + "','"
					+ dateFormat.format(modifyTime) + "')";
			stmt = connection.prepareStatement(sql);
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void putBucket(String name, Long ownerId, String meta) {
		putBucket(name, ownerId, false, true, meta, true, new Date(),
				new Date());
	}

	public static List<ScssObject> getBucket(Long Owner_ID, Long Bucket_ID) {
		Connection connection = null;
		Statement stmt = null;
		List<ScssObject> result = new ArrayList<ScssObject>();
		try {
			connection = connPool.getConnection();
			String sql = "select `ID`, `Key`, `BFS_File`, `Owner_ID`, `Bucket_ID`, "
					+ "`Meta`, `Size`, `Media_Type`, "
					+ "`Version_enabled`, `Version`, "
					+ "`Deleted`, `Expiration_time`, `Create_time`, `Modify_time` "
					+ "from `bfsapi`.`scss_object` as object "
					+ "where object.Bucket_ID="
					+ Bucket_ID
					+ " and object.Owner_ID=" + Owner_ID;
			stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				ScssObject so = new ScssObject();
				so.setBfsFile(rs.getLong("BFS_File"));
				so.setId(rs.getLong("ID"));
				so.setKey(rs.getString("Key"));
				so.setOwnerId(rs.getLong("Owner_ID"));
				so.setBucketId(rs.getLong("Bucket_ID"));
				so.setMeta(rs.getString("Meta"));
				so.setSize(rs.getLong("Size"));
				so.setMediaType(rs.getString("Media_Type"));
				so.setVersionEnabled(rs.getBoolean("Version_enabled"));
				so.setVersion(rs.getString("Version"));
				so.setDeleted(rs.getBoolean("Deleted"));
				so.setExpirationTime(rs.getDate("Expiration_time"));
				so.setCreateTime(rs.getDate("Create_time"));
				so.setModifyTime(rs.getDate("Modify_time"));
				result.add(so);
			}

			rs.close();
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static void putUser(String sohuId, String access_key) {
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "insert into scss_user(`Sohu_ID`," + "`access_key`) "
					+ "values ('" + sohuId + "','" + access_key + "')";
			stmt = connection.prepareStatement(sql);
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static ScssUser getUserBySohuId(String sohuId) {
		Connection connection = null;
		Statement stmt = null;
		ScssUser user = new ScssUser();
		try {
			connection = connPool.getConnection();
			String sql = "select `id`,`Sohu_ID`," + "`access_key`,"
					+ "`status` from `bfsapi`.`scss_user` as user "
					+ "where user.Sohu_ID='" + sohuId + "'";
			stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				user.setId(rs.getLong("ID"));
				user.setAccessKey(rs.getString("access_key"));
				user.setSohuId(sohuId);
				user.setStatus(rs.getString("status"));
			}
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return user;
	}

	public static ScssUser getUserByAccessKey(String access_key) {
		Connection connection = null;
		Statement stmt = null;
		ScssUser user = new ScssUser();
		try {
			connection = connPool.getConnection();
			String sql = "select `id`,`Sohu_ID`," + "`access_key`,"
					+ "`status` from `bfsapi`.`scss_user` as user "
					+ "where user.access_key='" + access_key + "'";
			stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				user.setId(rs.getLong("ID"));
				user.setAccessKey(rs.getString("access_key"));
				user.setSohuId(rs.getString("Sohu_ID"));
				user.setStatus(rs.getString("status"));
			}
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return user;
	}

	public static List<ScssUser> getUsersByGroupId(Long groupId) {
		ScssGroup groupById = getGroupById(groupId);
		return getUsersByGroup(groupById);
	}

	public static List<ScssUser> getUsersByGroup(ScssGroup group) {
		List<ScssUser> result = new ArrayList<ScssUser>();
		String userIds = group.getUserIds();
		if (userIds == null || "".equals(userIds)) {
			return result;
		}
		String[] split = userIds.split(",");
		for (int i = 1; i < split.length - 1; i++) {
			result.add(getUserById(Long.parseLong(split[i])));
		}
		return result;
	}

	private static ScssUser getUserById(long id) {
		Connection connection = null;
		Statement stmt = null;
		ScssUser user = new ScssUser();
		try {
			connection = connPool.getConnection();
			String sql = "select `id`,`Sohu_ID`," + "`access_key`,"
					+ "`status` from `bfsapi`.`scss_user` as user"
					+ "where user.id=" + id + "";
			stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				user.setId(rs.getLong("id"));
				user.setAccessKey(rs.getString("access_key"));
				user.setSohuId(rs.getString("Sohu_ID"));
				user.setStatus(rs.getString("status"));
			}
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return user;
	}

	public static void putGroup(String GroupName) {
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "insert into scss_group(`name`,`user_ids`) "
					+ "values ('" + GroupName + "',',')";
			stmt = connection.prepareStatement(sql);
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static ScssGroup getGroupById(Long id) {
		Connection connection = null;
		Statement stmt = null;
		ScssGroup g = new ScssGroup();
		try {
			connection = connPool.getConnection();
			String sql = "select `ID`," + "`name`," + "`user_ids` "
					+ "from `bfsapi`.`scss_group` " + "where ID=" + id + "";
			stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				g.setId(rs.getLong("ID"));
				g.setName(rs.getString("name"));
				g.setUserIds(rs.getString("user_ids"));
			}
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return g;
	}

	public static ScssGroup getGroupByName(String name) {
		Connection connection = null;
		Statement stmt = null;
		ScssGroup g = new ScssGroup();
		try {
			connection = connPool.getConnection();
			String sql = "select `ID`," + "`name`," + "`user_ids` "
					+ "from `bfsapi`.`scss_group` " + "where name='" + name
					+ "'";

			stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				g.setId(rs.getLong("ID"));
				g.setName(rs.getString("name"));
				g.setUserIds(rs.getString("user_ids"));
			}
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return g;
	}

	public static void putUserToGroup(ScssUser user, ScssGroup sg) {
		putUserIdsToGroup(user.getId() + "", sg);
	}

	public static void deleteUser(ScssUser user) {
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "delete from scss_user " + "where `id`="
					+ user.getId();
			stmt = connection.prepareStatement(sql);
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void removeUserFromGroup(ScssUser user, ScssGroup sg) {
		Connection connection = null;
		Statement stmt = null;
		String userIds = sg.getUserIds();
		userIds = userIds.replaceAll("," + user.getId() + ",", ",");
		try {
			connection = connPool.getConnection();

			String sql = "UPDATE scss_group set user_ids='" + userIds
					+ "' where id=" + sg.getId();
			stmt = connection.prepareStatement(sql);
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void putUserListToGroup(List<ScssUser> users, ScssGroup sg) {
		StringBuffer newIds = new StringBuffer();
		for (ScssUser u : users) {
			newIds.append(u.getId());
		}
		putUserIdsToGroup(newIds.toString(), sg);
	}

	public static void putUserIdsToGroup(String ids, ScssGroup sg) {
		if (ids == null || "".equals(ids)) {
			return;
		}
		Connection connection = null;
		Statement stmt = null;
		String newIds = sg.getUserIds();
		if (newIds == null || "".equals(newIds)) {
			newIds = "," + ids + ",";
		} else {
			newIds = newIds + ids + ",";
		}
		try {
			connection = connPool.getConnection();

			String sql = "UPDATE scss_group set user_ids='" + newIds
					+ "' where id=" + sg.getId();
			stmt = connection.prepareStatement(sql);
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		DBServiceHelper.putObject("123", 12345l, 123l, 1l, "", 11l,
				"Media_Type", false, "1024", false, new Date(), new Date(),
				new Date());
		ScssObject object = DBServiceHelper.getObject(12345l);
		ScssObject object2 = DBServiceHelper.getObjectByKey("123", 123l);
		// DBServiceHelper.deleteObject("123", 123l, 1234l);
		DBServiceHelper.putBucket("体育", 123l, "Meta");
		List<ScssObject> buckets = DBServiceHelper.getBucket(123l, 1l);
		DBServiceHelper.putUser("sohu.com.jack", "uuid+sohu.xxxxxxxxx");
		DBServiceHelper.putUser("sohu.com.jack2", "uuid+sohu.xxxxxxxxx");
		ScssUser user = DBServiceHelper
				.getUserByAccessKey("uuid+sohu.xxxxxxxxx");
		ScssUser user2 = DBServiceHelper.getUserBySohuId("sohu.com.jack2");
		DBServiceHelper.putGroup("普通用户组");
		ScssGroup sg = DBServiceHelper.getGroupByName("普通用户组");
		DBServiceHelper.putUserIdsToGroup("123", sg);
		DBServiceHelper.deleteGroup("普通用户组");
		// DBServiceHelper.d

	}
}
