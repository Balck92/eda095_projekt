package util;

// Bekväma metoder.
public class ChatUtil {
	
	public static StringPair getWhisperNameMessage(String text) {
		String nameAndMessage = text.substring(Communication.CHAT_PRIVATE_MESSAGE.length());
		int i, j;
		for (i = 0; i < text.length() && nameAndMessage.charAt(i) == ' '; i++) {}	// Skippa space i början.
		for (j = i; j < text.length() && nameAndMessage.charAt(j) != ' '; j++) {}	// Till första space efter namnet.
		String name = nameAndMessage.substring(i, j);
		if (name.isEmpty() || j >= text.length() - 1) {	// Inget namn eller inget meddelande.
			return null;
		}
		return new StringPair(name, nameAndMessage.substring(j + 1));	// Meddelandet är allt efter space efter namnet.
	}
}
