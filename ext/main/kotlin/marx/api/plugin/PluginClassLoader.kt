package marx.api.plugin

import mu.KotlinLogging
import org.slf4j.Logger
import java.io.File
import java.io.FileFilter
import java.net.MalformedURLException
import java.net.URL
import java.net.URLClassLoader


/**
 * This is used to sandbox an instance and only load the specified classes at runtime.
 */

/**
 *
 * @author Ricky Fung
 * @create 2016-11-12 14:27
 */
class PluginClassLoader(jar: File, parent: ClassLoader?) {
    private val logger: Logger = KotlinLogging.logger { }
    private var classLoader: URLClassLoader

    init {
        classLoader = createClassLoader(jar, parent)
    }

    constructor(jar: String) : this(File(jar), null) {}
    constructor(jar: File) : this(jar, null) {}

    /**
     * This will add the files from the given [baseDir] to the [classLoader]
     */
    fun addToClassLoader(
        baseDir: String, filter: FileFilter?,
        quiet: Boolean
    ) {
        val base = File(baseDir)
        if (base.exists() && base.isDirectory) {
            val files: Array<File>? = base.listFiles(filter)
            if (files?.isEmpty() == true) {
                if (!quiet) {
                    logger.error(
                        "No files added to classloader from lib: "
                                + baseDir + " (resolved as: "
                                + base.absolutePath + ")."
                    )
                }
            } else {
                classLoader = replaceClassLoader(classLoader, base, filter)
            }
        } else {
            if (!quiet) {
                logger.error(
                    ("Can't find (or read) directory to add to classloader: "
                            + baseDir
                            + " (resolved as: "
                            + base.absolutePath
                            + ").")
                )
            }
        }
    }

    @Throws(ClassNotFoundException::class)
    fun loadClass(className: String?): Class<*> {
        return classLoader.loadClass(className)
    }

    /**
     * This takes the old class loader and replaces making sure to copy over all of the urls
     */
    private fun replaceClassLoader(
        oldLoader: URLClassLoader,
        base: File?, filter: FileFilter?
    ): URLClassLoader {
        if ((null != base) && base.canRead() && base.isDirectory) {
            val files = base.listFiles(filter)
            if (null == files || files.isEmpty()) {
                logger.error("replaceClassLoader base dir:{} is empty", base.absolutePath)
                return oldLoader
            }
            logger.error("replaceClassLoader base dir: {} ,size: {}", base.absolutePath, files.size)
            val oldElements: Array<URL> = oldLoader.urLs
            val elements: Array<URL?> = arrayOfNulls(oldElements.size + files.size)
            System.arraycopy(oldElements, 0, elements, 0, oldElements.size)
            for (j in files.indices) {
                try {
                    val element: URL = files[j].toURI().normalize().toURL()
                    elements[oldElements.size + j] = element
                    logger.info("Adding '{}' to classloader", element.toString())
                } catch (e: MalformedURLException) {
                    logger.error("load jar file error", e)
                }
            }
            val oldParent = oldLoader.parent
            return URLClassLoader.newInstance(elements, oldParent)
        }
        return oldLoader
    }

    /**
     * This will create a new url class loader that can be used for loading classes to their own instances and other
     * things.
     */
    private fun createClassLoader(
        libDir: File,
        parent: ClassLoader? = null
    ): URLClassLoader =
        replaceClassLoader(
            URLClassLoader.newInstance(arrayOfNulls(0), parent ?: Thread.currentThread().contextClassLoader),
            libDir,
            null
        )



}