package myStarJavaMacros;

import star.base.report.AreaAverageReport;
import star.base.report.MaxReport;
import star.base.report.MinReport;
import star.common.FieldFunction;
import star.common.ImplicitUnsteadyModel;
import star.common.PrimitiveFieldFunction;
import star.common.Simulation;
import star.common.StarMacro;
import star.common.PhysicsContinuum;
import star.common.VectorMagnitudeFieldFunction;
import star.common.XYPlot;
import star.flow.ConstantDensityModel;
import star.keturb.KEpsilonTurbulence;
import star.keturb.KeTwoLayerAllYplusWallTreatment;
import star.keturb.RkeTwoLayerTurbModel;
import star.material.SingleComponentLiquidModel;
import star.metrics.ThreeDimensionalModel;
import star.segregatedflow.SegregatedFlowModel;
import star.turbulence.RansTurbulenceModel;
import star.turbulence.TurbulentModel;
import star.vis.LinePart;
import star.vis.Scene;
import starClasses.ContiuumBuilder;
import starClasses.DataReader;
import starClasses.DerivedParts;
import starClasses.DirectedMesher7_09;
import starClasses.FieldFunctions;
import starClasses.GeoData;
import starClasses.GeometryBuilder;
import starClasses.MeshElementData;
import starClasses.MeshSpacingData;
import starClasses.RegionBuilder;
import starClasses.ReportsMonitorsPlots;
import starClasses.Scenes;
import starClasses.SolversNode;
import starClasses.StoppingCriteria;

public class FlatPlateTest2 extends StarMacro 
{

