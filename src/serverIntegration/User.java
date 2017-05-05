package serverIntegration;

import java.io.Writer;

public class User {

	private String userName;
	private Writer writer;
	
	public User(String userName, Writer writer) {
		this.userName = userName;
		this.writer = writer;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof User) {
			return ((User) other).userName.equals(userName);
		}
		return false;
	}
	
	public String getName() {
		return userName;
	}
	
	public Writer getWriter() {
		return writer;
	}
	
	@Override
	public String toString() {
		return userName;
	}
	
	@Override
	public int hashCode() {
		return userName.hashCode();
	}
}
