package uk.gov.ida.shared.utils.featuretoggles;

import org.joda.time.DateTime;

public enum IdaFeatures implements Feature {

    UIRework(DateTime.parse("2014-08-14")),
    EncodeAssertions(DateTime.parse("2014-08-14"));

    private final DateTime createdDate;

    IdaFeatures(DateTime createdDate) {
        this.createdDate = createdDate;
    }

    private boolean active;

    public boolean isActive() {
        return active;
    }

    @Override
    public DateTime getCreateDate() {
        return createdDate;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
