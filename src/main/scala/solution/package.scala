import java.io.File

package object solution {

  /** A monad class to check file existence */
  //https://stackoverflow.com/questions/30015165/scala-delete-file-if-exist-the-scala-way
  implicit class FileMonads(f: File) {
    def check = if (f.exists) Option(f) else None
    def remove = if (f.delete()) Option(f) else None
  }

}
