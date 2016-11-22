import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovy.json.JsonBuilder


import java.math.RoundingMode

@Grab('org.codehaus.groovy:groovy-xmlrpc:0.8')
import groovy.net.xmlrpc.*

def server8082 = new XMLRPCServer()

server8082.echo = { it }

def serverSocket8082 = new ServerSocket( 8082 )

server8082.startServer( serverSocket8082 )
println "demarrage 8082"             
             
server8082.genererPdf = { pret, nomFichierPdf ->        
    def markdown = genererMarkdown pret
    def nomFichierMarkdown = creerFichierMarkdown markdown
    String commande = "pandoc ${nomFichierMarkdown} -o ${nomFichierPdf} --variable=lang:fr --variable=documentclass:scrartcl --variable=margin-left:1.25in --variable=margin-right:1.25in"
    commande.execute().waitForProcessOutput(System.out, System.err)
}

server8082.genererMarkdown = {pret->pret
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

    Scénario : **$pret.parametres.scenario**

    Date du prêt : **$pret.parametres.datePret**

    Montant initial du prêt : **${formatDollar(pret.parametres.montantInitial)}**

    Nombre de mensualités : **$pret.parametres.nombrePeriodes**

    Taux mensuel : **${formatPourcent(pret.parametres.tauxPeriodique)}**

    Versement mensuel : **${formatDollar(pret.parametres.versement)}**

    # Calendrier de remboursement

    | **No** | **Date** | **Solde début** | **Versement** | **Intérêt** | **Capital** | **Solde fin** |
    |:---|:----------:|------------:|----------:|----------:|----------:|------------:|
    ${markdownLignes(pret.calendrier)}

    # Totaux

    Total des paiements : **${formatDollar(pret.calendrier.last().totalCumulatif)}**

    Total des intérêts : **${formatDollar(pret.calendrier.last().interetCumulatif)}**

    Total du capital remboursé : **${formatDollar(pret.calendrier.last().capitalCumulatif)}**
    """.stripIndent()
}

server8082.markdownLignes = { calendrier -> calendrier
    calendrier.collect { ligne -> ligne.with {
        "|$periode|${formatDate(date)}|${formatDollar(capitalDebut)}" +
                "|${formatDollar(versement)}|${formatDollar(interet)}" +
                "|${formatDollar(capital)}|${formatDollar(capitalFin)}|\n    "
    } }.join()
}

server8082.formatDate = { date -> date
    date.format 'yyyy-MM-dd'
}

server8082.formatDollar = {montant -> montant
    def formateur = java.text.NumberFormat.currencyInstance
    formateur.format(montant as BigDecimal)
}

server8082.formatPourcent = { pourcent -> pourcent
    def formateur = new java.text.DecimalFormat('###.##\u00A0%')
    formateur.format(pourcent as BigDecimal)
}

server8082.creerFichierMarkdown = {markdown -> markdown
    def fichier = File.createTempFile 'pret-','.md', new File('.')
    fichier.text = markdown
    fichier.deleteOnExit()
    fichier.name
}