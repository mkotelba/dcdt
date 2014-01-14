package gov.hhs.onc.dcdt.crypto.certs;

import gov.hhs.onc.dcdt.beans.ToolBean;
import gov.hhs.onc.dcdt.crypto.utils.X500Utils;
import java.security.Principal;
import java.util.SortedMap;
import javax.annotation.Nullable;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralNames;

public interface CertificateName extends Principal, ToolBean {
    public X500Name toX500Name();

    public boolean hasSubjectAltNames();

    @Nullable
    public GeneralNames getSubjectAltNames();

    @Override
    public String getName();

    @Override
    public boolean equals(Object obj);

    @Override
    public int hashCode();

    @Override
    public String toString();

    public boolean hasCommonName();

    @Nullable
    public String getCommonName();

    public void setCommonName(@Nullable String commonName);

    public boolean hasMailAddress();

    @Nullable
    public String getMailAddress();

    public void setMailAddress(@Nullable String mailAddr);

    public boolean hasAttribute(ASN1ObjectIdentifier attrOid);

    @Nullable
    public String getAttributeValueString(ASN1ObjectIdentifier attrOid);

    @Nullable
    public ASN1Encodable getAttributeValue(ASN1ObjectIdentifier attrOid);

    public void putAttributeValueString(ASN1ObjectIdentifier attrOid, @Nullable String attrValueStr);

    public void putAttributeValue(ASN1ObjectIdentifier attrOid, @Nullable ASN1Encodable attrValue);

    public SortedMap<ASN1ObjectIdentifier, ASN1Encodable> getAttributeMap();
}