
public class MailboxThread extends Thread {

	private Mailbox mailbox;
	
	public MailboxThread(String name, Mailbox mailbox) {
		super(name);
		this.mailbox = mailbox;
	}
	
	public void run() {
		for (int i = 0; i < 5; i++) {
			try {
				sleep((long) (Math.random() * 1000.0));
				mailbox.setMessage(getName());
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
