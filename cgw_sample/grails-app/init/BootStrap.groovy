import grails.util.Environment
import grails.util.Holders
import org.apache.commons.lang.time.DateUtils
import org.apache.commons.logging.LogFactory

class BootStrap {
    def grailsApplication
    private static final log = LogFactory.getLog(BootStrap.class)
    def oracleDatabaseSequenceService
    def knowledgeBaseService
    def ontologyService
    def mongo
    def dataSourceUnproxied

    def init = { servletContext ->

        dataSourceUnproxied.setMinEvictableIdleTimeMillis(1800000)
        dataSourceUnproxied.setTimeBetweenEvictionRunsMillis(1800000)
        dataSourceUnproxied.setNumTestsPerEvictionRun(3)

        dataSourceUnproxied.setTestOnBorrow(true)
        dataSourceUnproxied.setTestWhileIdle(false)
        dataSourceUnproxied.setTestOnReturn(false)
        dataSourceUnproxied.setValidationQuery("SELECT 1")

        dataSourceUnproxied.properties.each { println it }

        def current = Environment.current

        System.out.println("--------------------------"+current)

        oracleDatabaseSequenceService.resetSequence()
      
    }



    def destroy = {
    }
}
