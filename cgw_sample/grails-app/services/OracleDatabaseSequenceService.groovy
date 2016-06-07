import groovy.sql.Sql
import org.apache.commons.logging.LogFactory
import org.grails.orm.hibernate.cfg.GrailsDomainBinder
import org.grails.orm.hibernate.cfg.Identity
import org.springframework.jdbc.datasource.DataSourceUtils

class OracleDatabaseSequenceService {

    static transactional = true

    def grailsApplication
    def dataSource
    def sessionFactory

    private static final log = LogFactory.getLog(this)

    public void resetSequence() {
        println("===================== OracleDatabaseSequenceService (CGW) ====================")
        println("--------------------------------")

        Sql sql = Sql.newInstance(DataSourceUtils.getConnection(dataSource))
        sql.connection.autoCommit = false
        def classes = grailsApplication.domainClasses*.clazz
        classes.each {
            // The condition skips classes which are for meant for mongo and allows only the classes meant for posgres
            // to pass through.
            println("Class:"+it)
            println("sessionFactory.getClassMetadata(it):"+sessionFactory.getClassMetadata(it))
            if (sessionFactory.getClassMetadata(it)) {
                def identity = GrailsDomainBinder.getMapping(it)?.identity
                String tableName = sessionFactory.getClassMetadata(it).getTableName()
                println("tableName:"+tableName)

					 Long  maxId = it.createCriteria().get {
                                projections {
                                    max "id"
                                }
                            }
					 println("Max ID ******:"+maxId)
                }
			}
                log.info("Done resetting sequences for all non-assertion classes")
                

                println("sql.commit()")
                sql.commit()
            
        
        println("sql.close()")
        //sql.close()
        println("--------------------------------")



    }
}