	public void execute() 
	{
		String folder = "D:/users/cj8q5/desktop/Data/";
		String sketchPlane = "YZ";
		Simulation activeSim = getActiveSimulation();
		
		// Reading in the geometry parameters from the external file
		DataReader reader = new DataReader();
		reader.readGeometryData(folder + "Plate_Geometry_Input.txt");
		GeoData geoData = reader.getGeoDetails();
		
		// Grabbing the geometry parameters from the GeoData object and converting to metric
		double plateLength = geoData.getPlateLength()*0.0254;
		double plateHeight = geoData.getPlateHeight()*0.0254;
		double testSectionWidth = geoData.getPlateWidth()*0.0254;
		double smallChannelHeight = geoData.getSmallChannelHeight()*0.0254;
		double largeChannelHeight = geoData.getLargeChannelHeight()*0.0254;
		double inletLength = geoData.getInletLength()*0.0254;
		double outletLength = geoData.getOutletLength()*0.0254;
		
		double[] initialVel = {0.0, -9.0, 0.0};
		double inletVel = Math.abs((smallChannelHeight + largeChannelHeight)/(smallChannelHeight + largeChannelHeight + plateHeight)*initialVel[1]);
				
		// Specifying the geometry parameters for each part
		double extrude = testSectionWidth; // Value for the length of all extrudes
		
		double[] yInletLarge = {plateLength, (plateLength + inletLength)};
		double[] zInletLarge = {plateHeight, (largeChannelHeight + plateHeight)};
		
		double[] yInletPlate = {plateLength, (plateLength + inletLength)};
		double[] zInletPlate = {0, plateHeight};
		
		double[] yInletSmall = {plateLength, (plateLength + inletLength)};
		double[] zInletSmall = {-smallChannelHeight, 0};

		double[] ySmall = {0, plateLength};
		double[] zSmall = {-smallChannelHeight, 0};
		
		double[] yLarge = {0, plateLength};
		double[] zLarge = {plateHeight, (largeChannelHeight + plateHeight)};
		
		double[] yOutletLarge = {-outletLength, 0};
		double[] zOutletLarge = {plateHeight, (largeChannelHeight + plateHeight)};
		
		double[] yOutletPlate = {-outletLength, 0};
		double[] zOutletPlate = {0, plateHeight};
		
		double[] yOutletSmall = {-outletLength, 0};
		double[] zOutletSmall = {-smallChannelHeight, 0};
		
		// Specifying the mesh element parameters for each part
		reader.readMeshElementData(folder + "Mesh_Element_Input.txt");
		MeshElementData meshElementData = reader.getMeshElementDetails();
		
		int extrudeElements = meshElementData.getExtrudeCell();
		
		int inletXelements = meshElementData.getInletX();
		int inletLargeYelements = meshElementData.getLargeInletY();
		int inletPlateYelements = meshElementData.getPlateInletY();
		int inletSmallYelements = meshElementData.getSmallInletY();
		
		int outletXelements = meshElementData.getOutletX();
		int outletLargeYelements = meshElementData.getLargeOutletY();
		int outletPlateYelements = meshElementData.getPlateOutletY();
		int outletSmallYelements = meshElementData.getSmallOutletY();
		
		int smallChannelXelements = meshElementData.getSmChannelX();
		int smallChannelYelements = meshElementData.getSmChannelY();
		int largeChannelXelements = meshElementData.getLgChannelX();
		int largeChannelYelements = meshElementData.getLgChannelY();
		
		// Specifying the mesh spacing parameters for each part
		reader.readMeshSpacingData(folder + "Mesh_Spacing_Input.txt");
		MeshSpacingData meshSpacingData = reader.getMeshSpacingDetails();
		
		double inletSpacingX = meshSpacingData.getInletSpacingX();
		double inletSpacingY = meshSpacingData.getInletSpacingY();
		
		double smallChannelSpacingX = meshSpacingData.getSmChannelSpacingX();
		double smallChannelSpacingY = meshSpacingData.getSmChannelSpacingY();
		
		double largeChannelSpacingX = meshSpacingData.getLgChannelSpacingX();
		double largeChannelSpacingY = meshSpacingData.getLgChannelSpacingY();
				
		double outletSpacingX = meshSpacingData.getOutletSpacingX();
		double outletSpacingY = meshSpacingData.getOutletSpacingY();
		
		// Names of the parts
		String inletLargeName = "InletPlenumLarge";
		String inletPlateName = "InletPlenumPlate";
		String inletSmallName = "InletPlenumSmall";
		String smallChannelName = "SmallChannel";
		String largeChannelName = "LargeChannel";
		String outletLargeName = "OutletPlenumLarge";
		String outletPlateName = "OutletPlenumPlate";
		String outletSmallName = "OutletPlenumSmall";
		String[] partNames = {"InletPlenumLarge", "InletPlenumPlate", "InletPlenumSmall", "SmallChannel",
				"LargeChannel", "OutletPlenumLarge", "OutletPlenumPlate", "OutletPlenumSmall"};
		
		
		// Names of the surfaces of each part
		String[] inletLargeSurfaceNames = {"InletLarge_LargeChannel-Interface", "InletLarge_Front", "InletLarge_Inlet", 
				"InletLarge_InletPlate-Interface", "InletLarge_Right", "InletLarge_Left"};
		
		String[] inletPlateSurfaceNames = {"InletPlate_FSI", "InletPlate_InletLarge-Interface", "InletPlate_Inlet", 
				"InletPlate_InletSmall-Interface", "InletPlate_Right", "InletPlate_Left"};
		
		String[] inletSmallSurfaceNames = {"InletSmall_SmallChannel-Interface", "InletSmall_InletPlate-Interface", "InletSmall_Inlet", 
				"InletSmall_Back", "InletSmall_Right", "InletSmall_Left"};
		
		String[] smallChannelSurfaceNames = {"SmallChannel_OutletSmall-Interface", "SmallChannel_FSI", "SmallChannel_InletSmall-Interface", 
				"SmallChannel_Back", "SmallChannel_Right", "SmallChannel_Left"};
		
		String[] largeChannelSurfaceNames = {"LargeChannel_OutletLarge-Interface", "LargeChannel_Front", "LargeChannel_InletLarge-Interface", 
				"LargeChannel_FSI", "LargeChannel_Right", "LargeChannel_Left"};
		
		String[] outletLargeSurfaceNames = {"OutletLarge_Outlet", "OutletLarge_Front", "OutletLarge_LargeChannel-Interface", 
				"OutletLarge_OutletPlate-Interface", "OutletLarge_Right", "OutletLarge_Left"};
		
		String[] outletPlateSurfaceNames = {"OutletPlate_Outlet", "OutletPlate_OutletLarge-Interface", "OutletPlate_FSI", 
				"OutletPlate_OutletSmall-Interface", "OutletPlate_Right","OutletPlate_Left"};
		
		String[] outletSmallSurfaceNames = {"OutletSmall_Outlet", "OutletSmall_OutletPlate-Interface", "OutletSmall_SmallChannel-Interface",
				"OutletSmall_Back", "OutletSmall_Right", "OutletSmall_Left"};
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			GEOMETRY NODE */
		// Building the parts
		InnerClass.partBuilder(activeSim, extrude, yInletLarge, zInletLarge, sketchPlane, inletLargeName, inletLargeSurfaceNames);
		InnerClass.partBuilder(activeSim, extrude, yInletPlate, zInletPlate, sketchPlane, inletPlateName, inletPlateSurfaceNames);
		InnerClass.partBuilder(activeSim, extrude, yInletSmall, zInletSmall, sketchPlane, inletSmallName, inletSmallSurfaceNames);
		
		InnerClass.partBuilder(activeSim, extrude, yLarge, zLarge, sketchPlane, largeChannelName, largeChannelSurfaceNames);
		InnerClass.partBuilder(activeSim, extrude, ySmall, zSmall, sketchPlane, smallChannelName, smallChannelSurfaceNames);
		
		InnerClass.partBuilder(activeSim, extrude, yOutletLarge, zOutletLarge, sketchPlane, outletLargeName, outletLargeSurfaceNames);
		InnerClass.partBuilder(activeSim, extrude, yOutletPlate, zOutletPlate, sketchPlane, outletPlateName, outletPlateSurfaceNames);
		InnerClass.partBuilder(activeSim, extrude, yOutletSmall, zOutletSmall, sketchPlane, outletSmallName, outletSmallSurfaceNames);
		
		// Meshing the parts
		InnerClass.partMesher(activeSim, inletLargeName, inletLargeSurfaceNames, inletXelements, inletLargeYelements, extrudeElements, 
				inletSpacingX, inletSpacingY, "Two", "Two", true, false);
		InnerClass.partMesher(activeSim, inletPlateName, inletPlateSurfaceNames,inletXelements, inletPlateYelements, extrudeElements, 
				inletSpacingX, inletSpacingY, "Two", "Constant", false, false);
		InnerClass.partMesher(activeSim, inletSmallName, inletSmallSurfaceNames,inletXelements, inletSmallYelements, extrudeElements, 
				inletSpacingX, inletSpacingY, "Two", "Two", true, true);
		
		InnerClass.partMesher(activeSim, largeChannelName, largeChannelSurfaceNames,largeChannelXelements, largeChannelYelements, extrudeElements, 
				largeChannelSpacingX, largeChannelSpacingY, "Two", "Two", true, false);
		InnerClass.partMesher(activeSim, smallChannelName, smallChannelSurfaceNames,smallChannelXelements, smallChannelYelements, extrudeElements, 
				smallChannelSpacingX, smallChannelSpacingY, "Two", "Two", true, false);
		
		InnerClass.partMesher(activeSim, outletLargeName, outletLargeSurfaceNames,outletXelements, outletLargeYelements, extrudeElements, 
				outletSpacingX, outletSpacingY, "One", "Two", true, false);
		InnerClass.partMesher(activeSim, outletPlateName, outletPlateSurfaceNames,outletXelements, outletPlateYelements, extrudeElements, 
				outletSpacingX, outletSpacingY, "One", "Constant", true, false);
		InnerClass.partMesher(activeSim, outletSmallName, outletSmallSurfaceNames,outletXelements, outletSmallYelements, extrudeElements, 
				outletSpacingX, outletSpacingY, "One", "Two", true, true);
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			REGION NODE */
		// Creating the fluid region
		RegionBuilder fluidRegion = new RegionBuilder(activeSim);
		//fluidRegion.parts2Region(activeSim, partNames, "Fluid");
		
		String[] regionNames_0 = {"InletPlenumLarge", "InletPlenumPlate"};
		String[] boundaryNames_0 = {"InletLarge_InletPlate-Interface", "InletPlate_InletLarge-Interface"};
		String interactionName_0 = "InletLarge_InletPlate-Interaction";
		String[] regionNames_1 = {"InletPlenumPlate", "InletPlenumSmall"};
		String[] boundaryNames_1 = {"InletPlate_InletSmall-Interface", "InletSmall_InletPlate-Interface"};
		String interactionName_1 = "InletPlate_InletSmall-Interaction";
		String[] regionNames_2 = {"InletPlenumLarge", "LargeChannel"};
		String[] boundaryNames_2 = {"InletLarge_LargeChannel-Interface", "LargeChannel_InletLarge-Interface"};
		String interactionName_2 = "InletLarge_LargeChannel-Interaction";
		String[] regionNames_3 = {"InletPlenumSmall", "SmallChannel"};
		String[] boundaryNames_3 = {"InletSmall_SmallChannel-Interface", "SmallChannel_InletSmall-Interface"};
		String interactionName_3 = "InletSmall_SmallChannel-Interaction";
		String[] regionNames_4 = {"OutletPlenumLarge", "OutletPlenumPlate"};
		String[] boundaryNames_4 = {"OutletLarge_OutletPlate-Interface", "OutletPlate_OutletLarge-Interface"};
		String interactionName_4 = "OutletLarge_OutletPlate-Interaction";
		String[] regionNames_5 = {"OutletPlenumPlate", "OutletPlenumSmall"};
		String[] boundaryNames_5 = {"OutletPlate_OutletSmall-Interface", "OutletSmall_OutletPlate-Interface"};
		String interactionName_5 = "OutletPlate_OutletSmall-Interaction";
		String[] regionNames_6 = {"OutletPlenumLarge", "LargeChannel"};
		String[] boundaryNames_6 = {"OutletLarge_LargeChannel-Interface", "LargeChannel_OutletLarge-Interface"};
		String interactionName_6 = "OutletLarge_LargeChannel-Interaction";
		String[] regionNames_7 = {"OutletPlenumSmall", "SmallChannel"};
		String[] boundaryNames_7 = {"OutletSmall_SmallChannel-Interface", "SmallChannel_OutletSmall-Interface"};
		String interactionName_7 = "OutletSmall_SmallChannel-Interaction";
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			INTERFACES NODE */
		fluidRegion.createInterface(regionNames_0, boundaryNames_0, interactionName_0);
		fluidRegion.createInterface(regionNames_1, boundaryNames_1, interactionName_1);
		fluidRegion.createInterface(regionNames_2, boundaryNames_2, interactionName_2);
		fluidRegion.createInterface(regionNames_3, boundaryNames_3, interactionName_3);
		fluidRegion.createInterface(regionNames_4, boundaryNames_4, interactionName_4);
		fluidRegion.createInterface(regionNames_5, boundaryNames_5, interactionName_5);
		fluidRegion.createInterface(regionNames_6, boundaryNames_6, interactionName_6);
		fluidRegion.createInterface(regionNames_7, boundaryNames_7, interactionName_7);
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			PHYSICS NODE */
		// Creating the physics continuum
		ContiuumBuilder physics = new ContiuumBuilder(activeSim);
		//physics.createPhysicsContinua(activeSim, "Water");
		
		PhysicsContinuum fluidPhysics = ((PhysicsContinuum) activeSim.getContinuumManager().getContinuum("Physics 1"));
		fluidPhysics.enable(ThreeDimensionalModel.class);
		fluidPhysics.enable(ImplicitUnsteadyModel.class);
		fluidPhysics.enable(SingleComponentLiquidModel.class);
		fluidPhysics.enable(SegregatedFlowModel.class);
		fluidPhysics.enable(ConstantDensityModel.class);
		fluidPhysics.enable(TurbulentModel.class);
		fluidPhysics.enable(RansTurbulenceModel.class);
		fluidPhysics.enable(KEpsilonTurbulence.class);
		fluidPhysics.enable(RkeTwoLayerTurbModel.class);
		fluidPhysics.enable(KeTwoLayerAllYplusWallTreatment.class);
		
		physics.setInitialConditionsVel("Physics 1", initialVel);
		
		// Setting the inlet surfaces/boundaries to velocity inlets and setting the inlet velocity
		String[] inletRegions = {"InletPlenumPlate", "InletPlenumSmall", "InletPlenumLarge"};
		String[] inletBoundaries = {"InletPlate_Inlet", "InletSmall_Inlet", "InletLarge_Inlet"};
		for (int i = 0; i < inletRegions.length; i++)
		{
			fluidRegion.setBoundaryCondition(inletRegions[i], inletBoundaries[i], "Velocity Inlet", inletVel);
		}
		
		// Setting the outlet surfaces/boundaries to pressure outlets
		String[] outletRegions = {"OutletPlenumPlate", "OutletPlenumSmall", "OutletPlenumLarge"};
		String[] outletBoundaries = {"OutletPlate_Outlet","OutletSmall_Outlet", "OutletLarge_Outlet"};
		for (int i = 0; i < outletRegions.length; i++)
		{
			fluidRegion.setBoundaryCondition(outletRegions[i], outletBoundaries[i], "Pressure Outlet", 0);
		}
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			DERIVED PARTS NODE */
		double[] planeDirectionVector = {1, 0, 0};
		double[] planeLocationVector = {(testSectionWidth*0.5) + 0.0127, 0, 0};
		DerivedParts planeSection = new DerivedParts(activeSim, partNames);
		planeSection.createSectionPlane(planeDirectionVector, planeLocationVector, "CenterPlane");
		
		DerivedParts lineProbe = new DerivedParts(activeSim, partNames);
		double[] coordinate0_LP1 = {testSectionWidth*0.5 + 0.5*0.0254, inletLength + plateLength, -smallChannelHeight*0.5};
		double[] coordinate1_LP1 = {testSectionWidth*0.5 + 0.5*0.0254, -outletLength, -smallChannelHeight*0.5};
		LinePart smChannelProbe = lineProbe.createLineProbe(coordinate0_LP1, coordinate1_LP1, 500, "SmallChannel_LineProbe");
		
		double[] coordinate0_LP2 = {testSectionWidth*0.5 + 0.5*0.0254, inletLength + plateLength, plateHeight + largeChannelHeight*0.5};
		double[] coordinate1_LP2 = {testSectionWidth*0.5 + 0.5*0.0254, -outletLength, plateHeight + largeChannelHeight*0.5};
		LinePart lgChannelProbe = lineProbe.createLineProbe(coordinate0_LP2, coordinate1_LP2, 500, "LargeChannel_LineProbe");
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			SOLVERS NODE */
		SolversNode solvers = new SolversNode(activeSim);
		solvers.setKepsilonRelax(0.6);
		solvers.setUnsteadyTimeStep(1.0, 2);
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			REPORTS, MONITORS, AND PLOTS NODES */
		String[] objectPath_0 = {"LargeChannel", "LargeChannel_FSI"};
		String[] objectPath_1 = {"SmallChannel", "SmallChannel_FSI"};
		
		String[] reportLargeNames = {"MinPressure_LargeCh", "MaxPressure_LargeCh", "AveragePressure_LargeCh"};
		String[] reportSmallNames = {"MinPressure_SmallCh", "MaxPressure_SmallCh", "AveragePressure_SmallCh"};
		String[] axesTitles = {"Iteration", "Static Pressure (Pa)"};
		String[] plotTitles = {"Min, max, and Avg. Pressure - Large Channel", "Min, max, and Avg. Pressure - Small Channel"};
		
		String[] reportLargeNamesStress = {"MinStress_LargeCh", "MaxStress_LargeCh", "AverageStress_LargeCh"};
		String[] reportSmallNamesStress = {"MinStress_SmallCh", "MaxStress_SmallCh", "AverageStress_SmallCh"};
		String[] axesTitlesStress = {"Iteration", "Wall Shear Stress (Pa)"};
		String[] plotTitlesStress = {"Min, max, and Avg. Wall Shear Stress - Large Channel", "Min, max, and Avg. Wall Shear Stress - Small Channel"};
		
		FieldFunctions fieldFunction = new FieldFunctions(activeSim);
		PrimitiveFieldFunction staticPressure = fieldFunction.getFieldFunctionScalar("StaticPressure");
		VectorMagnitudeFieldFunction wallShearStress = fieldFunction.getFieldFunctionVectorMag("WallShearStress");
		
		// Static Pressure reports for the large and small channel
		InnerClass.reportMonitorPlot(activeSim, objectPath_0, reportLargeNames, staticPressure, plotTitles[0], axesTitles);
		InnerClass.reportMonitorPlot(activeSim, objectPath_1, reportSmallNames, staticPressure, plotTitles[1], axesTitles);
		
		// Shear Stress reports for the large and small channel
		InnerClass.reportMonitorPlot(activeSim, objectPath_0, reportLargeNamesStress, wallShearStress, plotTitlesStress[0], axesTitlesStress);
		InnerClass.reportMonitorPlot(activeSim, objectPath_1, reportSmallNamesStress, wallShearStress, plotTitlesStress[1], axesTitlesStress);
		
		// Turning off the "Auto" normalization option for all the residual monitors
		ReportsMonitorsPlots reportsMonitorsPlots = new ReportsMonitorsPlots(activeSim);
		reportsMonitorsPlots.residualNormalization();
		
		// Creating an XY plot of the pressure profiles within the plate channels
		double[] directionVector = {0, 1, 0};
		XYPlot pressureProfiles = reportsMonitorsPlots.createXYPlot(directionVector, "PressureProfiles", "Static Pressure (Pa)");
		
		reportsMonitorsPlots.addObject2XYPlot(pressureProfiles, largeChannelName, largeChannelSurfaceNames[3]);
		reportsMonitorsPlots.addObject2XYPlot(pressureProfiles, smallChannelName, smallChannelSurfaceNames[1]);
	    fieldFunction.setXYPlotFieldFunction(pressureProfiles, "StaticPressure", "0");
	    
	    XYPlot fullPressureProfiles = reportsMonitorsPlots.createXYPlot(directionVector, "FullPressureProfiles", "Static Pressure (Pa)");
	    reportsMonitorsPlots.addLineProbe2XYPlot(fullPressureProfiles, smChannelProbe);
	    reportsMonitorsPlots.addLineProbe2XYPlot(fullPressureProfiles, lgChannelProbe);
	    fieldFunction.setXYPlotFieldFunction(fullPressureProfiles, "StaticPressure", "0");
	    
	    // Creating an XY plot of the wall y+ values in the channels
	    double[] directionVector_0 = {1, 0, 0};
	    XYPlot wallYplusPlot = reportsMonitorsPlots.createXYPlot(directionVector_0, "Wall Y+", "Wall Y+");
	    
	    reportsMonitorsPlots.addObject2XYPlot(wallYplusPlot, largeChannelName, largeChannelSurfaceNames[3]);
	    reportsMonitorsPlots.addObject2XYPlot(wallYplusPlot, largeChannelName, largeChannelSurfaceNames[1]);
	    reportsMonitorsPlots.addObject2XYPlot(wallYplusPlot, smallChannelName, smallChannelSurfaceNames[1]);
	    reportsMonitorsPlots.addObject2XYPlot(wallYplusPlot, smallChannelName, smallChannelSurfaceNames[3]);
	    fieldFunction.setXYPlotFieldFunction(wallYplusPlot, "WallYplus", "Magnitude");
	    
	    /**-----------------------------------------------------------------------------------------------------------------------------------------------------
			STOPPING CRITERIA NODE */
		StoppingCriteria stoppingCriteria = new StoppingCriteria(activeSim);
		stoppingCriteria.innerIterationStoppingCriteriaController(1000, "AND", false);
		
		for (int i = 0; i < reportLargeNames.length; i++)
		{
			stoppingCriteria.createAsymStoppingCriteria(reportLargeNames[i] + " Monitor", "Report", 1.0, 25, "AND", false);
			stoppingCriteria.createAsymStoppingCriteria(reportSmallNames[i] + " Monitor", "Report", 1.0, 25, "AND", false);
		}
		
		for (int i = 0; i < reportLargeNamesStress.length; i++)
		{
			stoppingCriteria.createAsymStoppingCriteria(reportLargeNamesStress[i] + " Monitor", "Report", 1.0, 25, "AND", false);
			stoppingCriteria.createAsymStoppingCriteria(reportSmallNamesStress[i] + " Monitor", "Report", 1.0, 25, "AND", false);
		}
		
		stoppingCriteria.maxPhysicalTime(125, "OR", false);
		stoppingCriteria.maxSteps(1000, "OR", false);
		
		stoppingCriteria.createMinStoppingCriteria("Continuity", "Residual", 5e-8, false);
		
		/**-----------------------------------------------------------------------------------------------------------------------------------------------------
			SCENES NODE */
		Scenes scene = new Scenes(activeSim);
		Scene wallYplus = scene.createScalarScene("Wall Y+");
		
		scene.addObject2Scene(wallYplus, largeChannelName, largeChannelSurfaceNames);
		scene.addObject2Scene(wallYplus, smallChannelName, smallChannelSurfaceNames);
		scene.addObject2Scene(wallYplus, inletSmallName, inletSmallSurfaceNames);
		scene.addObject2Scene(wallYplus, inletPlateName, inletPlateSurfaceNames);
		scene.addObject2Scene(wallYplus, inletLargeName, inletLargeSurfaceNames);
		scene.addObject2Scene(wallYplus, outletSmallName, outletSmallSurfaceNames);
		scene.addObject2Scene(wallYplus, outletPlateName, outletPlateSurfaceNames);
		scene.addObject2Scene(wallYplus, outletLargeName, outletLargeSurfaceNames);
		
		fieldFunction.setSceneFieldFunction("Wall Y+", "WallYplus", "0");
		
		Scene velocityScene = scene.createScalarScene("Velocity_CenterPlane");
		scene.addDerivedPart2Scene(velocityScene, new String[] {"CenterPlane"});
		fieldFunction.setSceneFieldFunction("Velocity_CenterPlane", "Velocity", "Magnitude");
		
		Scene pressureScene = scene.createScalarScene("Pressure");
		scene.addObject2Scene(pressureScene, largeChannelName, largeChannelSurfaceNames);
		scene.addObject2Scene(pressureScene, smallChannelName, smallChannelSurfaceNames);
		scene.addObject2Scene(pressureScene, inletSmallName, inletSmallSurfaceNames);
		scene.addObject2Scene(pressureScene, inletPlateName, inletPlateSurfaceNames);
		scene.addObject2Scene(pressureScene, inletLargeName, inletLargeSurfaceNames);
		scene.addObject2Scene(pressureScene, outletSmallName, outletSmallSurfaceNames);
		scene.addObject2Scene(pressureScene, outletPlateName, outletPlateSurfaceNames);
		scene.addObject2Scene(pressureScene, outletLargeName, outletLargeSurfaceNames);
		
		fieldFunction.setSceneFieldFunction("Pressure", "StaticPressure", "0");
		
	}//end execute method
	
