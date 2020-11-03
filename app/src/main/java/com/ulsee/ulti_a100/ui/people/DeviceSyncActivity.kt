package com.ulsee.ulti_a100.ui.people

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ulsee.ulti_a100.R
import com.ulsee.ulti_a100.databinding.ActivityDeviceSyncBinding
import com.ulsee.ulti_a100.model.Device
import com.ulsee.ulti_a100.ui.device.DeviceInfoRepository

private val TAG = DeviceSyncActivity::class.java.simpleName

class DeviceSyncActivity: AppCompatActivity() {
    private lateinit var binding: ActivityDeviceSyncBinding
    private lateinit var viewModel: DeviceSyncViewModel
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceSyncBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.progressView.visibility = View.VISIBLE
        viewModel = ViewModelProvider(this, DeviceSyncFactory(DeviceInfoRepository()))
            .get(DeviceSyncViewModel::class.java)

        recyclerView = binding.recyclerView
        recyclerView.adapter = DeviceSyncAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.addBtn.setOnClickListener { add() }

        observeOnlineList()
        observeSyncDevices()

    }

    private fun add() {
        val selectedIPList = ArrayList<String>()
        for (i in getSelectedItems()) {
            selectedIPList.add(i.value.getIP())
        }
        if (selectedIPList.size == 0) {
            Toast.makeText(this, "No device is selected", Toast.LENGTH_SHORT).show()
        } else {
            binding.progressView.visibility = View.VISIBLE
            val people = AttributeType.getAttributeData()
            viewModel.synFace(people, selectedIPList)
        }

    }

    private fun observeOnlineList() {
        viewModel.onlineList.observe(this, {
            Log.d(TAG, "[Enter] observeOnlineList size: ${it.size}")
            binding.progressView.visibility = View.INVISIBLE
            (recyclerView.adapter as DeviceSyncAdapter).setList(it)
        })
    }

    // TODO
    private fun observeSyncDevices() {
        viewModel.syncResult.observe(this, {
            binding.progressView.visibility = View.INVISIBLE
            if (it == true) {
                Toast.makeText(this, getString(R.string.done), Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                if (viewModel.syncErrorCode == ERROR_CODE_WORK_ID_EXISTS) {
                    Toast.makeText(this, "The input work ID already exists", Toast.LENGTH_SHORT).show()
                    viewModel.resetErrorCode()
                } else {
                    Toast.makeText(this, "some error occur during the process", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun getSelectedItems(): HashMap<Int, Device> {
        return (recyclerView.adapter as DeviceSyncAdapter).selectedItems
    }

}