package com.example.tm.Fragments

import android.content.Context
import android.content.Context.*
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.speech.tts.TextToSpeech
import android.view.InputDevice
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import com.example.tm.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private fun CameraManager.openCamera(cameraId: String?, stateCallback: CameraDevice.StateCallback) {

}


class CameraFragment : Fragment() {


    lateinit var camManager:CameraManager
    lateinit var textureView:TextureView
    lateinit var camCaptureSession: CameraCaptureSession
    lateinit var camDevice: CameraDevice
    lateinit var captureRequestBuilder: CaptureRequest.Builder
    lateinit var captureRequest: CaptureRequest
    lateinit var handlerThread : HandlerThread
    lateinit var handler :Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textureView = requireView().findViewById(R.id.cameraTextureView)
        camManager= requireContext().getSystemService(CAMERA_SERVICE) as CameraManager
        handlerThread= HandlerThread("videoThread")
        handlerThread.start()
        handler=Handler((handlerThread).looper)

        textureView.surfaceTextureListener=object:TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                openCam()

            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {

            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {

                return TODO("Provide the return value")
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

            }
        }
    }
    fun openCam(){

        val cameraId=camManager.cameraIdList[0]
        camManager.openCamera(cameraId, object: CameraDevice.StateCallback(){

            override fun onOpened(camera: CameraDevice) {
                camDevice=camera

                captureRequestBuilder= camDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                var surface =Surface(textureView.surfaceTexture)
                captureRequestBuilder.addTarget(surface)
                camDevice.createCaptureSession(listOf(surface), object:
                    CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        camCaptureSession = session
                        camCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null)
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        TODO("Not yet implemented")
                    }
                })

            }

            override fun onDisconnected(camera: CameraDevice) {
                TODO("Not yet implemented")
            }

            override fun onError(camera: CameraDevice, error: Int) {
                TODO("Not yet implemented")
            }

            })
        }


    }



private fun CameraDevice.createCaptureSession(
    listOf: List<Surface>,
    stateCallback: CameraCaptureSession.StateCallback
) {

}




