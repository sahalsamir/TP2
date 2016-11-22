import java.math.RoundingMode

@Grab('org.codehaus.groovy:groovy-xmlrpc:0.8')
import groovy.net.xmlrpc.*

def server8081 = new XMLRPCServer()

server8081.echo = { it }

def serverSocket8081 = new ServerSocket( 8081 )

server8081.startServer( serverSocket8081 )
println "demarrage 8081"   
 
 server8081.genererCalendrier = {parametres -> parametres
    def date = Date.parse 'yyyy-MM-dd', parametres.datePret
    def nombrePeriodes = parametres.nombrePeriodes as int
    def taux = parametres.tauxPeriodique as BigDecimal
    def capitalDebut = parametres.montantInitial as BigDecimal
    def versement = parametres.versement as BigDecimal
    def totalCumulatif = BigDecimal.ZERO
    def interetCumulatif = BigDecimal.ZERO
    def capitalCumulatif = BigDecimal.ZERO
    (1..nombrePeriodes).collect {
        use (groovy.time.TimeCategory) {
            date = date + 1.month
        }
        def interet = (capitalDebut * taux).setScale 2, RoundingMode.HALF_UP
        def capital = versement - interet
        def capitalFin = capitalDebut - capital
        totalCumulatif += versement
        interetCumulatif += interet
        capitalCumulatif += capital
        def ligne = [
                periode: it,
                date: date,
                capitalDebut: capitalDebut,
                versement: versement,
                interet: interet,
                capital: capital,
                capitalFin: capitalFin,
                totalCumulatif: totalCumulatif,
                interetCumulatif: interetCumulatif,
                capitalCumulatif: capitalCumulatif,
        ]
        capitalDebut = capitalFin
        ligne
    }
}