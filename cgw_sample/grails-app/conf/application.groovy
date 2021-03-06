//CGW-COMMON IMPORTS
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.time.DateUtils

import static java.io.File.separatorChar

//CGW
import org.apache.commons.io.FilenameUtils

//changed 
dataSource {
    url = "jdbc:postgresql://localhost:5432/cgw"
    driverClassName = "org.postgresql.Driver"
    username = "cgw_pat"
    password = "cgw_pat"
    dialect="org.hibernate.dialect.PostgreSQLDialect"
    loggingSql=true
    pooled= true
    jmxExport= true

}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.queries = false
    cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory'
//    cache.region.factory_class = 'org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory' //for cgw_common "maybe"
    cache.singleSession = true // configure OSIV singleSession mode
    cache.flush.mode = 'manual' // OSIV session flush mode outside of transactional context

}

/** --- Environment specific settings --- */ //changed 
environments {
	development {
        grails {
            mongodb {
                host = "localhost"
                port = 27017
				databaseName = "cgw"
            }
        }
		dataSource {
            dbCreate: update
            url= "jdbc:postgresql://localhost:5432/cgw"
			username = "cgw_pat"
			password = "cgw_pat"
			loggingSql=true
            pooled= true
            jmxExport= true
            jmxEnabled: true
		}
	}

}

//When we use grails basedir, we are getting empty map "[:]", so here we are fetching base dir using system properties
def basedir = System.properties['base.dir']

//Change this to alter the default package name and Maven publishing destination
grails.project.groupId = appName

grails{
    cache {
        order = 2000 // higher than default (1000) and plugins, usually 1500
        enabled = true
        clearAtStartup=true // reset caches when redeploying
        ehcache {
            reloadable = false
        }
    }
}
def uniqueCacheManagerName = appName + "ConfigEhcache-" + System.currentTimeMillis()

// customize temp ehcache cache manager name upon startup
grails.cache.ehcache.cacheManagerName = uniqueCacheManagerName

grails.cache.config = {
    provider {
        updateCheck false
        monitoring 'on'
        dynamicConfig false
        // unique name when configuring caches
        name uniqueCacheManagerName
    }
    defaultCache {
        maxElementsInMemory 10000
        eternal false
        timeToIdleSeconds 120
        timeToLiveSeconds 120
        overflowToDisk false // no disk use, this would require more config
        maxElementsOnDisk 10000000
        diskPersistent false
        diskExpiryThreadIntervalSeconds 120
        memoryStoreEvictionPolicy 'LRU' // least recently used gets kicked out
    }
}

//Enables the parsing of file extensions from URLs into the request format
grails.mime.file.extensions = true
grails.mime.use.accept.header = true
grails.mime.disable.accept.header.userAgents = ['None']
grails.mime.types = [html: ['text/html', 'application/xhtml+xml'],
                     xml: ['text/xml', 'application/xml'],
                     text: 'text/plain',
                     txt: 'text/plain',
                     js: 'text/javascript',
                     rss: 'application/rss+xml',
                     atom: 'application/atom+xml',
                     css: 'text/css',
                     csv: 'text/csv',
                     all: '*/*',
                     json: ['application/json', 'text/json'],
                     form: 'application/x-www-form-urlencoded',
                     vcf: 'text/plain',
                     pdf: 'application/pdf',
                     doc: 'application/msword',
                     multipartForm: 'multipart/form-data',
                     default: 'application/octet-stream',
                     out_D: 'text/plain',
                     out_BP: 'text/plain',
                     out_INV: 'text/plain',
                     out_LI: 'text/plain',
                     out_SI: 'text/plain',
                     out_TD: 'text/plain'
]

home.user.guide = "https://tools.pieriandx.com/confluence/display/CGWP/CGW+User+Guide"
home.faq.section = "https://tools.pieriandx.com/confluence/display/CGWP/FAQs"
/**
 * URL Mapping Cache Max Size, defaults to 5000
 * grails.urlmapping.cache.maxsize = 1000
 * The default codec used to encode data with ${}* To avoid the codec use '<%@page defaultCodec="none" %>' in .gsp or
 * use <%= %> instead of EL at expression level
 */
grails.views.default.codec = "html" //none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"

//Enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true

//Scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

//Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false

//Enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true

//Whether to install the java.util.logging bridge for sl4j. Disable for AppEngine!
grails.logging.jul.usebridge = true

//Packages to include in Spring bean scanning
grails.spring.bean.packages = []

//Request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']


/** ------------------------------- FROM CGW-COMMON --------------------------------- */
if (new File("${userHome}/.${appName}/${appName}-config.groovy").exists()) {
    grails.config.locations = ["file:${userHome}/.${appName}/${appName}-config.groovy"]
    println("loading configuration from: :${grails.config.locations}")
} else {
    println("No external configuration file defined.....")
}

/**GORM settings starts here*/
//If set to true, causes the save() method on domain classes to throw a grails.validation.ValidationException if validation fails during a save
grails.gorm.failOnError = true

//CGW Plugin (@since 1.5)
cgw.plugin.local.cache.location = "${userHome}${separatorChar}cgw-cache"
cgw.plugins.folder = "${userHome}${separatorChar}cgw_plugins"

//Plugin Repository @since 1.5
cgw.plugin.repositories.central.name = "Central Repository"
cgw.plugin.repositories.central.implementorClass = "edu.wustl.cgw.plugin.repository.HttpRepository"
cgw.plugin.repositories.central.location = "http://localhost:9898/appstore"

//local repository
cgw.plugin.repositories.local.name = "Local Repository"
cgw.plugin.repositories.local.implementorClass = "edu.wustl.cgw.plugin.repository.LocalFileSystemRepository"
cgw.plugin.repositories.local.location = "${userHome}${separatorChar}cgw-local-repo"

//plugin notifications
cgw.admin.panelNotificationTemplate = "src${separatorChar}groovy${separatorChar}edu${separatorChar}wustl${separatorChar}cgw${separatorChar}plugin${separatorChar}notification${separatorChar}pluginInstalled.ftl"

//child plugin org
cgw.panel.child.org = "local.panel"

grails.gorm.default.constraints = {
    /**
     * A shared constraint that allows alphanumerics and spaces
     * Usage:
     * In any domain class add as shown below:
     * static constraints = {*     propertyName(shared:'allowAlphanumericSpace')
     *}*/
    fileNameValidator(validator: { String val, obj ->
        //http://en.wikipedia.org/wiki/Filename#Reserved_characters_and_words
        if (StringUtils.containsAny(val, "/\\?%*:|\"<>. !@#\$^&+=';,")) {
            return ['alphanumeric.message']
        }
    })
}


grails.plugin.springsecurity.active = false

/** DATA MIGRATION RELATED*/
grails.plugin.databasemigration.updateOnStart = true
//grails.plugin.databasemigration.updateOnStartFileNames = ["changelog.groovy"]
grails.plugin.databasemigration.updateOnStartFileName = 'changelog.groovy'
grails.plugin.databasemigration.changelogLocation = 'migrations'
//--------------------


