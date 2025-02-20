package com.gallery.photos.editpic.callendservice.model;

import kotlin.jvm.internal.Intrinsics;

public final class ContactCDO {
    private int contactId;
    private final String contactPhotoThumbUri;
    private final String contactPhotoUri;
    private String nameSuffix;

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ContactCDO) {
            ContactCDO contactCDO = (ContactCDO) obj;
            return this.contactId == contactCDO.contactId && Intrinsics.areEqual(this.nameSuffix, contactCDO.nameSuffix) && Intrinsics.areEqual(this.contactPhotoUri, contactCDO.contactPhotoUri) && Intrinsics.areEqual(this.contactPhotoThumbUri, contactCDO.contactPhotoThumbUri);
        }
        return false;
    }

    public int hashCode() {
        int hashCode = ((this.contactId * 31) + this.nameSuffix.hashCode()) * 31;
        String str = this.contactPhotoUri;
        int hashCode2 = (hashCode + (str == null ? 0 : str.hashCode())) * 31;
        String str2 = this.contactPhotoThumbUri;
        return hashCode2 + (str2 != null ? str2.hashCode() : 0);
    }

    public String toString() {
        return "ContactCDO(contactId=" + this.contactId + ", nameSuffix=" + this.nameSuffix + ", contactPhotoUri=" + this.contactPhotoUri + ", contactPhotoThumbUri=" + this.contactPhotoThumbUri + ')';
    }

    public ContactCDO(int i, String nameSuffix, String str, String str2) {
        this.contactId = i;
        this.nameSuffix = nameSuffix;
        this.contactPhotoUri = str;
        this.contactPhotoThumbUri = str2;
    }

    public final int getContactId() {
        return this.contactId;
    }

    public final String getNameSuffix() {
        return this.nameSuffix;
    }

    public final String getContactPhotoUri() {
        return this.contactPhotoUri;
    }

    public final String getContactPhotoThumbUri() {
        return this.contactPhotoThumbUri;
    }
}
