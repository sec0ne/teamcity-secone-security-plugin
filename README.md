# Sec1 Security

[![Sec1](https://sec1.io/wp-content/uploads/2024/01/rounded-logo-sec1-git.png)](https://sec1.io)

Teamcity Version 2023.11.1 (build 147412)

## Introduction

Sec1 Security plugin help developers/teams to scan their SCM for open source vulnerabilities against Sec1 Security DB

## Usage
To use the plugin up you will need to take the following steps in order:

1. [Install the Sec1 Security Plugin](#1-install-the-sec1-security-plugin)
2. [Configure a Scm Access Token](#2-configure-a-scm-access-token)
3. [Configure Sec1 Security as Build Step](#3-configure-sec1-security-as-build-step)

## 1. Install the Sec1 Security Plugin

- Go to "Administration" > "Plugins". 
- Navigate to "Browse plugins repository"
- Search for "Sec1 Security" and click on the search result to navigate to plugin page.
- From the "Get" dropdown select "Install" option or download zip. 

  In case of zip download:

- Go to "Administration" > "Plugins". 
- Click on "Upload plugin zip"
- Select the plugin zip from downloaded location
- Click on "Upload plugin zip" of the popup window

Note: Make sure you have enabled the plugin

## 2. Configure a scm Access Token

  As Environment Variable
- Go to your "Project" > "Parameters"
- Click "Add new parameter"
- Select "Kind" as "Environment variable"
- Give environment variable name as "accessToken". This will be used by Sec1 plugin to access your vcs root url for scanning.
- Click "Edit" beside "Sepc"
- Select "Type" as "Password"

Note: If you want to scan public url then don't pass accessToken and credentialsId.

<blockquote>
<details>
<summary>📷 Show Preview</summary>

![Envionment Variable](docs/sec1-env-variable-teamcity.png)

</details>
</blockquote>

  As Configuraton Parameter
- Go to your "Project" > "Parameters"
- Click "Add new parameter"
- Select "Kind" as "Configuration parameter"
- Give configuration parameter a name. Remember this name as you need to pass it in your "Sec1 Build Step"
- Click "Edit" beside "Sepc"
- Select "Type" as "Password"

<blockquote>
<details>
<summary>📷 Show Preview</summary>

![Configuraton Parameter](docs/sec1-config-parameter-teamcity.png)

</details>
</blockquote>

## 3. Configure Sec1 Security as Build Step
- Navigate to "Build Steps" screen in your project
- Click on "Add build step"
- Search "Sec1 Security" in the search box and select the result
- Add "Sec1 API Key"  

  Note : To get `SEC1_API_KEY` navigate to [Scopy](https://scopy.sec1.io/) > "Login with GitHub" > "Settings" 
- In "API key" section, click on "Generate API key"
- Copy key for use.

<blockquote>
<details>
<summary>📷 Show Preview</summary>

![Sec1 API Token](docs/sec1-build-step-teamcity.png)

</details>
</blockquote>

## 4. Configuration Parameters - Sec1 Settigns

  * `Sec1 API Key`: (<b>required</b>)
  
    The API key to be used to access Sec1 API.

  * `GIT Credentials ID`: (<b>optional</b>)

    The ID which will be used to access the scm. This is your configuration parameter id which you set in "Parameters" > "Add new parameter"

  * `Apply Threshold` (<b>optional</b>)

    If selected define your vulnerability threshold levels. <br /> 
    
    If you define critical vulnerability threshold as <b>10</b> then your build will <b>fail</b>, if more than 10 critical vulnerabilities found in the scan.

    You can select what needs to happen in case vulnerability threshold is breached by selecting options: `Fail` amd `Continue`

## Troubleshooting

To see more information on your steps:

- View the "Console Output" for a specific build.

---

-- Sec1 team