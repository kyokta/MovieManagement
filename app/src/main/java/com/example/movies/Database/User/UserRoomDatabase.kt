package com.example.movies.Database.User

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserRoomDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao?

    companion object {
        @Volatile
        private var INSTANCE: UserRoomDatabase? = null
        fun getDatabase(context: Context): UserRoomDatabase? {
            if (INSTANCE == null) {
                synchronized(UserRoomDatabase::class.java) {
                    INSTANCE = databaseBuilder(
                        context.applicationContext,
                        UserRoomDatabase::class.java,
                        "user_database"
                    ).build()
                }
            }

            return INSTANCE
        }
    }
}