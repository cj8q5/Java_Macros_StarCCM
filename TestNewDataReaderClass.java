package myStarJavaMacros;

import java.io.IOException;

import star.common.StarMacro;
import starClasses.NewDataReader;

public class TestNewDataReaderClass extends StarMacro
{
	public void execute() 
	{
		String file2Read = "D:/Users/cj8q5/Desktop/Abaqus Default Work Directory/FSI_Input_File.txt";
		NewDataReader data = new NewDataReader();
		try 
		{
			data.readGeometryData(file2Read);
			//double plateLength = data.getDoubleData("plateLength");
			//int plateLengthNodes = data.getIntData("flPlLenNodes");
			//String abaqusModelName = data.getStringData("abaqusModelName");
		} 
		catch (NumberFormatException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
