# Kestra OpsGenie Plugin

## What

- Provides plugin components under `io.kestra.plugin.opsgenie`.
- Includes classes such as `OpsgenieExecution`, `OpsgenieAlert`, `OpsgenieTemplate`.

## Why

- What user problem does this solve? Teams need to create alerts in Opsgenie from orchestrated workflows instead of relying on manual console work, ad hoc scripts, or disconnected schedulers.
- Why would a team adopt this plugin in a workflow? It keeps Atlassian Opsgenie steps in the same Kestra flow as upstream preparation, approvals, retries, notifications, and downstream systems.
- What operational/business outcome does it enable? It reduces manual handoffs and fragmented tooling while improving reliability, traceability, and delivery speed for processes that depend on Atlassian Opsgenie.

## How

### Architecture

Single-module plugin. Source packages under `io.kestra.plugin`:

- `opsgenie`

Infrastructure dependencies (Docker Compose services):

- `app`

### Key Plugin Classes

- `io.kestra.plugin.opsgenie.OpsgenieAlert`
- `io.kestra.plugin.opsgenie.OpsgenieExecution`

### Project Structure

```
plugin-opsgenie/
├── src/main/java/io/kestra/plugin/opsgenie/
├── src/test/java/io/kestra/plugin/opsgenie/
├── build.gradle
└── README.md
```

## References

- https://kestra.io/docs/plugin-developer-guide
- https://kestra.io/docs/plugin-developer-guide/contribution-guidelines
