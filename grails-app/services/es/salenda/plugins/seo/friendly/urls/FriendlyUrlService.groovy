package es.salenda.plugins.seo.friendly.urls

import java.util.regex.Pattern

class FriendlyUrlService {
	
	static transactional = false

	/**
	 * This method transforms the text passed as an argument to a text without spaces,
	 * html entities, accents, dots and extranges characters (only %,a-z,A-Z,0-9, ,_ and - are allowed).
	 *
	 * Borrowed from Wordpress: file wp-includes/formatting.php, function sanitize_title_with_dashes
	 * http://core.svn.wordpress.org/trunk/wp-includes/formatting.php
	 */
	def sanitizeWithDashes(text) {
		// Preserve escaped octets
		text = text.replaceAll('%([a-fA-F0-9][a-fA-F0-9])','---$1---')
		text = text.replaceAll('%','')
		text = text.replaceAll('---([a-fA-F0-9][a-fA-F0-9])---','%$1')

		// Remove accents
		text = removeAccents(text)

		// To lower case
		text = text.toLowerCase()

		// Kill entities
		text = text.replaceAll('&.+?;','')

		// Dots -> ''
		text = text.replaceAll('\\.','')

		// Remove any character except %a-zA-Z0-9 _-
		text = text.replaceAll('[^%a-zA-Z0-9 _-]', '')

		// Trim
		text = text.trim()

		// Spaces -> dashes
		text = text.replaceAll('\\s+', '-')

		// Dashes -> dash
		text = text.replaceAll('-+', '-')

		// It must end in a letter or digit, otherwise we strip the last char
		if (!text[-1].charAt(0).isLetterOrDigit()) text = text[0..-2]

		return text
	}

