package org.kablink.teaming.client.rest.v1;

import org.junit.Assert;

import org.kablink.teaming.rest.v1.model.Access;
import org.kablink.teaming.rest.v1.model.Binder;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.BinderChanges;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchableObject;
import org.kablink.teaming.rest.v1.model.Share;
import org.kablink.teaming.rest.v1.model.ShareRecipient;
import org.kablink.teaming.rest.v1.model.User;
import org.kablink.teaming.rest.v1.model.admin.PersonalStorage;
import org.kablink.teaming.rest.v1.model.admin.WebAppConfig;

import java.io.ByteArrayInputStream;
import java.util.Date;

/**
 * User: David
 * Date: 11/18/14
 * Time: 9:48 AM
 */
public class ApiTestBinding {
    private AdminApi adminApi;
    public Api clientApiAsAdmin;
    public Api clientApiAsHomelessUser;
    public Api clientApiAsLdapUser;

    public ApiTestBinding() {
        ApiClient homelessClient = ApiClient.create("https://kamas.wal.novell.com:8443", "nohome", "novell");
        ApiClient ldapClient = ApiClient.create("https://kamas.wal.novell.com:8443", "dlewis2", "novell");
        ApiClient adminClient = ApiClient.create("https://kamas.wal.novell.com:8443", "admin", "novell");
        clientApiAsHomelessUser = new ApiImpl(homelessClient);
        clientApiAsLdapUser = new ApiImpl(ldapClient);
        clientApiAsAdmin = new ApiImpl(adminClient);
        adminApi = new AdminApiImpl(adminClient);
    }

    public Date givenMyFilesLibraryChildrenLastModifiedTime() {
        Binder myFiles = clientApiAsLdapUser.getMyFiles();
        SearchResultList<SearchableObject> searchableObjectSearchResultList = clientApiAsLdapUser.listChildren(myFiles);
        return searchableObjectSearchResultList.getLastModified();
    }

    public void thenMyFilesLibraryLibraryChangesFails(Date since) {
        Binder myFiles = clientApiAsLdapUser.getMyFiles();
        try {
            BinderChanges changes = clientApiAsLdapUser.listChanges(myFiles, since, 10);
            Assert.fail();
        } catch (ConflictException e) {
            // Expected
        }
    }

    public void thenMyFilesLibraryLibraryChangesReturnsNothing(Date since) {
        Binder myFiles = clientApiAsLdapUser.getMyFiles();
        BinderChanges changes = clientApiAsLdapUser.listChanges(myFiles, since, 10);
        Assert.assertEquals(0, (long)changes.getCount());
    }

    public void thenMyFilesLibraryChildrenChanged(Date origModTime) {
        Binder myFiles = clientApiAsLdapUser.getMyFiles();
        try {
            SearchResultList<SearchableObject> searchableObjectSearchResultList = clientApiAsLdapUser.listChildren(myFiles, origModTime);
            Date newModTime = searchableObjectSearchResultList.getLastModified();
            Assert.assertTrue(origModTime.before(newModTime));
        } catch (NotModifiedException e) {
            Assert.fail();
        }
    }

    public void thenSharedWithMeCountIs(int count) {
        Binder publicShares = clientApiAsLdapUser.getSharedWithMe();
        SearchResultList<SearchableObject> searchableObjectSearchResultList = clientApiAsLdapUser.listChildren(publicShares, 0, 100);
        Assert.assertEquals((long)count, (long)searchableObjectSearchResultList.getCount());
        Assert.assertEquals((long)count, (long)searchableObjectSearchResultList.getTotal());
    }

    public void thenPublicShareCountIs(int count) {
        Binder publicShares = clientApiAsLdapUser.getPublicShares();
        SearchResultList<SearchableObject> searchableObjectSearchResultList = clientApiAsLdapUser.listChildren(publicShares);
        Assert.assertEquals((long)count, (long)searchableObjectSearchResultList.getCount());
        Assert.assertEquals((long)count, (long)searchableObjectSearchResultList.getTotal());
    }

    public void thenMyFilesLibraryChildrenNotChanged(Date origModTime) {
        Binder myFiles = clientApiAsLdapUser.getMyFiles();
        try {
            SearchResultList<SearchableObject> searchableObjectSearchResultList = clientApiAsLdapUser.listChildren(myFiles, origModTime);
            Assert.fail();
        } catch (NotModifiedException e) {
            // Expected
        }
    }

