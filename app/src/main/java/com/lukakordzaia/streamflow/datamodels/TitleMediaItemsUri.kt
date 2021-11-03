package com.lukakordzaia.streamflow.datamodels

import android.net.Uri

data class TitleMediaItemsUri(
        val titleFileUri: Uri,
        val titleSubUri: Uri?
)