package sac.examples.rectpacking;

import java.io.IOException;

public class Copier {
	public static synchronized Object copy(Object o)  {
		try {
			byte[] array = Serializer.serializeAsByteArray(o);
			return Serializer.deserializeFromByteArray(array);			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}		
		return null;
	}
}