    public void whenGuestEnabledAndPublicShare() {
        whenGuestEnabled();
        whenPublicShare();
    }

    public void whenGuestEnabledAndNoPublicShares() {
        whenGuestEnabled();
        whenNoPublicShares();
    }

    public void whenGuestDisabledAndPublicShare() {
        whenGuestEnabled();
        whenPublicShare();
        whenGuestDisabled();
    }

    public void whenGuestDisabledAndNoPublicShares() {
        whenGuestDisabled();
        whenNoPublicShares();
    }

    public void whenGuestEnabled() {
        WebAppConfig config = new WebAppConfig();
        config.setAllowGuestAccess(true);
        adminApi.setWebAppConfig(config);
    }

    public void whenGuestDisabled() {
        WebAppConfig config = new WebAppConfig();
        config.setAllowGuestAccess(false);
        adminApi.setWebAppConfig(config);
    }

    public void thenGuestEnabled() {
        WebAppConfig webAppConfig = adminApi.getWebAppConfig();
        Assert.assertTrue(webAppConfig.getAllowGuestAccess());
    }

    public void thenGuestDisabled() {
        WebAppConfig webAppConfig = adminApi.getWebAppConfig();
        Assert.assertFalse(webAppConfig.getAllowGuestAccess());
    }

    public FileProperties whenFileExistsInAdminMyFiles(int counter) {
        Api clientApi = new ApiImpl(((AdminApiImpl)adminApi).conn);
        Binder myFiles = clientApi.getMyFiles();
        return clientApi.uploadFile(myFiles, "test" + counter + ".txt", true, new ByteArrayInputStream(("Test contents " + counter).getBytes()));
    }

    public void whenPublicShare() {
        whenPublicShare(whenFileExistsInAdminMyFiles(1));
    }

    public void whenPublicShare(FileProperties file) {
        Api clientApi = new ApiImpl(((AdminApiImpl)adminApi).conn);
        Share share = new Share();
        ShareRecipient recipient = new ShareRecipient();
        recipient.setType("public");
        share.setRecipient(recipient);
        Access access = new Access();
        access.setRole("VIEWER");
        share.setAccess(access);
        share.setSharedEntity(file.getOwningEntity());
        clientApi.shareFile(file, share);
    }

    public void whenSharedWithUser(FileProperties file, User user) {
        Api clientApi = new ApiImpl(((AdminApiImpl)adminApi).conn);
        Share share = new Share();
        ShareRecipient recipient = new ShareRecipient();
        recipient.setType("user");
        recipient.setId(user.getId());
        share.setRecipient(recipient);
        Access access = new Access();
        access.setRole("VIEWER");
        share.setAccess(access);
        share.setSharedEntity(file.getOwningEntity());
        clientApi.shareFile(file, share);
    }

    public void whenNoPublicShares() {
        SearchResultList<Share> publicShares = adminApi.getPublicShares();
        for (Share share : publicShares.getResults()) {
            adminApi.deleteShare(share);
        }
    }

    public void whenNoSharesWithUser(User user) {
        SearchResultList<Share> shares = adminApi.getSharesWithUser(user);
        for (Share share : shares.getResults()) {
            adminApi.deleteShare(share);
        }
    }

    public void thenPublicShares() {
        SearchResultList<Share> publicShares = adminApi.getPublicShares();
        Assert.assertNotSame(0, publicShares.getCount());
    }

    public void thenNoPublicShares() {
        SearchResultList<Share> publicShares = adminApi.getPublicShares();
        Assert.assertEquals(0, (int) publicShares.getCount());
    }

    public void whenPersonalStorageEnabled() {
        PersonalStorage storage = new PersonalStorage();
        storage.setAllowPersonalStorage(true);
        this.adminApi.setPersonalStorage(storage);
    }

    public void whenPersonalStorageDisabled() {
        PersonalStorage storage = new PersonalStorage();
        storage.setAllowPersonalStorage(false);
        this.adminApi.setPersonalStorage(storage);
    }

    public void whenPersonalStorageChanges() {
        PersonalStorage current = this.adminApi.getPersonalStorage();
        PersonalStorage storage = new PersonalStorage();
        storage.setAllowPersonalStorage(!current.getAllowPersonalStorage());
        this.adminApi.setPersonalStorage(storage);
    }

    public void thenPersonalStorageDisabled() {
        PersonalStorage personalStorage = this.adminApi.getPersonalStorage();
        Assert.assertFalse(personalStorage.getAllowPersonalStorage());
    }