	/**
	 * Converts all accent characters to ASCII characters.
	 *
	 * If there are no accent characters, then the string given is just returned.
	 *
	 * Borrowed from Wordpress, file wp-includes/formatting.php, function remove_accents
	 * http://core.svn.wordpress.org/trunk/wp-includes/formatting.php
	 */
	private def removeAccents(text) {
		def chars, out

		if(!Pattern.matches(".*[\\x80-\\xFF].*", text)) {
			return text
		}
		else if(seemsUtf8(text)) {
			chars = [
				// Decomposition for Latin-1 Supplement
				"\u00C3\u0080":"A", "\u00C3\u0081":"A", "\u00C3\u0082":"A", "\u00C3\u0083":"A", "\u00C3\u0084":"A", "\u00C3\u0085":"A",
				"\u00C3\u0087":"C", "\u00C3\u0088":"E", "\u00C3\u0089":"E", "\u00C3\u008A":"E", "\u00C3\u008B":"E", "\u00C3\u008C":"I",
				"\u00C3\u008D":"I", "\u00C3\u008E":"I", "\u00C3\u008F":"I", "\u00C3\u0091":"N", "\u00C3\u0092":"O", "\u00C3\u0093":"O",
				"\u00C3\u0094":"O", "\u00C3\u0095":"O", "\u00C3\u0096":"O", "\u00C3\u0099":"U", "\u00C3\u009A":"U", "\u00C3\u009B":"U",
				"\u00C3\u009C":"U", "\u00C3\u009D":"Y", "\u00C3\u009F":"s", "\u00C3\u00A0":"a", "\u00C3\u00A1":"a", "\u00C3\u00A2":"a",
				"\u00C3\u00A3":"a", "\u00C3\u00A4":"a", "\u00C3\u00A5":"a", "\u00C3\u00A7":"c", "\u00C3\u00A8":"e", "\u00C3\u00A9":"e",
				"\u00C3\u00AA":"e", "\u00C3\u00AB":"e", "\u00C3\u00AC":"i", "\u00C3\u00AD":"i", "\u00C3\u00AE":"i", "\u00C3\u00AF":"i",
				"\u00C3\u00B1":"n", "\u00C3\u00B2":"o", "\u00C3\u00B3":"o", "\u00C3\u00B4":"o", "\u00C3\u00B5":"o", "\u00C3\u00B6":"o",
				"\u00C3\u00B9":"u", "\u00C3\u00BA":"u", "\u00C3\u00BB":"u", "\u00C3\u00BC":"u", "\u00C3\u00BE":"y", "\u00C3\u00BF":"y",

				// Decomposition for Latin Extended-A
				"\u00C4\u0080":"A", "\u00C4\u0081":"a", "\u00C4\u0082":"A", "\u00C4\u0083":"a", "\u00C4\u0084":"A", "\u00C4\u0085":"a",
				"\u00C4\u0086":"C", "\u00C4\u0087":"c", "\u00C4\u0088":"C", "\u00C4\u0089":"c", "\u00C4\u008A":"C", "\u00C4\u008B":"c",
				"\u00C4\u008C":"C", "\u00C4\u008D":"c", "\u00C4\u008E":"D", "\u00C4\u008F":"d", "\u00C4\u0090":"D", "\u00C4\u0091":"d",
				"\u00C4\u0092":"E", "\u00C4\u0093":"e", "\u00C4\u0094":"E", "\u00C4\u0095":"e", "\u00C4\u0096":"E", "\u00C4\u0097":"e",
				"\u00C4\u0098":"E", "\u00C4\u0099":"e", "\u00C4\u009A":"E", "\u00C4\u009B":"e", "\u00C4\u009C":"G", "\u00C4\u009D":"g",
				"\u00C4\u009E":"G", "\u00C4\u009F":"g", "\u00C4\u00A0":"G", "\u00C4\u00A1":"g", "\u00C4\u00A2":"G", "\u00C4\u00A3":"g",
				"\u00C4\u00A4":"H", "\u00C4\u00A5":"h", "\u00C4\u00A6":"H", "\u00C4\u00A7":"h", "\u00C4\u00A8":"I", "\u00C4\u00A9":"i",
				"\u00C4\u00AA":"I", "\u00C4\u00AB":"i", "\u00C4\u00AC":"I", "\u00C4\u00AD":"i", "\u00C4\u00AE":"I", "\u00C4\u00AF":"i",
				"\u00C4\u00B0":"I", "\u00C4\u00B1":"i", "\u00C4\u00B2":"IJ", "\u00C4\u00B3":"ij", "\u00C4\u00B4":"J", "\u00C4\u00B5":"j",
				"\u00C4\u00B6":"K", "\u00C4\u00B7":"k", "\u00C4\u00B8":"k", "\u00C4\u00B9":"L", "\u00C4\u00BA":"l", "\u00C4\u00BB":"L",
				"\u00C4\u00BC":"l", "\u00C4\u00BD":"L", "\u00C4\u00BE":"l", "\u00C4\u00BF":"L",
				"\u00C5\u0080":"l", "\u00C5\u0081":"L", "\u00C5\u0082":"l", "\u00C5\u0083":"N", "\u00C5\u0084":"n", "\u00C5\u0085":"N",
				"\u00C5\u0086":"n", "\u00C5\u0087":"N", "\u00C5\u0088":"n", "\u00C5\u0089":"N", "\u00C5\u008A":"n", "\u00C5\u008B":"N",
				"\u00C5\u008C":"O", "\u00C5\u008D":"o", "\u00C5\u008E":"O", "\u00C5\u008F":"o", "\u00C5\u0090":"O", "\u00C5\u0091":"o",
				"\u00C5\u0092":"OE", "\u00C5\u0093":"oe", "\u00C5\u0094":"R", "\u00C5\u0095":"r", "\u00C5\u0096":"R", "\u00C5\u0097":"r",
				"\u00C5\u0098":"R", "\u00C5\u0099":"r", "\u00C5\u009A":"S", "\u00C5\u009B":"s", "\u00C5\u009C":"S", "\u00C5\u009D":"s",
				"\u00C5\u009E":"S", "\u00C5\u009F":"s", "\u00C5\u00A0":"S", "\u00C5\u00A1":"s", "\u00C5\u00A2":"T", "\u00C5\u00A3":"t",
				"\u00C5\u00A4":"T", "\u00C5\u00A5":"t", "\u00C5\u00A6":"T", "\u00C5\u00A7":"t", "\u00C5\u00A8":"U", "\u00C5\u00A9":"u",
				"\u00C5\u00AA":"U", "\u00C5\u00AB":"u", "\u00C5\u00AC":"U", "\u00C5\u00AD":"u", "\u00C5\u00AE":"U", "\u00C5\u00AF":"u",
				"\u00C5\u00B0":"U", "\u00C5\u00B1":"u", "\u00C5\u00B2":"U", "\u00C5\u00B3":"u", "\u00C5\u00B4":"W", "\u00C5\u00B5":"w",
				"\u00C5\u00B6":"Y", "\u00C5\u00B7":"y", "\u00C5\u00B8":"Y", "\u00C5\u00B9":"Z", "\u00C5\u00BA":"z", "\u00C5\u00BB":"Z",
				"\u00C5\u00BC":"z", "\u00C5\u00BD":"Z", "\u00C5\u00BE":"z", "\u00C5\u00BF":"s",

				// Euro sign
				"\u00E3\u0082\u00AC":"E",

				// GBP (Pound) sign
				"\u00C2\u00A3":""]
			// Replacing...
			chars.each{ key, value -> text = text.replace(key, value) }
		} else {
			// Assume ISO-8859-1 if not UTF-8
			chars = [128, 131, 138, 142, 154, 158, 159, 162, 165, 181, 192, 193,
			         194, 195, 196, 197, 199, 200, 201, 202, 203, 204, 205, 206,
			         207, 209, 210, 211, 212, 213, 214, 216, 217, 218, 219, 220, 
			         221, 224, 225, 226, 227, 228, 229, 231, 232, 233, 234, 235, 
			         236, 237, 238, 239, 241, 242, 243, 244, 245, 246, 248, 249, 
			         250, 251, 252, 253, 255]
			out = "EfSZszYcYuAAAAAACEEEEIIIINOOOOOOUUUUYaaaaaaceeeeiiiinoooooouuuuyy"

			chars.eachWithIndex{it,index -> text = text.replace(new String((char)it), new String(out.charAt(index)))}

			// Double chars
			text = text.replace(new String((char)140), "OE")
			text = text.replace(new String((char)156), "oe")
			text = text.replace(new String((char)198), "AE")
			text = text.replace(new String((char)208), "DH")
			text = text.replace(new String((char)222), "TH")
			text = text.replace(new String((char)223), "ss")
			text = text.replace(new String((char)230), "ae")
			text = text.replace(new String((char)240), "dh")
			text = text.replace(new String((char)254), "th")
		}
		return text
	}

