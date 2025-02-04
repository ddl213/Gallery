package com.example.pagergallery.unit

const val TABLE_COLL_NAME = "collection"
const val TABLE_QUERY_NAME = "history_query"
const val TABLE_CACHE = "table_cache"
const val TABLE_DOWNLOAD = "table_download"
const val TABLE_USER = "table_user"

const val SAVE_IMAGE_SUCCESS = 1
const val SAVE_IMAGE_FAIL = 0
const val REQUEST_WRITE_EXTERNAL_STORAGE = 1
const val MAX_MULTILINE_LENGTH=200
const val MAX_SINGLE_LENGTH=15











//        val requestList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            Manifest.permission.READ_MEDIA_IMAGES
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            Manifest.permission.READ_EXTERNAL_STORAGE
//        } else {
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//        }