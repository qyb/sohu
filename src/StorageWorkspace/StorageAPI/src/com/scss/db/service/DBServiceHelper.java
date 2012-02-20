package com.scss.db.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.scss.db.connpool.ConnectionPool;
import com.scss.db.exception.SameNameDirException;
import com.scss.db.model.ScssBucket;
import com.scss.db.model.ScssGroup;
import com.scss.db.model.ScssObject;
import com.scss.db.model.ScssUser;
import com.scss.utility.Logger;

public class DBServiceHelper {
	private static ConnectionPool connPool;
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private static final Logger logger = Logger
			.getLogger("DB/SERVICE", 0, true);
	private static DBServiceHelper DBHelper;

	static {
		if (connPool == null)
			connPool = new ConnectionPool("/db.properties");
	}

	public static void putObject(String key, Long BFS_File, Long owner_ID,
			Long Bucket_ID, String Meta, Long Size, String Media_Type,
			boolean Version_enabled, String Version, boolean Deleted,
			Date Expiration_time, Date Create_time, Date Modify_time)
			throws SameNameDirException {
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			connection = connPool.getConnection();

			String sql = "insert into scss_object(`Key`,`BFS_File`,`owner_ID`,`Bucket_ID`,"
					+ "`Meta`,`Size`,`Media_Type`,"
					+ "`Version_enabled`,`Version`,"
					+ "`Deleted`,`Expiration_time`,"
					+ "`Create_time`,`Modify_time`) "
					+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?)";