cgw.privateKey.masterPassword = '4fuPFI0aoz'
cgw.ui.metadata = [
        variant : [
                ["path": "geneName", "label": "Gene", "type": "string", "autocomplete": "true"],
                ["path": "gSyntax","label": "DNA change","type": "string", "autocomplete": "true"],
                ["path": "cSyntaxes","children":[
                        ["path": "pSyntax","label": "AA change","type": "string","autocomplete": "true", "renderer":"Multi-Line"]
                ]],
                ["path": "variantType","label": "Variant type/subtype","type": "enum","options": [
                        ["label": "Deletion","value": "DELETION"],
                        ["label": "Indel","value": "INDEL"],
                        ["label": "Insertion","value": "INSERTION"],
                        ["label": "Substitution","value": "SUBSTITUTION"],
                        ["label": "Copy Number Variant","value": "COPY_NUMBER"],
                        ["label": "Intrachromosomal Rearrangement - Inversion","value": "INVERSION"],
                        ["label": "Intrachromosomal Rearrangement - Reciprocal Translocation","value": "RECIPROCAL_TRANSLOCATION"],
                        ["label": "Intrachromosomal Rearrangement - Tandem Duplication","value": "TANDEM_DUPLICATION"]
                ]],
                ["path": "classification","label": "Classification","autocomplete": "true"],
                ["path": "sources","label": "Other sources","autocomplete": "true"],
                ["path": "consequence","label": "Consequence","autocomplete": "true"],
                ["path": "readDepth","label": "GATK Read Depth","type": "long"],
                [ "path": "NHLBI_ESP","children": [
                        [ "path": "africanAmericanGenotypeCounts", "children": [
                                [ "path": "genotype", "label": "African American Genotype", "type": "string","autocomplete": "true" ],
                                [ "path": "count", "label": "African American Genotype Count", "type": "long" ]
                        ]]
                ]],
                [ "path": "dbSNP", "children": [
                        ["path":"globalMinorAlleleFrequency","type":"long","label":"Global Minor Allele Frequency"],
                        ["path":"variantAlleleOrigin","type":"long","label":"Variant Allele Origin"],
                        [
                                "path": "greaterThanFivePercentMinorAlleleFrequencyInOneOrMorePopulations",
                                "label": "greaterThanFivePercentMinorAlleleFrequencyInOneOrMorePopulations",
                                "type": "string",
                                "autocomplete": "true"
                        ],
                        [ "path": "variationIsValidated", "label": "variationIsValidated", "type": "string","autocomplete": "true"]
                ]],
                ["path":"averageQualityVariantSupportingBase","type":"long","label":"Average quality of variant-supporting bases"],
                ["path":"lib1Maf","type":"long","label":"Library 1 VAF"],
                ["path":"lib2Maf","type":"long","label":"Library 2 VAF"],
                ["path":"vafVarScan","type":"long","label":"VAF"],
                ["path":"phredQualityScore","type":"long","label":"Phred Quality Score"],
                ["path":"homopolymerRun","type":"long","label":"Homopolymer Run"],
                ["path": "zygosity", "label": "Zygosity", "type": "string"],
                ["path":"pvalFromVarScan","type":"long","label":"P-value from VarScan"],
                ["path":"hotspotVariant","type":"long","label":"Hotspot variant"],
                ["path":"mafVariantForwardReads","type":"long","label":"VAF of variants on forward reads"],
                ["path":"mafVariantReverseReads","type":"long","label":"VAF of variants on reverse reads"],
                ["path":"strandBias","type":"long","label":"Strand bias"],
                ["path":"phenotypes","label": "Phenotypes","code":"diseaseCode","codeSystem":"HPO","type":"ontology","pathId":"phenotypes.path"]
        ],
        case: [
                ["path":"assignmentValue","label":"Assignee","type":"enum","options":[
                        ["label":"Me","value":"Me"],
                        ["label":"Any","value":"Any"]
                ]],
                ["path":"primarySpecimenAccessionNumber","label":"Case accession number","type":"string"],
                ["path":"patientName","label":"Patient name","type":"string"],
                ["path":"physicianName","label":"Physician name","type":"string"],
                ["path":"sampleType","label":"Sample type","type":"enum","options": [
                        ["label": "Patient Care Sample", "value": "PATIENT_CARE"],
                        ["label": "Clinical Trial Sample", "value": "CLINICAL_TRIAL"],
                        ["label": "Validation Sample", "value": "VALIDATION"],
                        ["label": "Proficiency Testing Sample", "value": "PROFICIENCY_TESTING"]
                ]],
                ["path":"diseaseCodeLabel","label":"Disease","code":"diseaseCode","codeSystem":"SNOMEDCT","type":"ontology","pathId":"diseasePath"],
                ["path":"primarySpecimenCodeLabel","label":"Specimen type","code":"primarySpecimenCode","codeSystem":"SNOMEDCT","type":"ontology","pathId":"primarySpecimenPath"],
                ["path":"panelIndication","label":"Indication","type": "string"],
                ["path":"dateCreated","label":"Date created","type":"date"],
                ["path":"signOutDate","label":"SignOut date","type":"date"],
                ["path":"reportStatus","label":"Report Status","type": "enum","options": [
                        ["label": "Preliminary", "value": "Preliminary"],
                        ["label": "Preliminary/Amended","value":"Preliminary_Amended"],
                        ["label": "Final","value":"Final"],["label":"Final/Amended","value":"Final_Amended"],
                        ["label":"Preliminary/Addended","value":"Preliminary_Addended"],
                        ["label":"Final/Addended","value":"Final_Addended"]
                ]],
                ["path":"primarySpecimenExternalId","label":"External specimen ID","type":"string"],
                ["path": "panelName","label":"Procedure name","type":"autocompleter"],
                ["path":"sequencingRunId","label":"Sequencer run ID","type":"string"],
                ["path": "jobStatus","label": "Job Status","type": "enum","options":[
                        ["label":"Waiting","value":"WAITING"],
                        ["label":"Ready","value":"READY"],
                        ["label":"Running","value":"RUNNING"],
                        ["label":"Generating","value":"GENERATING"],
                        ["label":"Completed","value":"COMPLETE"],
                        ["label":"Completed but no report generated","value":"COMPLETE_BUT_REPORT_NOT_GENERATED"],
                        ["label":"Failed","value":"FAILED"],
                        ["label":"Canceled","value":"CANCELED"],
                        ["label":"Held","value":"HELD"],
                        ["label":"System error","value":"ERROR"],
                        ["label":"Job files available","value":"JOB_FILES_AVAILABLE"],
                        ["label":"Job files downloading","value":"JOB_FILES_DOWNLOADING"],
                        ["label":"Job files downloaded","value":"JOB_FILES_DOWNLOADED"],
                        ["label":"Job files download failed","value":"JOB_FILES_DOWNLOAD_FAILED"],
                        ["label":"Syntax generation complete","value":"SYNTAX_GENERATION_COMPLETE"],
                        ["label":"Syntax generation failed","value":"SYNTAX_GENERATION_FAILED"],
                        ["label":"Syntax generation running","value":"SYNTAX_GENERATION_RUNNING"],
                        ["label":"Data load complete","value":"DATA_LOAD_COMPLETE"],
                        ["label":"Data load failed","value":"DATA_LOAD_FAILED"],
                        ["label":"Data load running","value":"DATA_LOAD_RUNNING"],
                        ["label":"Generated database views","value":"VIEW_LOAD_COMPLETE"],
                        ["label":"Database view load failed","value":"VIEW_LOAD_FAILED"],
                        ["label":"Database view load running","value":"VIEW_LOAD_RUNNING"],
                        ["label":"Rule processing complete","value":"RULE_PROCESSING_COMPLETE"],
                        ["label":"Rule processing failed","value":"RULE_PROCESSING_FAILED"],
                        ["label":"Rule processing running","value":"RULE_PROCESSING_RUNNING"],
                        ["label":"Report generation complete","value":"REPORT_GENERATION_COMPLETE"],
                        ["label":"Report generation failed","value":"REPORT_GENERATION_FAILED"],
                        ["label":"Report generation running","value":"REPORT_GENERATION_RUNNING"],
                        ["label":"Phenotype analysis running","value":"PHENOTYPE_ANALYSIS_RUNNING"],
                        ["label":"Phenotype analysis complete","value":"PHENOTYPE_ANALYSIS_COMPLETE"],
                        ["label":"Phenotype analysis failed","value":"PHENOTYPE_ANALYSIS_FAILED"]
                ]],
                ["path": "caseStatus","label": "Case Status","type": "enum","options": [
                        ["label": "Pending EHR", "value": "Pending EHR"],
                        ["label": "Sign-out Error", "value": "Sign-out Error"],
                        ["label":"Signed Out","value":"Signed Out"],
                        ["label":"Under review","value":"Under review"]
                ]],
                ["path": "reviewStatus","label": "Review Status","type": "enum","options": [
                        ["label": "Approved", "value": "APPROVED"],
                        ["label": "Rejected", "value": "REJECTED"],
                        ["label": "Draft", "value": "DRAFT"],
                        ["label": "Canceled", "value": "CANCELED"]
                ]],
                ["path": "dateAssigned","label": "Date assigned","type":"date"],
                ["path": "dateDue","label": "Date due","type":"date"],
                ["path":"studyId","label":"Study ID","type":"string"],
                ["path":"sequencingLane","label":"Sample lane","type":"string"],
                ["path": "inheritancePattern","label":"Inheritance pattern","type": "enum","options": [
                        ["label": "Autosomal dominant", "value": "AUTOSOMAL_DOMINANT"],
                        ["label": "Autosomal recessive", "value": "AUTOSOMAL_RECESSIVE"],
                        ["label": "X-linked dominant", "value": "X-LINKED_DOMINANT"],
                        ["label": "X-linked recessive", "value": "X-LINKED_RECESSIVE"],
                        ["label": "Somatic", "value": "SOMATIC"]
                ]],
                ["path":"phenotypes","label": "Phenotypes","code":"diseaseCode","codeSystem":"HPO","type":"ontology","pathId":"phenotypes.path"]
        ],
        plugin: [],
        literature: [
                ["path": "outcome", "label": "Outcome", "type": "string"],
                ["path": "classification", "label": "Classification", "autocomplete": "true", "type": "string"],
                ["path": "reference", "label": "Reference", "type": "string"],
                ["path": "publicationDate", "label": "Publication Date", "type": "date"],
                ["path": "populationSize", "label": "Population Size", "type": "long"],
                ["path": "excerpt", "label": "Excerpt", "type": "string"]
        ],
        interpretation: [
                ["path": "publisher", "label": "Publisher", "autocomplete": "true", "type": "string"],
                ["path": "gene", "label": "Gene", "autocomplete": "true", "type": "string"],
                ["path": "diseases.code", "label": "Disease", "type":"ontology", "pathId":"diseases.paths"],
                ["path": "nomenclature", "label": "Nomenclature", "type": "string"],
                ["path": "classification", "label": "Classification", "autocomplete": "true", "type": "string"],
                ["path": "interpretation", "label": "Interpretation", "type": "string", "orPath":"keywords"],
                ["path": "revision", "label": "Version", "type": "long"]
        ],
        clinicalInterpretation: [
                ["path": "publisher","label": "Publisher","autocomplete": "true","type":"string"],
                ["path": "gene","label": "Gene","autocomplete": "true","type":"string"],
                ["path": "diseases.code", "label": "Disease", "type":"ontology", "pathId":"diseases.paths"],
                ["path": "nomenclature","label": "Nomenclature","type":"string"],
                ["path": "classification","label":"Classification", "autocomplete": "true", "type":"string"],
                ["path": "interpretation","label": "Interpretation","type":"string", "orPath":"keywords"],
                ["path":"status","label":"Status","type":"enum","options": [
                        ["label":"Draft","value":"DRAFT"],
                        ["label":"Approved","value":"APPROVED"],
                        ["label":"Published","value":"PUBLISHED"],
                        ["label":"Unpublished","value":"UNPUBLISHED"]
                ]],
                ["path":"active","label":"State","type":"enum","options":[["label":"Active","value":"Active"],["label":"Inactive","value":"Inactive"]]],
                ["path": "revision","label": "Version","autocomplete": "true","type":"string"]
        ],
        guidelines: [
                ["path": "usage", "label": "Usage", "type":"string"],
                ["path": "publisher", "label": "Publisher", "autocomplete": "true", "type":"string"],
                ["path": "gene", "label": "Gene", "autocomplete": "true", "type":"string"],
                ["path": "diseasePath", "label":"Disease", "type":"ontology", "pathId":"diseases.diseasePath"],
                ["path": "nomenclature", "label": "Nomenclature", "type":"string"],
                ["path": "classification", "label":"Classification", "autocomplete": "true", "type":"string"],
                ["path": "treatment", "label": "Treatment", "type": "string"],
                ["path": "conclusion", "label": "Conclusion", "type": "string"]
        ],
        clinicalTrials: [
                ["path": "title", "label": "Title", "type":"string"],
                ["path": "diseasePath", "label":"Disease", "type":"ontology", "pathId":"diseases.diseasePath"],
                ["path": "treatment", "label": "Treatment", "type": "string"],
                ["path": "phases", "label": "Phase", "type":"string"],
                ["path": "location", "children": [
                        ["path": "address", "label": "Location", "type":"string"]
                ]],
                ["path": "excerpt", "label": "Excerpt", "type":"string"]
        ],

        populationFrequency: [
                ["path": "database", "label": "Database", "type": "string"],
                ["path": "population", "label": "Population", "type": "string"],
                ["path": "frequency", "label": "Frequency", "type": "long"]
        ],
        diseasePhenotype: [
                ["path": "phenotypePath", "label": "Phenotype", "type":"ontology", "pathId":"phenotypes.path"],
                ["path": "diseasePath", "label":"Disease", "type":"ontology", "pathId":"phenotypes.diseases.path"],
                ["path": "phenotypes", "children": [
                        ["path": "numberOfVariants", "label": "Variants that explain this Phenotype", "type": "long"],
                        ["path": "casePhenotype", "label": "Case Phenotype", "type":"enum","options": [
                                ["label":"yes","value":"true"],
                                ["label":"no","value":"false"]
                        ]]
                ]]
        ],
        clinicalEvidence: [
                ["path": "source", "label": "Source", "type":"string"],
                ["path": "gene", "label":"Gene", "type":"string"],
                ["path": "region", "label": "Region", "type": "string"],
                ["path": "clinicalSignificance", "label": "Clinical Significance", "type":"string"],
                ["path": "date", "label": "Date", "type":"date"],
                ["path": "publisher", "label": "Publisher", "type":"string"],
                ["path": "classification", "label": "Classification", "autocomplete": "true", "type": "string"]
        ],
        computationalEvidence: [
                ["path": "category", "label": "Category", "type": "string"],
                ["path": "algorithm", "label": "Algorithm", "type": "string"],
                ["path": "score", "label": "Score", "type": "long"],
                ["path": "interpretation", "label": "Interpretation", "type": "string"],
        ]
]