	private static class InnerClass
	{
		/** This method builds a part and meshes it using the directed mesher */
		private static void partBuilder(Simulation sim, double extrude, double[] y, double[] z, String sketchPlane, String partName, String[] surfaceNames)
		{
			GeometryBuilder part = new GeometryBuilder(sim);
			part.boxBuilder(sketchPlane, y, z, extrude, partName);
			part.splitSurface(partName, 89, surfaceNames);
			part.partTranslate(partName, 0.0127, 0, 0);
			
			RegionBuilder region = new RegionBuilder(sim);
			region.part2Region(partName, false);
		}// end nested method partBuilder
		
		/** This method builds the mesh around a part */
		private static void partMesher(Simulation sim, String partName, String[] surfaceNames,int meshNodeX, int meshNodeY, int meshNodeZ, 
				double meshSpacingX, double meshSpacingY, String flagX, String flagY, boolean isDirectionReversedX, boolean isDirectionReversedY)
		{
			DirectedMesher7_09 mesh = new DirectedMesher7_09(sim, partName);
			mesh.setSourceTargetSurfaces(surfaceNames[4], surfaceNames[5]);
			mesh.definePatchCurveParameters(meshNodeY, meshNodeX, meshSpacingY, meshSpacingX, flagY, flagX, isDirectionReversedY, isDirectionReversedX);
			mesh.createDirectedVolumeMesh(meshNodeZ);
		}
		
		/** This method creates reports, monitors, and a plot of a specfied field function */
		private static void reportMonitorPlot(Simulation sim, String[] objectPath, String[] reportNames, 
				FieldFunction fieldFunction, String plotTitles, String[] axesTitles)
		{
			ReportsMonitorsPlots reportsMonitorsPlots = new ReportsMonitorsPlots(sim);
			MinReport minPressureReport_0 = reportsMonitorsPlots.createMinReport(objectPath, reportNames[0]);
			minPressureReport_0.setScalar(fieldFunction);
			MaxReport maxPressureReport_0 = reportsMonitorsPlots.createMaxReport(objectPath, reportNames[1]);
			maxPressureReport_0.setScalar(fieldFunction);
			AreaAverageReport avgPressureReport_0 = reportsMonitorsPlots.createAverageReport(objectPath, reportNames[2]);
			avgPressureReport_0.setScalar(fieldFunction);
			reportsMonitorsPlots.createMonitorPlot2(reportNames, plotTitles, axesTitles, true);
		}

	}// end inner class InnerClass
	
}//end class testStarClasses