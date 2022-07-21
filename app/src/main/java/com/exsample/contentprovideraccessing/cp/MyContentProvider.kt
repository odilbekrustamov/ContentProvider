package com.exsample.contentprovideraccessing.cp

import android.content.*
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri


class MyContentProvider : ContentProvider() {
    companion object {
        const val PROVIDER_NAME = "com.demo.user.provider"
        const val URL = "content://" + PROVIDER_NAME + "/users"
        val CONTENT_URI = Uri.parse(URL)
        const val id = "id"
        const val name = "name"
        const val uriCode = 1
        var uriMatcher: UriMatcher? = null
        private val values: HashMap<String, String>? = null

        const val DATABASE_NAME = "UserDB"

        const val TABLE_NAME = "Users"

        const val DATABASE_VERSION = 1

        // sql query to create the table
        const val CREATE_DB_TABLE = (" CREATE TABLE " + TABLE_NAME
                + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + " name TEXT NOT NULL);")

        init {
            uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
            uriMatcher!!.addURI(PROVIDER_NAME, "users", uriCode)
            uriMatcher!!.addURI(PROVIDER_NAME, "users/*", uriCode)
        }
    }

    override fun getType(uri: Uri): String {
        return when (uriMatcher!!.match(uri)) {
            uriCode -> "vnd.android.cursor.dir/users"
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
    }

    override fun onCreate(): Boolean {
        val context = context
        val dbHelper = DatabaseHelper(context)
        db = dbHelper.writableDatabase
        return if (db != null) {
            true
        } else false
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?,
    ): Cursor? {
        var sortOrder = sortOrder
        val qb = SQLiteQueryBuilder()
        qb.tables = TABLE_NAME
        when (uriMatcher!!.match(uri)) {
            uriCode -> qb.projectionMap =
                values
            else -> throw IllegalArgumentException("Unknown URI $uri")
        }
        if (sortOrder == null || sortOrder === "") {
            sortOrder = id
        }
        val c = qb.query(db, projection, selection, selectionArgs, null,
            null, sortOrder)
        c.setNotificationUri(context!!.contentResolver, uri)
        return c
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        val rowID = db!!.insert(TABLE_NAME, "", values)
        if (rowID > 0) {
            val _uri = ContentUris.withAppendedId(CONTENT_URI, rowID)
            context!!.contentResolver.notifyChange(_uri, null)
            return _uri
        }
        throw SQLiteException("Failed to add a record into $uri")
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?,
    ): Int {
        var count = 0
        count =
            when (uriMatcher!!.match(uri)) {
                uriCode -> db!!.update(TABLE_NAME, values, selection, selectionArgs)
                else -> throw IllegalArgumentException("Unknown URI $uri")
            }
        context!!.contentResolver.notifyChange(uri, null)
        return count
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        var count = 0
        count =
            when (uriMatcher!!.match(uri)) {
                uriCode -> db!!.delete(TABLE_NAME, selection, selectionArgs)
                else -> throw IllegalArgumentException("Unknown URI $uri")
            }
        context!!.contentResolver.notifyChange(uri, null)
        return count
    }

    private var db: SQLiteDatabase? = null

    private class DatabaseHelper
    constructor(context: Context?) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(CREATE_DB_TABLE)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
            onCreate(db)
        }
    }
}