cgw {
    jbrowse{
        path="C:\\cgw\\jbrowse"
        refseq = "C:\\cgw\\jbrowse\\refseq"
        genes = "C:\\cgw\\jbrowse\\genes"
        samples = "C:\\cgw\\jbrowse\\samples"
        cases = "C:\\cgw\\jbrowse\\cases"
    }
}

cgw.hre.sources = ['tcga','cosmic']

cgw.humanResearchEvidence.basepair.interval=10
cgw.allowed.chromosomes = ["1","2","3","4","5","6","7","8","9","10","11","12",
                           "13","14","15","16","17","18","19","20","21","22","x","y"]
cgw.disease.root.cancer = "55342001"
cgw.disease.root.constitution = "56265001"



/** -------------- From CGW --------------------------*/


/**  ---- cgw-metadata config Starts --- */

// config entry for logging metrics during report generation
cgw.report.metrics.logging = false

cgw.report.metric.file.path = "/usr/local/mrgstorage/files/reports"
//User credentials to test signout harness
signout.test.harness.accounts = [["cgw_user_1","Password123!"],["cgw_user_2","Password123!"],["cgw_user_3","Password123!"],["cgw_user_4","Password123!"],["cgw_user_5","Password123!"]]
//CodeSystem for ontology
cgw.disease.code.system='SNOMEDCT'
cgw.specimenType.code.system='SNOMEDCT'
cgw.phenotype.code.system = 'HP'
disease {
    SNOMED {
        revision = "2014AB"
        urlPattern = "http://purl.bioontology.org/ontology/SNOMEDCT/\${code}"
    }
    SNOMEDCT {
        revision = "2014AB"
        urlPattern = "http://purl.bioontology.org/ontology/SNOMEDCT/\${code}"
    }
}
specimen {
    SNOMED {
        revision = "2014AB"
        urlPattern = "http://purl.bioontology.org/ontology/SNOMEDCT/\${code}"
    }
    SNOMEDCT {
        revision = "2014AB"
        urlPattern = "http://purl.bioontology.org/ontology/SNOMEDCT/\${code}"
    }
}
phenotypes {
    HPO {
        revision = "20150214"
        urlPattern = "http://purl.obolibrary.org/obo/\${code}"
    }
}
clinicalTrials {
    clinicalTrialsGov {
        revision = "RELEASE-15"
    }
}
guidelines {
    NCCN {
        revision = "8898933"
    }
}
//Data tables total no of records and date format
cgw.dataTable.max.records = 10
cgw.dataTable.date.format = "MM/dd/yyyy"
//For panel, rule parsing and serializing
cgw.default.rule.folder.name = "rules"
cgw.default.annotation.property.delimiter = ","
cgw.default.rule.domain.names = [
        'INTERPRETATION': ['edu.wustl.cgw.domain.rule.Rule', 'edu.wustl.cgw.domain.rule.ClinicalInterpretationRule'],
        'CLASSIFICATION': ['edu.wustl.cgw.domain.rule.Rule', 'edu.wustl.cgw.domain.rule.ClassificationRule'],
        'PUBLICATION':    ["edu.wustl.cgw.domain.rule.Rule", "edu.wustl.cgw.domain.rule.PublicationRule"],
        'CLINICAL_TRIAL': ["edu.wustl.cgw.domain.rule.Rule", "edu.wustl.cgw.domain.rule.ClinicalTrialRule"],
        'GUIDELINE':      ["edu.wustl.cgw.domain.rule.Rule", "edu.wustl.cgw.domain.rule.GuidelineRule"],
        'CLINICAL_EVIDENCE':      ["edu.wustl.cgw.domain.rule.Rule", "edu.wustl.cgw.domain.rule.ClinicalEvidenceRule"],
        'HUMAN_RESEARCH_EVIDENCE':      ["edu.wustl.cgw.domain.rule.Rule", "edu.wustl.cgw.domain.rule.HumanResearchEvidence"]
]

cgw.default.classification.names = [ '1':'Pathogenic',  '2':'Likely Pathogenic', '3':'Variant of Uncertain Significance', '4':'Likely Benign',  '5':'Benign' ]

cgw.ui.metadataExclusions = [
        variant: ["chromcsyntax", "chromcsyntaxWithoutAccNum", "csyntax", "psyntax", "psyntaxWithoutAccNum", "transcsyntax", "transcsyntaxWithoutAccNum", "gsyntax"]
]
cgw.default.test.accounts = [ [username: "cgw_user_1", password:"Password123!", email:"cgw_user_1@pieriandx.com"],
                              [username: "cgw_user_2", password:"Password123!", email:"cgw_user_2@pieriandx.com" ],
                              [username: "cgw_user_3", password:"Password123!", email:"cgw_user_3@pieriandx.com" ],
                              [username: "cgw_moffit_user", password:"Password123!", email:"cgw_moffit_user@pieriandx.com" ],
                              [username: "cgw_user_a", password:"Password123!", email:"cgw_user_a@pieriandx.com" ],
                              [username: "cgw_user_b", password:"Password123!", email:"cgw_user_b@pieriandx.com" ],
                              [username: "cgw_suresh_user", password:"Password123!", email:"cgw_sureshuser_3@pieriandx.com" ],
                              [username: "cgw_user_pieriandx", password:"Password123!", email:"cgw_user_pieriandx@pieriandx.com" ]]

//Cluster manager configuration
cgw.cluster.manager.enable=false
//pieriandx OR Pieriandx
cgw.default.institution='Pieriandx'
cgw.external.variants.syntax.gen.fail.log="F:\\workspace\\cgw\\syngenfail"

//Configuration for jbrowse
cgw.jBrowseDataPath="/jtracks"
cgw.jBrowseCaseDataPath="/jcasetracks"
cgw.jBrowseURL="/jbrowse"

//Configuration for xml generation
cgw{
    excelToRuleSetMapping {
        annotationPropertySet {
            org="edu.wustl.cgw.plugin.propertySet"
            name="annotation_properties"
            rev="1.0.0"
        }
        rulesetPropertySet {
            org="edu.wustl.cgw.plugin.propertySet"
            name="ruleset_properties"
            rev="1.0.0"
        }
        variantClassificationSet {
            org="edu.wustl.cgw.plugin.classificationScheme"
            name="wucamp_cancer_five_levels"
            rev="1.0.0"
        }
    }
}
cgw.excelToRuleSetMapping.templates.path = "/usr/local/mrgstorage/files/deployment_package/uat-app/templates"

cgw.rule.publication.date.format = "yyyy-MM-dd"
cgw.rule.published.date.format = "yyyy-MM-dd"
//default limit of quick text search
cgw.quick.text.default.search = 10
//default limit of characters length for quick text
cgw.quick.text.default.character.length = 100
//Default name for disease name in predicate.xml
cgw.rule.predicate.property.disease.name = 'diseaseCode'
//Default decimal digits to display
cgw.default.decimal.digits.length = 3

cgw.pluginLoaderIntervalSeconds = 10
cgw.pluginLoaderStartDelaySeconds = 0
cgw.qcreport.job.interval.seconds = 20
cgw.qcreport.job.delay.seconds = 0
cgw.qcreportjob.quartz.maxFailedAttempts = 5
cgw.humanResearchEvidence.rowCountToSplit = 500000
cgw.export.ruleSet.rowsForFile = 100000
cgw.humanResearchEvidence.blockSize = 20
cgw.seq.path="F:\\workspace\\cgw"
cgw.seq.chunkSize=20000
cgw.seq.filesToCache=50

//Order archiving job run intervals
cgw.archive.order.poll.interval = ONE_MINUTE
cgw.archive.order.poll.startdelay = ONE_MINUTE
cgw.archive.order.quartz.maxFailedAttempts = 5 //flag a job as ERROR if it fails more than 10 times consecutively

cgw.archive.bucket.name = 'pdx-dev-archive'

cgw.archive.bucket.pre.institution = "/usr/local/mrgstorage/files/deployment_package/"
cgw.archive.server.pre.institution = "/usr/local/mrgstorage/files/deployment_package/"

cgw.archive.bucket.post.institution = "/seqdata/All_Cases/"
cgw.archive.server.post.institution = "/seqdata/All_Cases/"

cgw.arhive.days = 30
cgw.archive.timeout = 5 * 60 * 60 * 1000 //5hours
cgw.archive.server.base.download.path = "/usr/local/mrgstorage/"
cgw.hre.maxThreads = 24
cgw.hre.minimumTrackLength = 2000
cgw.hre.cosmic.linkPattern = "http://cancer.sanger.ac.uk/cosmic/mutation/overview?id={identifier}"

cgw.kb.export.csvs.location = "/usr/local/mrgstorage/files/deployment_package/cgw_3-0_DEV_A1-app/@INSTITUTION_NAME@/seqdata/All_Cases"
cgw.panel.cancer.disease.codes = ["4147007", "399981008"]
cgw.report.factSet.batch.size=500
variants.reuse.load.files=false
cgw.variants.allowed.chromosomes = ["1","2","3","4","5","6","7","8","9","10","11","12",
                                    "13","14","15","16","17","18","19","20","21","22","x","y"]
cgw.variants.chunkSize=100
cgw.report.factSet.batch.size=500
cgw.report.inferences.batch.size=100
validChromosomeNames = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "X", "Y"]
cgw.variantDetailsSummary.populationFrequency.excludedSourcesFromLinking = ["dbNSFP"]
cgw.phenotypeCoverage.explorer.maxVariants = 100
cgw.report.ranking.evaluator.concurrent.jobs = 500
//http://api.mongodb.org/java/current/com/mongodb/AggregationOptions.html
cgw.mongodb.aggregate.batchSize = 5000
cgw.report.precedence.evaluator.concurrent.jobs = 250


