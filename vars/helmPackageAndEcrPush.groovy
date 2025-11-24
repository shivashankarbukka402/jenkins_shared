def call(Map config = [:]) {
    def chartDir   = config.chartDir ?: './k8s/backend/backend-chart'
    def chartName  = config.chartName ?: 'backend'
    def version    = config.version ?: new Date().format('yyyyMMddHHmmss')
    def ecrRepo    = config.ecrRepo ?: '637423622313.dkr.ecr.ap-south-1.amazonaws.com/helm-charts'
    def region     = config.awsRegion ?:'ap-south-1'
    def awsCredId  = config.awsCredId ?: error("Missing awsCredId parameter")

    def tgzFile = "${chartName}-${version}.tgz"
    def fullImage = "${ecrRepo}/${chartName}:${version}"

    withCredentials([usernamePassword(
        credentialsId: awsCredId,
        usernameVariable: 'AWS_ACCESS_KEY_ID',
        passwordVariable: 'AWS_SECRET_ACCESS_KEY'
    )]) {
        sh """
            aws configure set aws_access_key_id \$AWS_ACCESS_KEY_ID
            aws configure set aws_secret_access_key \$AWS_SECRET_ACCESS_KEY
            aws configure set region ${region}

            aws ecr get-login-password --region ${region} \
            | helm registry login --username AWS --password-stdin ${ecrRepo}

            helm dependency update ${chartDir}
            helm lint ${chartDir}
            helm package ${chartDir} --version ${version} --destination .

            helm push ${tgzFile} oci://${ecrRepo}
        """
    }

    echo "Helm chart pushed to: ${fullImage}"
}

// helmPackageAndPush(
//     chartDir: 'charts/backend',
//     chartName: 'backend',
//     version: "1.0.${env.BUILD_NUMBER}",
//     ecrRepo: '123456789012.dkr.ecr.us-east-1.amazonaws.com/helm-charts',
//     awsRegion: 'us-east-1',
//     awsCredId: 'aws-ecr-jenkins' // your Jenkins credential ID for aws (username and password)
// )