@Grapes( [
        @Grab('ch.qos.logback:logback-classic:1.1.7'),
        @Grab('org.codehaus.groovy:groovy-xmlrpc:0.8')
] )
import groovy.net.xmlrpc.*
import groovy.json.JsonOutput
import groovy.util.logging.Slf4j

@Slf4j
class MasterClient {
    static void main(String[] args){
        def url8080 = "http://localhost:8080"
        def url8081 = "http://localhost:8081"
        def url8082 = "http://localhost:8082"

        def versementProxy = new XMLRPCServerProxy(url8080)
        def calendrierProxy = new XMLRPCServerProxy(url8081)
        def markdownProxy = new XMLRPCServerProxy(url8082)

        log.info "ServiceVersement:: Calcul du versement périodique d’un prêt"
        def json = versementProxy.calculerVersement args[0]

        log.info "ServiceCalendrier:: Production du calendrier des paiements périodiques"
        def calendrier = calendrierProxy.calandrierPaiements json

        log.info "ServiceMarkdown:: Génération du markdown"
        def markdown = markdownProxy.genererMarkdown calendrier

        println JsonOutput.prettyPrint(json)
        println JsonOutput.prettyPrint(calendrier)
        println markdown
    }
}






