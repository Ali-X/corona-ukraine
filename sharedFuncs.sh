function deployImage() {
    local namespace=$1
    local versionFile=$2
    local appName=$3
    local stage=$4
    local serviceName=$5

    if [ ! -f $versionFile ]; then
        echo "100" >> $versionFile
    fi
    project="covid-272613"
    serviceName=$serviceName
    currentVersion=`cut -d ',' -f2 $versionFile`
    newVersion=`expr $currentVersion + 1`
    sed -i ".bak" "s/$currentVersion/$newVersion/g" $versionFile

    echo "Preparing new version: $newVersion..."

    host="gcr.io"

    docker build -t "$stage-$newVersion" .
    docker tag "$stage-$newVersion" "${host}/${project}/$project-$appName-$stage:$newVersion"
    docker tag "$stage-$newVersion" "${host}/${project}/$project-$appName-$stage"
    docker push "${host}/${project}/$project-$appName-$stage:latest"
    docker push "${host}/${project}/$project-$appName-$stage:$newVersion"

    # kubectl set image deployment/$appName app="${host}/${project}/$project-$appName-$stage:$newVersion" # -n $namespace
    gcloud config set project ${project}
    gcloud beta run deploy ${serviceName} --platform managed --region us-central1 \
        --set-env-vars VERSION=${newVersion} \
        --set-env-vars NODE_ENV=${stage} \
        --image ${host}/${project}/$project-$appName-$stage:$newVersion
}

export -f deployImage