cgw.default.sources.names = ["clinvar" : [style:"background-color: #caa7ff; border: 1px solid #5200a3;" ,content: "C"],
                             "lodv" : [style:"background-color: #99ccff; border: 1px solid #005ce6;" ,content: "L"],
                             "emvclass" : [style:"background-color: #8de28d; border: 1px solid #558855;",content:"E"],
                             "arup" : [style:"background-color: #eecdbc; border: 1px solid #8b776e;",content:"A"],
                             "clinvitae" :[style:"background-color: #ffb2b2; border: 1px solid #a17070;",content:"V"],
                             "other" : [style : "background-color: #eecdbc; border: 1px solid #8b776e;",content:"O"]]
/*limits the number of syntaxes(p,c,g) to show in autocompleter*/
cgw.variant.syntaxes.limit= 50
//Change this url later
cgw.password.change.url = 'https://www.pieriandx.com/'
//cgw's own LDAP server url
cgw.ldap.pierianDxLDAPUrl = 'ldap://localhost:389/'

cgw.variant.fixed.columns="Level,Gene,Variant,Status"
//limit number of characters in gSyntax
cgw.variant.gsyntax.limit=20
//limit number of characters in pSyntax other than refSeq transcript number
cgw.variant.psyntax.limit=20
cgw.variant.data.table.limit.characters = 15
cgw.variant.data.table.limit.width = 70
cgw.variant.exclude.tooltip.columns="Level,Gene,Variant,AA change,DNA change,Consequence,Variant type/subtype"
cgw.igvsort.max.records = 100000

grails.converters.json.default.deep = true

/**  ---- cgw-metadata config ends --- */






/** ---- CGW properties 4 starts----*/
//Co-path settings - Report sign-off submit to EHR?
cgw.submit.signoff.ehr = false

//Dictionary settings - cgw.dictionary.path is relative to <cgw-code-root>/web-app/
cgw.dictionary.path = "dict"
cgw.dictionary.format.medical = "en_US_OpenMedSpel"
cgw.dictionary.formats = ["en_GB", "en_US"]

//Importing medical facilities through CGW UI
cgw.excel.import.medicalFacility.details = [
        sheet: 'Sheet1',
        startRow: 1,
        columnMap: [
                'A': 'facility',
                'B': 'hospitalNumber',
        ]
]

//Importing diseases through CGW UI
cgw.excel.import.diseases.details = [
        sheet: 'Sheet1',
        startRow: 1,
        columnMap: [
                'A': 'code',
                'B': 'codeSystem',
                'C': 'codeLabel',
                'D': 'parentCode',
                'E': 'uri',
                'F': 'description',
                'G': 'link'
        ]
]
//Importing diseases through CGW UI
cgw.excel.import.specimenTypes.details = [
        sheet: 'Sheet1',
        startRow: 1,
        columnMap: [
                'A': 'URL',
                'B': 'ID',
                'C': 'CodeSystem',
                'D': 'Version',
                'E': 'Label',
                'F': 'Description',
                'G': 'Parents'
        ]
]

//To import phenotypes
cgw.excel.import.phenotypes.details = [
        sheet: 'Sheet1',
        startRow: 1,
        columnMap: [
                'A': 'URL',
                'B': 'ID',
                'C': 'CodeSystem',
                'D': 'Version',
                'E': 'Label',
                'F': 'Parents'
        ]
]

//To import ontological concept
cgw.excel.import.ontology.csv.details = [
        separatorChar: '\t'
]
//Base url for snomedct
cgw.ontology.snomedct.baseurl = 'http://data.bioontology.org/ontologies/SNOMEDCT/'
cgw.ontology.snomedct.apikey = 'deccbb0d-3674-4996-b904-a6e11efa99b1'


//CGW db sql log - Show SQL logs in UI
cgw.sql.log.folder = System.properties.getProperty('catalina.base', '.') + "/logs" /// spy.log..
cgw.sql.log.file.enable = false
cgw.sql.log.request.enable = false

/**
 * The MGR cluster must be mount on the app-server and the db-server. In addition to that on the db-server a directory must dedicated to keep the log files related to coverage loading.
 * The following properties are used to specify that configuration.
 * The folder location where MRG cluster node is mount on CGW App server. Make sure to end it with a slash('/').
 */
cgw.local.mrg.mount.location = '/usr/local/mrgstorage/';

//The folder location where MRG cluster node is mount on Database server. Make sure to end it with a slash('/').
cgw.db.mrg.mount.location = '/usr/local/mrgstorage/'

cgw.db.log.mount.location = '/usr/local/mrgstorage/files/seqdata/oracle_logs/'
cgw.app.log.mount.location = '/usr/local/mrgstorage/files/seqdata/oracle_logs/'

//Deleted CGW case related files/data can be found here
cgw.cases.archival.directory = '/usr/local/mrgstorage/files/deployment_package/development/seqdata/ALL_GPS_Cases/deleted_case_data'

//Contact information
cgw.help.email = 'cgw_admin@pieriandx.com'
cgw.help.phone = '(314) 362-8853'

//Default number of results shown in one page in YUI tables used in cases tab etc.
cgw.default.yuitable.search.results = 10

//Default admin user details
cgw.default.admin.user.username = 'cgw_user_1'
cgw.default.admin.user.password = 'Password123!'
cgw.admin.email.address = "cgw_admin@pieriandx.com"

//Address to which report generation notification mails are sent
cgw.reportGeneration.notification.mail.address = "cgw-dev@pieriandx.com"

//Pubmed syntax for which references are shown
cgw.default.pubmed.syntax = "(PMID):\\s*(\\d*)"

//Pubmed database url
cgw.pubmed.link.url = "http://www.ncbi.nlm.nih.gov/pubmed/";
cgw.pubmed.search.url = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi"

String file_sep = File.separatorChar

//Patient VCF mount location details
cgw.db.patient.vcf.mount.directory.name = "PV_LOAD"
cgw.db.patient.vcf.mount.directory.location = "/usr/local/mrgstorage/files/pv_load_dev1"
cgw.db.patient.vcf.mount.directory.ttl = 30 * DateUtils.MILLIS_PER_DAY



//CGW cache location
cgw.app.temp.location = "cache"

cgw.run.longrunning.test.case = true

//Default external variant sources vcf locations
cgw.dbsnp.vcf.files.location = "${userHome}/projects/dbsnp/"
cgw.cosmic.vcf.files.location = "${userHome}/projects/cosmic/"

/**
 * CGW Plugin (@since 1.5)
 * cgw.plugin.local.cache.location="${userHome}${file_sep}cgw-cache"
 * cgw.plugins.folder = "${userHome}${file_sep}cgw_plugins"
 */
cgw.plugins.folder = "${FilenameUtils.normalize(System.getProperty("base.dir") + "/../cgw_plugins")}"
cgw.plugin.local.cache.location = "${FilenameUtils.normalize(System.getProperty("base.dir") + "/../cgw_cache")}"

//Widgets location
cgw.widget.plugin.folder = "${FilenameUtils.normalize(System.getProperty("base.dir") + "/../cgw_plugins/edu.wustl.cgw.plugin.widgets")}"
cgw.widget.plugin.ui.context = "${FilenameUtils.normalize(System.getProperty("base.dir") + "/../cgw_plugins/edu.wustl.cgw.plugin.widgets")}"


cgw.genomic.sequence.folder = "/usr/local/mrgstorage/files"
cgw.genomic.annotation.folder = "/usr/local/mrgstorage/files"

/**
 * Specify the default genomic annotation for CGW
 * NOTE: THIS IS A MANDATORY CONFIGURATION AND LEAVING IT BLANK MIGHT CAUSE SOME PLUGINS TO NOT LOAD
 */
cgw {
    'default' {
        genomic {
            annotation {
                org = "edu.wustl.cgw.plugin.genomicAnnotationSet"
                name = "default_genomic_set"
                revision = "1.0.0"
            }
            sequence {
                org = "edu.wustl.cgw.plugin.genomicSequenceSet"
                name = "default_genomic_sequence"
                revision = "1.0.0"
            }
        }
    }
}


//----clear till here
//External variants db user name
cgw.external.variants.database.user = "cgw_ext"
//External variants mount location
external.variants.loading.dir.location = "${userHome}${file_sep}EV_LOAD_DEVELOPMENT${file_sep}"

//Clinical interpretation rule publisher name - can be found in curation tab -> clinical interpretation list -> Publisher column
cgw.clinicalInterpretationRule.default.publisher = "WashU"

//Working ruleSet default settings
cgw.working.set.org = "local.ruleSet"
cgw.working.set.revision = "1.0.0"
cgw.ruleSet.org = "edu.wustl.cgw.plugin.ruleSet"

cgw.serialize.knowledgeBase.condor = false
cgw.knowledgebase.location = "C:/kbs"
cgw.knowledgebase.compilation.max.wait.time = 5 * 60 * 60 * 1000  // 5 hours
cgw.maxRulesInKnowledgeBase = 50
cgw.compile.knowledgebase.cron.expression = "0 0/1 * 1/1 * ? *"

cgw.publish.ruleSet.cron.expression = "0 0/15 * 1/1 * ? *"


//External variants track zoom limit
cgw.genestab.externalVariantsTrack.zoom.limit = 1000

/** ---- CGW properties 4 ends ----*/




/** ---- RULES and RANKING 5 starts ---- */
//Rule templates and their locations
gsyntaxClinicalInterpretationRule = "${FilenameUtils.normalize(basedir.toString() + "${file_sep}..${file_sep}cgw-common${file_sep}src${file_sep}groovy${file_sep}edu${file_sep}wustl${file_sep}cgw${file_sep}rules${file_sep}templates${file_sep}gsyntaxClinicalInterpretationRule.ftl")}"
gsyntaxClassificationRule = "${FilenameUtils.normalize(basedir.toString() + "${file_sep}..${file_sep}cgw-common${file_sep}src${file_sep}groovy${file_sep}edu${file_sep}wustl${file_sep}cgw${file_sep}rules${file_sep}templates${file_sep}gsyntaxClassificationRule.ftl")}"
gsyntaxAssociationRule = "${FilenameUtils.normalize(basedir.toString() + "${file_sep}..${file_sep}cgw-common${file_sep}src${file_sep}groovy${file_sep}edu${file_sep}wustl${file_sep}cgw${file_sep}rules${file_sep}templates${file_sep}gsyntaxAssociationRule.ftl")}"

