package com.mohaberabi.kmp.imagekompression

import androidx.compose.runtime.Composable
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerImageURL
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject
import kotlin.coroutines.resume

class IosImagePicker(
) : ImagePicker {

    override suspend fun pickUpImage(): PickedImage? {
        return suspendCancellableCoroutine { contintuation ->
            val picker = UIImagePickerController().apply {
                sourceType =
                    UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
                mediaTypes = listOf("public.image")
                allowsImageEditing = false
                delegate = PickerDelegate(
                    onPicked = { uiimage, mime ->
                        if (uiimage != null) {
                            ImageUtils(uiimage).toByteArray()
                                ?.let {
                                    contintuation.resume(PickedImage(it, mime))
                                } ?: run { contintuation.resume(null) }
                        } else {
                            contintuation.resume(null)
                        }
                    },
                )
            }
            UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
                picker,
                animated = true,
                completion = null
            )
        }
    }
}

internal class PickerDelegate(
    private val onPicked: (UIImage?, ImageType) -> Unit
) : NSObject(), UIImagePickerControllerDelegateProtocol,
    UINavigationControllerDelegateProtocol {

    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {

        picker.dismissModalViewControllerAnimated(true)
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
        val imageUrl = didFinishPickingMediaWithInfo[UIImagePickerControllerImageURL] as? NSURL
        val mimeType = imageUrl?.pathExtension?.toImageType() ?: ImageType.Jpeg
        onPicked(image, mimeType)

    }
}


@Composable
actual fun rememberImagePicker(): ImagePicker {
    return IosImagePicker()
}
