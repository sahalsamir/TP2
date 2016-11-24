import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.math.RoundingMode

@Grab('org.codehaus.groovy:groovy-xmlrpc:0.8')
import groovy.net.xmlrpc.*

def calendrier_paiements_server = new XMLRPCServer()


def calendrier_paiements_socket = new ServerSocket(8081)

calendrier_paiements_server.startServer(calendrier_paiements_socket)
println "demarrage du serveice calendrier paiements sur le port 8081"

calendrier_paiements_server.calandrier_paiements = { json -> json
    JsonSlurper slurper = new JsonSlurper()
    def jsonMap = slurper.parseText(json)

    def date = Date.parse 'yyyy-MM-dd', jsonMap.datePret
    def nombrePeriodes = jsonMap.nombrePeriodes as int
    def taux = jsonMap.tauxPeriodique as BigDecimal
    def capitalDebut = jsonMap.montantInitial as BigDecimal
    def versement = jsonMap.versement as BigDecimal

    def totalCumulatif = BigDecimal.ZERO
    def interetCumulatif = BigDecimal.ZERO
    def capitalCumulatif = BigDecimal.ZERO

    def calendrier = (1..nombrePeriodes).collect {
        use(groovy.time.TimeCategory) {
            date = date + 1.month
        }
        def interet = (capitalDebut * taux).setScale 2, RoundingMode.HALF_UP
        def capital = versement - interet
        def capitalFin = capitalDebut - capital
        totalCumulatif += versement
        interetCumulatif += interet
        capitalCumulatif += capital


        def ligne = [
                periode         : it,
                date            : date,
                capitalDebut    : capitalDebut,
                versement       : versement,
                interet         : interet,
                capital         : capital,
                capitalFin      : capitalFin,
                totalCumulatif  : totalCumulatif,
                interetCumulatif: interetCumulatif,
                capitalCumulatif: capitalCumulatif,
        ]
        capitalDebut = capitalFin
        ligne
    }

    def json_extrant1 = [
            parametres: [
                    "scenario"      : jsonMap.scenario,
                    "datePret"      : jsonMap.datePret,
                    "montantInitial": jsonMap.montantInitial,
                    "nombrePeriodes": jsonMap.nombrePeriodes,
                    "tauxPeriodique": jsonMap.tauxPeriodique,
                    "versement"     : jsonMap.versement
            ],
            calendrier: calendrier
    ]
    JsonOutput.toJson(json_extrant1)
}