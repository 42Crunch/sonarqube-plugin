# SonarQube Plugin: 42Crunch REST API Static Security Testing

The plugin is powered by 42Crunch [API Contract Security Audit](https://docs.42crunch.com/latest/content/concepts/api_contract_security_audit.htm). Security Audit performs a static analysis of the API definition that includes more than 200 checks on best practices and potential vulnerabilities on how the API defines authentication, authorization, transport, and data coming in and going out. For more details on the checks, see [API Security Encyclopedia](https://apisecurity.io/encyclopedia/content/api-security-encyclopedia.htm).

API contracts must follow the OpenAPI Specification (OAS) (formely Swagger). Both OAS v2 and v3, and both JSON and YAML formats are supported.

You can create a free 42Crunch account at https://platform.42crunch.com/register, and then configure the plugin as described below.

## Installation and configuration

1. Download the latest version of the plugin [from this repository](https://github.com/42Crunch/sonarqube-plugin/releases).

2. Put the JAR file into SonarQube's \extensions\plugins folder.

3. Follow the configuration instructions from our [documentation](https://docs.42crunch.com/latest/content/tasks/integrate_sonarqube.htm)

## Support

If you run into an issue, or have a question not answered here, you can create a support ticket at [support.42crunch.com](https://support.42crunch.com/), or ask your questions on the Q&A tab here.

This plugin is maintained by support@42crunch.com.

If you’re reporting an issue, please include:

- the version of the plugin
- relevant logs, error messages, and screenshots
- steps to reproduce the issue
