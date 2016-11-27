@Grab('org.codehaus.groovy:groovy-xmlrpc:0.8')

import groovy.json.JsonSlurper
import groovy.net.xmlrpc.*
import java.text.DecimalFormat


def markdownServer = new XMLRPCServer()
def markdownSocket = new ServerSocket( 8082 )
markdownServer.startServer( markdownSocket )


markdownServer.genererMarkdown = {json -> json
    JsonSlurper slurper = new JsonSlurper()
    def calendrier = slurper.parseText(json)
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
    Date.parse("yyyy-MM-dd", date).format 'yyyy-MM-dd'
}

def formatDollar(montant) {
    DecimalFormat.getCurrencyInstance(Locale.CANADA_FRENCH).format(montant as BigDecimal)
}

def formatPourcent(pourcent) {
    def formateur = new java.text.DecimalFormat('###.##\u00A0%')
    formateur.format(pourcent as BigDecimal)
}

