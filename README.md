# SEO Friendly URL's Grails Plugin

This is a simple Grails plugin which helps to easily convert any string into a SEO-friendly one, 
eg, from `"The Lord of the Rings"` to `"the-lord-of-the-rings"`.

Useful if you want SEO-friendly URL's like `/book/the-lord-of-the-rings` instead of `/book/show/123`.

The code is borrowed from Wordpress's [formatting.php](http://core.svn.wordpress.org/trunk/wp-includes/formatting.php), 
and initially ported to Groovy by Jesús Lanchas.

## Usage

The plugin provides a simple Grails service, `friendlyUrlService`, which you can inject like any other service in your application.
That service has only one mehod, `sanitizeWithDashes(text)`.

For convenience, the method `asFriendlyUrl()` is also added to the String meta class.

So, given this domain class:
``` groovy
class Book {
	String title
	String sanitizedTitle
	
	def beforeInsert() {
		sanitizedTitle = title.asFriendlyUrl()
	}
	
	static constraints = {
		sanitizedTitle unique:true	//As an alternative, you may decide to make sanitizedTitle replace default id.
	}
}
```

And given the following URL mapping: 
``` groovy
class UrlMappings {

	static mappings = {
		"/book/$title"(controller:'book', action:'show')
	
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

	}
}
```
 
You can do the following in your controller:

``` groovy
class BookController {

	def show() {
		[book: Book.findBySanitizedTitle(params.title)]
	}

}
```

Note that you can also use `friendlyUrlService.sanitizeWithDashes()` in your controller.

## Examples

The following is a snippet of the provided 
[Spock unit test](seo-friendly-urls/blob/master/test/unit/es/salenda/plugins/seo/friendly/urls/FriendlyUrlServiceSpec.groovy):

``` groovy
string                      | sanitized
"The Lord of the Rings"     | "the-lord-of-the-rings"   //Basics
"Raúl González Blanco"      | "raul-gonzalez-blanco"    //Accents
"España"                    | "espana"                  //N-tilde chars
"Los 3 Mosqueteros"         | "los-3-mosqueteros"       //Numbers
"Real Madrid® C.F."         | "real-madrid-cf"          //Edge cases
```