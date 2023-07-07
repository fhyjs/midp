package org.eu.hanana.cirno.midp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;

public class CAudioRecorder {
    private static final int SAMPLERATE = 44100;
    private static final int REC_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    // !!! needs to be large enough for all devices
    private static final int MY_CHOSEN_BUFFER_SIZE = 8192;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    public boolean isRecording = false;
    public byte[] recordedAudioAsBytes;

    public void startRecording() {
        if (ActivityCompat.checkSelfPermission(MainActivity.mainActivity.getApplicationContext(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.mainActivity, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
            Log.i("ABC", "permission fail, returning.");
            return;
        }
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLERATE, REC_CHANNELS,
                ENCODING, MY_CHOSEN_BUFFER_SIZE);
        recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                Log.i("ABC", "the thread is running");
                writeAudioToLocalVariable();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }
    private void writeAudioToLocalVariable() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] temporaryChunkOfBytes = new byte[MY_CHOSEN_BUFFER_SIZE];
        int read = 0;
        while((read = recorder.read(temporaryChunkOfBytes, 0, MY_CHOSEN_BUFFER_SIZE)) >= 0){
            try {
                System.out.println("Appending to baos : " + temporaryChunkOfBytes);
                //printBytes(temporaryChunkOfBytes);
                baos.write(temporaryChunkOfBytes, 0, read);
            } catch (Exception e) {
                Log.i("ABC", "Exception while appending bytes : " + e); // <----- this is not called and that is good.
            }
        }
        recordedAudioAsBytes = baos.toByteArray();
    }

    public void stopRecording() {
        // stops the recording activity
        if (null != recorder) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }
    }
}
