package org.kabling.teaming.install.client.i18n;

import com.google.gwt.i18n.client.Messages;

public interface AppResource extends Messages
{
	String userNameColon();

	String passwordColon();

	String login();
	
	String userNamePasswordInvalid(); 

	String configuration();

	String logout();

	String yes();

	String no();

	String close();

	String cancel();

	String ok();
	
	String next();
	
	String previous();
	
	String finish();

	String environment();

	String clustering();

	String network();

	String lucene();
	
	String webDavAuthentication();
	String reverseProxy();

	String pleaseWait();

	String validatingCredentials();
	
	String creatingDatabase();

	String updatingDatabase();

	String reconfiguringServer();

	String startingServer();

	String allFieldsRequired();

	String wizDbPageTitleDesc();

	String database();
	
	String dbTypeColon();

	String hostNameColon();

	String portColon();

	String dbAdminColon();

	String dbAdminPasswordColon();

	String unableToConnectToDbServer();

	String dbNameColon();
	
	String wizLucenePageTitleDesc();
	
	String rmiPortColon();
	
	String validatingLuceneServer();
	
	String unableToConnectLuceneServer();
	
	String defaultIs1199();
	
	String webDavPageTitle();
	String webDavPageTitleDesc();
	String webDavPageDesc();
	String webDavPageAuthMethodColon();
	String basic();
	String digest();

	String webServices();
	String enableWebServices();
	String enableWssAuth();
	String enableBasicAuth();
	String enableTokenBasedAuth();
	String enableAnonymousAccess();
	
	String networkInfoPageTitleDesc();
	String networkInfoPageDesc();
	String hostColon();
	String keyStoreFileColon();
	String sessionTimeOutColon();
	String ajpPortColon();
	String shutdownPortColon();
	String secureListenPortColon();
	String listenPortColon();
	String secureHttpPortColon();
	String httpPortColon();
	
	String jdkPageTitleDesc();
	String javaHomeColon();
	String jdkPageDesc();
	String jvmHeapSizeColon();

	String jvmSettings();

	String reqConnectionsPageTitleDesc();
	String reqConnectionsPageDesc();
	String maxThreadsColon();
	String maxActiveColon();
	String maxIdleColon();
	String requestsAndConnections();
	
	String default200();
	String default20();
	String default50();

	String reverseProxyPageTitleDesc();
	String reverseProxyPageDesc();

	String requiredField();

	String jvmHeapSizeInvalid();

	String javaJDK();

	String outboundEmail();
	String tomcatRestartRequired();
	String configUpToDate();

	String restartFilrServer();

	String configurationSummary();

	String mysql();

	String oracle();

	String enabledColon();
	String tokenBasicAuthColon();
	String basicAuthColon();
	String anonymousAuthColon();
	
	String falseStr();
	String trueStr();

	String clusteringPageTitleDesc();
	String jvmRouteColon();
	String hibernateCachingProviderColon();
	String networkInterfaceForCacheDesc();
	String optional();
	String enableClusteredEnvironment();
	String multicastGroupAddrColon();
	String multicastGroupPortColon();

	String enableInternalSMTPServer();
	String smtpBindAddressColon();
	String smtpPortColon();
	String inboundEmail();

	String announceTLS();

	String protocolColon();
	String timeZoneColon();
	String authRequired();
	String allowSendEmailToAllUsers();
	String outboundPageTitleDesc();
	String outboundPageDesc();
	String smtp();
	String smtps();
	String tlsEnabledColon();

	String enableAccessGateway();
	String logoutUrlColon();
	String accessGatewayAddressesColon();
	String useAccessGatewayForWebDav();
	String webDavAccessGatewayAddrColon();

	String unableToSaveConfiguration();

	String logOffUrlColon();

	String accessGatewayEnabledWithInValidData();
	String gatewayWebDavEnabledWithInvalidData();

	String reconfigureServer();

	String importExportPageTitleDesc();
	String importExportPageDesc();
	String exportDesc();

	String export();
	String exportFilrServerConfig();
	String exportConfiguration();
	String importExport();

	String configFinishedMsg(String ipAddr);
	String filrServerInfo(String ipAddr);
}