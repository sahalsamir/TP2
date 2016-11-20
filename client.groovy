@Grab('org.codehaus.groovy:groovy-xmlrpc:0.8')

import groovy.net.xmlrpc.*
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

import java.math.RoundingMode

def url8080 = "http://localhost:8080"
def url8081 = "http://localhost:8081"
def url8082 = "http://localhost:8082"

def serverProxy8080 = new XMLRPCServerProxy(url8080)
def serverProxy8081 = new XMLRPCServerProxy(url8081)
def serverProxy8082 = new XMLRPCServerProxy(url8082)

println serverProxy8080.echo("Bonjour 8080 ")
println serverProxy8081.echo("Bonjour 8081 ")
println serverProxy8082.echo("Bonjour 8082 ")
 

def lireParametres = { nomFichierIntrant ->  nomFichierIntrant
                       def jsonSlurper = new JsonSlurper()
                       jsonSlurper.parse new File(nomFichierIntrant) 
                       }

def calculerVersement = { parametres -> parametres
    def montant = parametres.montantInitial as Double
    def taux = parametres.tauxPeriodique as Double
    def nombrePeriodes = parametres.nombrePeriodes as int
    def versement = montant * taux / (1 - (1 + taux) ** (- nombrePeriodes))
    versement.round(2) as String
}

def client8080 = {
    def parametres = lireParametres(args[0])
    parametres.versement = calculerVersement parametres
    def pret = [
            parametres: parametres,
            calendrier: genererCalendrier(parametres),
    ]
    genererPdf pret, "${args[0]}.pdf"
    println serverProxy8080.echo(pret)
    
}

def genererCalendrier = {  parametres->parametres

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
serverProxy8080.client8080()
