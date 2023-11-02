package cz.cez.trading.algo.interview.shared;

/**
 * Represents a type of change happening to some entity.
 */
public enum Operation {
	/**
	 * Entity was created or updated.
	 */
	SET,

	/**
	 * Entity was deleted.
	 */
	DELETE
}