chromcsyntaxClinicalInterpretationRule = "${FilenameUtils.normalize(basedir.toString() + "${file_sep}..${file_sep}cgw-common${file_sep}src${file_sep}groovy${file_sep}edu${file_sep}wustl${file_sep}cgw${file_sep}rules${file_sep}templates${file_sep}chromcsyntaxClinicalInterpretationRule.ftl")}"
chromcsyntaxClassificationRule = "${FilenameUtils.normalize(basedir.toString() + "${file_sep}..${file_sep}cgw-common${file_sep}src${file_sep}groovy${file_sep}edu${file_sep}wustl${file_sep}cgw${file_sep}rules${file_sep}templates${file_sep}chromcsyntaxClassificationRule.ftl")}"
chromcsyntaxAssociationRule = "${FilenameUtils.normalize(basedir.toString() + "${file_sep}..${file_sep}cgw-common${file_sep}src${file_sep}groovy${file_sep}edu${file_sep}wustl${file_sep}cgw${file_sep}rules${file_sep}templates${file_sep}chromcsyntaxAssociationRule.ftl")}"

transcsyntaxClinicalInterpretationRule = "${FilenameUtils.normalize(basedir.toString() + "${file_sep}..${file_sep}cgw-common${file_sep}src${file_sep}groovy${file_sep}edu${file_sep}wustl${file_sep}cgw${file_sep}rules${file_sep}templates${file_sep}transcsyntaxClinicalInterpretationRule.ftl")}"
transcsyntaxClassificationRule = "${FilenameUtils.normalize(basedir.toString() + "${file_sep}..${file_sep}cgw-common${file_sep}src${file_sep}groovy${file_sep}edu${file_sep}wustl${file_sep}cgw${file_sep}rules${file_sep}templates${file_sep}transcsyntaxClassificationRule.ftl")}"
transcsyntaxAssociationRule = "${FilenameUtils.normalize(basedir.toString() + "${file_sep}..${file_sep}cgw-common${file_sep}src${file_sep}groovy${file_sep}edu${file_sep}wustl${file_sep}cgw${file_sep}rules${file_sep}templates${file_sep}transcsyntaxAssociationRule.ftl")}"

psyntaxClinicalInterpretationRule = "${FilenameUtils.normalize(basedir.toString() + "${file_sep}..${file_sep}cgw-common${file_sep}src${file_sep}groovy${file_sep}edu${file_sep}wustl${file_sep}cgw${file_sep}rules${file_sep}templates${file_sep}psyntaxClinicalInterpretationRule.ftl")}"
psyntaxClassificationRule = "${FilenameUtils.normalize(basedir.toString() + "${file_sep}..${file_sep}cgw-common${file_sep}src${file_sep}groovy${file_sep}edu${file_sep}wustl${file_sep}cgw${file_sep}rules${file_sep}templates${file_sep}psyntaxClassificationRule.ftl")}"
psyntaxAssociationRule = "${FilenameUtils.normalize(basedir.toString() + "${file_sep}..${file_sep}cgw-common${file_sep}src${file_sep}groovy${file_sep}edu${file_sep}wustl${file_sep}cgw${file_sep}rules${file_sep}templates${file_sep}psyntaxAssociationRule.ftl")}"

headerTemplate = "${FilenameUtils.normalize(basedir.toString() + "${file_sep}..${file_sep}cgw-common${file_sep}src${file_sep}groovy${file_sep}edu${file_sep}wustl${file_sep}cgw${file_sep}rules${file_sep}templates${file_sep}headerTemplate.ftl")}"
ruleSetItemTemplate = "${FilenameUtils.normalize(basedir.toString() + "${file_sep}..${file_sep}cgw-common${file_sep}src${file_sep}groovy${file_sep}edu${file_sep}wustl${file_sep}cgw${file_sep}rules${file_sep}templates${file_sep}ruleSetItemTemplate.ftl")}"

gsyntaxPublicationRule = "${FilenameUtils.normalize(basedir.toString() + "${file_sep}..${file_sep}cgw-common${file_sep}src${file_sep}groovy${file_sep}edu${file_sep}wustl${file_sep}cgw${file_sep}rules${file_sep}templates${file_sep}gsyntaxPublicationRule.ftl")}"
chromcsyntaxPublicationRule = "${FilenameUtils.normalize(basedir.toString() + "${file_sep}..${file_sep}cgw-common${file_sep}src${file_sep}groovy${file_sep}edu${file_sep}wustl${file_sep}cgw${file_sep}rules${file_sep}templates${file_sep}chromcsyntaxPublicationRule.ftl")}"
transcsyntaxPublicationRule = "${FilenameUtils.normalize(basedir.toString() + "${file_sep}..${file_sep}cgw-common${file_sep}src${file_sep}groovy${file_sep}edu${file_sep}wustl${file_sep}cgw${file_sep}rules${file_sep}templates${file_sep}transcsyntaxPublicationRule.ftl")}"
psyntaxPublicationRule = "${FilenameUtils.normalize(basedir.toString() + "${file_sep}..${file_sep}cgw-common${file_sep}src${file_sep}groovy${file_sep}edu${file_sep}wustl${file_sep}cgw${file_sep}rules${file_sep}templates${file_sep}psyntaxPublicationRule.ftl")}"


//Ranking properties
ranking.evidenceLevel.strategy.weight = 10
ranking.patientDiseaseSpecificity.weight = 8
ranking.coordinateLevel.weight = 0.5
ranking.nomenclatureLevel.weight = 0.5
ranking.institution.weight = 0.1
ranking.panel.weight = 1
ranking.classificationLevel.weight = 6
ranking.interpretationPublicationStatus.weight = 3
ranking.guidelineOutcome.weight = 4

//Evidence level factor scoring strategy
ranking.evidenceLevel.strategy.interpretation = 10
ranking.evidenceLevel.strategy.guideline = 9
ranking.evidenceLevel.strategy.clinicalEvidence = 8
ranking.evidenceLevel.strategy.classification = 1
ranking.evidenceLevel.strategy.clinicalTrial = 0
ranking.evidenceLevel.strategy.publication = 0

//Institution factor scoring strategy
ranking.institution.strategy.currentInstitutionScore = 2000
ranking.institution.strategy.otherInstitutionScore = 1000

//Panel factor scoring stratey
ranking.panel.strategy.currentPanelScore = 2000
ranking.panel.strategy.otherPanelScore = 1000

//Nomenclature level factor scroring strategy
ranking.nomenclatureLevel.strategy.chromcsyntax = 1
ranking.nomenclatureLevel.strategy.transcsyntax = 0.5
ranking.nomenclatureLevel.strategy.psyntax = 0.4
ranking.nomenclatureLevel.strategy.gsyntax = 0.3

//Coordinate level factor scoring strategy
ranking.coordinateLevel.strategy.rna = 1
ranking.coordinateLevel.strategy.protein = 0.5
ranking.coordinateLevel.strategy.genome = 0.25

//Classification level factor scoring strategy
ranking.classificationLevel.strategy.class1 = 11
ranking.classificationLevel.strategy.class2 = 10
ranking.classificationLevel.strategy.class3 = 5
ranking.classificationLevel.strategy.class4 = 3
ranking.classificationLevel.strategy.class5 = 1

//Interpretation publication status factor scoring strategy
ranking.interpretationPublicationStatus.strategy.draft = 1
ranking.interpretationPublicationStatus.strategy.approved = 5
ranking.interpretationPublicationStatus.strategy.published = 10

//Disease specificity factor scoring strategy
ranking.patientDiseaseSpecificity.strategy.exactMatchWeight = 100
ranking.patientDiseaseSpecificity.strategy.childMatchWeight = 50

//Guildeline outcome factor scoring strategy
ranking.guidelineOutcome.strategy.OS = 10
ranking.guidelineOutcome.strategy.DFS = 9
ranking.guidelineOutcome.strategy.PFS = 8
ranking.guidelineOutcome.strategy.TTP = 9
ranking.guidelineOutcome.strategy.TTR = 8
ranking.guidelineOutcome.strategy.TOX = 4
ranking.guidelineOutcome.strategy.EFS = 7
ranking.guidelineOutcome.strategy.REM = 6
ranking.guidelineOutcome.strategy.MS = 7
ranking.guidelineOutcome.strategy.EP = 6
ranking.guidelineOutcome.strategy.RR = 6
ranking.guidelineOutcome.strategy.DP = 7
ranking.guidelineOutcome.strategy.PROG = 5
ranking.guidelineOutcome.strategy.PR = 4
ranking.guidelineOutcome.strategy.OR = 5
ranking.guidelineOutcome.strategy.DCR = 5
ranking.guidelineOutcome.strategy.TRM = 6
ranking.guidelineOutcome.strategy.TTF = 5
ranking.guidelineOutcome.strategy.PRS = 7
ranking.guidelineOutcome.strategy.TTFT = 5
ranking.guidelineOutcome.strategy.DR = 2
ranking.guidelineOutcome.strategy.defaultScore = 1

/** ---- RULES and RANKING 5 ends ---- */


/** ------- IGV JNLP  2 starts------- */

