package utils

import java.io.File

object FileUtils{
  def createPath(path: String){
    val directory = new java.io.File(path)
    try{
      if(!directory.exists){
        directory.getParentFile().mkdirs();
      }
    }
}

def writeToFile(path: String, content: String) {
    this.createPath(path)
    val file = new java.io.PrintWriter(new File(path))
    try {
      file.write(content)
    } finally {
      file.close()
    }
  }
}