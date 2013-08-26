package es.salenda.plugins.seo.friendly.urls

import grails.plugin.spock.UnitSpec

class FriendlyUrlServiceSpec extends UnitSpec {

	def "when using service method strings are sanitized"() {
		setup:
		def friendlyUrlService = new FriendlyUrlService()
		
		expect:
		friendlyUrlService.sanitizeWithDashes(string) == sanitized
		
		where:
		string						| sanitized
		""							| ""						//Empty string
		"The Lord of the Rings"		| "the-lord-of-the-rings"	//Basics
		"Raúl González Blanco"		| "raul-gonzalez-blanco"	//Accents
		"España"					| "espana"					//N-tilde chars
		"Los 3 Mosqueteros"			| "los-3-mosqueteros"		//Numbers
		"Real Madrid® C.F."			| "real-madrid-cf"			//Edge cases						
	}
	
}
