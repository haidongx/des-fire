# About this project

des-fire is a discrete event wildfire simulation tool based on DES framework.

# Researches based on this code
* [Data assimilation using sequential monte carlo methods in wildfire spread simulation](https://dl.acm.org/citation.cfm?id=2379816)
* [Coupled fire–atmosphere modeling of wildland fire spread using DEVS-FIRE and ARPS](https://link.springer.com/article/10.1007/s11069-015-1640-y)

# Set up dependencies

## Use Maven
1. Git pull
  * [HaidongXue/2d-cellular-automata-presentation](https://github.com/HaidongXue/2d-cellular-automata-presentation.git)
  * [HaidongXue/des](https://github.com/HaidongXue/des.git)
2. Maven insall all of the above
3. Maven download dependencies for this project

## Use other methods
One may also set up dependencies by other methods, e.g. package and add each of them as a jar file.

# Usage

## Sample fire simulation
Run edu.gsu.hxue.desFire.testing.TestFireSystem. One will see the simulation on September 2000 Moore Branch Fire.

## Coupled simulation with ARPS
Run edu.gsu.hxue.desFire.couplingWithARPS.CouplingShell with argument of a confiuration file e.g.

```
#Configuration of DES-FIRE/ARPS coupled simulation
#Tue May 22 17:39:00 EDT 2012

# GIS data
GISFileFolder=GISData/
fuelFileName=MooreBranch_fuel.txt
slopeFileName=MooreBranch_slope.txt
aspectFileName=MooreBranch_aspect.txt

# Initial wind schedule
initialWeatherFileName=weather_artificial_DEVSFIRE.txt

# Ignition
ignitionFile=IgnitionPoints_MB_corrected.txt
laterIgnitionFile=IgnitionPoints_later_empty.txt

# Initial contained cells
initialContainedCells=InitialContainedCells_MB.txt

# Coupling related data (numberOfSteps is the number of weather update steps)
weatherUpdateInterval=1.0
heatUpdateInterval=1
numberOfSteps=3

# When nested is true, a grid will be modeled by nxn cells, n is the scaleFactor
nested=false
scaleFactor=1

# If use new heat model for initial ignition cells
useNewHeatModelForInitialIgnitionCells = false
```

# More questions?
Contact [Xiaolin Hu](https://grid.cs.gsu.edu/~cscxlh/) or me.

