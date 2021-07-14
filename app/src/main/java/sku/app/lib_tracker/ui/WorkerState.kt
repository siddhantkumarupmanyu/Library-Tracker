package sku.app.lib_tracker.ui

import androidx.work.WorkInfo

enum class WorkerState {
    ENQUEUED,
    SUCCEEDED,
    FAILED,
    NOT_RAN,
    RUNNING,
    UNKNOWN_STATE;

}

fun WorkInfo.State.workerState(): WorkerState {
    return when (this) {
        WorkInfo.State.ENQUEUED -> WorkerState.ENQUEUED
        WorkInfo.State.SUCCEEDED -> WorkerState.SUCCEEDED
        WorkInfo.State.FAILED -> WorkerState.FAILED
        WorkInfo.State.CANCELLED -> WorkerState.FAILED
        WorkInfo.State.RUNNING -> WorkerState.RUNNING
        else -> WorkerState.UNKNOWN_STATE
    }
}

fun WorkerState.isFinished(): Boolean {
    return this == WorkerState.SUCCEEDED
            || this == WorkerState.FAILED
}
