package io.secone.plugins.teamcity.agent;

import org.jetbrains.annotations.NotNull;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.AgentBuildRunnerInfo;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildProcess;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;

public class Sec1ScanBuildRunner extends Sec1AbstractBuildRunner {

	public Sec1ScanBuildRunner(@NotNull ArtifactsWatcher artifactsWatcher) {
		super(artifactsWatcher);
	}

	@NotNull
	public AgentBuildRunnerInfo getRunnerInfo() {
		return new Sec1ScanBuildRunnerInfo();
	}

	@NotNull
	public BuildProcess createBuildProcess(@NotNull final AgentRunningBuild build,
			@NotNull final BuildRunnerContext context) throws RunBuildException {
		BuildProgressLogger progresslogger = build.getBuildLogger();
	//	progresslogger.message("Creating build process.");
		BuildProcess process;

		try {
			process = new Sec1ScanBuildProcessAdapter(artifactsWatcher, build, context, progresslogger);
		} catch (RunBuildException ex) {
			progresslogger.buildFailureDescription("Failed to create build process.");
			throw ex;
		}
		return process;
	}
}
