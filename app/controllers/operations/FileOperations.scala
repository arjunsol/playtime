package controllers.operations

import java.io.{File,FileInputStream,FileOutputStream, FileFilter}
import org.apache.commons.io.FileUtils

object FileOperations{
  
  def createPath(path: String){
    val dir = new java.io.File(path)
    try{
      if(!dir.exists){
        dir.getParentFile().mkdirs();
      }
    }
  }

  def writeToFile(path: String, file: String) {
    this.createPath(path)
    val outFile = new java.io.PrintWriter(new File(path))
    try {
      outFile.write(file)
    } finally {
      outFile.close()
    }
  }
  
  def deleteFile(path: String) {
    val file = new File(path)
    file.delete()
  }

  def copyPath(srcPath: String, dstPath: String, filter: FileFilter) {
    val src = new File(srcPath)
    val dst = new File(dstPath)
	FileUtils.copyDirectory(src, dst, filter)
  }

  def renameFile(path: String, from: String, to: String) {
	val fileFrom = new File(path + "\\" + from);
	val fileTo = new File(path + "\\" + to);
	fileFrom.renameTo(fileTo)
  }

}