package com.exsample.contentprovideraccessing.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact_table")
class Contact (
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val telNumber: String,
    val job: String
)