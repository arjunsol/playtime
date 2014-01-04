package controllers.operations

import java.io.{File,FileInputStream,FileOutputStream}
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

def copyPath(srcPath: String, dstPath: String) {
	//this.createPath(dstPath)
    val src = new File(srcPath)
    val dst = new File(dstPath)
	FileUtils.copyDirectory(src, dst)
}
}