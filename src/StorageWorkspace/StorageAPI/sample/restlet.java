import java.io.IOException;

import org.restlet.Server;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.scss.core.DynamicStreamRepresentation;
import com.scss.core.MultiPartReaderThread;


public class restlet extends ServerResource {  

   public static void main(String[] args) throws Exception {  
      // Create the HTTP server and listen on port 8182  
      new Server(Protocol.HTTP, 8182, restlet.class).start();  
   }

   @Get  
   public String toString() {  
      return "hello, world";  
   }
   
	@Get
	public Representation RequestGET() throws IOException{
//		Request req = this.getRequest();
//		StringBuilder sb = new StringBuilder();
//		for(Product p: req.getClientInfo().getAgentProducts()) {
//			sb.append(p.getName() + " - " + p.getVersion());
//		}
//		byte[] data = sb.toString().getBytes();
//		return new DynamicFileRepresentation(data, data.length, MediaType.TEXT_HTML);

//		java.io.File f = new java.io.File("./Sunset.jpg");
//		return new FileRepresentation(f, MediaType.IMAGE_JPEG);

		//java.io.FileInputStream fs = new java.io.FileInputStream("./Sunset.jpg");
		

		java.io.PipedInputStream pipe_in = new java.io.PipedInputStream();
		java.io.PipedOutputStream pipe_out = new java.io.PipedOutputStream(pipe_in);
		System.out.println("pipe created");
		
		MultiPartReaderThread t = new MultiPartReaderThread(pipe_out);
		t.start();
		System.out.println("thread started");
		
		return new DynamicStreamRepresentation(pipe_in, MediaType.IMAGE_JPEG);
	}

}  