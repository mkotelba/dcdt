package gov.hhs.onc.dcdt.utils.ldap.loader;

import gov.hhs.onc.dcdt.utils.LoaderUtility;
import gov.hhs.onc.dcdt.beans.LdapService;
import gov.hhs.onc.dcdt.utils.cli.UtilityCli;
import gov.hhs.onc.dcdt.ldap.LdapServiceWrapper;
import gov.hhs.onc.dcdt.ldap.ToolLdapException;
import gov.hhs.onc.dcdt.ldap.ldif.LdifBuilder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.log4j.Logger;

public class LdapLoader extends LoaderUtility<LdapLoaderCliOption>
{
	private final static String UTIL_NAME = "ldaploader";
	
	private final static String USER_CERT_LDIF_ENTRY_ATTRIB_ID = "userCertificate";
	
	private final static String INPUT_LDIFS_PATH_ATTRIB_NAME = "inputLdifsPath";
	
	private final static Logger LOGGER = Logger.getLogger(LdapLoader.class);
	
	public LdapLoader()
	{
		super(UTIL_NAME, new UtilityCli<>(LdapLoaderCliOption.class));
	}
	
	public static void main(String ... args)
	{
		new LdapLoader().execute(args);
	}
	
	@Override
	protected void execute(String ... args)
	{
		super.execute(args);

		this.loadEntries(this.config.getUtilString(LdapLoaderCliOption.INPUT_PATH));
		
		String inputLdifsPath = this.config.getUtilString(INPUT_LDIFS_PATH_ATTRIB_NAME), 
			outputLdifsPath = this.config.getUtilString(LdapLoaderCliOption.OUTPUT_LDIFS_PATH);
		boolean load = Boolean.parseBoolean(this.config.getUtilString(LdapLoaderCliOption.LOAD));
		
		for (LdapService ldapService : this.config.getLdapServices())
		{
			try
			{
				this.loadLdif(ldapService, getLdifPath(inputLdifsPath, ldapService), 
					getLdifPath(outputLdifsPath, ldapService), load);
			}
			catch (ToolLdapException e)
			{
				LOGGER.error(e);
				
				exitError();
			}
		}
	}
	
	@Override
	protected void processCmdLine()
	{
		super.processCmdLine();
		
		this.config.setUtilString(LdapLoaderCliOption.BIND_DN_NAME);
		this.config.setUtilString(LdapLoaderCliOption.BIND_PASS);
		this.config.setUtilString(LdapLoaderCliOption.DOMAIN);
		this.config.setUtilString(LdapLoaderCliOption.LOAD);
		this.config.setUtilString(LdapLoaderCliOption.LOAD_DN_NAME);
		
		this.config.setUtilString(LdapLoaderCliOption.INPUT_PATH);
		
		File inputPath = new File(this.config.getUtilString(LdapLoaderCliOption.INPUT_PATH));
		
		if (!inputPath.exists())
		{
			LOGGER.error("Input path does not exist: " + inputPath);
			
			exitError();
		}
		
		this.config.setUtilString(LdapLoaderCliOption.OUTPUT_LDIFS_PATH);
		
		File outputLdifsPath = new File(this.config.getUtilString(LdapLoaderCliOption.OUTPUT_LDIFS_PATH));
		
		if (outputLdifsPath.exists())
		{
			if (!outputLdifsPath.isDirectory())
			{
				LOGGER.error("LDIF file(s) output path is not a directory: " + outputLdifsPath);
				
				exitError();
			}
		}
		else
		{
			if (!outputLdifsPath.mkdirs())
			{
				LOGGER.error("Unable to make output LDIF file(s) path directory tree: " + outputLdifsPath);
				
				exitError();
			}
		}
	}
	
	private static String getLdifPath(String ldifsPath, LdapService ldapService)
	{
		return ldifsPath + File.separatorChar + ldapService.getName() + LdifBuilder.LDIF_FILE_EXT;
	}
	
	private boolean loadLdifEntry(LdifBuilder ldifBuilder, LdapServiceWrapper ldapServiceWrapper, LdifEntry ldifEntry)
		throws ToolLdapException
	{
		ldifBuilder.parseEntry(ldifEntry);
		
		return ldapServiceWrapper.modify(ldifEntry);
	}
	
	private void initLdifEntry(LdifEntry ldifEntry) throws ToolLdapException
	{
		if (ldifEntry.getEntry().containsAttribute(USER_CERT_LDIF_ENTRY_ATTRIB_ID))
		{
			try
			{
				LdifBuilder.setAttribute(ldifEntry, USER_CERT_LDIF_ENTRY_ATTRIB_ID, 
					this.entryMap.get(ldifEntry.get(SchemaConstants.CN_AT).getString()).getCert().getEncoded());
			}
			catch (CertificateEncodingException | LdapInvalidAttributeValueException e)
			{
				throw new ToolLdapException("Unable to set user certificate attribute (id=" + USER_CERT_LDIF_ENTRY_ATTRIB_ID + 
					") value in LDIF entry: " + ldifEntry, e);
			}
		}
	}
	
	private void loadLdif(LdapService ldapService, String inputLdifPath, String outputLdifPath, boolean load)
		throws ToolLdapException
	{
		if (ldapService.isReal())
		{
			LdapServiceWrapper ldapServiceWrapper = new LdapServiceWrapper(ldapService);
			LdifBuilder ldifBuilder = new LdifBuilder(this.config, ldapServiceWrapper);
			List<LdifEntry> ldifEntries, modifiedLdifEntries = new ArrayList<>();
			
			try
			{
				ldifEntries = ldifBuilder.readEntries(inputLdifPath);
			}
			catch (ToolLdapException e)
			{
				throw new ToolLdapException("Unable to read LDIF (path=" + inputLdifPath + ") entries.", e);
			}
			
			LOGGER.debug("Read " + CollectionUtils.size(ldifEntries) + " LDIF (path=" + inputLdifPath + ") entries.");
			
			if (load)
			{
				ldapServiceWrapper.bind();
			}
			
			for (LdifEntry ldifEntry : ldifEntries)
			{
				this.initLdifEntry(ldifEntry);
			}
			
			this.writeLdifEntries(ldifBuilder, ldifEntries, outputLdifPath);
			
			if (load)
			{
				for (LdifEntry ldifEntry : ldifEntries)
				{
					if (this.loadLdifEntry(ldifBuilder, ldapServiceWrapper, ldifEntry))
					{
						modifiedLdifEntries.add(ldifEntry);
					}
				}
				
				ldapServiceWrapper.disconnect();
				
				LOGGER.info("Loaded " + modifiedLdifEntries.size() + " LDIF (path=" + inputLdifPath + ") entries into LDAP service (" + 
					ldapService.toUrl() + ").");
			}
		}
	}
	
	private void writeLdifEntries(LdifBuilder ldifBuilder, List<LdifEntry> ldifEntries, String outputLdifPath)
		throws ToolLdapException
	{
		File ldifFile = new File(outputLdifPath);
		
		try
		{
			if (!ldifFile.exists())
			{
				FileUtils.touch(ldifFile);
			}
			
			ldifBuilder.writeEntries(ldifEntries, new FileOutputStream(outputLdifPath));
			
			LOGGER.info("Wrote " + ldifEntries.size() + " LDIF entries to file: " + ldifFile);
		}
		catch (IOException e)
		{
			throw new ToolLdapException("Unable to write " + ldifEntries.size() + " LDIF entries to file: " + ldifFile, e);
		}
	}
}