/**
 * Structure of IGV jnlp file:
 *
 * <?xml version="1.0" encoding="utf-8"?>
 *     <jnlp specs='6.0+' codebase='http://wucgwd8.cbmi.wucon.wustl.edu:8080/cgw/igv/IGV_2.3.49'>
 *         <information>
 *             <title>IGV_2.3.49</title>
 *             <vendor>The Broad Institute</vendor>
 *             <homepage href='http://www.broadinstitute.org/igv' />
 *             <description>IGV Software</description>
 *             <description kind='short'>IGV</description>
 *             <icon href='IGV_64.png' />
 *             <icon kind='splash' href='IGV_64.png' />
 *             <offline-allowed />
 *             <shortcut />
 *         </information>
 *         <security>
 *             <all-permissions />
 *         </security>
 *         <update check='always' policy='always' />
 *         <resources>
 *             <java version='1.6+' initial-heap-size='256m' max-heap-size='1228m' />
 *             <jar href='igv.jar' download='eager' main='true' />
 *             <jar href='batik-codec.jar' download='eager' />
 *             <jar href='goby-io-igv.jar' download='lazy' />
 *             <property name='apple.laf.useScreenMenuBar' value='true' />
 *             <property name='com.apple.mrj.application.growbox.intrudes' value='false' />
 *             <property name='com.apple.mrj.application.live-resize' value='true' />
 *             <property name='com.apple.macos.smallTabs' value='true' />
 *         </resources>
 *         <application-desc main-class='org.broad.igv.ui.Main'>
 *             <argument>http://wucgwd8.cbmi.wucon.wustl.edu:8080/cgw/cache/45fc2b65-f522-4e05-9ab9-152e16b52ea3/G13-0005/main.vcf,http://wucgwd8.cbmi.wucon.wustl.edu:8080/cgw/cache/45fc2b65-f522-4e05-9ab9-152e16b52ea3/G13-0005/main.clean.bam</argument>
 *             <argument>chr17:37868159-37868258</argument>
 *             <argument>-g</argument>
 *             <argument>hg19</argument>
 *             <argument>--preferences</argument>
 *             <argument>http://wucgwd8.cbmi.wucon.wustl.edu:8080/cgw/cache/prefs.properties</argument>
 *         </application-desc>
 *      </jnlp>
 *
 *      1. igv.jnlp.codebase - In the above xml structure this can be located in the tag <jnlp codebase=''>.
 *                             Location where IGV is hosted, for example in d8 it is hosted at http://wucgwd8.cbmi.wucon.wustl.edu:8080/cgw/igv/IGV_2.2.11
 *      2. igv.jnlp.specs - In the above xml structure this can be located in the tag <jnlp specs=''>
 *                          Minimum version of Java Web Start to be used to execute jnlp file
 *      3. igv.jnlp.applicationDesc.mainClass - In the above xml structure this can be located in the tag <application-desc main-class=''>.
 *                                              Specifies the name of the application's main class
 *      4. igv.jnlp.prefs.file.name - One of the arguments which indicates the file name and path for enabling soft clipped reads
 */
igv.jnlp.codebase = "/igv/IGV_2.3.60"
igv.jnlp.specs = '6.0+'
igv.jnlp.applicationDesc.mainClass = 'org.broad.igv.ui.Main'

igv.jnlp.prefs.file.property = "--preferences"
igv.jnlp.prefs.file.name = "${grails.serverURL}/cache/prefs.properties"

//Content to be parsed to generate jnlp file for launching IGV. The resulting parsed xml looks like the above IGV jnlp.
igv.jnlp.xml.data = """
        information {
            title('IGV_2.3.60')
            vendor('The Broad Institute')
            homepage(href: 'http://www.broadinstitute.org/igv')
            description('IGV Software')
            description(kind: 'short', 'IGV')
            icon(href: 'IGV_64.png')
            icon(kind: 'splash', href: 'IGV_64.png')
            'offline-allowed'()
            'shortcut'()
        }
        security {
            'all-permissions'()
        }
        update(check: 'always', policy: 'always')
        resources {
            java(version: '1.6+', 'initial-heap-size': '256m', 'max-heap-size': '@HEAP_MEMORY@m')
            jar(href: 'igv.jar', download:'eager', main: 'true')
            jar(href: 'batik-codec__V1.7.jar', download: 'eager')
            jar(href: 'goby-io-igv__V1.0.jar', download: 'lazy')
            property(name: 'apple.laf.useScreenMenuBar', value: 'true')
            property(name: 'com.apple.mrj.application.growbox.intrudes', value: 'false')
            property(name: 'com.apple.mrj.application.live-resize', value: 'true')
            property(name: 'com.apple.macos.smallTabs', value: 'true')
        }
"""

//IGV cache settings
igv.cache.path = "cache"
//IGV cache time to live - Elapsed time before it clears the genome cache (in ms)
igv.cache.ttl = 7200000
/** ------- IGV JNLP  2 ends------- */

/** ------- LDAP  3 starts------- */
//LDAP config
grails.plugin.springsecurity.ldap.context.managerDn = 'cn=admin,dc=wustl,dc=edu'
grails.plugin.springsecurity.ldap.context.managerPassword = 'secret'
grails.plugin.springsecurity.ldap.search.base = 'ou=people,dc=wustl,dc=edu'
grails.plugin.springsecurity.useLdap = true
grails.plugin.springsecurity.ldap.context.server = 'ldap://182.72.241.52:389/' //Make sure you have internet

grails.plugin.springsecurity.providerNames = ['cgwAuthProvider','cgwLdapLookupAuthenticationProvider', 'anonymousAuthenticationProvider']//,'rememberMeAuthenticationProvider'] // specify this when you want to skip attempting to load from db and only use LDAP
grails.plugin.springsecurity.ldap.authorities.retrieveDatabaseRoles = true
grails.plugin.springsecurity.ldap.authorities.retrieveGroupRoles = false
//grails.plugins.springsecurity.password.algorithm='SHA-1'//TODO  Enable this with proper encryption scheme when the LDAP server is using encryption to store password

//LDAP Authenticator
grails.plugin.springsecurity.ldap.authenticator.dnPatterns = "uid={0},ou=people"
grails.plugin.springsecurity.ldap.authenticator.useBind = true

grails.plugin.springsecurity.useHttpSessionEventPublisher = true

/** ------- LDAP  3 ends------- */


/** ------- INFORMATICS  1 ------- */
def ONE_MINUTE = 60 * 1000
//Informatics quartz job polling interval, start delay, and maximum number of failed attempts
cgw.informaticsjob.quartz.poll.interval = 5 * ONE_MINUTE
cgw.informaticsjob.quartz.poll.startdelay = 5 * ONE_MINUTE
cgw.informaticsjob.quartz.maxFailedAttempts = 5 //flag a job as ERROR if it fails more than 10 times consecutively

//Quartz job polling interval for each phase
cgw.informaticsjob.process.quartz.poll = [
        fileDownloadInterval: 5 * ONE_MINUTE,
        fileDownloadStartdelay: 5 * ONE_MINUTE,

        syntaxGenerationInterval: 5 * ONE_MINUTE,
        syntaxGenerationStartdelay: 6 * ONE_MINUTE,

        ruleProcessingInterval: 5 * ONE_MINUTE,
        ruleProcessingStartdelay: 7 * ONE_MINUTE,

        patientDataLoadInterval: 5 * ONE_MINUTE,
        patientDataLoadStartdelay: 8 * ONE_MINUTE,

        reportGenerationInterval: 5 * ONE_MINUTE,
        reportGenerationStartdelay: 9 * ONE_MINUTE,

        maxFailedAttempts: 5
]

//Informatics service url
cgw.informaticsjob.serviceurl = "http://wucgwo1.cbmi.wucon.wustl.edu:8080/informatics/service/"
/**
 * Mapping between Informatics output and CGW user-friendly name
 * For ex : Allele_Frequency_Plot_for_Miseq_2566_Dup_Fix.pdf returned by Informatics service should be shown as  Allele frequency plot
 */
informaticsJobOutputMappings = [
        [fileName: "Allele_Frequency", fileType: "PDF", cgwUserFriendlyName: "Allele frequency plot"],

        [fileName: "Hotspot_Report", fileType: "PDF", cgwUserFriendlyName: "Hotspot Report"],

        [fileName: "QC_Report", fileType: "PDF", cgwUserFriendlyName: "Sequencing QC Metrics Report"],

        [fileName: "main.pindel.out_D", fileType: "INDELS_IN_EXON", cgwUserFriendlyName: "Pindel Raw Output Deletion File"],
        [fileName: "main.pindel.out_SI", fileType: "INDELS_IN_EXON", cgwUserFriendlyName: "Pindel Raw output Insertion File"],
        [fileName: "main.clean.bam", fileType: "BAM", cgwUserFriendlyName: "BAM File"],
        [fileName: "main.gatk.vcf", fileType: "VCF", cgwUserFriendlyName: "GATK VCF File"],
        [fileName: "main.pindel.vcf", fileType: "VCF", cgwUserFriendlyName: "Pindel VCF File"],
        [fileName: "main.vcf", fileType: "VCF", cgwUserFriendlyName: "VCF"],
        [fileName: "main.bam", fileType: "BAM", cgwUserFriendlyName: "BAM"]
]
/** ------- INFORMATICS  1 ends------- */




