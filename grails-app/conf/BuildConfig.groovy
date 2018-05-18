grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "info" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()
        mavenCentral()
		
//		mavenRepo "http://portal.iminer.com:8091/nexus/content/repositories/thirdparty/"
		mavenRepo "http://portal.iminer.com:8091/nexus/content/repositories/snapshots/"
		mavenRepo "http://portal.iminer.com:8091/nexus/content/repositories/releases/"
		mavenRepo "http://portal.iminer.com:8091/nexus/service/local/repositories/thirdparty/content/"
		mavenRepo "http://portal.iminer.com:8091/nexus/content/groups/public/"
		

        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

         compile 'mysql:mysql-connector-java:5.1.20'
		 
		 compile 'org.apache.poi:poi:3.14'
		 compile 'org.apache.poi:poi-ooxml:3.14'
		 
//		 compile 'com.iminer:common:0.0.15'
		 
		 runtime ('com.iminer:portal-query-client:0.0.5',"com.iminer:irpc-resources-client:0.0.4","com.iminer:irpc-common-client:0.0.6"){
			 exclude group: "org.springframework" ,module:"spring"
		 }
		 compile "commons-httpclient:commons-httpclient:3.1"
		 
		 compile "ant:ant:1.6.5"
		 
		 compile "org.apache.commons:commons-lang3:3.4"
		 
		 compile "joda-time:joda-time:2.7"
		 
		 compile 'com.iminer:moncluster:0.0.4'
		 
		 compile 'javax.mail:mail:1.4.1'
		 
    }

    plugins {
        runtime ":hibernate:$grailsVersion"
        runtime ":jquery:1.8.0"
        runtime ":resources:1.1.6"

        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        //runtime ":yui-minify-resources:0.1.4"

        build ":tomcat:$grailsVersion"

        runtime ":database-migration:1.1"

        compile ':cache:1.0.0'
//		compile ":quartz:1.0.1"
//		compile 'org.grails.plugins:quartz:1.0.2'

//		compile 'org.grails.plugins:quartz:1.0.2'
    }
}
