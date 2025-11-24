def call(Map config = [:]) {
    def image = config.image ?: error("Missing required parameter: image")
    def reportName = config.reportName ?: image.replaceAll(/[^a-zA-Z0-9]/, "-") + "-trivy-report"
    def reportDir = config.reportDir ?: "trivy-reports"
    def reportFile = "${reportDir}/${reportName}.html"
    def publish = config.publish ?: true

    // Create report directory
    sh "mkdir -p ${reportDir}"

    // Download template if not present
    if (!fileExists('html.tpl')) {
        sh "curl -sSL https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/html.tpl -o html.tpl"
    }

    // Run Trivy scan
    sh """
        trivy image --format template \
        --template '@html.tpl' \
        -o ${reportFile} \
        ${image}
    """

    // Archive and publish report if enabled
    if (publish) {
        archiveArtifacts artifacts: "${reportFile}", fingerprint: true
        publishHTML([
            reportDir: reportDir,
            reportFiles: "${reportName}.html",
            reportName: "Trivy Report - ${reportName}"
        ])
    }

    echo "Trivy scan completed for ${image}. Report at: ${reportFile}"
}