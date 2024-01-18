package io.secone.plugins.teamcity.agent;

import static io.secone.plugins.teamcity.common.Sec1SecurityConstants.APPLY_THRESHOLD;
import static io.secone.plugins.teamcity.common.Sec1SecurityConstants.CREDENTIALS_ID;
import static io.secone.plugins.teamcity.common.Sec1SecurityConstants.CRITICAL;
import static io.secone.plugins.teamcity.common.Sec1SecurityConstants.HIGH;
import static io.secone.plugins.teamcity.common.Sec1SecurityConstants.LOW;
import static io.secone.plugins.teamcity.common.Sec1SecurityConstants.MEDIUM;
import static io.secone.plugins.teamcity.common.Sec1SecurityConstants.SEC1_API_KEY;
import static io.secone.plugins.teamcity.common.Sec1SecurityConstants.STATUS_ACTION;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;

public class Sec1ScanBuildProcessAdapter extends Sec1AbstractBuildProcessAdapter {

	private static final String API_CONTEXT = "/rest/foss";

	private static final String API_ENDPOINT = "https://api.sec1.io";

	private static final String SCAN_API = "/scan";

	private static final String API_KEY_HEADER = "sec1-api-key";

	public static BuildProgressLogger publicProgressLogger;

	public Sec1ScanBuildProcessAdapter(@NotNull final ArtifactsWatcher artifactsWatcher,
			@NotNull final AgentRunningBuild build, @NotNull final BuildRunnerContext context,
			@NotNull final BuildProgressLogger progressLogger) throws RunBuildException {
		super(artifactsWatcher, build, context, progressLogger);

		publicProgressLogger = progressLogger;
	}

	@Override
	protected void runProcess() throws RunBuildException {
		try {
			if (!isInterrupted()) {
				scanRequestHandler(runnerParameters);
			} else {
				throw new RunBuildException("Scan request is interrupted.");
			}
		} catch (Exception e) {
			throw new RunBuildException(e);
		}
	}

