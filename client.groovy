@Grab('org.codehaus.groovy:groovy-xmlrpc:0.8')

import groovy.net.xmlrpc.*
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

import java.math.RoundingMode

"groovy  microservice_calcul_versement.groovy &".execute()
"groovy  microservice_calendrier_paiements.groovy &".execute()
"groovy  microservice_generateur_markdown.groovy &".execute()


def url8080 = "http://localhost:8080"
def url8081 = "http://localhost:8081"
def url8082 = "http://localhost:8082"

def calcul_versement_proxy = new XMLRPCServerProxy(url8080)
def calendrier_paiements_proxy = new XMLRPCServerProxy(url8081)
def generateur_markdown_proxy = new XMLRPCServerProxy(url8082)


def json = calcul_versement_proxy.calculer_versement args[0]

def calendrier = calendrier_paiements_proxy.calandrier_paiements json

def markdown = generateur_markdown_proxy.generer_markdown calendrier
println markdown

"fuser -k 8080/tcp &".execute()
"fuser -k 8081/tcp &".execute()
"fuser -k 8082/tcp &".execute()

