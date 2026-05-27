# How to use the Opsgenie plugin

Create Opsgenie alerts and send execution summaries from Kestra flows.

## Authentication

Set `url` to the Opsgenie alert creation API endpoint and `authorizationToken` to your GenieKey (found in Settings → API key management). Store the key in a [secret](https://kestra.io/docs/concepts/secret).

## Tasks

`OpsgenieAlert` creates an alert as a step within a flow — set `payload` to a JSON body in the [Opsgenie Create Alert API format](https://docs.opsgenie.com/docs/alert-api#create-alert).

`OpsgenieExecution` sends a structured execution summary including status, duration, and an execution link, and is designed for use with a [Flow trigger](https://kestra.io/docs/workflow-components/triggers) in a dedicated monitoring namespace that watches other namespaces for failures.
