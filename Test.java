package myStarJavaMacros;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JOptionPane;

public class Test 
{
	double[] m_timeStep;
	public void Main()
	{
		FileReader file;
		try 
		{
			file = new FileReader("D:/Users/cj8q5/Simulations/Current Simulation/Abaqus/TimeSteps.txt");
			BufferedReader reader = new BufferedReader(file);
			
			String line;
			int i = 0;
			while((line=reader.readLine()) != null)
			{
				if(line.startsWith("*"))
				{
					continue;
				}
				else
				{
					 m_timeStep[i] = Double.parseDouble(line);
				}
				i = i++;
			}
			reader.close();
		} 
		catch (FileNotFoundException e) 
		{
			JOptionPane.showMessageDialog(null, e.toString());
		} 
		catch (NumberFormatException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
