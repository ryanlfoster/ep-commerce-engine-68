package com.elasticpath.releng.builds


/**
 * Contains utility methods for processing pipeline jobs.
 */
class PipelineUtils {
	/**
	 * Adds the trigger invocations to the current job.
	 *
	 * @param jobs all jenkins jobs (as jenkins-maven-plugin jobs)
	 * @param job current job we are processing
	 * @param node {@link Node} to make modifications to
	 * @param successParameters parameters to pass on a successful build
	 * @param failureParameters parameters to pass on a failed build
	 * @return the prefix of the current job
	 */
	/*
	 * TODO: the next version of jenkins-maven-plugin will pass `jobs', a list of all jobs it parsed
	 * which we can use to warn the user if they missing a build in the pipeline.
	 */
	String addTriggerJobs(String jobId, Node node, Map successParameters = [:], Map failureParameters = [:]) {
		def matcher = jobId =~ /^(.*?)(-Build|-Archetypes|-Final)$/
		if (!matcher) {
			throw new IllegalArgumentException("Job `$jobId' does not follow naming"
					+ " convention, must end with -Build, -Archetypes, -CMClient or -Final")
		}

		def prefix = matcher[0][1]
		def suffix = matcher[0][2]
		String nextFailedBuild = "$prefix-Final"
		String nextSuccessBuild
		if (suffix == '-Build') {
			nextSuccessBuild = "$prefix-Archetypes"
		} else if (suffix == '-Archetypes') {
			nextSuccessBuild = "$prefix-Final"
		} else if (suffix == '-Final') {
			nextFailedBuild = null
		}

		Map failParams = [ NEXUS_DEPLOY: false ]
		failParams.putAll(failureParameters)

		if (!nextSuccessBuild && !nextFailedBuild) {
			return null
		}

		def builder = new NodeBuilder()
		if (node.publishers) {
			builder.current = node.publishers[0]
		} else {
			builder.current = node
			builder = node.publishers
		}

		builder.'hudson.plugins.parameterizedtrigger.BuildTrigger' {
			configs {
				if (nextSuccessBuild) {
					'hudson.plugins.parameterizedtrigger.BuildTriggerConfig' {
						configs {
							'hudson.plugins.parameterizedtrigger.CurrentBuildParameters'()
							'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters' {
								properties(successParameters.collect { it }.join('\n'))
							}
						}

						projects(nextSuccessBuild)
						condition('UNSTABLE_OR_BETTER')
						triggerWithNoParameters(false)
					}
				}

				if (nextFailedBuild) {
					'hudson.plugins.parameterizedtrigger.BuildTriggerConfig' {
						configs {
							'hudson.plugins.parameterizedtrigger.CurrentBuildParameters'()
							'hudson.plugins.parameterizedtrigger.PredefinedBuildParameters' {
								properties(failParams.collect { it }.join('\n'))
							}
						}

						projects(nextFailedBuild)
						condition('FAILED')
						triggerWithNoParameters(false)
					}
				}
			}
		}

		return prefix
	}
}
