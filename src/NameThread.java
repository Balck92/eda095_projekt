
public class NameThread extends Thread {
	
	public static void main(String[] args) {
		for (int i = 1; i <= 10; i++) {
			new NameThread("Thread " + i).start();
		}
	}
	
	public NameThread(String name) {
		super(name);
	}

	public void run() {
		for (int i = 0; i < 5; i++) {
			System.out.println(getName());
			try {
				sleep((long) (Math.random() * 1000.0));
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
