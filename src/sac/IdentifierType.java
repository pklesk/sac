package sac;

/**
 * Enumeration of identifier types. In the class sac.Identifier a static field type points to one of these types and
 * implies how states (inside search algorithms) shall be identified. The field can be set up by a setter or via a
 * configurator object. With type HASH_CODE (default), states are identified by integers produced via the hashCode()
 * method (to be overridden by the user). With type STRING, states are identified by strings produced via the toString()
 * method (to be overridden by the user).
 * 
 * @author Przemysław Klęsk (<a href="mailto:pklesk@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 * @author Marcin Korzeń (<a href="mailto:mkorzen@wi.zut.edu.pl">wi.zut.edu.pl</a>)
 */
public enum IdentifierType {
	HASH_CODE, STRING
}