package io.secone.plugins.teamcity.server;

import static io.secone.plugins.teamcity.common.Sec1SecurityConstants.SEC1_API_KEY;
import static io.secone.plugins.teamcity.common.Sec1SecurityConstants.CRITICAL;
import static io.secone.plugins.teamcity.common.Sec1SecurityConstants.HIGH;
import static jetbrains.buildServer.util.PropertiesUtil.isEmptyOrNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.util.containers.hash.HashMap;

import io.secone.plugins.teamcity.common.Sec1SecurityConstants;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
import jetbrains.buildServer.web.openapi.PluginDescriptor;

public class Sec1SecurityServer extends RunType {

	@NotNull
	private final PluginDescriptor pluginDescriptor;

	public Sec1SecurityServer(@NotNull final RunTypeRegistry runTypeRegistry,
			@NotNull final PluginDescriptor pluginDescriptor) {
		this.pluginDescriptor = pluginDescriptor;
		runTypeRegistry.registerRunType(this);
	}

	@NotNull
	@Override
	public String getType() {
		return Sec1SecurityConstants.RUNNER_TYPE;
	}

	@NotNull
	@Override
	public String getDisplayName() {
		return Sec1SecurityConstants.RUNNER_DISPLAY_NAME;
	}

	@NotNull
	@Override
	public String getDescription() {
		return Sec1SecurityConstants.RUNNER_DESCRIPTION;
	}

	@Nullable
	@Override
	public PropertiesProcessor getRunnerPropertiesProcessor() {
		return properties -> {
			if (properties == null) {
				return Collections.emptyList();
			}

			List<InvalidProperty> findings = new ArrayList<>(0);
			if (isEmptyOrNull(properties.get(SEC1_API_KEY))) {
				findings.add(new InvalidProperty(SEC1_API_KEY, "Sec1 API key must be specified."));
			}
			return findings;
		};
	}

	@Nullable
	@Override
	public String getEditRunnerParamsJspFilePath() {
		return pluginDescriptor.getPluginResourcesPath("editSec1SecurityConfigParameters.jsp");
	}

	@Nullable
	@Override
	public String getViewRunnerParamsJspFilePath() {
		return pluginDescriptor.getPluginResourcesPath("viewSec1SecurityConfigParameters.jsp");
	}

	@Nullable
	@Override
	public Map<String, String> getDefaultRunnerProperties() {
		Map<String, String> defaultProperties = new HashMap<>(3);

		defaultProperties.put(CRITICAL, "0");
		defaultProperties.put(HIGH, "10");

		return defaultProperties;
	}

	@NotNull
	@Override
	public String describeParameters(@NotNull Map<String, String> parameters) {
		return "Sec1 Security plugin help developers/teams to scan their SCM for open source vulnerabilities against Sec1 Security DB";
	}

}
