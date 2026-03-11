package io.kestra.plugin.opsgenie;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import io.kestra.core.exceptions.IllegalVariableEvaluationException;
import io.kestra.core.http.HttpRequest;
import io.kestra.core.http.client.configurations.HttpConfiguration;
import io.kestra.core.http.client.configurations.TimeoutConfiguration;
import io.kestra.core.models.annotations.PluginProperty;
import io.kestra.core.models.property.Property;
import io.kestra.core.models.tasks.RunnableTask;
import io.kestra.core.models.tasks.Task;
import io.kestra.core.models.tasks.VoidOutput;
import io.kestra.core.runners.RunContext;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
public abstract class AbstractOpsgenieConnection extends Task implements RunnableTask<VoidOutput> {
    @Schema(
        title = "Tune Opsgenie HTTP client",
        description = "Optional HTTP client settings for Opsgenie calls. Connect timeout, read idle timeout, default charset, and headers are applied; other fields are currently ignored by the client builder."
    )
    @PluginProperty(dynamic = true)
    protected RequestOptions options;

    protected HttpConfiguration httpClientConfigurationWithOptions() throws IllegalVariableEvaluationException {
        HttpConfiguration.HttpConfigurationBuilder configuration = HttpConfiguration.builder();

        if (this.options != null) {

            configuration
                .timeout(
                    TimeoutConfiguration.builder()
                        .connectTimeout(this.options.getConnectTimeout())
                        .readIdleTimeout(this.options.getReadIdleTimeout())
                        .build()
                )
                .defaultCharset(this.options.getDefaultCharset());
        }

        return configuration.build();
    }

    protected HttpRequest.HttpRequestBuilder createRequestBuilder(
        RunContext runContext) throws IllegalVariableEvaluationException {

        HttpRequest.HttpRequestBuilder builder = HttpRequest.builder();

        if (this.options != null && this.options.getHeaders() != null) {
            Map<String, String> headers = runContext.render(this.options.getHeaders())
                .asMap(String.class, String.class);

            if (headers != null) {
                headers.forEach(builder::addHeader);
            }
        }
        return builder;
    }

    @Getter
    @Builder
    public static class RequestOptions {
        @Schema(
            title = "Connect timeout",
            description = "Maximum time to open the connection before failing; uses the HTTP client default when unset."
        )
        private final Property<Duration> connectTimeout;

        @Schema(
            title = "Read timeout",
            description = "Maximum time allowed for reading the response body; kept for compatibility, not applied to the current client builder."
        )
        @Builder.Default
        private final Property<Duration> readTimeout = Property.ofValue(Duration.ofSeconds(10));

        @Schema(
            title = "Read idle timeout",
            description = "Closes idle read connections after inactivity; defaults to PT5M."
        )
        @Builder.Default
        private final Property<Duration> readIdleTimeout = Property.ofValue(Duration.of(5, ChronoUnit.MINUTES));

        @Schema(
            title = "Connection pool idle timeout",
            description = "Idle lifetime for pooled connections before close; currently not applied by the client builder."
        )
        @Builder.Default
        private final Property<Duration> connectionPoolIdleTimeout = Property.ofValue(Duration.ofSeconds(0));

        @Schema(
            title = "Max content length",
            description = "Maximum response size in bytes; currently not applied by the client builder."
        )
        @Builder.Default
        private final Property<Integer> maxContentLength = Property.ofValue(1024 * 1024 * 10);

        @Schema(
            title = "Default request charset",
            description = "Charset used for request bodies; defaults to UTF-8."
        )
        @Builder.Default
        private final Property<Charset> defaultCharset = Property.ofValue(StandardCharsets.UTF_8);

        @Schema(
            title = "HTTP headers",
            description = "Additional headers rendered and added to each request."
        )
        public Property<Map<String, String>> headers;
    }
}
