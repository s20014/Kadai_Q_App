package jp.ac.itcollege.std.s20014.kadai_q_app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper (context:Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        companion object {
            private const val DATABASE_NAME = "quiz.db"
            private const val DATABASE_VERSION = 1
        }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE quiz (
                _id INTEGER PRIMARY KEY,
                question TEXT,
                answers LONG,
                quiz1 TEXT,
                quiz2 TEXT,
                quiz3 TEXT,
                quiz4 TEXT,
                quiz5 TEXT,
                quiz6 TEXT
            );  
        """.trimIndent()
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        println("アップグレード")

    }

}
