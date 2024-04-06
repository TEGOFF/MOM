package com.example.tm.Fragments

import android.annotation.SuppressLint
import android.content.Context.*
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.tm.R
import com.example.tm.databinding.FragmentCameraBinding
import ModulesAndAdapters.FireHelper
import java.io.File
import java.io.FileOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"




class CameraFragment : Fragment() {

    lateinit var navControl: NavController
    lateinit var camManager:CameraManager
    lateinit var textureView:TextureView
    lateinit var camCaptureSession: CameraCaptureSession
    lateinit var camDevice: CameraDevice
    lateinit var captureRequestBuilder: CaptureRequest.Builder
    lateinit var handlerThread : HandlerThread
    lateinit var handler :Handler
    lateinit var binding:FragmentCameraBinding
    lateinit var imageReader: ImageReader
    var tempFile: File? = null
    var fos: FileOutputStream? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navControl = Navigation.findNavController(view)

        textureView = requireView().findViewById(R.id.cameraTextureView)
        camManager = requireContext().getSystemService(CAMERA_SERVICE) as CameraManager
        handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler((handlerThread).looper)
        imageReader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG, 2)
        var imreadersurf = imageReader.surface
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                openCam()

            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {

            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {

                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

            }
        }

            binding.takePhotoBtn.setOnClickListener {
                captureRequestBuilder = camDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
                captureRequestBuilder.addTarget(imreadersurf)
                camCaptureSession.capture(captureRequestBuilder.build(), null, null)
                navControl.navigate(R.id.action_cameraFragment_to_settingsFragment)
            }


            imageReader.setOnImageAvailableListener({ reader ->
                val tempDir = context?.cacheDir
                tempFile = File.createTempFile("temp_image", ".jpg", tempDir)
                val image = reader?.acquireLatestImage()
                val buffer = image!!.planes[0].buffer
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes)
                fos = FileOutputStream(tempFile)
                fos!!.write(bytes)
                FireHelper.storeImage(tempFile?.toUri(), requireContext())
                image.close()
                fos!!.flush()
                fos!!.close()


            }, handler)



    }


        @SuppressLint("MissingPermission")
        fun openCam() {

            val cameraId = camManager.cameraIdList[0]
            camManager.openCamera(cameraId, object : CameraDevice.StateCallback() {

                override fun onOpened(camera: CameraDevice) {
                    camDevice = camera

                    captureRequestBuilder =
                        camDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                    val surface = Surface(textureView.surfaceTexture)
                    val listofsurf = listOf(surface, imageReader.surface)
                    captureRequestBuilder.addTarget(surface)
                    camDevice.createCaptureSession(listofsurf, object :
                        CameraCaptureSession.StateCallback() {
                        override fun onConfigured(session: CameraCaptureSession) {
                            camCaptureSession = session
                            camCaptureSession.setRepeatingRequest(
                                captureRequestBuilder.build(),
                                null,
                                null
                            )
                        }

                        override fun onConfigureFailed(session: CameraCaptureSession) {
                            TODO("Not yet implemented")
                        }
                    }, handler)

                }

                override fun onDisconnected(camera: CameraDevice) {
                    camera.close()
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    TODO("Not yet implemented")
                }

            }, handler)
        }

    }









