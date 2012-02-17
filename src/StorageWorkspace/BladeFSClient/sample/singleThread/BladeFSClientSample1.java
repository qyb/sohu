import com.bladefs.client.BladeFSClient;

public class BladeFSClientSample1{
	public static void main(String args[]) throws Exception {
		byte[] data = new byte[2];
		data[0] = 0;
		data[1] = 1;
		int len = 2;
		BladeFSClient client = new BladeFSClient(".\\conf\\client.properties");	
		long fn = client.write(data, len);
		System.out.println(fn);// file name
		byte[] info = client.read(fn); // file data
	}
}