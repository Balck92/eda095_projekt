
public class MailboxReader extends Thread {

	public static void main(String[] args) {
		Mailbox mailbox = new Mailbox();
		int numThreads = 3;
		Thread[] threads = new Thread[numThreads];
		for (int i = 0; i < numThreads; i++) {
			threads[i] = new MailboxThread("Thread " + i, mailbox);
		}
		Thread reader = new MailboxReader(mailbox);

		for (int i = 0; i < numThreads; i++) {
			threads[i].start();
		}
		reader.start();
	}

	private Mailbox mailbox;

	public MailboxReader(Mailbox mailbox) {
		this.mailbox = mailbox;
	}

	public void run() {
		while (true) {
			try {
				String message = mailbox.getMessage();
				System.out.println(message);
				sleep((long) (1000.0 * Math.random()));
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
