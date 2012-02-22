package com.bladefs.client.de;

public class Protocol {
	
	public final static short WRITE_ON = 0;
	public final static short READ_ONLY = 1;
	public final static short NO_SERVICE = 2;
	public final static short OUT_OF_DATE = 3;
	
	/***************************** 协议 ******************************/
	///////////////////////Data Service//////////////////////
	public final static int Client2DS_READ = 0x1001;
	public final static int Client2DS_WRITE = 0x1002;
	public final static int Client2DS_DELETE = 0x1003;
	public final static int Client2DS_RECOVER = 0x1004;
	public final static int DS2Client_READ = 0x2001;
	public final static int DS2Client_WRITE = 0x2002;
	public final static int DS2Client_DELETE = 0x2003;
	public final static int DS2Client_RECOVER = 0x2004;
	
	////////////////Name Service/////////////////////////////
	public final static int Client2NS_ReqGStat = 0x3001;
	public final static int Client2NS_ReqGroup = 0x3002;
	public final static int Client2NS_ReqBlock = 0x3003;
	public final static int Client2NS_ReqCheck = 0x3004;
	public final static int NS2Client_ReqGStat = 0x4001;
	public final static int NS2Client_ReqGroup = 0x4002;
	public final static int NS2Client_ReqBlock = 0x4003;
	public final static int NS2Client_ReqCheck = 0x4004;
	
	/****************************length******************************/ 
	public final static int keylen  = 2;
	public final static int checksumlen  = 16;
	
	/************************ NameService****************************/ 
	public final static int verlen  = 4;
	public final static int errlen  = 4;
	public final static int blocksumlen  = 4;
	public final static int groupsumlen  = 4;
	public final static int blockstatlen  = 1;
	public final static int groupIDlen  = 2;
	public final static int machinesumlen  = 4;
	public final static int IPlen  = 4;
	public final static int portlen  = 2;
	public final static int masterlen  = 1;
	public final static int groupstatlen  = 1;
	public final static int groupweightlen  = 1; 
	
	/************DataService**************/ 
	public final static int fileNamelen  = 8;
	
	/************DataService ERROR**************/ 
	public final static int beyondSize  = -4;
	public final static int serverPause  = -3;
	public final static int netOutOfTime  = -2;
	public final static int serverException  = -1;
}
