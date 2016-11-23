import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovy.json.JsonBuilder


import java.math.RoundingMode

@Grab('org.codehaus.groovy:groovy-xmlrpc:0.8')
import groovy.net.xmlrpc.*
import java.text.DecimalFormat

def generateur_markdown_server = new XMLRPCServer()

generateur_markdown_server.echo = { it }

def generateur_markdown_socket = new ServerSocket( 8082 )

generateur_markdown_server.startServer( generateur_markdown_socket )
println "demarrage 8082"             
             

generateur_markdown_server.generer_markdown = {json_calendrier -> json_calendrier
    JsonSlurper slurper = new JsonSlurper()
    def calendrier = slurper.parseText(json_calendrier)
    """\
    % Calcul du remboursement d'un prêt
    % Louis Martin -- INF5153 -- UQAM
    % \\today

    ---
    header-includes:
        - \\usepackage{floatrow}
        - \\DeclareFloatFont{normalsize}{\\normalsize}
        - \\floatsetup[table]{font=normalsize}
    ---

    # Paramètres du prêt

    Scénario : **$calendrier.parametres.scenario**

    Date du prêt : **$calendrier.parametres.datePret**

    Montant initial du prêt : **${formatDollar(calendrier.parametres.montantInitial)}**

    Nombre de mensualités : **$calendrier.parametres.nombrePeriodes**

    Taux mensuel : **${formatPourcent(calendrier.parametres.tauxPeriodique)}**

    Versement mensuel : **${formatDollar(calendrier.parametres.versement)}**

    # Calendrier de remboursement

    | **No** | **Date** | **Solde début** | **Versement** | **Intérêt** | **Capital** | **Solde fin** |
    |:---|:----------:|------------:|----------:|----------:|----------:|------------:|
    ${markdownLignes(calendrier.calendrier)}

    # Totaux

    Total des paiements : **${formatDollar(calendrier.calendrier.last().totalCumulatif)}**

    Total des intérêts : **${formatDollar(calendrier.calendrier.last().interetCumulatif)}**

    Total du capital remboursé : **${formatDollar(calendrier.calendrier.last().capitalCumulatif)}**
    """.stripIndent()
}

def markdownLignes(calendrier) {
    calendrier.collect { ligne -> ligne.with {
        "|$periode|${formatDate(date)}|${formatDollar(capitalDebut)}" +
                "|${formatDollar(versement)}|${formatDollar(interet)}" +
                "|${formatDollar(capital)}|${formatDollar(capitalFin)}|\n    "
    } }.join()
}

def formatDate(date) {
    Date date1 = Date.parse("yyyy-MM-dd", date)
    date1.format 'yyyy-MM-dd'
}

def formatDollar(montant) {
//    def formateur = java.text.NumberFormat.currencyInstance
//    formateur.format(montant as BigDecimal)
    DecimalFormat.getCurrencyInstance(Locale.CANADA_FRENCH).format(montant as BigDecimal)
}

def formatPourcent(pourcent) {
    def formateur = new java.text.DecimalFormat('###.##\u00A0%')
    formateur.format(pourcent as BigDecimal)
}