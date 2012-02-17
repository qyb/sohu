/*
 * Copyright (c) 2011,搜狐研发中心
 * All rights reserved.
 * @name TaskType
 * @tasks bladefs_client dealed.
 * @author wanghaiyun 
 * @date 2011-7-19
 */

package com.bladefs.client.de;

public class TaskType {
	/***************************** Client Task ******************************/
	// Client 接到的任务类型
	public final static int Read = 1;
	public final static int Delete = 2;
	public final static int Recover = 3;
	public final static int Write = 4;
}
//End of File