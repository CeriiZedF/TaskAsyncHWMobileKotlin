package com.example.myapplication2

import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.taskasynchwmobilekotlin.MyConstant
import com.example.taskasynchwmobilekotlin.R
import kotlin.random.Random

class ProgressFragment : Fragment() {
    private var integers: IntArray? = null
    private var indicatorBar: ProgressBar? = null
    private var statusView: TextView? = null
    private var progressTask: ProgressTask? = null

    private var btnFetch: Button? = null
    private var btnCancel: Button? = null
    private var btnResume: Button? = null
    private var btnPause: Button? = null

    private var isPaused = false
    private var currentProgress = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.progress_fragment, container, false)

        val random = Random
        val number = random.nextInt(MyConstant.MIN_ITERATION, MyConstant.MAX_ITERATION)
        integers = IntArray(number) { it + 1 }

        indicatorBar = view.findViewById(R.id.indicator)
        statusView = view.findViewById(R.id.statusView)
        btnFetch = view.findViewById<Button>(R.id.progressBtn)
        btnResume = view.findViewById<Button>(R.id.resumeBtn)
        btnPause = view.findViewById<Button>(R.id.pauseBtn)
        btnCancel = view.findViewById<Button>(R.id.progressCancelBtn)

        indicatorBar?.max = number

        btnFetch?.setOnClickListener {
            progressTask = ProgressTask()
            progressTask?.execute()
            btnFetch?.visibility = View.GONE
            btnCancel?.visibility = View.VISIBLE
            btnPause?.visibility = View.VISIBLE
        }

        btnPause?.setOnClickListener {
            isPaused = true
            btnPause?.visibility = View.GONE
            btnResume?.visibility = View.VISIBLE
        }

        btnResume?.setOnClickListener {
            isPaused = false
            btnPause?.visibility = View.VISIBLE
            btnResume?.visibility = View.GONE
        }

        btnCancel?.setOnClickListener {
            progressTask?.cancel(true)
            btnFetch?.visibility = View.VISIBLE
            btnCancel?.visibility = View.GONE
            btnPause?.visibility = View.GONE
            btnResume?.visibility = View.GONE
        }

        return view
    }

    inner class ProgressTask : AsyncTask<Void, Int, Void>() {

        override fun onPreExecute() {
            super.onPreExecute()
            statusView?.text = "Status: Pending"
        }

        override fun doInBackground(vararg params: Void?): Void? {
            val random: Random = Random
            publishProgress(-1)  // Update status to "Running" when the task starts
            statusView?.post { statusView?.text = "Status: Running" }

            for (i in currentProgress until integers!!.size) {
                if (isCancelled) break

                while (isPaused) {
                    SystemClock.sleep(100)
                }

                publishProgress(i)
                currentProgress = i
                SystemClock.sleep(random.nextLong(MyConstant.MIN_TIME, MyConstant.MAX_TIME))
            }
            return null
        }

        override fun onProgressUpdate(vararg values: Int?) {
            val progress = values[0] ?: 0
            indicatorBar?.progress = progress + 1
            statusView?.text = "Status: ${(progress + 1)}%"
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            statusView?.text = "Status: Finished"
            Toast.makeText(activity, "Задача завершена", Toast.LENGTH_SHORT).show()
            btnFetch?.visibility = View.VISIBLE
            btnCancel?.visibility = View.GONE
            btnPause?.visibility = View.GONE
            btnResume?.visibility = View.GONE
        }

        override fun onCancelled() {
            super.onCancelled()
            statusView?.text = "Status: Cancelled"
            Toast.makeText(activity, "Задача отменена", Toast.LENGTH_SHORT).show()
            btnFetch?.visibility = View.VISIBLE
            btnCancel?.visibility = View.GONE
            btnPause?.visibility = View.GONE
            btnResume?.visibility = View.GONE
        }
    }
}

