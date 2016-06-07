package cgw_sample


import grails.test.mixin.integration.Integration
import grails.transaction.*
import grails.util.Holders
import spock.lang.*

@Integration
@Rollback
class BookSpec extends Specification {

    def mongoDBName
    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {

        mongoDBName = Holders.config.environments.development.grails.mongodb.databaseName
        expect:"fix me"

        println("==============================================")
        println("mongoDBName: " + mongoDBName)

        println("==============================================")
    }
}
