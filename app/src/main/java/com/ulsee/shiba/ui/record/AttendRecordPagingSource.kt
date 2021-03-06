package com.ulsee.shiba.ui.record

import android.util.Log
import androidx.paging.PagingSource
import com.ulsee.shiba.data.response.AttendRecord
import com.ulsee.shiba.data.response.QueryAttendRecord
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val RECORD_QUERY_COUNT = 10
private val TAG = AttendRecordPagingSource::class.java.simpleName

class AttendRecordPagingSource(private val repository: AttendRecordRepository, private val totalCount: Int, private val defaultStartId: Int, private val endId: Int) : PagingSource<Int, AttendRecord>() {
    private var lastStartId: Int = -1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AttendRecord> {
        val startId = params.key ?: getStartIndex()

        return try {
            Log.d(TAG, "lastStartId: $lastStartId")
            val requestBody = createJsonRequestBody("startId" to startId, "reqCount" to getRequestCount(startId))
            val records = repository.requestAttendRecord(requestBody)
//            Log.d(TAG, "record size: " + records.recordCount)
            lastStartId = startId

            if (records.data != null) {
                LoadResult.Page(data = getSortedList(records), prevKey = getPreviousKey(startId), nextKey = getNextKey(startId))
            } else {
                // empty list
                LoadResult.Page(data = listOf(), prevKey = null, nextKey = null)
            }

        } catch (exception: IOException) {
            Log.d(TAG, "IOException: "+ exception.message)
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            Log.d(TAG, "HttpException: "+ exception.message)
            LoadResult.Error(exception)
        } catch (exception: Exception) {
            Log.d(TAG, "Exception: "+ exception.message)
            LoadResult.Error(exception)
        }
    }

    private fun getSortedList(records: QueryAttendRecord): List<AttendRecord> {
        return records.data!!.sortedByDescending {
            parseTime(it.timestamp)?.time
        }
    }

    private fun getPreviousKey(startId: Int): Int? {
        Log.d(TAG, "[Enter] getPreviousKey")
        return if (startId == endId - RECORD_QUERY_COUNT) {
            Log.d(TAG, "previous key = null")
            null
        } else {
            Log.d(TAG, "previous key = ${(startId + RECORD_QUERY_COUNT)}")
            startId + RECORD_QUERY_COUNT
        }
    }

    private fun getNextKey(startId: Int): Int? {
        Log.d(TAG, "[Enter] getNextKey")
        return if (startId < defaultStartId) {
            Log.d(TAG, "next key = null")
            null
        } else {
            Log.d(TAG, "next key = ${(startId - RECORD_QUERY_COUNT)}")
            (startId - RECORD_QUERY_COUNT)
        }
    }

    private fun getRequestCount(startId: Int): Int {
        return if (startId >= defaultStartId) {
            RECORD_QUERY_COUNT
        } else {
            if (lastStartId != -1) {          // the last page of the list data
                lastStartId - defaultStartId + 1
            } else {                         // the total count of the list data is less than RECORD_QUERY_COUNT
                totalCount
            }
        }
    }

    private fun getStartIndex(): Int {
        return endId - RECORD_QUERY_COUNT
    }

//    private fun createJsonRequestBody(vararg params: Pair<String, Int>) =
//        JSONObject(mapOf(*params)).toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

    private fun createJsonRequestBody(vararg params: Pair<String, Int>): RequestBody {
        val tmp = JSONObject(mapOf(*params, "needImg" to true)).toString()
        Log.d(TAG, "JSONObject toString: $tmp")
        return tmp.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    private fun parseTime(time: String): Date? {
        val pattern = "yyyy-MM-dd HH:mm:ss"
        val simpleDateFormat = SimpleDateFormat(pattern)
        return try {
            simpleDateFormat.parse(time)
        } catch (e: Exception) {
            simpleDateFormat.parse("1970-01-01 00:00:00")
        }
    }
//    override fun getRefreshKey(state: PagingState<Int, AttendRecord>): Int? {
//        return state.anchorPosition
//    }
//
//    override val jumpingSupported = true

}