/** --- Define per-ENVIRONMENT configurations here 6 starts --- */
//Environment CGW settings
environments {
    development {
        //grails.plugins.springsecurity.providerNames = ['cgwAuthProvider'] // use this to avoid LDAP authentication if LDAP server is down $
        //grails.serverURL = "http://moffit.pieriandx.com:8080/${appName}"
        cgw.serverName = "localhost"

        //File used to run the db migration scripts
        grails.plugin.databasemigration.updateOnStartFileNames = ['changelog.groovy', 'changelog-dev.groovy']

        //Enable SQL logs to be shown in UI
        cgw.sql.log.file.enable = false
        cgw.sql.log.request.enable = false

        //Mrg, application and db log locations
        cgw.db.mrg.mount.location = "${basedir}${file_sep}web-app${file_sep}${igv.cache.path}${file_sep}"  // Make sure to end it with a slash('/').
        cgw.db.log.mount.location = "${basedir}${file_sep}web-app${file_sep}${igv.cache.path}${file_sep}"  // Make sure to end it with a slash('/').
        cgw.app.log.mount.location = "${basedir}${file_sep}web-app${file_sep}${igv.cache.path}${file_sep}"  // Make sure to end it with a slash('/').

        //Location where IGV is hosted
        igv.jnlp.codebase = "igv/IGV_2.3.49"

        //Patient VCF mount location
        cgw.db.patient.vcf.mount.directory.location = "${userHome}/cgw-temp-files/cgw-patient-data-cache-development"

        //All CGW case related files are stored here
        cgw.uploadDir = "${userHome}/cgw-temp-files/files/deployment_package/development/seqdata/ALL_GPS_Cases"
        cgw.informatics.job.basedir = "${userHome}/cgw-temp-files/files/deployment_package/development/seqdata"

        //External variants db user name
        cgw.external.variants.database.user = "cgw_ext"
        cgw.external.variants.database.url = "jdbc:postgresql://localhost:5432/cgw"
        cgw.external.variants.database.password = "cgw_ext"
        cgw.external.variants.database.driver =  "org.postgresql.Driver"

    }
    test {
        //Grails resources plugin debugging enabled and minifying settings
        grails.resources.debug = false
        grails.resources.rewrite.css = false
        grails.resources.mappers.yuicssminify.disable = true
        grails.resources.mappers.yuijsminify.disable = true
        grails.resources.processing.enabled = false

        //Informatics service url
        cgw.informaticsjob.serviceurl = "http://localhost:8080/informatics/service/"

        //Spring security enabling/disabling
        grails.plugin.springsecurity.active = true

        grails.serverURL = "http://localhost:8080/${appName}"
        cgw.serverName = "localhost"
        cgw.informaticsjob.quartz.poll.startdelay = 30000000
        cgw.signoff.quartz.poll.startdelay = 30000000
        cgw.db.mrg.mount.location = "${basedir}${file_sep}web-app${file_sep}${igv.cache.path}${file_sep}"  // Make sure to end it with a slash('/').
        cgw.db.log.mount.location = "${basedir}${file_sep}web-app${file_sep}${igv.cache.path}${file_sep}"  // Make sure to end it with a slash('/').
        cgw.app.log.mount.location = "${basedir}${file_sep}web-app${file_sep}${igv.cache.path}${file_sep}"  // Make sure to end it with a slash('/').
        cgw.db.patient.vcf.mount.directory.location ="${userHome}/cgw-temp-files/cgw-patient-data-cache-development"
        cgw.cases.archival.directory = "${userHome}/cgw-temp-files/deleted_case_data"

        //Quartz job polling interval for each phase
        cgw.informaticsjob.process.quartz.poll = [
                ruleProcessingInterval: 60 * 60 * 3 * 1000,
                ruleProcessingStartdelay: 60 * 60 * 3 * 1000,

                patientDataLoadInterval: 60 * 60 * 3 * 1000,
                patientDataLoadStartdelay: 60 * 60 * 3 * 1000,

                fileDownloadInterval: 60 * 60 * 3 * 1000,
                fileDownloadStartdelay: 60 * 60 * 3 * 1000,

                syntaxGenerationInterval: 60 * 60 * 3 * 1000,
                syntaxGenerationStartdelay: 60 * 60 * 3 * 1000,

                reportGenerationInterval: 60 * 60 * 3 * 1000,
                reportGenerationStartdelay: 60 * 60 * 3 * 1000,

                maxFailedAttempts: 5

        ]


        //CGW plugin - @since 1.5
        cgw.plugin.repositories.central.name = "Central Repository"
        cgw.plugin.repositories.central.implementorClass = "edu.wustl.cgw.plugin.repository.HttpRepository"
        cgw.plugin.repositories.central.location = "http://localhost:9898/appstore"

        //Local repository
        cgw.plugin.repositories.local.name = "Local Repository"
        cgw.plugin.repositories.local.implementorClass = "edu.wustl.cgw.plugin.repository.LocalFileSystemRepository"
        cgw.plugin.repositories.local.location = "${userHome}${file_sep}cgw-local-test-repo"

        //External variants db user name
        cgw.external.variants.database.user = "cgw_ext"
        cgw.external.variants.database.url = "jdbc:postgresql://localhost:5432/cgw"
        cgw.external.variants.database.password = "cgw_ext"
        cgw.external.variants.database.driver =  "org.postgresql.Driver"


        //All CGW case related files are stored here
        cgw.uploadDir = "${userHome}/cgw-temp-files/files/deployment_package/test/seqdata/ALL_GPS_Cases"
        cgw.informatics.job.basedir = "${userHome}/cgw-temp-files/files/deployment_package/test/seqdata"

        /**
         * Dag configuration starts from here.
         *
         * Please note: what you see here in this section might have been overwritten below in per-environment sections.
         * Please keep this in mind when trying to determine what settings are taking place in your environment.
         * In most cases, your customizations should go under the 'production' environment further below.
         * However, please do go over this section anyway, because it contains useful explanation of configuration parameters.
         */
        reportGeneration {

            //Use mock Aviary service or real aviary service
            mock.aviary = false

            /**
             * This section contains URLs to RH MRG Aviary web services. Normally, you would only need to change host names, ports, and timeouts the rest is not expected to change.
             * NOTE: This parameter is overridden in 'production' environment (see below).
             */
            mrg {
                //Milliseconds.
                timeout = 30000

                aviary.job.url = "http://localhost:4556/services/job"
                aviary.query.url = "http://localhost:45567/services/query"
                aviary.job.submitJob.url = aviary.job.url
                aviary.job.removeJob.url = aviary.job.url
                aviary.query.getJobStatus.url = aviary.query.url
                aviary.query.getJobData.url = aviary.query.url
                aviary.query.getJobDetails.url = aviary.query.url + "/getJobDetails"

                /**
                 * Priority of submitted jobs. An integer value >= 0.
                 * NOTE: This parameter is overridden in 'production' environment (see below).
                 */
                job_priority = 0
            }

            /**
             * List of DAGs available for execution through this service. This list is returned to service clients by 'getDAGs' operation.
             * Please use unique names for the DAGs. See this link for additional information:
             * https://docs.google.com/a/semanticbits.com/document/d/1LEXHyIhlTvOA0_GjdqTX_9SjeIS-wRK0IgO5omms5xA/edit?hl=en_US#heading=h.ervt2rrghb4l
             * NOTE: 'production' environment adds additional DAGs to this list (see below).
             */
            dags {

                report_generation {

                    //Unique ID of the DAG.
                    id = "report_generation"

                    description = "DAG that runs the report generation on condor "

                    //Set to true if you want this DAG to be a default one,
                    default_dag = false

                    /**
                     * Name of the condor scheduler
                     * http://research.cs.wisc.edu/htcondor/manual/current/condor_submit.html
                     * used as - condor_submit -name mrgsp1m1.cbmi.wucon.wustl.edu -name sched_name
                     * Submit to the specified condor_sched. Use this option to submit to a condor_sched other than the default local one.
                     * Sched_name is the value of the Name ClassAd attribute on the machine where the condor_sched daemon runs.
                     */
                    condor_scheduler = "mrgsp1m1.cbmi.wucon.wustl.edu"

                    /**
                     * Define the parameters that the DAG needs.
                     * These parameters should be passed to the 'runInformatics' operation by service clients.
                     * Each parameter must have a name, true/false mandatory indicator, and a description.
                     * 'runInformatics' operation will raise an error if a mandatory parameter is not specified by service client.
                     * The remainder of the DAG configuration can refer to parameter values by using @<param_name>@ placeholder, which at runtime will be replaced with actual parameter value.
                     * For example: @runID@. Unspecified optional parameters will have empty string value.
                     * There is one implicit 'JOB_ID' parameter generated at runtime for each DAG run. It will match the Informatic Job's ID.
                     * At this point we have only two parameters. However, sample lane number might be added soon.
                     parameters = [
                     ['caseInformation', true, 'information about case'],
                     ['panelID', true, 'panel name'],
                     ['cgwServerID', true, 'CGW server name from which ever server the job is submitted']
                     ]
                     */
                    parameters = []

                    /**
                     * IMPORTANT: this should point to the directory described as $INFORMATICS_SCRIPTS in section 9.2 Deployment
                     * Steps of the https://wikip2m1.wustl.edu/foswiki/bin/view/CGW/CGWDeploymentPlan
                     */
                    scripts = '/usr/local/mrgstorage/files/deployment_package/ci/scripts/ReportGeneration/scripts'

                    /**
                     * Linux user under which the job will run on the cluster.
                     * Default is the same user that runs this web application, e.g. cgw_dev.
                     */
                    owner = "cgw_dev"

                    /**
                     * After the job completes, Informatics Service expects the job's output to be at the following location.
                     * 'getOutputInfo' operation will work off this location.
                     * Again, you can use parameter placeholders as described above.
                     * Important: please make sure that the user that runs Informatics Service has permissions to create this directory.
                     * output  = run_dir+"/@acc_num@_@cgw_order_id@/@JOB_ID@"
                     */
                    output = "/usr/local/mrgstorage/files/deployment_package/ci/report_generation/@JOB_ID@/report-service-client"

                    /**
                     * Initial working directory.
                     * Working directory must be set to the output directory, so that relative paths in *.condor files get properly resolved.
                     * If you inspect *.condor files you'll see that log file names are specified with relative static path. However, each job instance will
                     * get a unique log file, because the job output directory is unique (see above). Job output directory becomes the working directory, against which
                     * the relative paths will be resolved.
                     * Why not just specify a unique log file name right in .condor file in the first place? Because that would require using macros within the file name,
                     * and DAGMan prohibits using macros within the name of the UserLog (but not in names of Err or Out log).
                     */
                    iwd = output

                    /**
                     * IMPORTANT: a coupling point between Grid Service and Condor job description files.
                     * Condor jobs are set up to write into "./logs" directory.
                     * See *.condor files for details. This value must correspond to what's in the *.condor files.
                     * logs = run_dir+"/@acc_num@_@cgw_order_id@/@JOB_ID@/logs"
                     */
                    logs = output + "/logs"

                    /**
                     * Shell command that executes the job.
                     * cmd = "/usr/bin/condor_dagman"
                     */
                    cmd = "/usr/bin/condor_submit"

                    /**
                     * http://research.cs.wisc.edu/htcondor/manual/current/condor_submit.html
                     * condor_submit will exit with a status value of 0 (zero) upon success, and a non-zero value upon failure.
                     * A list of return codes that will indicate a successful completion of the job.
                     * Aviary API returns "COMPLETE" for both successful and failed jobs
                     * that's why we need to inspect the exit code separately to determine the outcome. Normally, zero means success.
                     */
                    success_codes = [0]

                    /**
                     * Any command line arguments go here.
                     * You can find description of these DAGMan parameters here: http://www.cs.wisc.edu/condor/manual/v7.5/condor_dagman.html
                     * We are using unique lock and rescue files to avoid clashes among jobs.
                     * args = '-f -l . -Lockfile /tmp/@JOB_ID@.lock -rescue /tmp/@JOB_ID@.rescue -outfile_dir '+output+'  -AutoRescue 0 -Dag ' + scripts + '/reportgeneartion.dag -allowversionmismatch -CsdVersion $CondorVersion:$ -Force -Dagman /usr/bin/condor_dagman'
                     */
                    args = '-verbose -name ' + condor_scheduler + ' ' + scripts + '/reportgeneartion.condor'

                    /**
                     * Job requirements: http://www.cs.wisc.edu/condor/manual/v7.5/2_5Submitting_Job.html#SECTION00352000000000000000
                     * IMPORTANT: this one must be set right; otherwise DAGMan gets stuck in IDLE state.
                     */
                    requirements = [['FILESYSTEM', 'mrgsp1m1.cbmi.wucon.wustl.edu']]

                    //Any extra parameters that need to be passed to RH MRG scheduler. These are Condor-specific. See http://www.cs.wisc.edu/condor/manual/v7.5/2_5Submitting_Job.html for details.
                    extra = [

                            ['JobPrio', 'INTEGER', '' + reportGeneration.mrg.job_priority],
                            //['JobUniverse', 'INTEGER', '7'],
                            //['RemoveKillSig', 'STRING', 'SIGUSR1'],
                            ['Requirements', 'EXPRESSION', 'true'], // <-- this one is important to prevent DAGMan from getting stuck in IDLE state.

                            ['Err', 'STRING', logs + '/condor_dagman.err'],

                            //This is the master DAGMan log file. Please 'tail -f' it to monitor the status and progress of the DAG.
                            ['Out', 'STRING', logs + '/condor_dagman.out'],
                            ['UserLog', 'STRING', logs + '/condor_dagman.log'],

                            /**
                             * IMPORTANT: a coupling point between CGW Grid Service and Condor job description files.
                             * Various parameters are being passed into the pipeline scripts through the environment variables specified here.
                             * See *.condor files for details.
                             */
                            ['Env', 'STRING', '_CONDOR_MAX_DAGMAN_LOG=0;_CONDOR_DAGMAN_LOG_ON_NFS_IS_ERROR=false;_REpORT_GENERATION_PRIO=' + reportGeneration.mrg.job_priority + ';_CONDOR_DAGMAN_LOG=' + logs + '/condor_dagman.out;_OUTPUT_DIR=' + output + ';JOB_ID=@JOB_ID@'],
                            ['EnvDelim', 'STRING', ';']
                    ]

                    /**
                     * A Groovy script that needs to be executed prior to starting a new job instance.
                     * It gets executed before "submitJob" command is sent to Aviary.
                     * If the script fails with an error, the job won't be started and an error will be reported back to the service client.
                     * See production DAG below for an example. Again, you can use parameter placeholders as described above.
                     * This Groovy script will execute before each job launch. It will create output and log directories on MRG storage.
                     * This must be done prior to invoking DAGMan; it won't run otherwise.
                     */
                    pre_hook = "def dir = new java.io.File('" + logs + "'); if (!((dir.exists() && dir.isDirectory()) || dir.mkdirs())) throw new RuntimeException('Unable to create directory for log files: ${logs}')"

                    /**
                     * To work around Aviary's intermittent submit problems (see https://access.redhat.com/support/cases/00553116), we re-try failed job submissions.
                     * This is the number of re-tries.
                     */
                    retry_attempts = 30

                    //Millisecond delay between attempts.
                    retry_delay = 1000
                }
            }
        }
    }
}

