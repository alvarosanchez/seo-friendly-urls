class SeoFriendlyUrlsGrailsPlugin {
    // the plugin version
    def version = "1.0.2"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    def title = " " // Headline display name of the plugin
    def author = "Álvaro Sánchez-Mariscal"
    def authorEmail = "alvaro.sanchez@salenda.es"
    def description = '''\
Helps to easily convert any string into a SEO-friendly one, eg from "The Lord of the Rings" to "the-lord-of-the-rings".

Useful if you want SEO-friendly URL's like /book/the-lord-of-the-rings instead of /book/show/123.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/seo-friendly-urls"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "GPL2"

    // Details of company behind the plugin (if there is one)
    def organization = [ name: "Salenda", url: "http://www.salenda.es" ]

    // Any additional developers beyond the author specified above.
    def developers = [ 
		[ name: "Jesús Lanchas", email: "jesus.lanchas@daureos.com" ]
	]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "http://github.com/alvarosanchez/seo-friendly-urls" ]
	
    def doWithDynamicMethods = { ctx ->
		def friendlyUrlService = ctx.friendlyUrlService
		
        String.metaClass.asFriendlyUrl = { ->
			friendlyUrlService.sanitizeWithDashes(delegate)
		}
    }

}
