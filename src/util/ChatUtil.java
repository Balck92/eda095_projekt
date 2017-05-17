package util;

// Bekväma metoder.
public class ChatUtil {
	
	public static StringPair getWhisperNameMessage(String text) {
		text = text.substring(Communication.CHAT_PRIVATE_MESSAGE.length());
		int i, j;
		for (i = 0; i < text.length() && text.charAt(i) == ' '; i++) {}	// Skippa space i början.
		for (j = i; j < text.length() && text.charAt(j) != ' '; j++) {}	// Till första space efter namnet.
		String name = text.substring(i, j);
		if (name.isEmpty() || j >= text.length() - 1) {	// Inget namn eller inget meddelande.
			return null;
		}
		return new StringPair(name, text.substring(j + 1));	// Meddelandet är allt efter space efter namnet.
	}
}
