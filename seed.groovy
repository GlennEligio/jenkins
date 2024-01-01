folder('dntx') {
}

folder('dntx/deploy') {
}

pipelineJob('dntx/deploy/fe-deploy') {
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        github('GlennEligio/jenkins')
                    }
                }
            }
            scriptPath('fe-deploy.groovy')
        }
    }
}

pipelineJob('dntx/deploy/be-deploy') {
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        github('GlennEligio/jenkins')
                    }
                }
            }
            scriptPath('be-deploy.groovy')
        }
    }
}
