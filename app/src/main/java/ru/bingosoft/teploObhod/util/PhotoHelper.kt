package ru.bingosoft.teploObhod.util

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import ru.bingosoft.teploObhod.models.Models
import ru.bingosoft.teploObhod.ui.mainactivity.MainActivity
import ru.bingosoft.teploObhod.util.Const.RequestCodes.PHOTO
import timber.log.Timber
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


class PhotoHelper {
    lateinit var parentFragment: Fragment
    var lastPhotoFileName = ""

    /**
     * Метод для создания фото и сохранения ее в файл и БД
     *
     */
    fun createPhoto(dirName: String, step: Models.TemplateControl) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        var uri: Uri? = null
        try {
            val photoFile = createImageFile("$dirName/${step.guid}")
            Timber.d("photoFile=${photoFile.absolutePath}")
            (parentFragment.requireActivity() as MainActivity).lastKnownFilenamePhoto =
                photoFile.absolutePath

            uri = FileProvider.getUriForFile(
                parentFragment.requireContext(), "${parentFragment.context?.packageName}.provider",
                photoFile
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            parentFragment.requireActivity().startActivityForResult(intent, PHOTO)


        } catch (ex: IOException) {
            Timber.d("Ошибка createImageFile " + ex.message)
        }
    }

    /**
     * Метод для создание структуры папок для фотографии и самого файла для фото
     *
     * @return - возвращается файл
     * @throws IOException - метод может вызвать исключение
     */
    @Throws(IOException::class)
    private fun createImageFile(dirname: String): File {
        // Имя для папки с файлами PhotoForApp/+<id_заявки>. Если потребуется делать фотки Захоронений и Памятников, в папке с местом захоронения создадим еще 2 папки
        val stDir = "PhotoForApp/$dirname" //+Integer.toString(inSector)+"."+Integer

        Timber.d("папка с фото $stDir")

        // Создадим имя для файла с картинкой
        val timeStamp = SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale("ru", "RU")).format(Date())

        val imageFileName = "JPEG_ $timeStamp" + "_"
        val storageDir =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), stDir)

        if (!storageDir.exists()) {
            Timber.d("создадим папку")
            storageDir.mkdirs() // Создадим сразу все необходимые каталоги
        }


        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )

        return image
    }

    /**
     * Метод который создает zip архив
     */
    @Throws(IOException::class)
    private fun zip(directory: File, base: File, zos: ZipOutputStream) {
        val files = directory.listFiles()
        val buffer = ByteArray(1024)
        if (files != null) {
            for (file in files) {
                if (file.isDirectory) {
                    zip(file, base, zos)
                } else {
                    val fis = FileInputStream(file)
                    val entry = ZipEntry(
                        file.path.substring(
                            base.path.length + 1
                        )
                    )
                    zos.putNextEntry(entry)
                    while (true) {
                        val readBytes = fis.read(buffer)
                        if (readBytes == -1) {
                            break
                        }
                        zos.write(buffer, 0, readBytes)
                    }
                    fis.close()
                }
            }
        }
    }

    /**
     * Метод, который создает архив из переданной ему папки
     *
     */
    fun prepareZip(syncDirs: List<String>): File? {
        Timber.d("prepareZip=$syncDirs")

        // Создадим папку для архива
        val zipDir =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/zipDir")
        if (!zipDir.exists()) {
            zipDir.mkdirs()
        }

        // Копируем в нее нужные файлы с фото
        syncDirs.forEach {
            Timber.d("dirSources=$it")
            val dirSources =
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/PhotoForApp/$it")
            // Почистим папку от нулевых файлов
            if (dirSources.isDirectory) {
                dirSources.listFiles()?.forEach { file ->
                    if (file.length() == 0L) {
                        file.delete()
                        Timber.d("Удалили ${file.name}")
                    }
                }
            }

            val dirTarget =
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/zipDir/$it")
            copyDir(dirSources, dirTarget)
        }


        // Создадим архив из папки с фото
        val storageDir =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/zipDir")
        val fileZip =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/zipDir.zip")

        // Проверим может архив уже есть
        if (fileZip.exists()) {
            return fileZip
        } else {
            if (storageDir.exists()) {
                val zos: ZipOutputStream
                try {
                    zos = ZipOutputStream(FileOutputStream(fileZip))
                    try {
                        zip(storageDir, storageDir, zos)
                        zos.close()

                        // Удалим папку zipDir с фото
                        deleteRecursive(storageDir)

                        return fileZip
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            } else {
                return null
            }
        }

        return null
    }

    /**
     * Метод для удаления папки с фото
     */
    private fun deleteRecursive(fileOrDirectory: File) {

        if (fileOrDirectory.isDirectory) {
            if (!fileOrDirectory.listFiles().isNullOrEmpty()) {
                fileOrDirectory.listFiles()!!.forEach {
                    deleteRecursive(it)
                }
            }
        }

        fileOrDirectory.delete()
    }

    /**
     * Метод копирует папки с фото
     */
    @Throws(IOException::class)
    fun copyDir(sourceLocation: File, targetLocation: File) {
        if (sourceLocation.isDirectory) {
            Timber.d("targetLocation=${targetLocation.path}")
            if (!targetLocation.exists()) {
                Timber.d("создаем")
                targetLocation.mkdirs()
            }
            val children = sourceLocation.list()
            if (children != null && children.isNotEmpty()) {
                children.forEach {
                    copyDir(
                        File(sourceLocation, it),
                        File(targetLocation, it)
                    )
                }
            }

        } else {
            val inputStream = FileInputStream(sourceLocation)
            val outputStream = FileOutputStream(targetLocation)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) {
                outputStream.write(buf, 0, len)
            }
            inputStream.close()
            outputStream.close()
        }
    }

    fun checkDirAndEmpty(dirName: String): Boolean {
        val file =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/PhotoForApp/$dirName")
        if (file.exists() && file.isDirectory && !file.listFiles().isNullOrEmpty()) {
            return true
        }
        return false
    }
}