    public void thenPersonalStorageEnabled() {
        PersonalStorage personalStorage = this.adminApi.getPersonalStorage();
        Assert.assertTrue(personalStorage.getAllowPersonalStorage());
    }

    public void thenGetMyFilesSucceeds() {
        Binder binder = this.clientApiAsHomelessUser.getMyFiles();
        Assert.assertNotNull(binder);
    }

    public void thenGetMyFilesByIdSucceeds() {
        SearchResultList<BinderBrief> binders = this.clientApiAsHomelessUser.getBinders(new Long[]{Api.MY_FILES_ID});
        Assert.assertEquals(1, (int) binders.getCount());
        Assert.assertEquals(1, binders.getResults().size());
        Assert.assertEquals(Api.MY_FILES_ID, binders.getResults().get(0).getId());
    }

    public void thenGetMyFilesByIdReturnsNothing() {
        SearchResultList<BinderBrief> binders = this.clientApiAsHomelessUser.getBinders(new Long[]{Api.MY_FILES_ID});
        Assert.assertEquals(0, binders.getResults().size());
    }

    public void thenGetSelfHasMyFilesLink() {
        User user = clientApiAsHomelessUser.getSelf();
        Assert.assertNotNull(user.findRelatedLink("my_files"));
    }

    public void thenGetSelfDoesNotHaveMyFilesLink() {
        User user = clientApiAsHomelessUser.getSelf();
        Assert.assertNull(user.findRelatedLink("my_files"));
    }

    public void thenGetSelfHasPublicSharesLink() {
        User user = clientApiAsHomelessUser.getSelf();
        Assert.assertNotNull(user.findRelatedLink("public_shares"));
    }

    public void thenGetSelfDoesNotHavePublicSharesLink() {
        User user = clientApiAsHomelessUser.getSelf();
        Assert.assertNull(user.findRelatedLink("public_shares"));
    }

    public void thenGetPublicSharesByIdSucceeds() {
        SearchResultList<BinderBrief> binders = this.clientApiAsHomelessUser.getBinders(new Long[]{Api.PUBLIC_SHARES_ID});
        Assert.assertEquals(1, (int) binders.getCount());
        Assert.assertEquals(1, binders.getResults().size());
        Assert.assertEquals(Api.PUBLIC_SHARES_ID, binders.getResults().get(0).getId());
    }

    public void thenGetPublicSharesByIdReturnsNothing() {
        SearchResultList<BinderBrief> binders = this.clientApiAsHomelessUser.getBinders(new Long[]{Api.PUBLIC_SHARES_ID});
        Assert.assertEquals(0, binders.getResults().size());
    }

    public void thenGetMyFilesFails() {
        try {
            Binder binder = ((ApiImpl)this.clientApiAsHomelessUser).getJSONResourceBuilder("/self/my_files").get(Binder.class);
        } catch (Exception e) {
            return;
        }
        Assert.fail();
    }

    private boolean getRootsIncludesId(Long id) {
        SearchResultList<BinderBrief> topLevelFolders = clientApiAsHomelessUser.getTopLevelFolders();
        Assert.assertNotNull(topLevelFolders);
        boolean found = false;
        for (BinderBrief binder : topLevelFolders.getResults()) {
            if (binder.getId()==id) {
                found = true;
            }
        }
        return found;
    }

    public void thenGetRootsIncludesMyFiles() {
        Assert.assertTrue(getRootsIncludesId(Api.MY_FILES_ID));
    }

    public void thenGetRootsDoesNotIncludeMyFiles() {
        Assert.assertFalse(getRootsIncludesId(Api.MY_FILES_ID));
    }

    public void thenGetRootsIncludesPublicShares() {
        Assert.assertTrue(getRootsIncludesId(Api.PUBLIC_SHARES_ID));
    }

    public void thenGetRootsDoesNotIncludePublicShares() {
        Assert.assertFalse(getRootsIncludesId(Api.PUBLIC_SHARES_ID));
    }

    public void thenGetPublicSharesRootSucceeds() {
        Binder binder = ((ApiImpl)this.clientApiAsHomelessUser).getJSONResourceBuilder("/self/public_shares").get(Binder.class);
        Assert.assertNotNull(binder);
    }

    public void thenGetPublicSharesRootFails() {
        try {
            Binder binder = ((ApiImpl)this.clientApiAsHomelessUser).getJSONResourceBuilder("/self/public_shares").get(Binder.class);
        } catch (Exception e) {
            return;
        }
        Assert.fail();
    }

}