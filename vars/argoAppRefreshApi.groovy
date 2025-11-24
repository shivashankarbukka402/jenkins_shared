def call(Map config = [:]) {
    def appName      = config.appName ?: error("Missing appName")
    def argocdUrl    = config.argocdUrl ?: 'https://argocd.qtgem.com'
    def authType     = config.authType ?: 'token' // or 'basic'
    def argocdToken  = config.argocdToken ?: ''
    def username     = config.username ?: ''
    def password     = config.password ?: ''
    def insecure     = config.insecure ?: true // skip TLS verify

    def curlOpts = insecure ? '-k' : ''

    def authHeader = ""
    if (authType == 'token') {
        authHeader = "-H 'Authorization: Bearer ${argocdToken}'"
    } else {
        authHeader = "-u ${username}:${password}"
    }

    echo "🔁 Triggering ArgoCD app refresh for: ${appName}"

    sh """
      curl ${curlOpts} -X POST ${argocdUrl}/api/v1/applications/${appName}/sync \
        ${authHeader} \
        -H 'Content-Type: application/json'
    """
    
    echo "ArgoCD sync triggered for app: ${appName}"
}

// Using Token:
// argoAppRefreshApi(
//     appName: 'backend',
//     argocdUrl: 'https://argocd.qtgem.com',
//     authType: 'token',
//     argocdToken: credentials('argocd-api-token') // Jenkins credential ID with token
// )

// Using username/password:
// withCredentials([usernamePassword(
//     credentialsId: 'argocd-basic-auth',
//     usernameVariable: 'ARGO_USER',
//     passwordVariable: 'ARGO_PASS'
//   )]) {
//     argoAppRefreshApi(
//       appName: 'backend',
//       argocdUrl: 'https://argocd.qtgem.com',
//       authType: 'basic',
//       username: env.ARGO_USER,
//       password: env.ARGO_PASS
//     )
//   }