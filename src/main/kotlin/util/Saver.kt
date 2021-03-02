package util

import java.io.*
import java.nio.file.Files

class Saver {
    companion object{
        @Throws(IOException::class)
        fun write(data: ByteArray?, filename: String?) {
            val fos = FileOutputStream(filename)
            fos.write(data)
            fos.close()
        }

        @Throws(IOException::class)
        fun readBytes(filename: String?): ByteArray? {
            return Files.readAllBytes(File(filename).toPath())
        }

        fun restored(input: ByteArray?): Any? {
            var o: Any? = null
            val bis = ByteArrayInputStream(input)
            var `in`: ObjectInput? = null
            try {
                `in` = ObjectInputStream(bis)
                o = `in`.readObject()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } finally {
                try {
                    `in`?.close()
                } catch (ex: IOException) {
                }
            }
            return o
        }

        fun savedToBLOB(input: Any?): ByteArray? {
            var Result: ByteArray? = null
            val bos = ByteArrayOutputStream()
            var out: ObjectOutput? = null
            try {
                out = ObjectOutputStream(bos)
                out.writeObject(input)
                out.flush()
                Result = bos.toByteArray()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    bos.close()
                } catch (ex: IOException) {
                }
            }
            return Result
        }

    }
}