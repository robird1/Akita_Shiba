package com.ulsee.shiba.ui.record

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ulsee.shiba.api.ApiService
import com.ulsee.shiba.data.response.AttendRecord
import com.ulsee.shiba.data.response.ClearAttendRecord
import com.ulsee.shiba.data.response.GetAttendRecordCount
import com.ulsee.shiba.data.response.QueryAttendRecord
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody

class AttendRecordRepository(val url: String) {
    private var pagingSource: AttendRecordPagingSource? = null

    suspend fun requestAttendRecordCount(): GetAttendRecordCount {
        return ApiService.create(url).requestAttendRecordCount()
    }

    suspend fun requestAttendRecord(requestBody: RequestBody): QueryAttendRecord {
        return ApiService.create(url).requestAttendRecord(requestBody)
    }

    suspend fun clearAttendRecord(requestBody: RequestBody): ClearAttendRecord {
        return ApiService.create(url).clearAttendRecord(requestBody)
    }

    fun getSearchResultStream(totalCount: Int, startId: Int, endId: Int): Flow<PagingData<AttendRecord>> {
        pagingSource = AttendRecordPagingSource(this, totalCount, startId, endId)

        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = { pagingSource!! }
        ).flow
    }

    fun invalidatePagingSource() {
        pagingSource?.invalidate()
    }


    companion object {
        // PAGE_SIZE = 1 => 1 page per request
        private const val PAGE_SIZE = 1
    }
}