package sku.app.lib_tracker.ui

import androidx.work.WorkInfo

enum class WorkerState {
    SUCCESS,
    FAILED,
    NOT_RAN,
    RUNNING,
    UNKNOWN_STATE
}

fun WorkInfo.State.workerState(): WorkerState {
    return when (this) {
        WorkInfo.State.SUCCEEDED -> WorkerState.SUCCESS
        WorkInfo.State.FAILED -> WorkerState.FAILED
        WorkInfo.State.CANCELLED -> WorkerState.FAILED
        WorkInfo.State.RUNNING -> WorkerState.RUNNING
        else -> WorkerState.UNKNOWN_STATE
    }
}