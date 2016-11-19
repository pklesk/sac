package sac.util;

/**
 * Simple logger to the console (System.out) for SaC purposes.
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public class ConsoleLogger {

	/**
	 * Outputs a message to the console prefixed by '[SaC] '.
	 * 
	 * @param message message to be output
	 */
	public static void info(String message) {
		System.out.println("[SaC] " + message);
	}
}
