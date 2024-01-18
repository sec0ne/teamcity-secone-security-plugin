package io.secone.plugins.teamcity.agent;

import org.jetbrains.annotations.NotNull;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.AgentBuildRunner;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildProcess;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;

public abstract class Sec1AbstractBuildRunner implements AgentBuildRunner{
	
	protected final ArtifactsWatcher artifactsWatcher;
	
	public Sec1AbstractBuildRunner(@NotNull final ArtifactsWatcher artifactsWatcher) {
		this.artifactsWatcher = artifactsWatcher;
	}
		
	@NotNull
	public abstract BuildProcess createBuildProcess(@NotNull final AgentRunningBuild build,
	                                                @NotNull final BuildRunnerContext context) throws RunBuildException;
}
