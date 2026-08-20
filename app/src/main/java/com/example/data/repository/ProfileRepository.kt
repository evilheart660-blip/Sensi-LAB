package com.example.data.repository

import com.example.data.local.dao.ProfileDao
import com.example.data.local.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow

class ProfileRepository(private val profileDao: ProfileDao) {

    val allProfiles: Flow<List<ProfileEntity>> = profileDao.getAllProfiles()

    suspend fun saveProfile(profile: ProfileEntity): Long {
        return profileDao.insertProfile(profile)
    }

    suspend fun deleteProfile(profile: ProfileEntity) {
        profileDao.deleteProfile(profile)
    }

    suspend fun deleteById(id: Long) {
        profileDao.deleteById(id)
    }

    suspend fun clearAll() {
        profileDao.clearAll()
    }
}
