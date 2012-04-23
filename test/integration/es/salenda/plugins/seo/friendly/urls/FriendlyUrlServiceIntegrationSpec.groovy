package es.salenda.plugins.seo.friendly.urls

import grails.plugin.spock.IntegrationSpec

class FriendlyUrlServiceIntegrationSpec extends IntegrationSpec {

	def "String dynamic method is injected"() {
		setup:
		def friendlyUrlService = new FriendlyUrlService()
				
		expect:
		string.asFriendlyUrl() == friendlyUrlService.sanitizeWithDashes(string)
		string.asFriendlyUrl() == "the-lord-of-the-rings"
		
		where:
		string = "The Lord of the Rings"
	}
	
	
}