	/**
	 * Checks to see if a string is UTF encoded.
	 *
	 * NOTE: This function checks for 5-Byte sequences, UTF8
	 *       has Bytes Sequences with a maximum length of 4.
	 *
	 * Borrowed from Wordpress, file wp-includes/formatting.php, function seems_utf8
	 * http://core.svn.wordpress.org/trunk/wp-includes/formatting.php
	 *
	 * @author bmorel at ssi dot fr (modified)
	 *
	 * @param string str The string to be checked
	 * @return bool True if str fits a UTF-8 model, false otherwise.
	 */
	private def seemsUtf8(str) {
		int c, n

		for (int i=0; i < str.size(); i++) {
			c = (int)str.charAt(i)
			if (c < 0x80) n = 0 // 0bbbbbbb
			else if ((c & 0xE0) == 0xC0) n=1 // 110bbbbb
			else if ((c & 0xF0) == 0xE0) n=2 // 1110bbbb
			else if ((c & 0xF8) == 0xF0) n=3 // 11110bbb
			else if ((c & 0xFC) == 0xF8) n=4 // 111110bb
			else if ((c & 0xFE) == 0xFC) n=5 // 1111110b
			else return false // Does not match any model

			for (int j=0; j < n; j++) { // n bytes matching 10bbbbbb follow ?
				if (++i == str.size() || ((((int)str.charAt(i)) & 0xC0) != 0x80))
					return false
			}
		}
		return true
	}


}
