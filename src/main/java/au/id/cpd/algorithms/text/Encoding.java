/**
 * 
 */
package au.id.cpd.algorithms.text;

/**
 * An enumeration representing the available
 * encodings allowed by Java Strings
* Character set to use to decode data.
US-ASCII  	Seven-bit ASCII, a.k.a. ISO646-US, a.k.a. the Basic Latin block of the Unicode character set
ISO-8859-1   	ISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1
UTF-8 	Eight-bit UCS Transformation Format
UTF-16BE 	Sixteen-bit UCS Transformation Format, big-endian byte order
UTF-16LE 	Sixteen-bit UCS Transformation Format, little-endian byte order
UTF-16 	Sixteen-bit UCS Transformation Format, byte order identified by an optional byte-order mark
 * @author Chris Davey cd@cpd.id.au
 *
 */
public enum Encoding {
	
	/**
	 * US-ASCII  	Seven-bit ASCII, a.k.a. ISO646-US, a.k.a. the Basic Latin block of the Unicode character set
	 */
	US_ASCII("US-ASCII"),
	/**
	 * ISO-8859-1   	ISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1
	 */
	ISO_8859_1("ISO-8859-1"),
	/**
	 * UTF-8 	Eight-bit UCS Transformation Format
	 */
	UTF_8("UTF-8"),
	/**
	 * UTF-16BE 	Sixteen-bit UCS Transformation Format, big-endian byte order
	 */
	UTF_16BE("UTF-16BE"),
	/**
	 * UTF-16LE 	Sixteen-bit UCS Transformation Format, little-endian byte order
	 */
	UTF_16LE("UTF-16LE"),
	/**
	 * UTF-16 	Sixteen-bit UCS Transformation Format, byte order identified by an optional byte-order mark
	 */
	UTF_16("UTF-16");
 
	
	private String _name;
	
	private Encoding(String name) {
		_name = name;
	}
	
	public String toString() {
		return _name;
	}
 
}
