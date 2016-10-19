package WebScanner;

import java.io.IOException;

public class XSS extends Thread{

	@Override
	public void run() {
		try {
			XSS_NormalTest.main(null);
			XSS_AttackDectect.main(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
