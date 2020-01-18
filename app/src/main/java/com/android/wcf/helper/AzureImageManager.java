/**
 * ----------------------------------------------------------------------------------
 * Microsoft Developer & Platform Evangelism
 * <p>
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * <p>
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * ----------------------------------------------------------------------------------
 * The example companies, organizations, products, domain names,
 * e-mail addresses, logos, people, places, and events depicted
 * herein are fictitious.  No association with any real company,
 * organization, product, domain name, email address, logo, person,
 * places, or events is intended or should be inferred.
 * ----------------------------------------------------------------------------------
 **/

package com.android.wcf.helper;

import android.os.AsyncTask;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.File;
import java.io.FileInputStream;

public class AzureImageManager {

    private static CloudBlobContainer getContainer() throws Exception {
        // Retrieve storage account from connection-string.
        CloudStorageAccount storageAccount = CloudStorageAccount
                .parse(ManifestHelper.Companion.getWcbImageServerConnectionString());

        // Create the blob client.
        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

        // Get a reference to a container.
        // The container name must be lower case
        CloudBlobContainer container = blobClient.getContainerReference(ManifestHelper.Companion.getWcbImageServerContainer());

        return container;
    }

    public void UploadImage(String imgPath, BlobUpdateCallback callback) {

        try {
            new BlobUploadTask(imgPath, callback).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class BlobUploadTask extends AsyncTask<String, Void, Void> {
        String imgPath = null;
        BlobUpdateCallback callback = null;


        public BlobUploadTask(String imgPath, BlobUpdateCallback callback) {
            this.imgPath = imgPath;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(String... arg0) {
            String filename = null;

            try {
                // Setup the cloud storage account.
                CloudStorageAccount storageAccount = CloudStorageAccount
                        .parse(ManifestHelper.Companion.getWcbImageServerConnectionString());

                // Create a blob service client
                CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

                // Retrieve reference to a previously created container.
                String storageContainer = ManifestHelper.Companion.getWcbImageServerContainer();
                CloudBlobContainer container = blobClient.getContainerReference(storageContainer);

                // Create the container if it does not exist
                container.createIfNotExists();

                File source = new File(imgPath);
                filename = source.getName();

                String wcbImageFolderName = ManifestHelper.Companion.getWcbImageFolder();
                String blobName = (wcbImageFolderName.length() > 0 ? wcbImageFolderName + "/" : "") + filename;

                CloudBlockBlob blob = container.getBlockBlobReference(blobName);
                blob.upload(new FileInputStream(source), source.length());
                callback.onUploadSuccess(filename);

            } catch (Throwable exception) {
                callback.onUploadError(filename, exception);
            }

            return null;
        }
    }

    public interface BlobUpdateCallback {
        void onUploadSuccess(String filename);
        void onUploadError(String filename, Throwable exception);
    }
}
