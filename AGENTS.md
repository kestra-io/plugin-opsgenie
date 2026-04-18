# Kestra OpsGenie Plugin

## What

- Provides plugin components under `io.kestra.plugin.opsgenie`.
- Includes classes such as `OpsgenieExecution`, `OpsgenieAlert`, `OpsgenieTemplate`.

## Why

- This plugin integrates Kestra with Atlassian Opsgenie.
- It provides tasks that create alerts in Opsgenie.

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
