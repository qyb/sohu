package com.bladefs.client.io.cmd;

import java.io.DataInputStream;
import java.io.IOException;

import com.bladefs.client.util.MD5Utils;

public class PackUnit {
	public int getContentLen(DataInputStream in) throws IOException {
		int len = in.readInt();
		return len; // content length
	}
	
	public boolean checksumOK(byte[] src, byte[] checksum) {
		if(src == null | checksum == null)
			return false;
		
		byte[] res;
		try {
			res = MD5Utils.md5(src);
			for (int i = 0; i < 16; i++) {
				if (res[i] != checksum[i])
					return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public short bytes2Short(byte a, byte b) {
		int aa = a & 0xff;
		int bb = b & 0xff;
		 if ((aa | bb) < 0)
		 return -1;

		return (short) ((aa << 8) + (bb << 0));
	}

	public int bytes2Int(byte a, byte b, byte c, byte d) throws IOException {
		int aa = a & 0xff;
		int bb = b & 0xff;
		int cc = c & 0xff;
		int dd = d & 0xff;
		if ((aa | bb | cc | dd) < 0)
			return -1;

		return ((aa << 24) + (bb << 16) + (cc << 8) + (dd << 0));
	}

	public String ints2IP(int a, int b, int c, int d) {
		return Integer.toString(a) + '.' + Integer.toString(b) + '.'
				+ Integer.toString(c) + '.' + Integer.toString(d);
	}

	public long bytes2Long(byte[] b) {
		if (b == null || b.length != 8) {
			return -1;
		}

		long l = 0;
		l |= ((long) (b[0] & 0xff) << 56);
		l |= ((long) (b[1] & 0xff) << 48);
		l |= ((long) (b[2] & 0xff) << 40);
		l |= ((long) (b[3] & 0xff) << 32);
		l |= ((long) (b[4] & 0xff) << 24);
		l |= ((long) (b[5] & 0xff) << 16);
		l |= ((long) (b[6] & 0xff) << 8);
		l |= (b[7] & 0xff);
		return l;
	}
	
	public int storeIntBytes(DataInputStream in, byte[] a, int offset)
			throws IOException {
		int res = -1;
		byte tmpa = in.readByte();
		byte tmpb = in.readByte();
		byte tmpc = in.readByte();
		byte tmpd = in.readByte();
		res = bytes2Int(tmpa, tmpb, tmpc, tmpd);
		if (res != -1) {
			a[offset++] = tmpa;
			a[offset++] = tmpb;
			a[offset++] = tmpc;
			a[offset++] = tmpd;
		}

		return res;
	}

	public short storeShortBytes(DataInputStream in, byte[] a, int offset)
			throws IOException {
		short res = -1;
		int index = offset;
		byte tmpa = in.readByte();
		byte tmpb = in.readByte();
		res = bytes2Short(tmpa, tmpb);
		if (res != -1) {
			a[index++] = tmpa;
			a[index++] = tmpb;
		}

		return res;
	}
	
	public boolean reachBottom(DataInputStream in) throws IOException {
		boolean ret = false;
		try {
				in.readByte();
		} catch (IOException e) {
				ret = true;
		}
		return ret;
	}
}
