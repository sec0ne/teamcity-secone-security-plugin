package io.secone.plugins.teamcity.common;

public class Sec1SecurityConstants {
	public static final String RUNNER_TYPE = "sec1Security";
	public static final String RUNNER_DISPLAY_NAME = "Sec1 Security";
	public static final String RUNNER_DESCRIPTION = "Runner for finding vulnerabilities in your dependencies";

	public static final String SEC1_API_KEY = "secure:sec1ApiKey";

	public static final String APPLY_THRESHOLD = "applyThreshold";

	public static final String CRITICAL = "critical";
	public static final String HIGH = "high";
	public static final String MEDIUM = "medium";
	public static final String LOW = "low";

	public static final String STATUS_ACTION = "statusAction";

	public static final String CREDENTIALS_ID = "credentialsId";

	public String getRunnerType() {
		return RUNNER_TYPE;
	}

	public String getRunnerDisplayName() {
		return RUNNER_DISPLAY_NAME;
	}

	public String getRunnerDescription() {
		return RUNNER_DESCRIPTION;
	}

	public String getApplyThreshold() {
		return APPLY_THRESHOLD;
	}

	public String getCritical() {
		return CRITICAL;
	}

	public String getHigh() {
		return HIGH;
	}

	public String getMedium() {
		return MEDIUM;
	}

	public String getLow() {
		return LOW;
	}

	public String getSec1ApiKey() {
		return SEC1_API_KEY;
	}

	public String getStatusAction() {
		return STATUS_ACTION;
	}

	public String getCredentialsId() {
		return CREDENTIALS_ID;
	}
}
