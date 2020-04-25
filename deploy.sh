. sharedFuncs.sh

namespace="default"
versionFile="productionVersion.txt"
appName="covid-bot"
stage="production"
serviceName="covid-bot"
historyFile="productionHistory"

deployImage $namespace $versionFile $appName $stage $serviceName $historyFile 
