package util;

// Bekv�ma metoder.
public class ChatUtil {
	
	public static StringPair getWhisperNameMessage(String text) {
		String nameAndMessage = text.substring(Communication.CHAT_PRIVATE_MESSAGE.length());
		int i, j;
		for (i = 0; i < text.length() && nameAndMessage.charAt(i) == ' '; i++) {}	// Skippa space i b�rjan.
		for (j = i; j < text.length() && nameAndMessage.charAt(j) != ' '; j++) {}	// Till f�rsta space efter namnet.
		String name = nameAndMessage.substring(i, j);
		if (name.isEmpty() || j >= text.length() - 1) {	// Inget namn eller inget meddelande.
			return null;
		}
		return new StringPair(name, nameAndMessage.substring(j + 1));	// Meddelandet �r allt efter space efter namnet.
	}
}
