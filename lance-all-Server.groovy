def lien = "Desktop/INF5153/TP2"
def server8080 = ['/bin/bash', '-c', /osascript -e 'tell application "Terminal" to do script "cd $lien && groovy microservice_calcul_versement.groovy"'/].execute().text
def server8081 = ['/bin/bash', '-c', /osascript -e 'tell application "Terminal" to do script "cd $lien && groovy microservice_calendrier_paiements.groovy"'/].execute().text
def server8082 = ['/bin/bash', '-c', /osascript -e 'tell application "Terminal" to do script "cd $lien && groovy microservice_generateur_markdown.groovy "'/].execute().text
