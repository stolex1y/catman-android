package ru.stolexiy.catman.ui.util.udf

import androidx.work.WorkManager
import androidx.work.WorkRequest
import kotlinx.coroutines.flow.Flow

fun WorkRequest.toFlow(wm: WorkManager): Flow<SimpleLoadingState> {

}