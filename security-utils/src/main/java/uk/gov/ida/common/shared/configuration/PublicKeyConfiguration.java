package uk.gov.ida.common.shared.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class PublicKeyConfiguration extends KeyConfiguration {

    protected PublicKeyConfiguration() {
    }

    @Valid
    @NotNull
    @Size(min = 1)
    @JsonProperty
    protected String keyName;

    @Valid
    @JsonProperty
    protected Boolean includeInMetadata = true;

    @Valid
    @JsonProperty
    protected Boolean activeKey = true;

    public Boolean getActiveKey() {
        return activeKey;
    }

    public String getKeyName() {
        return keyName;
    }

    public Boolean getIncludeInMetadata() {
        return includeInMetadata;
    }
}
