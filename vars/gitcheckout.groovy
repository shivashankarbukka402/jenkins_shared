def call(String git_branch = 'main', String git_credentails = null, String git_url = null) {
     echo "This is gitcheck shared library"
     checkout([$class: 'GitSCM',
               branches: [[name: "${git_branch}"]],
               userRemoteConfigs: [[credentialsId: "${git_credentails}",
               url: "${git_url}"]]])
     echo "Git checkout to ${git_url} is success"
}

// gitCheckout('main', 'github_creds', 'repo_url')
