def call(String git_branch = null, String git_credentailes = null, String git_url = null) {
  echo "This is git checkout shared library"
  checkout{[$class: 'GitSCM',
            branches: [[name: "${git_branch}"]],
            userRemoteConfigs: [[credentialsId: "${git_credentails}",
            url: "${git_url}"]]])
  sh 'pwd; ls -lrt'
}
