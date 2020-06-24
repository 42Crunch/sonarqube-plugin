package com.xliic.sonar;

import java.util.List;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import static java.util.Arrays.asList;

public class AuditProperties {

	public static final String GRADE = "42crunch.sonar.assessment.report.grade";
	public static final String ISSUES_TOTAL_NB = "42crunch.sonar.assessment.report.issues_total";
	public static final String ISSUES_CRITICAL_NB = "42crunch.sonar.assessment.report.issues_critical";
	public static final String ISSUES_HIGH_NB = "42crunch.sonar.assessment.report.issues_high";
	public static final String ISSUES_MEDIUM_NB = "42crunch.sonar.assessment.report.issues_medium";
	public static final String ISSUES_LOW_NB = "42crunch.sonar.assessment.report.issues_low";

	public static final String REPORT_PATH_KEY = "42crunch.sonar.assessment.filename.report.path";
	public static final String OAS_FILE_SUFFIXES_KEY = "42crunch.sonar.assessment.filename.oas.suffixes";
	public static final String OAS_FILE_DIR_KEY = "42crunch.sonar.assessment.filename.oas.directory";

	public static List<PropertyDefinition> getProperties() {
		return asList(
				PropertyDefinition.builder(REPORT_PATH_KEY).defaultValue("summaryReport.csv").multiValues(false)
						.category("API Assessment").name("Summary report to analyze")
						.description("Assessment report file name extension").build(),
				PropertyDefinition.builder(OAS_FILE_SUFFIXES_KEY).defaultValue(".json").multiValues(true)
						.category("API Assessment").name("OAS file suffixes")
						.description("Assessment OAS file name extension").build(),
				PropertyDefinition.builder(OAS_FILE_DIR_KEY).defaultValue("OASFiles/").multiValues(false)
						.category("API Assessment").name("OAS directory")
						.description("Directory containing the OAS file yaml and json").build(),
				PropertyDefinition.builder(GRADE).defaultValue("80").multiValues(false).category("API Assessment")
						.name("Min grade").description("Minimum grade to consider the API as valid")
						.onQualifiers(Qualifiers.PROJECT).build(),
				PropertyDefinition.builder(ISSUES_TOTAL_NB).defaultValue("10").multiValues(false)
						.category("API Assessment").name("Max number of errors")
						.description("Number max of errors in the report").onQualifiers(Qualifiers.PROJECT).build(),
				PropertyDefinition.builder(ISSUES_CRITICAL_NB).defaultValue("0").multiValues(false)
						.category("API Assessment").name("Max number of critical errors")
						.description("Number max of critical errors in the report").onQualifiers(Qualifiers.PROJECT)
						.build(),
				PropertyDefinition.builder(ISSUES_HIGH_NB).defaultValue("0").multiValues(false)
						.category("API Assessment").name("Max number of error with high level")
						.description("Number max of error with high level in the report")
						.onQualifiers(Qualifiers.PROJECT).build(),
				PropertyDefinition.builder(ISSUES_MEDIUM_NB).defaultValue("10").multiValues(false)
						.category("API Assessment").name("Max number of error with medium level")
						.description("Number max of error with medium level in the report")
						.onQualifiers(Qualifiers.PROJECT).build(),
				PropertyDefinition.builder(ISSUES_LOW_NB).defaultValue("20").multiValues(false)
						.category("API Assessment").name("Max number of error with low level")
						.description("Number max of error with low level in the report")
						.onQualifiers(Qualifiers.PROJECT).build());
	}

}
