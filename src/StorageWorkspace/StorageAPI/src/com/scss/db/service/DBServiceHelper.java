package com.scss.db.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

	public static void putObject(String key, Long BFS_File, Long Owner_ID,
			Long Bucket_ID, String Meta, Long Size, String Media_Type,
			boolean Version_enabled, String Version, boolean Deleted,
			Date Expiration_time, Date Create_time, Date Modify_time)
			throws SameNameDirException {
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = connPool.getConnection();

			String sql = "insert into scss_object(`Key`,`BFS_File`,`Owner_ID`,`Bucket_ID`,`Meta`,`Size`,`Media_Type`,`Version_enabled`,`Version`,`Deleted`,`Expiration_time`,`Create_time`,`Modify_time`) values ('"
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

	public static void putObject(String key, Long BFS_File, Long Owner_ID,
			Long Bucket_ID, String Meta, Long Size, String Media_Type)
			throws SameNameDirException {
		putObject(key, BFS_File, Owner_ID, Bucket_ID, "Meta", Long
				.valueOf(1024L), Media_Type, false, "v1.0", true, new Date(
				System.currentTimeMillis() + 604800000L), new Date(),
				new Date());
	}

	public static ScssObject getObject(Long BFS_File) {
		Connection connection = null;
		Statement stmt = null;
		ScssObject so = new ScssObject();
		try {
			connection = connPool.getConnection();
<<<<<<< HEAD
			String sql = "select `ID`, `Key`, `BFS_File`, `Owner_ID`, `Bucket_ID`, "
					+ "`Meta`, `Size`, `Media_Type`, "
					+ "`Version_enabled`, `Version`, "
					+ "`Deleted`, `Expiration_time`, `Create_time`, `Modify_time` "
					+ "from `scss_object` as object"
					+ " where object.BFS_File=" + BFS_File + "";
=======
			String sql = "select `ID`, `Key`, `BFS_File`, `Owner_ID`, `Bucket_ID`, `Meta`, `Size`, `Media_Type`, `Version_enabled`, `Version`, `Deleted`, `Expiration_time`, `Create_time`, `Modify_time` from `scss_object` as object where object.BFS_File="
					+ BFS_File;
>>>>>>> 6476be820ef3c1dbee6bd2baf9a898bcb055465b
			stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				so.setBfsFile(BFS_File);
				so.setId(Long.valueOf(rs.getLong("ID")));
				so.setKey(rs.getString("Key"));
				so.setOwnerId(Long.valueOf(rs.getLong("Owner_ID")));
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

	public static ScssObject getObjectByKey(String key, Long Owner_ID) {
		Connection connection = null;
		Statement stmt = null;
		ScssObject so = new ScssObject();
		try {
			connection = connPool.getConnection();
<<<<<<< HEAD
			String sql = "select `ID`, `Key`, `BFS_File`, `Owner_ID`, `Bucket_ID`, "
					+ "`Meta`, `Size`, `Media_Type`, "
					+ "`Version_enabled`, `Version`, "
					+ "`Deleted`, `Expiration_time`, `Create_time`, `Modify_time` "
					+ "from `scss_object` as object "
					+ "where object.key='"
					+ key
					+ "' and object.Owner_ID="
					+ Owner_ID;
=======
			String sql = "select `ID`, `Key`, `BFS_File`, `Owner_ID`, `Bucket_ID`, `Meta`, `Size`, `Media_Type`, `Version_enabled`, `Version`, `Deleted`, `Expiration_time`, `Create_time`, `Modify_time` from `scss_object` as object where object.key='"
					+ key + "' and object.Owner_ID=" + Owner_ID;
>>>>>>> 6476be820ef3c1dbee6bd2baf9a898bcb055465b
			stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				so.setBfsFile(Long.valueOf(rs.getLong("BFS_File")));
				so.setId(Long.valueOf(rs.getLong("ID")));
				so.setKey(rs.getString("Key"));
				so.setOwnerId(Long.valueOf(rs.getLong("Owner_ID")));
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

	public static void deleteObject(String key, Long Owner_ID, Long Bucket_ID) {
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "delete from scss_object where `key`='" + key
					+ "' and owner_id=" + Owner_ID + " and Bucket_ID="
					+ Bucket_ID;
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

	public static void deleteGroup(ScssGroup sg) {
		deleteGroup(sg.getId());
	}

	public static void deleteGroup(String name) {
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "delete from scss_group where `name`='" + name + "'";
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

	public static void deleteGroup(Long gid) {
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "delete from scss_group where `id`=" + gid;
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

	public static void deleteBucket(String Name, Long Owner_ID) {
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "delete from scss_bucket where `name`='" + Name
					+ "' and owner_id=" + Owner_ID;
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

	public static void deleteBucketById(Long bucket_ID) {
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "delete from scss_bucket where `id`=" + bucket_ID;

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

	public static void putBucket(String name, Long ownerId,
			Boolean exprirationEnabled, Boolean loggingEnabled, String meta,
			Boolean deleted, Date createTime, Date modifyTime)
			throws SameNameDirException {
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "insert into scss_bucket(`name`,`Owner_id`,`expriration_enabled`,`Logging_enabled`,`Meta`,`deleted`,`create_time`,`Modify_time` ) values ('"
					+ name
					+ "',"
					+ ownerId
					+ ","
					+ exprirationEnabled
					+ ","
					+ loggingEnabled
					+ ",'"
					+ meta
					+ "',"
					+ deleted
					+ ",'"
					+ dateFormat.format(createTime)
					+ "','"
					+ dateFormat.format(modifyTime) + "')";
			stmt = connection.prepareStatement(sql);
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

	public static List<ScssObject> getBucket(Long Owner_ID, Long Bucket_ID) {
		Connection connection = null;
		Statement stmt = null;
<<<<<<< HEAD
		List<ScssObject> result = new ArrayList<ScssObject>();
		try {
			connection = connPool.getConnection();
			String sql = "select `ID`, `Key`, `BFS_File`, `Owner_ID`, `Bucket_ID`, "
					+ "`Meta`, `Size`, `Media_Type`, "
					+ "`Version_enabled`, `Version`, "
					+ "`Deleted`, `Expiration_time`, `Create_time`, `Modify_time` "
					+ "from `scss_object` as object "
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

	public static List<ScssObject> getBucket(long Owner_ID, String name) {
		Connection connection = null;
		Statement stmt = null;
		List<ScssObject> result = new ArrayList<ScssObject>();
		try {
			connection = connPool.getConnection();
			String sql = "select `object`.`ID`, `object`.`Key`, `object`.`BFS_File`, `object`.`Owner_ID`, `object`.`Bucket_ID`, "
					+ "`object`.`Meta`, `object`.`Size`, `object`.`Media_Type`, "
					+ "`object`.`Version_enabled`, `object`.`Version`, "
					+ "`object`.`Deleted`, `object`.`Expiration_time`, `object`.`Create_time`, `object`.`Modify_time` "
					+ "from `scss_object` as object ,`scss_bucket` as bucket "
					+ "where bucket.name='"
					+ name
					+ "' and object.Owner_ID="
					+ Owner_ID + " and object.Bucket_ID= bucket.id";
=======
		List result = new ArrayList();
		try {
			connection = connPool.getConnection();
			String sql = "select `ID`, `Key`, `BFS_File`, `Owner_ID`, `Bucket_ID`, `Meta`, `Size`, `Media_Type`, `Version_enabled`, `Version`, `Deleted`, `Expiration_time`, `Create_time`, `Modify_time` from `scss_object` as object where object.Bucket_ID="
					+ Bucket_ID + " and object.Owner_ID=" + Owner_ID;
>>>>>>> 6476be820ef3c1dbee6bd2baf9a898bcb055465b
			stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				ScssObject so = new ScssObject();
				so.setBfsFile(Long.valueOf(rs.getLong("BFS_File")));
				so.setId(Long.valueOf(rs.getLong("ID")));
				so.setKey(rs.getString("Key"));
				so.setOwnerId(Long.valueOf(rs.getLong("Owner_ID")));
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
		Statement stmt = null;
		try {
			connection = connPool.getConnection();
<<<<<<< HEAD
			String sql = "select bucket.* "
					+ "from `scss_bucket` as bucket "
					+ "where bucket.Owner_ID=" + Owner_ID;
			stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				ScssBucket bo = new ScssBucket();
				bo.setId(rs.getLong("ID"));
				bo.setName(rs.getString("Name"));
				bo.setOwnerId(rs.getLong("Owner_ID"));
				bo.setExprirationEnabled(rs.getByte("Expriration_Enabled"));
				bo.setCreateTime(rs.getDate("Create_time"));
				bo.setModifyTime(rs.getDate("Modify_time"));
				bo.setDeleted(rs.getByte("Deleted"));
				bo.setLoggingEnabled(rs.getByte("Logging_Enabled"));
				bo.setMeta(rs.getString("Meta"));
				result.add(bo);
			}

			rs.close();
=======
			String sql = "insert into scss_user(`Sohu_ID`,`access_key`) values ('"
					+ sohuId + "','" + access_key + "')";
			stmt = connection.prepareStatement(sql);
			stmt.executeUpdate(sql);
>>>>>>> 6476be820ef3c1dbee6bd2baf9a898bcb055465b
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
		Statement stmt = null;
		ScssUser user = new ScssUser();
		try {
			connection = connPool.getConnection();
<<<<<<< HEAD
			String sql = "select `id`,`Sohu_ID`," + "`access_key`,"
					+ "`status` from `scss_user` as user "
					+ "where user.Sohu_ID='" + sohuId + "'";
=======
			String sql = "select `id`,`Sohu_ID`,`access_key`,`status` from `scss_user` as user where user.Sohu_ID='"
					+ sohuId + "'";
>>>>>>> 6476be820ef3c1dbee6bd2baf9a898bcb055465b
			stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
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
		Statement stmt = null;
		ScssUser user = new ScssUser();
		try {
			connection = connPool.getConnection();
<<<<<<< HEAD
			String sql = "select `id`,`Sohu_ID`," + "`access_key`,"
					+ "`status` from `scss_user` as user "
					+ "where user.access_key='" + access_key + "'";
=======
			String sql = "select `id`,`Sohu_ID`,`access_key`,`status` from `scss_user` as user where user.access_key='"
					+ access_key + "'";
>>>>>>> 6476be820ef3c1dbee6bd2baf9a898bcb055465b
			stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
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
		Statement stmt = null;
		ScssUser user = new ScssUser();
		try {
			connection = connPool.getConnection();
<<<<<<< HEAD
			String sql = "select `id`,`Sohu_ID`," + "`access_key`,"
					+ "`status` from `scss_user` as user"
					+ "where user.id=" + id + "";
=======
			String sql = "select `id`,`Sohu_ID`,`access_key`,`status` from `scss_user` as userwhere user.id="
					+ id;
>>>>>>> 6476be820ef3c1dbee6bd2baf9a898bcb055465b
			stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
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

	public static void putGroup(String GroupName) {
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "insert into scss_group(`name`,`user_ids`) values ('"
					+ GroupName + "',',')";
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
		Statement stmt = null;
		ScssGroup g = new ScssGroup();
		try {
			connection = connPool.getConnection();
<<<<<<< HEAD
			String sql = "select `ID`," + "`name`," + "`user_ids` "
					+ "from `scss_group` " + "where ID=" + id + "";
=======
			String sql = "select `ID`,`name`,`user_ids` from `scss_group` where ID="
					+ id;
>>>>>>> 6476be820ef3c1dbee6bd2baf9a898bcb055465b
			stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
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
		Statement stmt = null;
		ScssGroup g = new ScssGroup();
		try {
			connection = connPool.getConnection();
<<<<<<< HEAD
			String sql = "select `ID`," + "`name`," + "`user_ids` "
					+ "from `scss_group` " + "where name='" + name
					+ "'";
=======
			String sql = "select `ID`,`name`,`user_ids` from `scss_group` where name='"
					+ name + "'";
>>>>>>> 6476be820ef3c1dbee6bd2baf9a898bcb055465b

			stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
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
		Statement stmt = null;
		try {
			connection = connPool.getConnection();
			String sql = "delete from scss_user where `id`=" + user.getId();
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
		Statement stmt = null;
		String newIds = sg.getUserIds();
		if ((newIds == null) || ("".equals(newIds)))
			newIds = "," + ids + ",";
		else
			newIds = newIds + ids + ",";
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
			putBucket("ÌåÓý", Long.valueOf(123L), "xxxxxxxxx");
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
		Statement stmt = null;
		List result = new ArrayList();
		try {
			connection = connPool.getConnection();
			String sql = "select `id`,`name`,`Owner_id`,`expriration_enabled`,`Logging_enabled`,`Meta`,`deleted`,`create_time`,`Modify_time` from `scss_bucket` as bucket,`scss_user` as user  where  bucket.Owner_id=user.id and user.Sohu_ID='"
					+ name + "'";
			stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				ScssBucket so = new ScssBucket();
				so.setId(Long.valueOf(rs.getLong("ID")));
				so.setOwnerId(Long.valueOf(rs.getLong("Owner_ID")));
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
		Statement stmt = null;
		List result = new ArrayList();
		try {
			connection = connPool.getConnection();
			String sql = "select `id`,`name`,`Owner_id`,`expriration_enabled`,`Logging_enabled`,`Meta`,`deleted`,`create_time`,`Modify_time` from `scss_bucket` as bucket,`scss_user` as user  where  bucket.Owner_id=user.id and user.ID="
					+ ID;
			stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				ScssBucket so = new ScssBucket();
				so.setId(Long.valueOf(rs.getLong("ID")));
				so.setOwnerId(Long.valueOf(rs.getLong("Owner_ID")));
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

	public static List<ScssObject> getBucket(long Owner_ID, String name) {
		Connection connection = null;
		Statement stmt = null;
		List result = new ArrayList();
		try {
			connection = connPool.getConnection();
			String sql = "select object.`ID`, `Key`, `BFS_File`, "
					+ "`Owner_ID`, `Bucket_ID`, `Meta`, `Size`, "
					+ "`Media_Type`, `Version_enabled`, `Version`, "
					+ "`Deleted`, `Expiration_time`, `Create_time`, "
					+ "`Modify_time` from `scss_object` as object ,"
					+ "`scss_bucket` as bucket where bucket.name='" + name
					+ "' and object.Owner_ID=" + Owner_ID
					+ " and object.Bucket_ID= bucket.id";
			stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				ScssObject so = new ScssObject();
				so.setBfsFile(Long.valueOf(rs.getLong("BFS_File")));
				so.setId(Long.valueOf(rs.getLong("ID")));
				so.setKey(rs.getString("Key"));
				so.setOwnerId(Long.valueOf(rs.getLong("Owner_ID")));
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
		Statement stmt = null;
		ScssBucket so = new ScssBucket();
		try {
			connection = connPool.getConnection();
			String sql = "select bucket.`id`,`name`,`Owner_id`,`expriration_enabled`,`Logging_enabled`,`Meta`,`deleted`,`create_time`,`Modify_time` from `scss_bucket` as bucket,`scss_user` as user  where  bucket.Owner_id=user.id and user.ID="
					+ userId + " and bucket.name='" + bucketName + "'";
			stmt = connection.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {

				so.setId(Long.valueOf(rs.getLong("ID")));
				so.setOwnerId(Long.valueOf(rs.getLong("Owner_ID")));
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
