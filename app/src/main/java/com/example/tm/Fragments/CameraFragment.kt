package com.example.tm.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.*
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.media.ImageReader
import android.os.Bundle
import android.os.Environment
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
import android.widget.Toast
import androidx.core.content.ContextCompat.getExternalFilesDirs
import androidx.core.content.ContextCompat.getSystemService
import com.example.tm.R
import com.example.tm.databinding.FragmentCameraBinding
import com.example.tm.databinding.FragmentDoneTasksBinding
import java.io.Reader

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"




class CameraFragment : Fragment() {


    lateinit var camManager:CameraManager
    lateinit var textureView:TextureView
    lateinit var camCaptureSession: CameraCaptureSession
    lateinit var camDevice: CameraDevice
    lateinit var captureRequestBuilder: CaptureRequest.Builder
    lateinit var captureRequest: CaptureRequest
    lateinit var handlerThread : HandlerThread
    lateinit var handler :Handler
    lateinit var binding:FragmentCameraBinding
    lateinit var imageReader: ImageReader
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root

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

        imageReader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG, 1)
        imageReader.setOnImageAvailableListener(object: ImageReader.OnImageAvailableListener {
            override fun onImageAvailable(reader: ImageReader?) {
                var image= reader?.acquireLatestImage()
                var buffer = image!!.planes[0].buffer
                var bytes= ByteArray(buffer.remaining())
                buffer.get(bytes)


            }

        }, handler)
        binding.takePhotoBtn.apply(){
            setOnClickListener{
                captureRequestBuilder=camDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
                captureRequestBuilder.addTarget(imageReader.surface)
                camCaptureSession.capture(captureRequestBuilder.build(), null, null)
            }

        }


    }
    @SuppressLint("MissingPermission")
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
                }, handler)

            }

            override fun onDisconnected(camera: CameraDevice) {
                TODO("Not yet implemented")
            }

            override fun onError(camera: CameraDevice, error: Int) {
                TODO("Not yet implemented")
            }

            }, handler)
        }


    }








