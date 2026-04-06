package io.kestra.plugin.opsgenie;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.IOUtils;

import io.kestra.core.models.property.Property;
import io.kestra.core.models.tasks.VoidOutput;
import io.kestra.core.runners.RunContext;
import io.kestra.core.serializers.JacksonMapper;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import io.kestra.core.models.annotations.PluginProperty;

@SuperBuilder
@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
public abstract class OpsgenieTemplate extends OpsgenieAlert {

    @Schema(
        title = "Template to use",
        hidden = true
    )
    @PluginProperty(group = "advanced")
    protected Property<String> templateUri;

    @Schema(
        title = "Template variables",
        description = "Key/value map rendered into the Pebble template before building the alert payload."
    )
    @PluginProperty(group = "advanced")
    protected Property<Map<String, Object>> templateRenderMap;

    @Schema(
        title = "Alert message",
        description = "Overrides `message` in the rendered template; supports expressions."
    )
    @PluginProperty(group = "advanced")
    protected Property<String> message;

    @Schema(
        title = "Alert alias",
        description = "Optional alias override for the alert payload."
    )
    @PluginProperty(group = "advanced")
    protected Property<String> alias;

    @Schema(
        title = "Responders map",
        description = "Map of responder id to type (`team`, `user`, `escalation`, `schedule`); converted to the Opsgenie responders list."
    )
    @PluginProperty(group = "advanced")
    protected Property<Map<String, String>> responders;

    @Schema(
        title = "Visible-to map",
        description = "Map of entity id to type granting visibility (`team` or `user`); converted to the Opsgenie visibleTo list."
    )
    @PluginProperty(group = "destination")
    protected Property<Map<String, String>> visibleTo;

    @Schema(
        title = "Alert tags",
        description = "List of tags added to the alert payload."
    )
    @PluginProperty(group = "advanced")
    protected Property<List<String>> tags;

    @Schema(
        title = "Alert priority",
        description = "Priority code such as `P1`–`P5`; overrides the template value if present."
    )
    @PluginProperty(group = "advanced")
    protected Property<String> priority;

    @SuppressWarnings("unchecked")
    @Override
    public VoidOutput run(RunContext runContext) throws Exception {
        Map<String, Object> map = new HashMap<>();

        final var renderedTemplateUri = runContext.render(this.templateUri).as(String.class);
        if (renderedTemplateUri.isPresent()) {
            String template = IOUtils.toString(
                Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(renderedTemplateUri.get())),
                StandardCharsets.UTF_8
            );

            String render = runContext.render(
                template, templateRenderMap != null ? runContext.render(templateRenderMap).asMap(String.class, Object.class) : Map.of()
            );
            map = (Map<String, Object>) JacksonMapper.ofJson().readValue(render, Object.class);
        }

        if (runContext.render(message).as(String.class).isPresent()) {
            map.put("message", runContext.render(message).as(String.class).get());
        }

        if (runContext.render(alias).as(String.class).isPresent()) {
            map.put("alias", runContext.render(alias).as(String.class).get());
        }

        final Map<String, String> renderedResponders = runContext.render(this.responders).asMap(String.class, String.class);
        if (!renderedResponders.isEmpty()) {
            List<Map<String, String>> respondersList = renderedResponders.entrySet().stream()
                .map(entry -> Map.of("id", entry.getKey(), "type", entry.getValue()))
                .toList();

            map.put("responders", respondersList);
        }

        final Map<String, String> renderedVisibleTo = runContext.render(this.visibleTo).asMap(String.class, String.class);
        if (!renderedVisibleTo.isEmpty()) {
            List<Map<String, String>> visibleToList = renderedVisibleTo.entrySet().stream()
                .map(entry -> Map.of("id", entry.getKey(), "type", entry.getValue()))
                .toList();
            map.put("visibleTo", visibleToList);
        }

        final List<String> renderedTagList = runContext.render(tags).asList(String.class);
        if (!renderedTagList.isEmpty()) {
            map.put("tags", renderedTagList);
        }

        if (runContext.render(priority).as(String.class).isPresent()) {
            map.put("priority", runContext.render(priority).as(String.class).get());
        }

        this.payload = Property.ofValue(JacksonMapper.ofJson().writeValueAsString(map));

        return super.run(runContext);
    }

}
