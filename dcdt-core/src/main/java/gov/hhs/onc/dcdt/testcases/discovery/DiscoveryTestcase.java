package gov.hhs.onc.dcdt.testcases.discovery;

import gov.hhs.onc.dcdt.config.InstanceConfig;
import gov.hhs.onc.dcdt.testcases.ToolTestcase;
import java.util.List;
import javax.annotation.Nullable;

public interface DiscoveryTestcase extends ToolTestcase<DiscoveryTestcaseDescription, DiscoveryTestcaseResult> {
    public boolean hasCredentials();

    @Nullable
    public List<DiscoveryTestcaseCredential> getCredentials();

    public void setCredentials(@Nullable List<DiscoveryTestcaseCredential> creds);

    public boolean hasInstanceConfig();

    @Nullable
    public InstanceConfig getInstanceConfig();

    public void setInstanceConfig(@Nullable InstanceConfig instanceConfig);

    public boolean hasMailAddress();

    @Nullable
    public String getMailAddress();

    public void setMailAddress(@Nullable String mailAddr);
}