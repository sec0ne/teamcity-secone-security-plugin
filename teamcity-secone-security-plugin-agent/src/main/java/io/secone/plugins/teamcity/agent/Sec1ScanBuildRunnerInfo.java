package io.secone.plugins.teamcity.agent;

import static io.secone.plugins.teamcity.common.Sec1SecurityConstants.RUNNER_TYPE;

import org.jetbrains.annotations.NotNull;

import jetbrains.buildServer.agent.AgentBuildRunnerInfo;
import jetbrains.buildServer.agent.BuildAgentConfiguration;

public class Sec1ScanBuildRunnerInfo implements AgentBuildRunnerInfo {
	@NotNull
	@Override
	public String getType() {
		return RUNNER_TYPE;
	}

	@Override
	public boolean canRun(@NotNull BuildAgentConfiguration buildAgentConfiguration) {
		return true;
	}
}