	private void scanRequestHandler(Map<String, String> runnerParameters) throws Exception {

		String sec1ApiKey = runnerParameters.get(SEC1_API_KEY);

		String scmUrl = getGitUrl(context);
		boolean applyThreshold = Boolean.parseBoolean(runnerParameters.get(APPLY_THRESHOLD));

		logMessage("==================== SEC1 SCAN CONFIG ====================");
		logMessage("SCM Url                " + scmUrl);
		logMessage("Threshold Enabled      " + applyThreshold);

		String critical = runnerParameters.get(CRITICAL);
		String high = runnerParameters.get(HIGH);
		String medium = runnerParameters.get(MEDIUM);
		String low = runnerParameters.get(LOW);

		if (applyThreshold) {
			logMessage("Threshold Values       " + "Critical " + (StringUtils.isNotBlank(critical) ? critical : "NA")
					+ "," + " High " + (StringUtils.isNotBlank(high) ? high : "NA") + "," + " Medium "
					+ (StringUtils.isNotBlank(medium) ? medium : "NA") + "," + " Low "
					+ (StringUtils.isNotBlank(low) ? low : "NA"));
		}

		JSONObject inputParamsMap = new JSONObject();
		inputParamsMap.put("location", scmUrl);

		StringBuilder appName = new StringBuilder();
		if (StringUtils.isBlank(scmUrl)) {
			throw new RunBuildException("SCM Url not configured. Please check your configuration.");
		}
		try {
			appName.append(getSubUrl(scmUrl));
		} catch (Exception ex) {
			logMessage("Error - extracting app name from url =>" + ex);
			logMessage("Issue extracting app name from url, setting it to default");
			appName = new StringBuilder(scmUrl);
		}
		inputParamsMap.put("urlType", "github");
		inputParamsMap.put("appName", appName);
		inputParamsMap.put("source", "teamcity");

		String accessTokenStr = getCredentials(context);

		if (StringUtils.isNotBlank(accessTokenStr)) {
			inputParamsMap.put("accessToken", accessTokenStr);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set(API_KEY_HEADER, sec1ApiKey);

		String scanUrl = API_ENDPOINT + API_CONTEXT + SCAN_API;

		RestTemplate rest = new RestTemplate();
		HttpEntity<String> request = new HttpEntity<String>(inputParamsMap.toString(), headers);
		try {
			ResponseEntity<String> responseEntity = rest.exchange(scanUrl, HttpMethod.POST, request, String.class);

			JSONObject responseJson = new JSONObject(responseEntity.getBody());
			if (responseJson.has("cveCountDetails")) {
				int criticalFinding = responseJson.optJSONObject("cveCountDetails") != null
						? responseJson.getJSONObject("cveCountDetails").optInt("CRITICAL")
						: 0;
				int highFinding = responseJson.optJSONObject("cveCountDetails") != null
						? responseJson.getJSONObject("cveCountDetails").optInt("HIGH")
						: 0;
				int mediumFinding = responseJson.optJSONObject("cveCountDetails") != null
						? responseJson.getJSONObject("cveCountDetails").optInt("MEDIUM")
						: 0;
				int lowFinding = responseJson.optJSONObject("cveCountDetails") != null
						? responseJson.getJSONObject("cveCountDetails").optInt("LOW")
						: 0;

				logMessage("==================== SEC1 SCAN RESULT ====================");
				if (StringUtils.isBlank(responseJson.optString("errorMessage"))) {
					logMessage("Vulnerabilities Found  " + "Critical " + criticalFinding + "," + " High " + highFinding
							+ "," + " Medium " + mediumFinding + "," + " Low " + lowFinding);
					logMessage("RAG Status             " + responseJson.optString("overallRagStatus"));
					logMessage("Report Url             " + responseJson.optString("reportUrl"));

					// logMessage("=====================================================");

					if (applyThreshold) {
						try {
							if (criticalFinding != 0 && StringUtils.isNotBlank(critical)
									&& NumberUtils.isDigits(critical)
									&& criticalFinding >= Integer.parseInt(critical)) {
								String message = "Critical Vulnerability Threshold breached.";
								failBuildOnThresholdBreach(message);
							}
							if (highFinding != 0 && StringUtils.isNotBlank(high) && NumberUtils.isDigits(high)
									&& highFinding >= Integer.parseInt(high)) {
								String message = "High Vulnerability Threshold breached.";
								failBuildOnThresholdBreach(message);
							}
							if (mediumFinding != 0 && StringUtils.isNotBlank(medium) && NumberUtils.isDigits(medium)
									&& mediumFinding >= Integer.parseInt(medium)) {
								String message = "Medium Vulnerability Threshold breached.";
								failBuildOnThresholdBreach(message);
							}
							if (lowFinding != 0 && StringUtils.isNotBlank(low) && NumberUtils.isDigits(low)
									&& lowFinding >= Integer.parseInt(low)) {
								String message = "Low Vulnerability Threshold breached.";
								failBuildOnThresholdBreach(message);
							}
						} catch (NumberFormatException ex) {
							throw new RunBuildException(
									"Check values configured for vulnerability threshold. Only numbers are allowed.");
						}
					}
				} else {
					logError("Error Details : " + responseJson.optString("errorMessage"));
				}
			}
		} catch (HttpClientErrorException ex) {
			logError("Error Details : " + ex.getResponseBodyAsString());

		} catch (HttpServerErrorException ex) {
			try {
				JSONObject responseJson = new JSONObject(ex.getResponseBodyAsString());
				logError("Error Details : " + responseJson.optString("errorMessage"));
			} catch (Exception e) {
				throw new RunBuildException("Scan failed. Issue while getting result.");
			}
		}
	}

	private String getSubUrl(String scmUrl) throws MalformedURLException {
		URL apiUrl = new URL(scmUrl);

		int subUrlLocation = StringUtils.indexOf(scmUrl, apiUrl.getHost()) + apiUrl.getHost().length() + 1;
		if (apiUrl.getPort() != -1) {
			subUrlLocation = StringUtils.indexOf(scmUrl, apiUrl.getHost()) + apiUrl.getHost().length()
					+ String.valueOf(apiUrl.getPort()).length() + 1;
		}
		return StringUtils.substring(scmUrl, subUrlLocation);
	}

	private String getGitUrl(BuildRunnerContext context) {
		String vcsRootId = context.getBuild().getSharedBuildParameters().getSystemProperties()
				.get("teamcity.buildType.id");
		String vcsRootUrl = context.getConfigParameters().get("vcsroot." + vcsRootId + ".url");
		return vcsRootUrl;
	}

	private String getCredentials(BuildRunnerContext context) {

		String credentialsId = context.getRunnerParameters().get(CREDENTIALS_ID);
		String credentials = "";
		if (StringUtils.isBlank(credentialsId)) {
			credentials = context.getBuildParameters().getEnvironmentVariables().get("accessToken");
		} else {
			credentials = context.getConfigParameters().get(credentialsId);
		}
		return credentials;
	}

	private void failBuildOnThresholdBreach(String message) throws RunBuildException {
		String statusAction = runnerParameters.get(STATUS_ACTION);
		if (StringUtils.isNotBlank(statusAction)) {
			if (StringUtils.equalsIgnoreCase(statusAction, "fail")) {
				throw new RunBuildException(message + " Failing the build.");
			} else {
				logMessage(message);
			}
		} else {
			throw new RunBuildException(message + " Failing the build.");
		}
	}

	private void logMessage(String message) {
		progressLogger.message(message);
	}

	private void logError(String message) {
		progressLogger.error(message);
	}
}
