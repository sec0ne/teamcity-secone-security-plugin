<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout"%>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags"%>

<jsp:useBean id="propertiesBean" scope="request"
	type="jetbrains.buildServer.controllers.BasePropertiesBean" />
<jsp:useBean id="constants"
	class="io.secone.plugins.teamcity.common.Sec1SecurityConstants" />

<l:settingsGroup title="Sec1 Settings">
	<tr>
		<th><label>Sec1 API Key:</label></th>
		<td><props:displayValue name="${constants.sec1ApiKey}"
				className="longField" /></td>
	</tr>
	<tr>
		<th><label>GIT Credentials ID:</label></th>
		<td><props:displayValue name="${constants.credentialsId}"
				className="longField" /> <span class="smallNote">The
				credentials ID which will be used to access the scm.</span></td>
	</tr>
	<tr>
		<th><label>Apply Threshold:</label></th>
		<td><props:displayValue name="${constants.applyThreshold}" /> <span
			class="smallNote"> </span>
			<table id="${constants.applyThreshold}.tr">
				Define your vulnerability threshold levels.
				<br /> e.g. if you define critical vulnerability threshold as
				<b>10</b> then your build will
				<b>fail</b> if more than 10 critical vulnerabilities found in the
				scan.
				<tr>
					<td><label for="${constants.critical}.text">Critical</label> <props:displayValue
							name="${constants.critical}" className="longField"
							style="width: 150px" id="${constants.critical}.text" /></td>
					<td><label for="${constants.high}.text">High</label> <props:displayValue
							name="${constants.high}" className="longField"
							style="width: 150px" id="${constants.high}.text" /></td>
					<td><label for="${constants.medium}.text">Medium</label> <props:displayValue
							name="${constants.medium}" className="longField"
							style="width: 150px" id="${constants.medium}.text" /></td>
					<td><label for="${constants.low}.text">Low</label> <props:displayValue
							name="${constants.low}" className="longField"
							style="width: 150px" id="${constants.low}.text" /></td>
				</tr>
				<tr>
					<td colspan="4">On threshold breached, mark the <b>Build
							Status</b> as
					</td>
				</tr>
				<tr>
					<td>Fail <props:displayValue name="${constants.statusAction}"
							checked="true" value="fail" />
					</td>
					<!-- 	<td> 	
                 		Unstable <props:radioButtonProperty name="${constants.statusAction}" value="unstable"/>
                	</td> -->
					<td>Continue <props:displayValue
							name="${constants.statusAction}" value="continue" />
					</td>
				</tr>
			</table></td>
	</tr>
	<!-- <tr id="thresholdTr" style="display: none;">
		<td colspan>
			
		</td>
	</tr> -->
</l:settingsGroup>