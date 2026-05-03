package com.myApp.vizualearnfinal.screens.step3

import android.content.Context
import android.net.Uri
import com.myApp.vizualearnfinal.data.model.StudySet
import com.myApp.vizualearnfinal.data.repository.StudySetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Step3Model(
    private val context: Context,
    private val repository: StudySetRepository
) {
    suspend fun getStudySet(setId: Int): StudySet? {
        return repository.getSetById(setId)
    }

    suspend fun readFileData(uriString: String): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                val uri = Uri.parse(uriString)
                val inputStream = context.contentResolver.openInputStream(uri)
                inputStream?.readBytes()
            } catch (e: Exception) {
                null
            }
        }
    }
}