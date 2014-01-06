package controllers.operations;

import java.io.File;
import java.io.FileFilter;

public class ModuleFileFilter implements FileFilter
{
	@Override
	public boolean accept(File path)
	{
		String name = path.getName();
		
		if (name.equals(".gitignore")) {
			return false;
		}
		
		if (name.equals("project") && path.isDirectory()) {
			return false;
		}
		
		if (name.equals("views") && path.isDirectory()) {
			return false;
		}
		
		return true;
	}

}
