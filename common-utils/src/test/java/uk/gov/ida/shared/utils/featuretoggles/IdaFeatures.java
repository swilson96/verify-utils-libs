package uk.gov.ida.shared.utils.featuretoggles;

import org.joda.time.DateTime;

public enum IdaFeatures implements Feature {

    UIRework,
    EncodeAssertions;

    private boolean active;

    public boolean isActive() {
        return active;
    }

    @Override
    public DateTime getCreateDate() {
        return new DateTime(2014, 8, 14, 10, 7);
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