//Environment GORM settings
environments {
    development {
        grails.resources.mappers.yuicssminify.disable = true
        grails.resources.mappers.yuijsminify.disable = true
        grails.resources.processing.enabled = false
        //grails.resources.debug = true

        grails.plugin.quartz2.autoStartup = false

        grails.plugin.databasemigration.updateOnStart = true

        cgw.informaticsjob.process.quartz.poll = [
                ruleProcessingInterval: 60 * 60 * 3 * 1000,
                ruleProcessingStartdelay: 60 * 60 * 3 * 1000,

                patientDataLoadInterval: 60 * 60 * 3 * 1000,
                patientDataLoadStartdelay: 60 * 60 * 3 * 1000,

                fileDownloadInterval: 60 * 60 * 3 * 1000,
                fileDownloadStartdelay: 60 * 60 * 3 * 1000,

                syntaxGenerationInterval: 60 * 60 * 3 * 1000,
                syntaxGenerationStartdelay: 60 * 60 * 3 * 1000,

                reportGenerationInterval: 60 * 60 * 3 * 1000,
                reportGenerationStartdelay: 60 * 60 * 3 * 1000,

                maxFailedAttempts: 5
        ]
    }
    test {
        grails.resources.mappers.yuicssminify.disable = true
        grails.resources.mappers.yuijsminify.disable = true
        grails.resources.processing.enabled = false

        grails.plugin.quartz2.autoStartup = false
    }
    ci {
        grails.plugin.quartz2.autoStartup = false
        grails.resources.mappers.yuicssminify.disable = true
        grails.resources.mappers.yuijsminify.disable = true
    }
    functional_ci {
        grails.plugin.quartz2.autoStartup = false
        grails.resources.mappers.yuicssminify.disable = true
        grails.resources.mappers.yuijsminify.disable = true
    }
}

//Excludes the resources in the given directory
grails.resources.adhoc.excludes = ["/js/tinymce/**/*.*"]

/** --- Define per-ENVIRONMENT configurations here 6 ends --- */




/** ---- GORM settings 7 starts ---- */
/**
 * GORM settings starts here
 * If set to true, causes the save() method on domain classes to throw a grails.validation.ValidationException if validation fails during a save
 */
grails.gorm.failOnError = true

//Spring Security Core plugin settings start here:
grails.plugin.springsecurity.successHandler.defaultTargetUrl = '/cases'
grails.plugin.springsecurity.userLookup.userDomainClassName = 'edu.wustl.cgw.domain.User'
grails.plugin.springsecurity.securityConfigType = 'Annotation'
grails.plugin.springsecurity.voterNames = ['authenticatedVoter', 'customerURLAccessDecisionVoter']

//Deny access to all URLs that do not have an applicable URL-Privilege configuration
grails.plugin.springsecurity.rejectIfNoRule = true

//Set errorPage to null to send Error 403 instead of showing error page
grails.plugin.springsecurity.adh.ajaxErrorPage = null
grails.plugin.springsecurity.adh.errorPage = null

/**
 * Register security event listener
 * @see: http://grails-plugins.github.com/grails-spring-security-core/docs/manual/guide/7%20Events.html
 */
grails.plugin.springsecurity.useSecurityEventListener = true

//You can also define 'static' mappings that cannot be expressed in the controllers, such as '/**' or for JavaScript, CSS, or image URLs.
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
        '/js/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/**/js/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '**/js/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '**/css/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '**/*.js': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/**/*.js': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/**/*.css': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/**/*.png': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '**/*.css': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '**/*.png': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/**/*.woff': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/**/*.ttf': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '**/*.woff': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '**/*.ttf': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/**/css/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '**/images/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/**/images/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '**/cache/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '**/services/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/services/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '**/igv/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/**/igv/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/igv/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/**/cache/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/cache/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/css/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/images/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/login/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
        '/logout/**': ['IS_AUTHENTICATED_FULLY'],
        '/tmpfiles/SampleMedicalFacilities.xls': ['UPDATE_MEDICAL_FACILITIES'],
        '/tmpfiles/SampleSequencingDetails.csv': ['CREATE_SEQUENCING_RUN','EDIT_SEQUENCING_RUN'],
        '/tmpfiles/SampleDiseases.xls': ['UPDATE_DISEASE'],
]

//Mail plugin configuration
grails {
    mail {
        host = "smtp.gmail.com"
        port = 465
        username = "cgw_email@pieriandx.com"
        password = "P!er!an123@"
        props = ["mail.smtp.auth": "true",
                 "mail.smtp.socketFactory.port": "465",
                 "mail.smtp.socketFactory.class": "javax.net.ssl.SSLSocketFactory",
                 "mail.smtp.socketFactory.fallback": "false"
        ]
    }
}

user.deactivation.emailSubject = "Your account has been deactivated by admin. "
user.approval.emailSubject = "CGW account approval"
user.create.emailSubject = "CGW account created"

//Jquery as default JS library. No need to add <g:javascript library="jquery" /> to gsp files
grails.views.javascript.library = "jquery"

cgw.privateKey.masterPassword = '4fuPFI0aoz'

cgw.default.source = "CGW";

//This is the current genomic load info. GenomicLoadInfo
current.genomic.load.info = 1
taxnomoy.id = 5432

//Start the process using: /<OFFICE_HOME>/program/soffice.bin --accept=socket,host=127.0.0.1,port=2002 --headless -nocrashreport --nodefault --nofirststartwizard --nolockcheck --nologo --norestore
office {
    //ExternalOfficeManager connects to only one port
    port = 2002
}

grails.plugin.quartz2.autoStartup = true
//Quartz job sign-off settings
cgw.signoff.quartz.poll.interval = 60 * 60 * 1000 //3600 seconds
cgw.signoff.quartz.poll.startdelay = 60 * 60 * 1000 //180 seconds

//Refer plugin docs at https://github.com/9ci/grails-quartz2
org {
    quartz {
        //anything here will get merged into the quartz.properties so you don't need another file
        scheduler.instanceName = 'CGWQuartzScheduler'
        threadPool.class = 'org.quartz.simpl.SimpleThreadPool'
        threadPool.threadCount = 12  //There are 4 threads in the thread pool, which means that a maximum of 4 jobs can be run simultaneously.
        threadPool.threadsInheritContextClassLoaderOfInitializingThread = true
        jobStore.class = 'org.quartz.simpl.RAMJobStore' //RAMJobStore is used to store scheduling information (job, triggers and calendars) within memory. RAMJobStore is fast and lightweight, but all scheduling information is lost when the process terminates.

    }
}

//Load reports on condor or not..by default false i.e. don't load reports on condor..
cgw.load.report.condor = false


grails.gorm.default.constraints = {
    /**
     * A shared constraint that allows alphanumerics and spaces
     * Usage:
     * In any domain class add as shown below:
     * static constraints = {*     propertyName(shared:'allowAlphanumericSpace')
     *}*/
    fileNameValidator(validator: { String val, obj ->
        //http://en.wikipedia.org/wiki/Filename#Reserved_characters_and_words
        if (StringUtils.containsAny(val, "/\\?%*:|\"<>. !@#\$^&+=';,")) {
            return ['alphanumeric.message']
        }
    })
}

/**
 * Fix grails taglib g:paginate to work with bootstrap css.
 * @see: https://github.com/groovydev/twitter-bootstrap-grails-plugin/blob/master/README.md
 */
//grails.plugins.twitterbootstrap.fixtaglib = true
grails.plugins.twitterbootstrap.defaultBundle = 'bundle_bootstrap'

gbrowser.variantstab.default.restrictedfields =  ["chromosome", "cytogeneticLocation", "userVariant"]

// external config reload configuration parms
grails.plugins.reloadConfig.includeConfigLocations = true
//This is the time in milliseconds that the polling job will run to check for file modifications. setting it currently to half hour
grails.plugins.reloadConfig.interval = 1800000
//If set to false, this will disable the polling job completely. This may be used to disable polling in certain environments. This is false by default in the test environment but true in all others.
grails.plugins.reloadConfig.enabled = true
report.widget.auto.save.interval = 10

/** ---- GORM settings 7 ends ---- */
