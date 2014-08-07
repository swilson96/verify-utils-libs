package uk.gov.ida.shared.utils.featuretoggles;

import org.joda.time.DateTime;

public interface Feature {
    public void setActive(boolean active);
    public boolean isActive();
    public DateTime getCreateDate();
}
