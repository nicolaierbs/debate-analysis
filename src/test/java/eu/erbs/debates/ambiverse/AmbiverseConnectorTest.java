package eu.erbs.debates.ambiverse;

import java.io.IOException;

import org.junit.Test;

public class AmbiverseConnectorTest {

	@Test
	public void test() throws IOException, InterruptedException, ClassNotFoundException {
		AmbiverseConnector.getEntities("Paul McCartney played the guitar with the Beatles in Liverpool.");
		AmbiverseConnector.getEntities("We cannot let it happen. Under my plan, I'll be reducing taxes tremendously, from 35 percent to 15 percent for companies, small and big businesses. That's going to be a job creator like we haven't seen since Ronald Reagan. It's going to be a beautiful thing to watch. Companies will come. They will build. They will expand. New companies will start. And I look very, very much forward to doing it. We have to renegotiate our trade deals, and we have to stop these countries from stealing our companies and our jobs.");  
	}

}
