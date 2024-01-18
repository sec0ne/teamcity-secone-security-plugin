package io.secone.plugins.teamcity.agent;

import java.io.File;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildProcessAdapter;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.log.Loggers;

public abstract class Sec1AbstractBuildProcessAdapter extends BuildProcessAdapter {

	protected ArtifactsWatcher artifactsWatcher;
	protected @NotNull final AgentRunningBuild build;
	protected @NotNull final BuildRunnerContext context;

	// Logger to use for messages sent to the build log on server.
	protected BuildProgressLogger progressLogger;
	protected File workingRoot;
	protected File reportingRoot;
	protected Map<String, String> runnerParameters;

	private volatile boolean isFinished;
	private volatile boolean isFailed;
	private volatile boolean isInterrupted;

	protected Sec1AbstractBuildProcessAdapter(@NotNull final ArtifactsWatcher artifactsWatcher,
			@NotNull final AgentRunningBuild build, @NotNull final BuildRunnerContext context,
			@NotNull final BuildProgressLogger progresslogger) {
		this.artifactsWatcher = artifactsWatcher;
		this.build = build;
		this.context = context;
		
		this.runnerParameters = context.getRunnerParameters();
		this.progressLogger = progresslogger;
		this.workingRoot = build.getCheckoutDirectory();
		this.reportingRoot = build.getBuildTempDirectory();
		this.artifactsWatcher = artifactsWatcher;
		this.isFinished = false;
		this.isFailed = false;
		this.isInterrupted = false;
	}

	@Override
	public void interrupt() {
		isInterrupted = true;
	}

	@Override
	public boolean isInterrupted() {
		return isInterrupted;
	}

	@Override
	public boolean isFinished() {
		return isFinished;
	}

	@NotNull
	@Override
	public BuildFinishedStatus waitFor() throws RunBuildException {
		while (!isInterrupted && !isFinished) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RunBuildException(e);
			}
		}

		return isFinished ? (isFailed ? BuildFinishedStatus.FINISHED_FAILED : BuildFinishedStatus.FINISHED_SUCCESS)
				: BuildFinishedStatus.INTERRUPTED;
	}

	@Override
	public void start() throws RunBuildException {
		try {
			runProcess();
		} catch (RunBuildException e) {
			progressLogger.buildFailureDescription(e.getMessage());
			Loggers.AGENT.error(e);
			isFailed = true;
		} finally {
			isFinished = true;
		}
	}

	protected abstract void runProcess() throws RunBuildException;
}
