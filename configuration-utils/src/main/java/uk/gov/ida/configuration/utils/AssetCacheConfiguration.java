package uk.gov.ida.configuration.utils;

import java.lang.String;public interface AssetCacheConfiguration {

    public boolean shouldCacheAssets();

    public String getAssetsCacheDuration();
}
