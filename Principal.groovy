import groovy.json.JsonSlurper
import groovy.json.JsonOutput

import java.math.RoundingMode

principal()

def principal() {
    def parametres = lireParametres(args[0])
    parametres.versement = calculerVersement parametres
    def pret = [
            parametres: parametres,
            calendrier: genererCalendrier(parametres),
    ]
    genererPdf pret, "${args[0]}.pdf"
}

def lireParametres(nomFichierIntrant) {
    def jsonSlurper = new JsonSlurper()
    jsonSlurper.parse new File(nomFichierIntrant)
}

def calculerVersement(parametres) {
    def montant = parametres.montantInitial as Double
    def taux = parametres.tauxPeriodique as Double
    def nombrePeriodes = parametres.nombrePeriodes as int
    def versement = montant * taux / (1 - (1 + taux) ** (- nombrePeriodes))
    versement.round(2) as String
}

def genererCalendrier(parametres) {
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

def genererPdf(pret, nomFichierPdf) {
    def markdown = genererMarkdown pret
    def nomFichierMarkdown = creerFichierMarkdown markdown
    String commande = "pandoc ${nomFichierMarkdown} -o ${nomFichierPdf} --variable=lang:fr --variable=documentclass:scrartcl --variable=margin-left:1.25in --variable=margin-right:1.25in"
    commande.execute().waitForProcessOutput(System.out, System.err)
}

def genererMarkdown(pret) {
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

def markdownLignes(calendrier) {
    calendrier.collect { ligne -> ligne.with {
        "|$periode|${formatDate(date)}|${formatDollar(capitalDebut)}" +
                "|${formatDollar(versement)}|${formatDollar(interet)}" +
                "|${formatDollar(capital)}|${formatDollar(capitalFin)}|\n    "
    } }.join()
}

def formatDate(date) {
    date.format 'yyyy-MM-dd'
}

def formatDollar(montant) {
    def formateur = java.text.NumberFormat.currencyInstance
    formateur.format(montant as BigDecimal)
}

def formatPourcent(pourcent) {
    def formateur = new java.text.DecimalFormat('###.##\u00A0%')
    formateur.format(pourcent as BigDecimal)
}

def creerFichierMarkdown(markdown) {
    def fichier = File.createTempFile 'pret-','.md', new File('.')
    fichier.text = markdown
    fichier.deleteOnExit()
    fichier.name
}