			stmt = connection.prepareStatement(sql);
			stmt.setString(1, key);
			stmt.setLong(2, BFS_File);
			stmt.setLong(3, owner_ID);
			stmt.setLong(4, Bucket_ID);
			stmt.setString(5, Meta);
			stmt.setLong(6, Size);
			stmt.setString(7, Media_Type);
			stmt.setBoolean(8, Version_enabled);
			stmt.setString(9, Version);
			stmt.setBoolean(10, Deleted);
			stmt.setDate(11, new java.sql.Date(Expiration_time.getTime()));
			stmt.setDate(12, new java.sql.Date(Create_time.getTime()));
			stmt.setDate(13, new java.sql.Date(Modify_time.getTime()));
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			String message = e.getMessage();
			logger.debugT(message);
			if (message.indexOf("Duplicate entry") != -1) {
				SameNameDirException ename = new SameNameDirException(
						"key,user and BucketName", "Duplicate entry");
				throw ename;
			}
		} finally {
			try {
				if ((stmt != null) && (!stmt.isClosed())) {
					stmt.close();
				}
				if ((connection != null) && (!connection.isClosed()))
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void putObject(String key, Long BFS_File, Long owner_ID,
			Long Bucket_ID, String Meta, Long Size, String Media_Type)
			throws SameNameDirException {
		putObject(key, BFS_File, owner_ID, Bucket_ID, "Meta", Long
				.valueOf(1024L), Media_Type, false, "v1.0", true, new Date(
				System.currentTimeMillis() + 604800000L), new Date(),
				new Date());
	}

	public static ScssObject getObject(Long BFS_File) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ScssObject so = new ScssObject();
		try {
			connection = connPool.getConnection();
			String sql = "select `ID`, `Key`, `BFS_File`, `owner_ID`, `Bucket_ID`, `Meta`, `Size`, "
					+ "`Media_Type`, `Version_enabled`, `Version`, `Deleted`, `Expiration_time`, "
					+ "`Create_time`, `Modify_time` "
					+ "from `scss_object` as object "
					+ "where object.BFS_File=?";
			stmt = connection.prepareStatement(sql);
			stmt.setLong(1, BFS_File);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				so.setBfsFile(BFS_File);
				so.setId(Long.valueOf(rs.getLong("ID")));
				so.setKey(rs.getString("Key"));
				so.setOwnerId(Long.valueOf(rs.getLong("owner_ID")));
				so.setBucketId(Long.valueOf(rs.getLong("Bucket_ID")));
				so.setMeta(rs.getString("Meta"));
				so.setSize(Long.valueOf(rs.getLong("Size")));
				so.setMediaType(rs.getString("Media_Type"));
				so.setVersionEnabled(Boolean.valueOf(rs
						.getBoolean("Version_enabled")));
				so.setVersion(rs.getString("Version"));
				so.setDeleted(Boolean.valueOf(rs.getBoolean("Deleted")));
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
				if ((stmt != null) && (!stmt.isClosed())) {
					stmt.close();
				}
				if ((connection != null) && (!connection.isClosed()))
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return so;
	}

	public static ScssObject getObjectByKey(String key, Long owner_ID) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ScssObject so = new ScssObject();
		try {
			connection = connPool.getConnection();
			String sql = "select `ID`, `Key`, `BFS_File`, "
					+ "`owner_ID`, `Bucket_ID`, `Meta`, "
					+ "`Size`, `Media_Type`, `Version_enabled`, "
					+ "`Version`, `Deleted`, `Expiration_time`, "
					+ "`Create_time`, `Modify_time` "
					+ "from `scss_object` as object where object.key=? and object.owner_ID=?";
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, key);
			stmt.setLong(2, owner_ID);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				so.setBfsFile(Long.valueOf(rs.getLong("BFS_File")));
				so.setId(Long.valueOf(rs.getLong("ID")));
				so.setKey(rs.getString("Key"));
				so.setOwnerId(Long.valueOf(rs.getLong("owner_ID")));
				so.setBucketId(Long.valueOf(rs.getLong("Bucket_ID")));
				so.setMeta(rs.getString("Meta"));
				so.setSize(Long.valueOf(rs.getLong("Size")));
				so.setMediaType(rs.getString("Media_Type"));
				so.setVersionEnabled(Boolean.valueOf(rs
						.getBoolean("Version_enabled")));
				so.setVersion(rs.getString("Version"));
				so.setDeleted(Boolean.valueOf(rs.getBoolean("Deleted")));
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
				if ((stmt != null) && (!stmt.isClosed())) {
					stmt.close();
				}
				if ((connection != null) && (!connection.isClosed()))
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return so;
	}

	public static void deleteObject(String key, Long owner_ID, Long Bucket_ID) {
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "delete from scss_object where `key`=? and owner_ID=? and Bucket_ID=?";
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, key);
			stmt.setLong(2, owner_ID);
			stmt.setLong(3, Bucket_ID);
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if ((stmt != null) && (!stmt.isClosed())) {
					stmt.close();
				}
				if ((connection != null) && (!connection.isClosed()))
					connection.close();
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
		PreparedStatement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "delete from scss_group where `name`=?";

			stmt = connection.prepareStatement(sql);
			stmt.setString(1, name);
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if ((stmt != null) && (!stmt.isClosed())) {
					stmt.close();
				}
				if ((connection != null) && (!connection.isClosed()))
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void deleteGroup(Long gid) {
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "delete from scss_group where `id`=?";
			stmt = connection.prepareStatement(sql);
			stmt.setLong(1, gid);
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if ((stmt != null) && (!stmt.isClosed())) {
					stmt.close();
				}
				if ((connection != null) && (!connection.isClosed()))
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void deleteBucket(String name, Long ownerID) {
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "delete from scss_bucket where `name`=? and owner_ID=?";
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, name);
			stmt.setLong(2, ownerID);
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if ((stmt != null) && (!stmt.isClosed())) {
					stmt.close();
				}
				if ((connection != null) && (!connection.isClosed()))
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void deleteBucketById(Long bucket_ID) {
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "delete from scss_bucket where `id`=?";
			stmt = connection.prepareStatement(sql);
			stmt.setLong(1, bucket_ID);
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if ((stmt != null) && (!stmt.isClosed())) {
					stmt.close();
				}
				if ((connection != null) && (!connection.isClosed()))
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void putBucket(String name, Long ownerId,
			Boolean exprirationEnabled, Boolean loggingEnabled, String meta,
			Boolean deleted, Date createTime, Date modifyTime)
			throws SameNameDirException {
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "insert into " + "scss_bucket(`name`,`owner_ID`,"
					+ "`expriration_enabled`," + "`Logging_enabled`,`Meta`,"
					+ "`deleted`,`create_time`," + "`Modify_time` ) "
					+ "values (?,?,?,?,?,?,?,?)";
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, name);
			stmt.setLong(2, ownerId);
			stmt.setBoolean(3, exprirationEnabled);
			stmt.setBoolean(4, loggingEnabled);
			stmt.setString(5, meta);
			stmt.setBoolean(6, deleted);
			stmt.setDate(7, new java.sql.Date(createTime.getTime()));
			stmt.setDate(8, new java.sql.Date(modifyTime.getTime()));
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			String message = e.getMessage();
			if ((message.indexOf("Duplicate entry") != -1)
					&& (message.indexOf("'name'") != -1)) {
				SameNameDirException ename = new SameNameDirException(
						"name and user", "Duplicate entry name and user");
				throw ename;
			}
		} finally {
			try {
				if ((stmt != null) && (!stmt.isClosed())) {
					stmt.close();
				}
				if ((connection != null) && (!connection.isClosed()))
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void putBucket(String name, Long ownerId, String meta)
			throws SameNameDirException {
		putBucket(name, ownerId, Boolean.valueOf(false), Boolean.valueOf(true),
				meta, Boolean.valueOf(true), new Date(), new Date());
	}

	public static List<ScssObject> getBucket(Long owner_ID, Long Bucket_ID) {
		Connection connection = null;
		PreparedStatement stmt = null;
		List result = new ArrayList();
		try {
			connection = connPool.getConnection();
			String sql = "select `ID`, `Key`, `BFS_File`, `owner_ID`, `Bucket_ID`, `Meta`, "
					+ "`Size`, `Media_Type`, `Version_enabled`, `Version`, `Deleted`, "
					+ "`Expiration_time`, `Create_time`, `Modify_time` "
					+ "from `scss_object` as object where object.Bucket_ID=? and object.owner_ID=?";
			stmt = connection.prepareStatement(sql);
			stmt.setLong(1, Bucket_ID);
			stmt.setLong(2, owner_ID);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ScssObject so = new ScssObject();
				so.setBfsFile(Long.valueOf(rs.getLong("BFS_File")));
				so.setId(Long.valueOf(rs.getLong("ID")));
				so.setKey(rs.getString("Key"));
				so.setOwnerId(Long.valueOf(rs.getLong("owner_ID")));
				so.setBucketId(Long.valueOf(rs.getLong("Bucket_ID")));
				so.setMeta(rs.getString("Meta"));
				so.setSize(Long.valueOf(rs.getLong("Size")));
				so.setMediaType(rs.getString("Media_Type"));
				so.setVersionEnabled(Boolean.valueOf(rs
						.getBoolean("Version_enabled")));
				so.setVersion(rs.getString("Version"));
				so.setDeleted(Boolean.valueOf(rs.getBoolean("Deleted")));
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
				if ((stmt != null) && (!stmt.isClosed())) {
					stmt.close();
				}
				if ((connection != null) && (!connection.isClosed()))
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static void putUser(String sohuId, String access_key)
			throws SameNameDirException {
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "insert into scss_user(`Sohu_ID`,`access_key`) values (?,?)";
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, sohuId);
			stmt.setString(2, access_key);
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			String message = e.getMessage();
			logger.debugT(message);
			if (message.indexOf("Duplicate entry") != -1) {
				if (message.indexOf("access_key") != -1) {
					SameNameDirException ename = new SameNameDirException(
							"access_key", message);
					throw ename;
				}
				if (message.indexOf("Sohu_ID") != -1) {
					SameNameDirException ename = new SameNameDirException(
							"Sohu_ID", message);
					throw ename;
				}
			}
		} finally {
			try {
				if ((stmt != null) && (!stmt.isClosed())) {
					stmt.close();
				}
				if ((connection != null) && (!connection.isClosed()))
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static ScssUser getUserBySohuId(String sohuId) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ScssUser user = new ScssUser();
		try {
			connection = connPool.getConnection();
			String sql = "select `id`,`Sohu_ID`,`access_key`,`status` from `scss_user` as user where user.Sohu_ID=?";
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, sohuId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				user.setId(Long.valueOf(rs.getLong("ID")));
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
				if ((stmt != null) && (!stmt.isClosed())) {
					stmt.close();
				}
				if ((connection != null) && (!connection.isClosed()))
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return user;
	}

	public static ScssUser getUserByAccessKey(String access_key) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ScssUser user = new ScssUser();
		try {
			connection = connPool.getConnection();
			String sql = "select `id`,`Sohu_ID`,`access_key`,`status` "
					+ "from `scss_user` as user where user.access_key=?";
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, access_key);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				user.setId(Long.valueOf(rs.getLong("ID")));
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
				if ((stmt != null) && (!stmt.isClosed())) {
					stmt.close();
				}
				if ((connection != null) && (!connection.isClosed()))
					connection.close();
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
		List result = new ArrayList();
		String userIds = group.getUserIds();
		if ((userIds == null) || ("".equals(userIds))) {
			return result;
		}
		String[] split = userIds.split(",");
		for (int i = 1; i < split.length - 1; ++i) {
			result.add(getUserById(Long.parseLong(split[i])));
		}
		return result;
	}

	private static ScssUser getUserById(long id) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ScssUser user = new ScssUser();
		try {
			connection = connPool.getConnection();
			String sql = "select `id`,`Sohu_ID`,`access_key`,`status` "
					+ "from `scss_user` as userwhere user.id=?";
			stmt.setLong(1, id);
			stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				user.setId(Long.valueOf(rs.getLong("id")));
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
				if ((stmt != null) && (!stmt.isClosed())) {
					stmt.close();
				}
				if ((connection != null) && (!connection.isClosed()))
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return user;
	}

	public static void putGroup(String groupName) {
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "insert into scss_group(`name`,`user_ids`) values ('?',',')";
			stmt.setString(1, groupName);
			stmt = connection.prepareStatement(sql);
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if ((stmt != null) && (!stmt.isClosed())) {
					stmt.close();
				}
				if ((connection != null) && (!connection.isClosed()))
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static ScssGroup getGroupById(Long id) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ScssGroup g = new ScssGroup();
		try {
			connection = connPool.getConnection();
			String sql = "select `ID`,`name`,`user_ids` from `scss_group` where ID=?";
			stmt.setLong(1, id);
			stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				g.setId(Long.valueOf(rs.getLong("ID")));
				g.setName(rs.getString("name"));
				g.setUserIds(rs.getString("user_ids"));
			}
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if ((stmt != null) && (!stmt.isClosed())) {
					stmt.close();
				}
				if ((connection != null) && (!connection.isClosed()))
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return g;
	}

	public static ScssGroup getGroupByName(String name) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ScssGroup g = new ScssGroup();
		try {
			connection = connPool.getConnection();
			String sql = "select `ID`,`name`,`user_ids` from `scss_group` where name=?";
			stmt.setString(1, name);
			stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				g.setId(Long.valueOf(rs.getLong("ID")));
				g.setName(rs.getString("name"));
				g.setUserIds(rs.getString("user_ids"));
			}
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if ((stmt != null) && (!stmt.isClosed())) {
					stmt.close();
				}
				if ((connection != null) && (!connection.isClosed()))
					connection.close();
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
		PreparedStatement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "delete from scss_user where `id`=?";
			stmt.setLong(1, user.getId());
			stmt = connection.prepareStatement(sql);
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if ((stmt != null) && (!stmt.isClosed())) {
					stmt.close();
				}
				if ((connection != null) && (!connection.isClosed()))
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void removeUserFromGroup(ScssUser user, ScssGroup sg) {
		Connection connection = null;
		PreparedStatement stmt = null;
		String userIds = sg.getUserIds();
		userIds = userIds.replaceAll("," + user.getId() + ",", ",");
		try {
			connection = connPool.getConnection();

			String sql = "UPDATE scss_group set user_ids=? where id=?";

			stmt = connection.prepareStatement(sql);
			stmt.setString(1, userIds);
			stmt.setLong(2, sg.getId());
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if ((stmt != null) && (!stmt.isClosed())) {
					stmt.close();
				}
				if ((connection != null) && (!connection.isClosed()))
					connection.close();
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
		if ((ids == null) || ("".equals(ids))) {
			return;
		}
		Connection connection = null;
		PreparedStatement stmt = null;
		String newIds = sg.getUserIds();
		if ((newIds == null) || ("".equals(newIds)))
			newIds = "," + ids + ",";
		else
			newIds = newIds + ids + ",";
		try {
			connection = connPool.getConnection();

			String sql = "UPDATE scss_group set user_ids=? where id=?";
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, newIds);
			stmt.setLong(2, sg.getId());
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if ((stmt != null) && (!stmt.isClosed())) {
					stmt.close();
				}
				if ((connection != null) && (!connection.isClosed()))
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		ScssUser user2;
		try {
			putBucket("����", Long.valueOf(123L), "xxxxxxxxx");
			putUser("sohu.com.jack", "uuid+sohu.xxxxxxxxx");
			putGroup("");
			getBucketsByUserID(123L);
			getBucketsByUserName("");
			ScssUser user = getUserByAccessKey("uuid+sohu.xxxxxxxxx");
			user2 = getUserBySohuId("sohu.com.jack2");
		} catch (SameNameDirException e) {
			e.printStackTrace();
		}
	}

	public static List<ScssBucket> getBucketsByUserName(String name) {
		Connection connection = null;
		PreparedStatement stmt = null;
		List result = new ArrayList();
		try {
			connection = connPool.getConnection();
			String sql = "select `id`,`name`,`owner_ID`,`expriration_enabled`,`Logging_enabled`,"
					+ "`Meta`,`deleted`,`create_time`,`Modify_time` "
					+ "from `scss_bucket` as bucket,`scss_user` as user  "
					+ "where  bucket.owner_ID=user.id and user.Sohu_ID=?";
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ScssBucket so = new ScssBucket();
				so.setId(Long.valueOf(rs.getLong("ID")));
				so.setOwnerId(Long.valueOf(rs.getLong("owner_ID")));
				so.setMeta(rs.getString("Meta"));
				so.setCreateTime(rs.getDate("create_time"));
				so.setModifyTime(rs.getDate("Modify_time"));
				so.setDeleted(Byte.valueOf(rs.getByte("deleted")));
				so.setExprirationEnabled(Byte.valueOf(rs
						.getByte("expriration_enabled")));
				so.setLoggingEnabled(Byte
						.valueOf(rs.getByte("Logging_enabled")));
				result.add(so);
			}

			rs.close();
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if ((stmt != null) && (!stmt.isClosed())) {
					stmt.close();
				}
				if ((connection != null) && (!connection.isClosed()))
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static List<ScssBucket> getBucketsByUserID(long ID) {
		Connection connection = null;
		PreparedStatement stmt = null;
		List result = new ArrayList();
		try {
			connection = connPool.getConnection();
			String sql = "select `id`,`name`,`owner_ID`,`expriration_enabled`,"
					+ "`Logging_enabled`,`Meta`,`deleted`,`create_time`,"
					+ "`Modify_time` "
					+ "from `scss_bucket` as bucket "
					+ "where  bucket.owner_ID=?";
			stmt = connection.prepareStatement(sql);
			stmt.setLong(1, ID);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ScssBucket so = new ScssBucket();
				so.setId(Long.valueOf(rs.getLong("ID")));
				so.setOwnerId(Long.valueOf(rs.getLong("owner_ID")));
				so.setMeta(rs.getString("Meta"));
				so.setCreateTime(rs.getDate("create_time"));
				so.setModifyTime(rs.getDate("Modify_time"));
				so.setDeleted(Byte.valueOf(rs.getByte("deleted")));
				so.setExprirationEnabled(Byte.valueOf(rs
						.getByte("expriration_enabled")));
				so.setLoggingEnabled(Byte
						.valueOf(rs.getByte("Logging_enabled")));
				result.add(so);
			}

			rs.close();
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if ((stmt != null) && (!stmt.isClosed())) {
					stmt.close();
				}
				if ((connection != null) && (!connection.isClosed()))
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static List<ScssObject> getBucket(long owner_ID, String name) {
		Connection connection = null;
		PreparedStatement stmt = null;
		List result = new ArrayList();
		try {
			connection = connPool.getConnection();
			String sql = "select object.`ID`, object.`Key`, object.`BFS_File`, "
					+ "object.`owner_ID`, object.`Bucket_ID`, object.`Meta`, object.`Size`, "
					+ "object.`Media_Type`, object.`Version_enabled`, object.`Version`, "
					+ "object.`Deleted`, object.`Expiration_time`, object.`Create_time`, "
					+ "object.`Modify_time` from `scss_object` as object ,"
					+ "`scss_bucket` as bucket where bucket.name=?"
					+ " and object.owner_ID=? and object.Bucket_ID= bucket.id";
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, name);
			stmt.setLong(2, owner_ID);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ScssObject so = new ScssObject();
				so.setBfsFile(Long.valueOf(rs.getLong("BFS_File")));
				so.setId(Long.valueOf(rs.getLong("ID")));
				so.setKey(rs.getString("Key"));
				so.setOwnerId(Long.valueOf(rs.getLong("owner_ID")));
				so.setBucketId(Long.valueOf(rs.getLong("Bucket_ID")));
				so.setMeta(rs.getString("Meta"));
				so.setSize(Long.valueOf(rs.getLong("Size")));
				so.setMediaType(rs.getString("Media_Type"));
				so.setVersionEnabled(Boolean.valueOf(rs
						.getBoolean("Version_enabled")));
				so.setVersion(rs.getString("Version"));
				so.setDeleted(Boolean.valueOf(rs.getBoolean("Deleted")));
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
				if ((stmt != null) && (!stmt.isClosed())) {
					stmt.close();
				}
				if ((connection != null) && (!connection.isClosed()))
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static ScssBucket getBucketByName(String bucketName, Long userId) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ScssBucket so = new ScssBucket();
		try {
			connection = connPool.getConnection();
			String sql = "select bucket.`id`,`name`,`owner_ID`,`expriration_enabled`,"
					+ "`Logging_enabled`,`Meta`,`deleted`,`create_time`,`Modify_time` "
					+ "from `scss_bucket` as bucket,`scss_user` as user  "
					+ "where  bucket.owner_ID=user.id and user.ID=? and bucket.name=?";
			stmt = connection.prepareStatement(sql);
			stmt.setLong(1, userId);
			stmt.setString(2, bucketName);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {

				so.setId(Long.valueOf(rs.getLong("ID")));
				so.setOwnerId(Long.valueOf(rs.getLong("owner_ID")));
				so.setMeta(rs.getString("Meta"));
				so.setCreateTime(rs.getDate("create_time"));
				so.setModifyTime(rs.getDate("Modify_time"));
				so.setDeleted(Byte.valueOf(rs.getByte("deleted")));
				so.setExprirationEnabled(Byte.valueOf(rs
						.getByte("expriration_enabled")));
				so.setLoggingEnabled(Byte
						.valueOf(rs.getByte("Logging_enabled")));
			}

			rs.close();
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if ((stmt != null) && (!stmt.isClosed())) {
					stmt.close();
				}
				if ((connection != null) && (!connection.isClosed()))
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return so;
	}
}