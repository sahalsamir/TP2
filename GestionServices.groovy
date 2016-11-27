@Grapes( [
        @Grab('ch.qos.logback:logback-classic:1.1.7'),
        @GrabConfig(systemClassLoader=true)
] )


import groovy.util.logging.Slf4j

@Slf4j
class MasterServicres {
    static void main(String[] args){
        log.info 'Début de l\'exécution'
        boolean isMcroservicesOn = false;

        while (true) {

            if (!isMcroservicesOn) {
                isMcroservicesOn = lanceMicroservices()
            }

            def cmd = System.console().readLine()

            if (cmd.equals('stop')) {

                "fuser -k 8080/tcp ".execute()
                "fuser -k 8081/tcp ".execute()
                "fuser -k 8082/tcp ".execute()
                break;
            } else {
                println('<Stop> pour arrête les trois microservices')
            }
        }
        log.info 'Fin de l\'exécution'
    }

    static def lanceMicroservices() {

        Thread.start {
            log.info "demarrage du serveice calcul versement sur le port 8080"
            def processVersement = "groovy  ServiceVersement.groovy".execute()
            processVersement.waitForProcessOutput(System.out, System.err)
            log.info 'Arrêt  du serveice calcul versement sur le port 8080'
        }
        Thread.start{
            log.info "demarrage du serveice calendrier paiements sur le port 8081"
            def processCalendrier = 'groovy  ServiceCalendrier.groovy'.execute()
            processCalendrier.waitForProcessOutput(System.out, System.err)
            log.info 'Arrêt  du serveice calendrier paiements sur le port 8081'
        }

        Thread.start{
            log.info "demarrage du serveice generateur markdown sur le port 8082"
            def processMarkdown = 'groovy  ServiceMarkdown.groovy'.execute()
            processMarkdown.waitForProcessOutput(System.out, System.err)
            log.info 'Arrêt  du serveice generateur markdown sur le port 8082'
        }

        true
    }
}



