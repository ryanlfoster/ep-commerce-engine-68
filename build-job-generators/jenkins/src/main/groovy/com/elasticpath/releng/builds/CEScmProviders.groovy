package com.elasticpath.releng.builds

import com.elasticpath.releng.utils.ConsumingNodeBuilder

class SvnWorkspaceWipeSourceReleaseScmProvider extends SvnWorkspaceWipeScmProvider {
	String referenceType = 'svn-wipe-source-release'

	@Override
	Node getRepositoriesAsNode() {
		def node = super.repositoriesAsNode
		node.depthFirst().findAll { it instanceof Node && it.name() == 'repository' }.each {
			def builder = new ConsumingNodeBuilder()
			builder.current = it
			builder.remote('$EP_PLATFORM_SCM')
		}
		return node
	}
}

class GitCustomRemoteWorkspaceWipeSourceReleaseScmProvider extends GitWorkspaceWipeScmProvider {
	String referenceType = 'git-wipe-source-release'

	@Override
	Node getRepositoriesAsNode() {
		def node = super.repositoriesAsNode
		node.depthFirst().findAll { it instanceof Node && it.name() == 'repository' }.each {
			def builder = new ConsumingNodeBuilder()
			builder.current = it
			builder.remote('$EP_PLATFORM_SCM')
		}
		return node
	}
}
