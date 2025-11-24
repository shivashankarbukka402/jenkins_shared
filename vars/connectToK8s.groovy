def call(String apiUrl, String credsId, String contextName, String clusterName, String userName) {
    withCredentials([file(credentialsId: credsId, variable: 'KUBE_FILE')]) {
        script {
            // Create isolated kubeconfig
            String tempConfig = "${env.WORKSPACE}/kubeconfig"
            sh 'mkdir -p ${WORKSPACE}'
            sh "cp \"${KUBE_FILE}\" \"${tempConfig}\""

            // Update the cluster and context in kubeconfig
            sh """
                kubectl --kubeconfig=${tempConfig} config set-cluster ${clusterName} --server=${apiUrl} --insecure-skip-tls-verify=true
                kubectl --kubeconfig=${tempConfig} config set-context ${contextName} --cluster=${clusterName} --user=${userName}
                kubectl --kubeconfig=${tempConfig} config use-context ${contextName}
            """

            // Validate connection
            withEnv(["KUBECONFIG=${tempConfig}"]) {
                timeout(time: 15, unit: 'SECONDS') {
                    echo "Connecting to Kubernetes at ${apiUrl} using context '${contextName}'..."
                    sh 'kubectl get nodes'
                    echo "Connected to Kubernetes successfully."
                }
            }
        }
    }
}