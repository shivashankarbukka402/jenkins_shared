def call(Map config = [:]) {
    def appName     = config.appName ?: error("Missing appName")
    def argocdHost  = config.argocdHost ?: 'argocd.qtgem.com'
    def username    = config.username ?: ''
    def password    = config.password ?: ''
    def insecure    = config.insecure ?: true
    def context     = config.context ?: 'argocd' // optional CLI context name

    def skipTLS = insecure ? '--insecure' : ''

    echo "🔐 Logging in to ArgoCD CLI"
    sh """
        argocd login ${argocdHost} \
            --username ${username} \
            --password ${password} \
            ${skipTLS} \
            --grpc-web
    """

    echo "🔁 Syncing ArgoCD application: ${appName}"
    sh "argocd app sync ${appName} ${skipTLS}"

    echo "✅ Sync triggered for ArgoCD app: ${appName}"
}

// withCredentials([
//     usernamePassword(credentialsId: 'argocd-cli-creds', usernameVariable: 'ARGO_USER', passwordVariable: 'ARGO_PASS')
//   ]) {
//     argoAppSyncWithCLI(
//       appName: 'backend',
//       argocdHost: 'argocd.qtgem.com',
//       username: env.ARGO_USER,
//       password: env.ARGO_PASS,
//       insecure: true
//     )
//   }