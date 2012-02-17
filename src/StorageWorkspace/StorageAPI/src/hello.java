import java.io.IOException;

import com.bladefs.client.BladeFSClient;
import com.bladefs.client.exception.BladeFSException;
import com.bladefs.client.exception.NameServiceException;


public class hello {

	/**
	 * @param args
	 * @throws NameServiceException 
	 * @throws BladeFSException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception {
		byte[] data = new byte[2];
		data[0] = 0;
		data[1] = 1;
		int len = 2;
		BladeFSClient client = null;
		try {
			client = new BladeFSClient(".\\conf\\client.properties");	
			//long fn = client.write(data, len);
			//System.out.println(fn);// file name
			
			long fn = 38654708895L;
			byte[] info = client.read(fn); // file data
			for (int i = 0; i<info.length; i++)
				System.out.println(info[i]);
			
//			if (client.delete(fn))
//				System.out.println("Deleted");
//			else
//				System.out.println("Delete fail.");
			
//			if (client.recover(fn))
//				System.out.println("recovered");
//			else
//				System.out.println("recoverd fail.");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (client != null) client = null;
		}

	}

}
