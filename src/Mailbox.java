
public class Mailbox {

	private String message;
	
	public synchronized boolean hasMessage() {
		return message != null;
	}
	
	public synchronized void setMessage(String message) {
		while (hasMessage()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		this.message = message;
		
		notifyAll();
	}
	
	public synchronized String getMessage() {
		while (!hasMessage()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		String mess = message;
		message = null;
		
		notifyAll();
		
		return mess;
	}
}
